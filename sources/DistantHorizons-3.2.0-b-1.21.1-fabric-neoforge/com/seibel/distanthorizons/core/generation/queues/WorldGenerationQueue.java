package com.seibel.distanthorizons.core.generation.queues;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGeneratorReturnType;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.IDhApiWorldGenerator;
import com.seibel.distanthorizons.api.objects.data.DhApiChunk;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dataObjects.transformers.LodDataBuilder;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.generation.DhLightingEngine;
import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalResult;
import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalTask;
import com.seibel.distanthorizons.core.level.IDhServerLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.core.util.objects.RollingAverage;
import com.seibel.distanthorizons.core.util.objects.UncheckedInterruptedException;
import com.seibel.distanthorizons.core.util.threading.PriorityTaskPicker;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.world.DhApiWorldProxy;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class WorldGenerationQueue implements IFullDataSourceRetrievalQueue, IDebugRenderable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   private static final AbstractDebugWireframeRenderer DEBUG_RENDERER = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
   private final IDhApiWorldGenerator generator;
   private final IDhServerLevel level;
   private final ConcurrentHashMap<Long, DataSourceRetrievalTask> waitingTasks = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<Long, DataSourceRetrievalTask> inProgressGenTasksByLodPos = new ConcurrentHashMap<>();
   public final byte lowestDataDetail;
   public final byte highestDataDetail;
   private volatile CompletableFuture<Void> generatorClosingFuture = null;
   private final ExecutorService queueingThread = ThreadUtil.makeSingleThreadPool("World Gen Queue");
   private boolean generationQueueRunning = false;
   private DhBlockPos2D generationTargetPos = DhBlockPos2D.ZERO;
   private int estimatedRemainingTaskCount = 0;
   private int estimatedRemainingChunkCount = 0;
   private final RollingAverage rollingAverageChunkGenTimeInMs = new RollingAverage(Runtime.getRuntime().availableProcessors() * 500);

   @Override
   public RollingAverage getRollingAverageChunkGenTimeInMs() {
      return this.rollingAverageChunkGenTimeInMs;
   }

   public WorldGenerationQueue(IDhApiWorldGenerator generator, IDhServerLevel level) {
      LOGGER.info("Creating world gen queue");
      this.generator = generator;
      this.level = level;
      this.lowestDataDetail = generator.getLargestDataDetailLevel();
      this.highestDataDetail = generator.getSmallestDataDetailLevel();
      DEBUG_RENDERER.register(this, Config.Client.Advanced.Debugging.DebugWireframe.showWorldGenQueue);
      LOGGER.info("Created world gen queue");
   }

   @Override
   public CompletableFuture<DataSourceRetrievalResult> submitRetrievalTask(long pos, byte requiredDataDetail) {
      if (this.generatorClosingFuture != null) {
         CompletableFuture<DataSourceRetrievalResult> f = new CompletableFuture<>();
         f.completeExceptionally(new CancellationException());
         return f;
      } else {
         DataSourceRetrievalTask existingGenTask = this.waitingTasks.get(pos);
         if (existingGenTask != null) {
            return existingGenTask.future;
         } else if (requiredDataDetail < this.highestDataDetail) {
            throw new UnsupportedOperationException("Current generator does not meet requiredDataDetail level");
         } else {
            if (requiredDataDetail > this.lowestDataDetail) {
               requiredDataDetail = this.lowestDataDetail;
            }

            LodUtil.assertTrue(DhSectionPos.getDetailLevel(pos) > requiredDataDetail + 4);
            DataSourceRetrievalTask genTask = new DataSourceRetrievalTask(pos, requiredDataDetail);
            this.waitingTasks.put(pos, genTask);
            return genTask.future;
         }
      }
   }

   @Override
   public void removeRetrievalRequestIf(DhSectionPos.ICancelablePrimitiveLongConsumer removeIf) {
      this.waitingTasks.forEachKey(100L, genPos -> {
         if (removeIf.accept(genPos)) {
            DataSourceRetrievalTask removedTask = this.waitingTasks.remove(genPos);
            if (removedTask != null) {
               removedTask.future.cancel(true);
            }
         }
      });
   }

   @Override
   public void startAndSetTargetPos(DhBlockPos2D targetPos) {
      this.generationTargetPos = targetPos;
      this.tryQueueNewWorldGenRequestsAsync();
   }

   private synchronized void tryQueueNewWorldGenRequestsAsync() {
      if (DhApiWorldProxy.INSTANCE.worldLoaded() && !DhApiWorldProxy.INSTANCE.tryGetReadOnly()) {
         if (!this.generationQueueRunning) {
            this.generationQueueRunning = true;
            this.queueingThread.execute(() -> {
               try {
                  this.generator.preGeneratorTaskStart();
                  boolean taskStarted = true;

                  while (!this.isGeneratorBusy() && taskStarted) {
                     taskStarted = this.tryStartNextWorldGenTask(this.generationTargetPos);
                  }
               } catch (Exception var5) {
                  LOGGER.error("queueing exception: " + var5.getMessage(), var5);
               } finally {
                  this.generationQueueRunning = false;
               }
            });
         }
      }
   }

   private boolean isGeneratorBusy() {
      PriorityTaskPicker.Executor executor = ThreadPoolUtil.getWorldGenExecutor();
      if (executor == null) {
         return true;
      } else {
         int worldGenThreadCount = Math.max(Config.Common.MultiThreading.numberOfThreads.get(), 1);
         return this.inProgressGenTasksByLodPos.size() > worldGenThreadCount;
      }
   }

   private boolean tryStartNextWorldGenTask(DhBlockPos2D targetPos) {
      if (this.waitingTasks.isEmpty()) {
         return false;
      } else {
         WorldGenerationQueue.TaskDistancePair closestTaskPair = this.waitingTasks.reduceEntries(1024L, entry -> {
            DataSourceRetrievalTask task = entry.getValue();
            int distance = DhSectionPos.getCenterBlockPos(task.pos).chebyshevDist(targetPos);
            return new WorldGenerationQueue.TaskDistancePair(entry.getValue(), distance);
         }, (aTaskPair, bTaskPair) -> (WorldGenerationQueue.TaskDistancePair)(aTaskPair.dist < bTaskPair.dist ? aTaskPair : bTaskPair));
         if (closestTaskPair == null) {
            return false;
         } else {
            DataSourceRetrievalTask closestTask = closestTaskPair.task;
            this.waitingTasks.remove(closestTask.pos, closestTask);
            if (this.canGenerateDetailLevel(DhSectionPos.getDetailLevel(closestTask.pos))) {
               DataSourceRetrievalTask existingTask = this.inProgressGenTasksByLodPos.get(closestTask.pos);
               if (existingTask == null) {
                  this.startWorldGenTaskGroup(closestTask);
               } else {
                  existingTask.future.thenApply(result -> {
                     closestTask.future.complete(result);
                     return closestTask.future;
                  });
                  existingTask.future.exceptionally(throwable -> {
                     closestTask.future.completeExceptionally(throwable);
                     return null;
                  });
               }
            } else {
               closestTask.future.complete(DataSourceRetrievalResult.CreateSplit());
            }

            return true;
         }
      }
   }

   private boolean canGenerateDetailLevel(byte taskDetailLevel) {
      byte requestedDetailLevel = (byte)(taskDetailLevel - 6);
      return this.highestDataDetail <= requestedDetailLevel && requestedDetailLevel <= this.lowestDataDetail;
   }

   private void startWorldGenTaskGroup(DataSourceRetrievalTask worldGenTask) {
      long taskPos = worldGenTask.pos;
      LodUtil.assertTrue(
         worldGenTask.requestDetailLevel >= this.highestDataDetail && worldGenTask.requestDetailLevel <= this.lowestDataDetail,
         "World gen task started that isn't within the range that the generator can create."
      );
      long generationStartMsTime = System.currentTimeMillis();
      CompletableFuture<FullDataSourceV2> generationFuture = this.startGenerationEvent(worldGenTask);
      generationFuture.thenRun(() -> {
         long totalGenTimeInMs = System.currentTimeMillis() - generationStartMsTime;
         int chunkCount = worldGenTask.widthInChunks * worldGenTask.widthInChunks;
         double timePerChunk = (double)totalGenTimeInMs / chunkCount;
         this.rollingAverageChunkGenTimeInMs.add(timePerChunk);
      });
      generationFuture.handle((fullDataSource, exception) -> {
         try {
            if (exception != null) {
               if (!ExceptionUtil.isInterruptOrReject(exception)) {
                  LOGGER.error("Error generating data for pos: " + DhSectionPos.toString(taskPos), exception);
               }

               LodUtil.assertTrue(fullDataSource == null);
               worldGenTask.future.completeExceptionally(exception);
            } else {
               boolean taskRemoved = this.inProgressGenTasksByLodPos.remove(taskPos, worldGenTask);
               LodUtil.assertTrue(taskRemoved, "Unable to find in progress generator task with position [" + DhSectionPos.toString(taskPos) + "]");
               worldGenTask.future.complete(DataSourceRetrievalResult.CreateSuccess(taskPos, fullDataSource));
            }
         } catch (Exception var10) {
            LOGGER.error("Unexpected error completing world gen task at pos: [" + DhSectionPos.toString(taskPos) + "].", var10);
            worldGenTask.future.completeExceptionally(var10);
         } finally {
            this.tryQueueNewWorldGenRequestsAsync();
         }

         return null;
      });
   }

   private CompletableFuture<FullDataSourceV2> startGenerationEvent(DataSourceRetrievalTask task) {
      this.inProgressGenTasksByLodPos.put(task.pos, task);
      DhChunkPos chunkPosMin = new DhChunkPos(new DhBlockPos2D(DhSectionPos.getMinCornerBlockX(task.pos), DhSectionPos.getMinCornerBlockZ(task.pos)));
      EDhApiDistantGeneratorMode generatorMode = Config.Common.WorldGenerator.distantGeneratorMode.get();
      EDhApiWorldGeneratorReturnType returnType = this.generator.getReturnType();
      switch (returnType) {
         case VANILLA_CHUNKS:
            return this.startVanillaChunkGenerationEvent(task, chunkPosMin, generatorMode);
         case API_CHUNKS:
            return this.startApiChunkGenerationEvent(task, chunkPosMin, generatorMode);
         case API_DATA_SOURCES:
            return this.startApiDataSourceGenerationEvent(task, chunkPosMin, generatorMode);
         default:
            Config.Common.WorldGenerator.enableDistantGeneration.set(false);
            throw new LodUtil.AssertFailureException("Unknown return type: " + returnType);
      }
   }

   private CompletableFuture<FullDataSourceV2> startVanillaChunkGenerationEvent(
      DataSourceRetrievalTask task, DhChunkPos chunkPosMin, EDhApiDistantGeneratorMode generatorMode
   ) {
      CompletableFuture<FullDataSourceV2> returnFuture = new CompletableFuture<>();
      ArrayList<IChunkWrapper> generatedChunks = new ArrayList<>(task.widthInChunks * task.widthInChunks);
      CompletableFuture<Void> chunkGenFuture = this.generator
         .generateChunks(
            chunkPosMin.getX(),
            chunkPosMin.getZ(),
            task.widthInChunks,
            task.requestDetailLevel,
            generatorMode,
            ThreadPoolUtil.getWorldGenExecutor(),
            generatedObjectArray -> {
               try {
                  IChunkWrapper chunkWrapper = WRAPPER_FACTORY.createChunkWrapper(generatedObjectArray);
                  generatedChunks.add(chunkWrapper);
               } catch (ClassCastException var3) {
                  LOGGER.error("World generator return type incorrect. Error: [" + var3.getMessage() + "]. World generator disabled.", var3);
                  Config.Common.WorldGenerator.enableDistantGeneration.set(false);
               } catch (Exception var4x) {
                  LOGGER.error("Unexpected world generator error. Error: [" + var4x.getMessage() + "]. World generator disabled.", var4x);
                  Config.Common.WorldGenerator.enableDistantGeneration.set(false);
               }
            }
         );
      chunkGenFuture.exceptionally(throwable -> {
         returnFuture.completeExceptionally(throwable);
         return null;
      });
      chunkGenFuture.thenRun(() -> {
         FullDataSourceV2 requestedDataSource = FullDataSourceV2.createEmpty(task.pos);

         for (int i = 0; i < generatedChunks.size(); i++) {
            IChunkWrapper chunkWrapper = generatedChunks.get(i);
            if (!chunkWrapper.isDhBlockLightingCorrect()) {
               ArrayList<IChunkWrapper> nearbyChunkList = new ArrayList<>();
               nearbyChunkList.add(chunkWrapper);
               byte maxSkyLight = (byte)(this.level.getLevelWrapper().hasSkyLight() ? 15 : 0);
               DhLightingEngine.INSTANCE.bakeChunkBlockLighting(chunkWrapper, nearbyChunkList, maxSkyLight);
            }

            FullDataSourceV2 generatedDataSource = LodDataBuilder.createFromChunk(this.level.getLevelWrapper(), chunkWrapper);

            try {
               LodUtil.assertTrue(generatedDataSource != null);
               requestedDataSource.updateFromDataSource(generatedDataSource);
            } catch (Throwable var11) {
               if (generatedDataSource != null) {
                  try {
                     generatedDataSource.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (generatedDataSource != null) {
               generatedDataSource.close();
            }
         }

         DhLightingEngine.INSTANCE.bakeDataSourceSkyLight(requestedDataSource, 15);
         returnFuture.complete(requestedDataSource);
      });
      return returnFuture;
   }

   private CompletableFuture<FullDataSourceV2> startApiChunkGenerationEvent(
      DataSourceRetrievalTask task, DhChunkPos chunkPosMin, EDhApiDistantGeneratorMode generatorMode
   ) {
      CompletableFuture<FullDataSourceV2> returnFuture = new CompletableFuture<>();
      ArrayList<DhApiChunk> generatedChunks = new ArrayList<>(task.widthInChunks * task.widthInChunks);
      CompletableFuture<Void> chunkGenFuture = this.generator
         .generateApiChunks(
            chunkPosMin.getX(),
            chunkPosMin.getZ(),
            task.widthInChunks,
            task.requestDetailLevel,
            generatorMode,
            ThreadPoolUtil.getWorldGenExecutor(),
            apiChunk -> generatedChunks.add(apiChunk)
         );
      chunkGenFuture.exceptionally(throwable -> {
         returnFuture.completeExceptionally(throwable);
         return null;
      });
      chunkGenFuture.thenRun(() -> {
         FullDataSourceV2 requestedDataSource = FullDataSourceV2.createEmpty(task.pos);

         for (int i = 0; i < generatedChunks.size(); i++) {
            DhApiChunk apiChunk = generatedChunks.get(i);

            try {
               FullDataSourceV2 generatedDataSource = LodDataBuilder.createFromApiChunkData(apiChunk, this.generator.runApiValidation());

               try {
                  requestedDataSource.updateFromDataSource(generatedDataSource);
               } catch (Throwable var11) {
                  if (generatedDataSource != null) {
                     try {
                        generatedDataSource.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }
                  }

                  throw var11;
               }

               if (generatedDataSource != null) {
                  generatedDataSource.close();
               }
            } catch (IllegalArgumentException | DataCorruptedException var12) {
               LOGGER.error("World generator returned a corrupt API chunk. Error: [" + var12.getMessage() + "]. World generator disabled.", var12);
               Config.Common.WorldGenerator.enableDistantGeneration.set(false);
            }
         }

         returnFuture.complete(requestedDataSource);
      });
      return returnFuture;
   }

   private CompletableFuture<FullDataSourceV2> startApiDataSourceGenerationEvent(
      DataSourceRetrievalTask task, DhChunkPos chunkPosMin, EDhApiDistantGeneratorMode generatorMode
   ) {
      CompletableFuture<FullDataSourceV2> returnFuture = new CompletableFuture<>();
      FullDataSourceV2 pooledDataSource = FullDataSourceV2.createEmpty(task.pos);
      pooledDataSource.setRunApiSetterValidation(this.generator.runApiValidation());
      pooledDataSource.applyToChildren = DhSectionPos.getDetailLevel(pooledDataSource.getPos()) > 6;
      pooledDataSource.applyToParent = DhSectionPos.getDetailLevel(pooledDataSource.getPos()) < 15;
      CompletableFuture<Void> lodGenFuture = this.generator
         .generateLod(
            chunkPosMin.getX(),
            chunkPosMin.getZ(),
            DhSectionPos.getX(task.pos),
            DhSectionPos.getZ(task.pos),
            (byte)(DhSectionPos.getDetailLevel(task.pos) - 6),
            pooledDataSource,
            generatorMode,
            ThreadPoolUtil.getWorldGenExecutor(),
            apiDataSource -> {}
         );
      lodGenFuture.exceptionally(throwable -> {
         returnFuture.completeExceptionally(throwable);
         pooledDataSource.close();
         return null;
      });
      lodGenFuture.thenRun(() -> returnFuture.complete(pooledDataSource));
      return returnFuture;
   }

   @Override
   public int getWaitingTaskCount() {
      return this.waitingTasks.size();
   }

   @Override
   public int getInProgressTaskCount() {
      return this.inProgressGenTasksByLodPos.size();
   }

   @Override
   public byte lowestDataDetail() {
      return this.lowestDataDetail;
   }

   @Override
   public byte highestDataDetail() {
      return this.highestDataDetail;
   }

   @Override
   public String getRetrievalTypeName() {
      return "generating chunks";
   }

   @Override
   public int getEstimatedRemainingTaskCount() {
      return this.estimatedRemainingTaskCount;
   }

   @Override
   public void setEstimatedRemainingTaskCount(int newEstimate) {
      this.estimatedRemainingTaskCount = newEstimate;
   }

   @Override
   public int getRetrievalEstimatedRemainingChunkCount() {
      return this.estimatedRemainingChunkCount;
   }

   @Override
   public void setRetrievalEstimatedRemainingChunkCount(int newEstimate) {
      this.estimatedRemainingChunkCount = newEstimate;
   }

   @Override
   public void addDebugMenuStringsToList(List<String> messageList) {
   }

   @Override
   public int getQueuedChunkCount() {
      int chunkCount = 0;

      for (long pos : this.waitingTasks.keySet()) {
         int chunkWidth = DhSectionPos.getBlockWidth(pos) / 16;
         chunkCount += chunkWidth * chunkWidth;
      }

      return chunkCount;
   }

   @Override
   public void debugRender(AbstractDebugWireframeRenderer renderer) {
      int levelMinY = this.level.getLevelWrapper().getMinHeight();
      int levelMaxY = this.level.getLevelWrapper().getMaxHeight();
      int levelHeightRange = levelMaxY - levelMinY;
      int maxY = levelMaxY - levelHeightRange / 2;
      this.waitingTasks.keySet().forEach(pos -> renderer.renderBox(new AbstractDebugWireframeRenderer.Box(pos, levelMinY, maxY, 0.05F, Color.blue)));
      this.inProgressGenTasksByLodPos
         .forEach((pos, task) -> renderer.renderBox(new AbstractDebugWireframeRenderer.Box(pos, levelMinY, maxY, 0.05F, Color.red)));
   }

   @Override
   public CompletableFuture<Void> startClosingAsync(boolean cancelCurrentGeneration, boolean alsoInterruptRunning) {
      LOGGER.info("Closing world gen queue");
      this.queueingThread.shutdownNow();
      ArrayList<CompletableFuture<Void>> inProgressTasksCancelingFutures = new ArrayList<>(this.inProgressGenTasksByLodPos.size());
      this.inProgressGenTasksByLodPos
         .values()
         .forEach(
            genTask -> {
               CompletableFuture<DataSourceRetrievalResult> genFuture = genTask.future;
               if (cancelCurrentGeneration) {
                  genFuture.cancel(alsoInterruptRunning);
               }

               inProgressTasksCancelingFutures.add(
                  genFuture.handle(
                     (result, throwable) -> {
                        if (throwable instanceof CompletionException) {
                           throwable = throwable.getCause();
                        }

                        if (!UncheckedInterruptedException.isInterrupt(throwable) && !(throwable instanceof CancellationException)) {
                           LOGGER.error(
                              "Error when terminating data generation for pos: ["
                                 + DhSectionPos.toString(genTask.pos)
                                 + "], error: ["
                                 + throwable.getMessage()
                                 + "].",
                              throwable
                           );
                        }

                        if (result != null && result.dataSource != null) {
                           result.dataSource.close();
                        }

                        return null;
                     }
                  )
               );
            }
         );
      this.generatorClosingFuture = CompletableFuture.allOf(inProgressTasksCancelingFutures.toArray(new CompletableFuture[0]));
      return this.generatorClosingFuture;
   }

   @Override
   public void close() {
      LOGGER.info("Closing " + WorldGenerationQueue.class.getSimpleName() + "...");
      if (this.generatorClosingFuture == null) {
         this.startClosingAsync(true, true);
      }

      LodUtil.assertTrue(this.generatorClosingFuture != null);
      LOGGER.info("Shutting down world generator thread pool...");
      PriorityTaskPicker.Executor executor = ThreadPoolUtil.getWorldGenExecutor();
      if (executor != null) {
         int queueSize = executor.getQueueSize();
         executor.clearQueue();
         LOGGER.info("World generator thread pool shutdown with [" + queueSize + "] incomplete tasks.");
      }

      this.inProgressGenTasksByLodPos.values().forEach(inProgressWorldGenTaskGroup -> inProgressWorldGenTaskGroup.future.cancel(true));
      this.waitingTasks.values().forEach(worldGenTask -> worldGenTask.future.cancel(true));
      this.generator.close();
      DEBUG_RENDERER.unregister(this, Config.Client.Advanced.Debugging.DebugWireframe.showWorldGenQueue);

      try {
         this.generatorClosingFuture.cancel(true);
      } catch (Throwable var3) {
         LOGGER.warn("Failed to close generation queue: ", var3);
      }

      LOGGER.info("Finished closing " + WorldGenerationQueue.class.getSimpleName());
   }

   private static class TaskDistancePair {
      public final DataSourceRetrievalTask task;
      public final int dist;

      public TaskDistancePair(DataSourceRetrievalTask task, int dist) {
         this.task = task;
         this.dist = dist;
      }
   }
}
