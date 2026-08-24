package com.seibel.distanthorizons.core.file.fullDatafile;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.WorldChunkUpdateManager;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.file.fullDatafile.V2.FullDataSourceProviderV2;
import com.seibel.distanthorizons.core.file.structure.ISaveStructure;
import com.seibel.distanthorizons.core.generation.DhLightingEngine;
import com.seibel.distanthorizons.core.generation.queues.IFullDataSourceRetrievalQueue;
import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalResult;
import com.seibel.distanthorizons.core.generation.tasks.ERetrievalResultState;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.delayedSaveCache.DelayedDataSourceSaveCache;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratedFullDataSourceProvider extends FullDataSourceProviderV2 implements IDebugRenderable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final int MAX_WORLD_GEN_REQUESTS_PER_THREAD = 20;
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("Generated Provider");
   private final AtomicReference<IFullDataSourceRetrievalQueue> worldGenQueueRef = new AtomicReference<>(null);
   private final ArrayList<GeneratedFullDataSourceProvider.IOnWorldGenCompleteListener> onWorldGenTaskCompleteListeners = new ArrayList<>();
   protected final DelayedDataSourceSaveCache delayedFullDataSourceSaveCache = new DelayedDataSourceSaveCache(this::onDataSourceSaveAsync, 10000);
   private final ConcurrentHashMap<Long, CompletableFuture<DataSourceRetrievalResult>> queuedRetrievalFutureByPos = new ConcurrentHashMap<>();

   public GeneratedFullDataSourceProvider(IDhLevel level, ISaveStructure saveStructure) throws SQLException, IOException {
      this(level, saveStructure, null);
   }

   public GeneratedFullDataSourceProvider(IDhLevel level, ISaveStructure saveStructure, @Nullable File saveDirOverride) throws SQLException, IOException {
      super(level, saveStructure, saveDirOverride);
   }

   public void addWorldGenCompleteListener(GeneratedFullDataSourceProvider.IOnWorldGenCompleteListener listener) {
      synchronized (this.onWorldGenTaskCompleteListeners) {
         this.onWorldGenTaskCompleteListeners.add(listener);
      }
   }

   public void removeWorldGenCompleteListener(GeneratedFullDataSourceProvider.IOnWorldGenCompleteListener listener) {
      synchronized (this.onWorldGenTaskCompleteListeners) {
         this.onWorldGenTaskCompleteListeners.remove(listener);
      }
   }

   private void onWorldGenTaskComplete(@NotNull Long genPos, @Nullable DataSourceRetrievalResult genTaskResult, @Nullable Throwable exception) {
      try {
         if (exception == null) {
            Objects.requireNonNull(genTaskResult);
            if (genTaskResult.state == ERetrievalResultState.SUCCESS) {
               LodUtil.assertTrue(genTaskResult.dataSource != null, "Successful retrieval object should have a datasource.");
               this.dataUpdater.updateDataSource(genTaskResult.dataSource);
               synchronized (this.onWorldGenTaskCompleteListeners) {
                  for (GeneratedFullDataSourceProvider.IOnWorldGenCompleteListener listener : this.onWorldGenTaskCompleteListeners) {
                     listener.onWorldGenTaskComplete(genTaskResult.pos);
                  }
               }

               genTaskResult.dataSource.close();
               return;
            } else {
               if (genTaskResult.state == ERetrievalResultState.REQUIRES_SPLITTING) {
                  LodUtil.assertTrue(genTaskResult.dataSource == null, "Split retrieval object should not have a datasource.");
               } else {
                  LOGGER.warn(
                     "Unexpected gen Task state at: ["
                        + DhSectionPos.toString(genTaskResult.pos)
                        + "], state: ["
                        + genTaskResult.state
                        + "], datasource: NULL, exception: NULL."
                  );
               }

               return;
            }
         }

         if (!ExceptionUtil.isInterruptOrReject(exception)) {
            LOGGER.error("Uncaught Gen Task Exception at [" + genPos + "], error: [" + exception.getMessage() + "].", exception);
         }
      } catch (Exception var13) {
         LOGGER.error("Unexpected issue during onWorldGenTaskComplete, error: [" + var13.getMessage() + "].", var13);
         return;
      } finally {
         this.queuedRetrievalFutureByPos.remove(genPos);
      }
   }

   public byte lowestDataDetailLevel() {
      IFullDataSourceRetrievalQueue fullDataSourceRetrievalQueue = this.worldGenQueueRef.get();
      return fullDataSourceRetrievalQueue == null ? 6 : (byte)(6 + fullDataSourceRetrievalQueue.lowestDataDetail());
   }

   public void setWorldGenerationQueue(IFullDataSourceRetrievalQueue newWorldGenQueue) {
      boolean oldQueueExists = this.worldGenQueueRef.compareAndSet(null, newWorldGenQueue);
      LodUtil.assertTrue(oldQueueExists, "previous world gen queue is still here!");
      LOGGER.info("Set world gen queue for level [" + this.levelId + "].");
   }

   @Override
   public boolean canRetrieveMissingDataSources() {
      return true;
   }

   @Override
   public void setEstimatedRemainingRetrievalChunkCount(int newCount) {
      IFullDataSourceRetrievalQueue worldGenQueue = this.worldGenQueueRef.get();
      if (worldGenQueue != null) {
         worldGenQueue.setRetrievalEstimatedRemainingChunkCount(newCount);
      }
   }

   @Override
   public boolean canQueueRetrievalNow() {
      return this.canQueueRetrievalNow(false);
   }

   public boolean canQueueRetrievalNow(boolean pruneWaitingTasksAboveLimit) {
      if (!super.canQueueRetrievalNow()) {
         return false;
      } else {
         IFullDataSourceRetrievalQueue worldGenQueue = this.worldGenQueueRef.get();
         if (worldGenQueue == null) {
            return false;
         } else {
            int maxWorldGenQueueCount = 20 * Config.Common.MultiThreading.numberOfThreads.get();
            int currentQueueCount = WorldChunkUpdateManager.INSTANCE.getTotalQueuedCount();
            if (currentQueueCount >= maxWorldGenQueueCount) {
               return false;
            } else if (this.delayedFullDataSourceSaveCache.getUnsavedCount() >= maxWorldGenQueueCount) {
               this.delayedFullDataSourceSaveCache.flush();
               return false;
            } else {
               int availableTaskSlots = maxWorldGenQueueCount - worldGenQueue.getWaitingTaskCount();
               if (availableTaskSlots == 0) {
                  return false;
               } else {
                  if (availableTaskSlots < 0) {
                     if (!pruneWaitingTasksAboveLimit) {
                        return false;
                     }

                     AtomicInteger tasksToCancel = new AtomicInteger(availableTaskSlots * -1);
                     worldGenQueue.removeRetrievalRequestIf(taskPos -> tasksToCancel.getAndDecrement() > 0);
                  }

                  return true;
               }
            }
         }
      }
   }

   @Override
   public CompletableFuture<DataSourceRetrievalResult> queuePositionForRetrieval(Long genPos) {
      IFullDataSourceRetrievalQueue worldGenQueue = this.worldGenQueueRef.get();
      if (worldGenQueue == null) {
         return null;
      } else {
         CompletableFuture<DataSourceRetrievalResult> worldGenFuture = worldGenQueue.submitRetrievalTask(
            genPos, (byte)(DhSectionPos.getDetailLevel(genPos) - 6)
         );
         CompletableFuture<DataSourceRetrievalResult> oldWorldGenFuture = this.queuedRetrievalFutureByPos.putIfAbsent(genPos, worldGenFuture);
         if (oldWorldGenFuture == null) {
            worldGenFuture.whenComplete((r, e) -> this.onWorldGenTaskComplete(genPos, r, e));
         }

         return worldGenFuture;
      }
   }

   @Override
   public void removeRetrievalRequestIf(DhSectionPos.ICancelablePrimitiveLongConsumer removeIf) {
      IFullDataSourceRetrievalQueue worldGenQueue = this.worldGenQueueRef.get();
      if (worldGenQueue != null) {
         worldGenQueue.removeRetrievalRequestIf(removeIf);
      }
   }

   @Override
   public void clearRetrievalQueue() {
      this.worldGenQueueRef.set(null);
   }

   public boolean generationStepsAreFullyGenerated(ByteArrayList columnGenerationSteps) {
      return IntStream.range(0, columnGenerationSteps.size()).noneMatch(intValue -> {
         byte value = columnGenerationSteps.getByte(intValue);
         return value == EDhApiWorldGenerationStep.EMPTY.value || value == EDhApiWorldGenerationStep.DOWN_SAMPLED.value;
      });
   }

   @Override
   public LongArrayList getPositionsToRetrieve(long pos) {
      IFullDataSourceRetrievalQueue worldGenQueue = this.worldGenQueueRef.get();
      if (worldGenQueue == null) {
         return null;
      } else {
         label42:
         if (this.repo.existsWithKey(pos)) {
            PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteArrays(1);

            LongArrayList var12;
            label61: {
               try {
                  ByteArrayList columnGenStepArray = checkout.getByteArray(0, 4096);
                  this.repo.getColumnGenerationStepForPos(pos, columnGenStepArray);
                  if (columnGenStepArray.size() != 0) {
                     boolean positionFullyGenerated = true;

                     for (int i = 0; i < columnGenStepArray.size(); i++) {
                        if (columnGenStepArray.getByte(i) == EDhApiWorldGenerationStep.EMPTY.value
                           || columnGenStepArray.getByte(i) == EDhApiWorldGenerationStep.DOWN_SAMPLED.value) {
                           positionFullyGenerated = false;
                           break;
                        }
                     }

                     if (positionFullyGenerated) {
                        var12 = new LongArrayList();
                        break label61;
                     }
                  }
               } catch (Throwable var9) {
                  if (checkout != null) {
                     try {
                        checkout.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }
                  }

                  throw var9;
               }

               if (checkout != null) {
                  checkout.close();
               }
               break label42;
            }

            if (checkout != null) {
               checkout.close();
            }

            return var12;
         }

         LongArrayList generationList = new LongArrayList();
         byte lowestGeneratorDetailLevel = (byte)Math.min(worldGenQueue.lowestDataDetail() + 6, DhSectionPos.getDetailLevel(pos));
         DhSectionPos.forEachChildAtDetailLevel(pos, lowestGeneratorDetailLevel, genPos -> {
            if (!this.repo.existsWithKey(genPos)) {
               generationList.add(genPos);
            } else {
               EDhApiWorldGenerationStep currentMinWorldGenStep = EDhApiWorldGenerationStep.LIGHT;
               PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteArrays(1);

               label95: {
                  try {
                     ByteArrayList columnGenerationSteps = checkout.getByteArray(0, 4096);
                     this.repo.getColumnGenerationStepForPos(genPos, columnGenerationSteps);
                     if (columnGenerationSteps.isEmpty()) {
                        break label95;
                     }

                     label73:
                     for (int x = 0; x < 64; x++) {
                        for (int z = 0; z < 64; z++) {
                           int index = FullDataSourceV2.relativePosToIndex(x, z);
                           byte genStepValue = columnGenerationSteps.getByte(index);
                           if (genStepValue < currentMinWorldGenStep.value) {
                              EDhApiWorldGenerationStep newWorldGenStep = EDhApiWorldGenerationStep.fromValue(genStepValue);
                              if (newWorldGenStep != null && newWorldGenStep.value < currentMinWorldGenStep.value) {
                                 currentMinWorldGenStep = newWorldGenStep;
                              }
                           }

                           if (currentMinWorldGenStep == EDhApiWorldGenerationStep.EMPTY || currentMinWorldGenStep == EDhApiWorldGenerationStep.DOWN_SAMPLED) {
                              break label73;
                           }
                        }
                     }
                  } catch (Throwable var13) {
                     if (checkout != null) {
                        try {
                           checkout.close();
                        } catch (Throwable var12x) {
                           var13.addSuppressed(var12x);
                        }
                     }

                     throw var13;
                  }

                  if (checkout != null) {
                     checkout.close();
                  }

                  if (currentMinWorldGenStep != EDhApiWorldGenerationStep.EMPTY && currentMinWorldGenStep != EDhApiWorldGenerationStep.DOWN_SAMPLED) {
                     return;
                  }

                  generationList.add(genPos);
                  return;
               }

               if (checkout != null) {
                  checkout.close();
               }
            }
         });
         return generationList;
      }
   }

   @Override
   public void close() {
      super.close();
      this.delayedFullDataSourceSaveCache.close();
   }

   private CompletableFuture<Void> onDataSourceSaveAsync(FullDataSourceV2 fullDataSource) {
      int skyLight = this.level.getLevelWrapper().hasSkyLight() ? 15 : 0;
      DhLightingEngine.INSTANCE.bakeDataSourceSkyLight(fullDataSource, skyLight);
      return this.updateDataSourceAsync(fullDataSource);
   }

   public interface IOnWorldGenCompleteListener {
      boolean shouldDoWorldGen();

      DhBlockPos2D getTargetPosForGeneration();

      void onWorldGenTaskComplete(long l);
   }
}
