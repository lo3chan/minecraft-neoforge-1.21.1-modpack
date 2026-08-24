package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling.ChunkFileReader_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DummyLightEngine_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.LightGetterAdaptor_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.GlobalWorldGenParams_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepBiomes_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepFeatures_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepNoise_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepStructureReference_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepStructureStart_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepSurface_fabric;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.ChunkUpdateQueueManager;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.WorldChunkUpdateManager;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.generation.DhLightingEngine;
import com.seibel.distanthorizons.core.level.IDhServerLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.TimerUtil;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import com.seibel.distanthorizons.core.util.objects.UncheckedInterruptedException;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.ChunkLightStorage;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.worldGeneration.IBatchGeneratorEnvironmentWrapper;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_2794;
import net.minecraft.class_2806;
import net.minecraft.class_2839;
import net.minecraft.class_2891;
import net.minecraft.class_2897;
import net.minecraft.class_2902;
import net.minecraft.class_3754;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BatchGenerationEnvironment_fabric implements IBatchGeneratorEnvironmentWrapper {
   public static final DhLogger LOGGER = new DhLoggerBuilder().name("LOD World Gen").fileLevelConfig(Config.Common.Logging.logWorldGenEventToFile).build();
   public static final DhLogger RATE_LIMITED_LOGGER = new DhLoggerBuilder().name("LOD World Gen").maxCountPerSecond(1).build();
   @NotNull
   public static final ImmutableMap<EDhApiWorldGenerationStep, Integer> WORLD_GEN_CHUNK_BORDER_NEEDED_BY_GEN_STEP;
   public static final int MAX_WORLD_GEN_CHUNK_BORDER_NEEDED = 0;
   public static final long EXCEPTION_TIMER_RESET_TIME = TimeUnit.NANOSECONDS.convert(1L, TimeUnit.SECONDS);
   public static final int EXCEPTION_COUNTER_TRIGGER = 20;
   private static final int MS_TO_IGNORE_CHUNK_AFTER_COMPLETION = 5000;
   private final IDhServerLevel dhServerLevel;
   @Nullable
   private final ChunkUpdateQueueManager updateManager;
   public final InternalServerGenerator_fabric internalServerGenerator;
   public final ChunkFileReader_fabric chunkFileReader;
   private final Timer chunkSaveIgnoreTimer = TimerUtil.CreateTimer("ChunkSaveIgnoreTimer");
   public final LinkedBlockingQueue<GenerationEvent_fabric> generationEventQueue = new LinkedBlockingQueue<>();
   public final GlobalWorldGenParams_fabric globalParams;
   public final StepStructureStart_fabric stepStructureStart = new StepStructureStart_fabric(this);
   public final StepStructureReference_fabric stepStructureReference = new StepStructureReference_fabric(this);
   public final StepBiomes_fabric stepBiomes = new StepBiomes_fabric(this);
   public final StepNoise_fabric stepNoise = new StepNoise_fabric(this);
   public final StepSurface_fabric stepSurface = new StepSurface_fabric(this);
   public final StepFeatures_fabric stepFeatures = new StepFeatures_fabric(this);
   public boolean unsafeThreadingRecorded = false;
   public boolean generatedChunkWithoutBiomeWarningLogged = false;
   public int unknownExceptionCount = 0;
   public long lastExceptionTriggerTime = 0L;
   public static ThreadLocal<Boolean> isDhWorldGenThreadRef = new ThreadLocal<>();

   public static boolean isThisDhWorldGenThread() {
      return isDhWorldGenThreadRef.get() != null;
   }

   public BatchGenerationEnvironment_fabric(IDhServerLevel dhServerLevel) {
      this.dhServerLevel = dhServerLevel;
      this.updateManager = WorldChunkUpdateManager.INSTANCE.getByLevelWrapper(this.dhServerLevel.getServerLevelWrapper());
      this.globalParams = new GlobalWorldGenParams_fabric(dhServerLevel);
      this.internalServerGenerator = new InternalServerGenerator_fabric(this.globalParams, this.dhServerLevel);
      this.chunkFileReader = new ChunkFileReader_fabric(this.globalParams);
      class_2794 generator = ((ServerLevelWrapper_fabric)dhServerLevel.getServerLevelWrapper()).getLevel().method_14178().method_12129();
      boolean isMcGenerator = generator instanceof class_3754 || generator instanceof class_2891 || generator instanceof class_2897;
      if (!isMcGenerator) {
         if (generator.getClass().toString().equals("class com.terraforged.mod.chunk.TFChunkGenerator")) {
            LOGGER.info("TerraForge Chunk Generator detected: [" + generator.getClass() + "], Distant Generation will try its best to support it.");
            LOGGER.info("If it does crash, turn Distant Generation off or set it to to [" + EDhApiDistantGeneratorMode.PRE_EXISTING_ONLY + "].");
         } else {
            LOGGER.warn("Unknown Chunk Generator detected: [" + generator.getClass() + "], Distant Generation May Fail!");
            LOGGER.warn("If it does crash, disable Distant Generation or set the Generation Mode to [" + EDhApiDistantGeneratorMode.PRE_EXISTING_ONLY + "].");
         }
      }
   }

   public <T> T confirmFutureWasRunSynchronously(CompletableFuture<T> future) {
      if (!this.unsafeThreadingRecorded && !future.isDone()) {
         LOGGER.warn(
            "Unsafe MultiThreading in Distant Horizons Chunk Generator. \nThis can happen if world generation is run on one of Minecraft's thread pools instead of the thread DH provided. \nThis can likely be ignored, however if world generator crashes occur setting DH's world generation thread count to 1 may improve stability. ",
            new RuntimeException("Incorrect thread pool use")
         );
         this.unsafeThreadingRecorded = true;
      }

      return future.join();
   }

   @Override
   public void updateAllFutures() {
      if (this.unknownExceptionCount > 0 && System.nanoTime() - this.lastExceptionTriggerTime >= EXCEPTION_TIMER_RESET_TIME) {
         this.unknownExceptionCount = 0;
      }

      Iterator<GenerationEvent_fabric> iter = this.generationEventQueue.iterator();

      while (iter.hasNext()) {
         GenerationEvent_fabric event = iter.next();
         if (event.future.isDone()) {
            if (event.future.isCompletedExceptionally() && !event.future.isCancelled()) {
               try {
                  event.future.get();
                  LodUtil.assertNotReach("Exceptionally completed world gen Future should have thrown an exception.");
               } catch (Exception var4) {
                  this.unknownExceptionCount++;
                  this.lastExceptionTriggerTime = System.nanoTime();
                  LOGGER.error("Batching World Generator event [" + event + "] threw an exception: " + var4.getMessage(), var4);
               }
            }

            iter.remove();
         }
      }

      if (this.unknownExceptionCount > 20) {
         LOGGER.error("Too many exceptions in Batching World Generator! Disabling the generator.");
         this.unknownExceptionCount = 0;
         Config.Common.WorldGenerator.enableDistantGeneration.set(false);
      }
   }

   public void generateEvent(GenerationEvent_fabric genEvent) throws RejectedExecutionException {
      LodUtil.assertTrue(genEvent.widthInChunks % 2 == 0, "Generation events are expected to be an evan number of chunks wide.");
      if (!DhApi.isDhThread() && ModInfo.IS_DEV_BUILD) {
         throw new IllegalStateException(
            "Batch world generation should be called from one of DH's world gen thread. Current thread: [" + Thread.currentThread().getName() + "]"
         );
      } else {
         int borderSize = MAX_WORLD_GEN_CHUNK_BORDER_NEEDED;
         int refSize = genEvent.widthInChunks - 1 + borderSize * 2;
         int refPosX = genEvent.minPos.getX() - borderSize;
         int refPosZ = genEvent.minPos.getZ() - borderSize;
         LightGetterAdaptor_fabric lightGetterAdaptor = new LightGetterAdaptor_fabric(this.globalParams.mcServerLevel);
         DummyLightEngine_fabric dummyLightEngine = new DummyLightEngine_fabric(lightGetterAdaptor);
         Map<DhChunkPos, ChunkLightStorage> chunkSkyLightingByDhPos = Collections.synchronizedMap(new HashMap<>());
         Map<DhChunkPos, ChunkLightStorage> chunkBlockLightingByDhPos = Collections.synchronizedMap(new HashMap<>());
         Map<DhChunkPos, ChunkWrapper_fabric> chunkWrappersByDhPos = Collections.synchronizedMap(new HashMap<>());
         HashMap<DhChunkPos, CompletableFuture<ChunkWrapper_fabric>> readFutureByDhChunkPos = new HashMap<>();
         Iterator<class_1923> existingChunkPosIterator = ChunkPosGenStream_fabric.getIterator(
            genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0
         );

         while (existingChunkPosIterator.hasNext()) {
            class_1923 chunkPos = existingChunkPosIterator.next();
            DhChunkPos dhChunkPos = McObjectConverter_fabric.convert(chunkPos);
            CompletableFuture<ChunkWrapper_fabric> getExistingChunkFuture = this.chunkFileReader
               .createEmptyOrPreExistingChunkWrapperAsync(
                  dhChunkPos.getX(), dhChunkPos.getZ(), chunkSkyLightingByDhPos, chunkBlockLightingByDhPos, chunkWrappersByDhPos
               );
            readFutureByDhChunkPos.put(dhChunkPos, getExistingChunkFuture);
         }

         for (CompletableFuture<ChunkWrapper_fabric> readChunkFuture : readFutureByDhChunkPos.values()) {
            readChunkFuture.join();
         }

         Iterator<class_1923> emptyChunkPosIterator = ChunkPosGenStream_fabric.getIterator(
            genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 8
         );

         while (emptyChunkPosIterator.hasNext()) {
            class_1923 chunkPos = emptyChunkPosIterator.next();
            DhChunkPos dhChunkPos = McObjectConverter_fabric.convert(chunkPos);
            if (!readFutureByDhChunkPos.containsKey(dhChunkPos)) {
               ChunkWrapper_fabric chunkWrapper = this.chunkFileReader.CreateProtoChunkWrapper(this.globalParams.mcServerLevel, chunkPos);
               chunkWrappersByDhPos.put(dhChunkPos, chunkWrapper);
            }
         }

         try {
            for (int xOffset = 0; xOffset < 2; xOffset++) {
               int xOffsetFinal = xOffset;

               for (int zOffset = 0; zOffset < 2; zOffset++) {
                  int zOffsetFinal = zOffset;
                  int radius = refSize / 2;
                  int centerX = refPosX + radius + xOffset;
                  int centerZ = refPosZ + radius + zOffset;
                  BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_fabric fallbackChunkGetterFunc = (chunkPosX, chunkPosZ) -> Objects.requireNonNull(
                     chunkWrappersByDhPos.get(new DhChunkPos(chunkPosX, chunkPosZ)).getChunk(),
                     () -> String.format("Requested chunk [%d, %d] unavailable during world generation", chunkPosX, chunkPosZ)
                  );
                  ArrayGridList<class_2791> regionChunks = new ArrayGridList<>(
                     refSize, (relX, relZ) -> fallbackChunkGetterFunc.getChunk(relX + refPosX + xOffsetFinal, relZ + refPosZ + zOffsetFinal)
                  );
                  class_2791 centerChunk = regionChunks.stream()
                     .filter(chunk -> chunk.method_12004().field_9181 == centerX && chunk.method_12004().field_9180 == centerZ)
                     .findFirst()
                     .orElseGet(() -> regionChunks.getFirst());
                  DhLitWorldGenRegion_fabric region = new DhLitWorldGenRegion_fabric(
                     centerX,
                     centerZ,
                     centerChunk,
                     this.globalParams.mcServerLevel,
                     dummyLightEngine,
                     regionChunks,
                     class_2806.field_16423,
                     radius,
                     fallbackChunkGetterFunc
                  );
                  lightGetterAdaptor.setRegion(region);
                  genEvent.threadedParam.makeStructFeatManager(region, this.globalParams);
                  ArrayGridList<ChunkWrapper_fabric> chunkWrapperList = new ArrayGridList<>(regionChunks.gridSize);
                  regionChunks.forEachPos((relX, relZ) -> {
                     DhChunkPos chunkPos = new DhChunkPos(relX + refPosX + xOffsetFinal, relZ + refPosZ + zOffsetFinal);
                     class_2791 chunk = regionChunks.get(relX, relZ);
                     if (chunkWrappersByDhPos.containsKey(chunkPos)) {
                        chunkWrapperList.set(relX, relZ, chunkWrappersByDhPos.get(chunkPos));
                     } else if (chunk != null) {
                        ChunkWrapper_fabric chunkWrapper = new ChunkWrapper_fabric(chunk, this.dhServerLevel.getLevelWrapper());
                        chunkWrapper.createDhHeightMaps();
                        chunkWrapperList.set(relX, relZ, chunkWrapper);
                        if (chunkBlockLightingByDhPos.containsKey(chunkWrapper.getChunkPos())) {
                           ChunkLightStorage blockLightStorage = chunkBlockLightingByDhPos.get(chunkWrapper.getChunkPos());
                           if (blockLightStorage != null && !blockLightStorage.isEmpty()) {
                              chunkWrapper.setBlockLightStorage(blockLightStorage);
                              chunkWrapper.setIsDhBlockLightCorrect(true);
                           }

                           ChunkLightStorage skyLightStorage = chunkSkyLightingByDhPos.get(chunkWrapper.getChunkPos());
                           if (skyLightStorage != null && !skyLightStorage.isEmpty()) {
                              chunkWrapper.setSkyLightStorage(skyLightStorage);
                              chunkWrapper.setIsDhSkyLightCorrect(true);
                           }
                        }

                        chunkWrappersByDhPos.put(chunkPos, chunkWrapper);
                     } else {
                        LodUtil.assertNotReach("Programmer Error: No chunk found in grid list, position offset is likely wrong.");
                     }
                  });

                  try {
                     this.generateDirect(genEvent, chunkWrapperList, region);
                  } catch (InterruptedException var27) {
                     throw new CompletionException(var27);
                  }
               }
            }

            Iterator<class_1923> iterator = ChunkPosGenStream_fabric.getIterator(genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0);

            while (iterator.hasNext()) {
               class_1923 chunkPos = iterator.next();
               DhChunkPos dhChunkPos = McObjectConverter_fabric.convert(chunkPos);
               ChunkWrapper_fabric wrappedChunk = chunkWrappersByDhPos.get(dhChunkPos);
               if (wrappedChunk.getStatus().method_12165(class_2806.field_12794)) {
                  genEvent.resultConsumer.accept(wrappedChunk);
               } else if (!this.generatedChunkWithoutBiomeWarningLogged) {
                  this.generatedChunkWithoutBiomeWarningLogged = true;
                  LOGGER.warn("Chunk [" + dhChunkPos + "] wasn't generated up to BIOMES, world gen may appear empty.");
               }
            }
         } catch (UncheckedInterruptedException | CompletionException var28) {
            boolean isShutdownException = ExceptionUtil.isShutdownException(var28);
            if (!isShutdownException) {
               LOGGER.error("Completion error during world gen for min chunk pos [" + genEvent.minPos + "], error: [" + var28.getMessage() + "].", var28);
            }
         } catch (Exception var29) {
            LOGGER.error("Unexpected error during world gen for min chunk pos [" + genEvent.minPos + "], error: [" + var29.getMessage() + "].", var29);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void generateDirect(GenerationEvent_fabric genEvent, ArrayGridList<ChunkWrapper_fabric> chunkWrappersToGenerate, DhLitWorldGenRegion_fabric region) throws InterruptedException {
      if (!Thread.interrupted()) {
         boolean var17 = false /* VF: Semaphore variable */;

         label859: {
            label860: {
               label861: {
                  label862: {
                     label863: {
                        label864: {
                           label865: {
                              try {
                                 var17 = true;
                                 chunkWrappersToGenerate.forEach(chunkWrapperx -> {
                                    if (chunkWrapperx.getChunk() instanceof class_2839 protoChunk) {
                                       protoChunk.method_17032(region.method_22336());
                                    }

                                    if (this.updateManager != null) {
                                       this.updateManager.addPosToIgnore(chunkWrapperx.getChunkPos());
                                    }
                                 });
                                 if (genEvent.generatorMode == EDhApiDistantGeneratorMode.PRE_EXISTING_ONLY) {
                                    this.stepBiomes
                                       .generateGroup(genEvent.threadedParam, region, GetCutoutFrom(chunkWrappersToGenerate, EDhApiWorldGenerationStep.BIOMES));
                                    var17 = false;
                                    break label859;
                                 }

                                 EDhApiWorldGenerationStep step = genEvent.targetGenerationStep;
                                 if (step == EDhApiWorldGenerationStep.EMPTY) {
                                    var17 = false;
                                    break label860;
                                 }

                                 throwIfThreadInterrupted();
                                 this.stepStructureStart
                                    .generateGroup(
                                       genEvent.threadedParam, region, GetCutoutFrom(chunkWrappersToGenerate, EDhApiWorldGenerationStep.STRUCTURE_START)
                                    );
                                 if (step == EDhApiWorldGenerationStep.STRUCTURE_START) {
                                    var17 = false;
                                    break label861;
                                 }

                                 throwIfThreadInterrupted();
                                 this.stepStructureReference
                                    .generateGroup(
                                       genEvent.threadedParam, region, GetCutoutFrom(chunkWrappersToGenerate, EDhApiWorldGenerationStep.STRUCTURE_REFERENCE)
                                    );
                                 if (step == EDhApiWorldGenerationStep.STRUCTURE_REFERENCE) {
                                    var17 = false;
                                    break label862;
                                 }

                                 throwIfThreadInterrupted();
                                 this.stepBiomes
                                    .generateGroup(genEvent.threadedParam, region, GetCutoutFrom(chunkWrappersToGenerate, EDhApiWorldGenerationStep.BIOMES));
                                 if (step == EDhApiWorldGenerationStep.BIOMES) {
                                    var17 = false;
                                    break label863;
                                 }

                                 throwIfThreadInterrupted();
                                 this.stepNoise
                                    .generateGroup(genEvent.threadedParam, region, GetCutoutFrom(chunkWrappersToGenerate, EDhApiWorldGenerationStep.NOISE));
                                 if (step == EDhApiWorldGenerationStep.NOISE) {
                                    var17 = false;
                                    break label864;
                                 }

                                 throwIfThreadInterrupted();
                                 this.stepSurface
                                    .generateGroup(genEvent.threadedParam, region, GetCutoutFrom(chunkWrappersToGenerate, EDhApiWorldGenerationStep.SURFACE));
                                 if (step == EDhApiWorldGenerationStep.SURFACE) {
                                    var17 = false;
                                    break label865;
                                 }

                                 throwIfThreadInterrupted();

                                 for (ChunkWrapper_fabric chunkWrapper : chunkWrappersToGenerate) {
                                    class_2791 chunk = chunkWrapper.getChunk();
                                    class_2902.method_16684(chunk, class_2806.field_12801.method_12160());
                                 }

                                 throwIfThreadInterrupted();
                                 this.stepFeatures
                                    .generateGroup(genEvent.threadedParam, region, GetCutoutFrom(chunkWrappersToGenerate, EDhApiWorldGenerationStep.FEATURES));
                                 var17 = false;
                              } finally {
                                 if (var17) {
                                    int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
                                    ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

                                    for (int i = 0; i < chunkWrappersToGenerate.size(); i++) {
                                       ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(i);
                                       if (chunkWrapper.getStatus() != class_2806.field_12798) {
                                          iChunkWrapperList.add(chunkWrapper);
                                       }
                                    }

                                    for (int ix = 0; ix < iChunkWrapperList.size(); ix++) {
                                       ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ix);
                                       if (centerChunkWrapper != null) {
                                          throwIfThreadInterrupted();
                                          centerChunkWrapper.createDhHeightMaps();
                                          if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                                             DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
                                          }

                                          List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
                                          if (!activeBeamList.isEmpty()) {
                                             this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
                                          }
                                       }
                                    }

                                    int ixx = 0;

                                    while (true) {
                                       if (ixx >= iChunkWrapperList.size()) {
                                          ;
                                       } else {
                                          ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixx);
                                          if (chunkWrapper != null) {
                                             this.chunkSaveIgnoreTimer
                                                .schedule(
                                                   new TimerTask(chunkWrapper) {
                                                      {
                                                         this.val$chunkWrapper = chunkWrapper_fabric;
                                                      }

                                                      @Override
                                                      public void run() {
                                                         if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                                                            BatchGenerationEnvironment_fabric.this.updateManager
                                                               .removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                                                         }
                                                      }
                                                   },
                                                   5000L
                                                );
                                          }

                                          ixx++;
                                       }
                                    }
                                 }
                              }

                              int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
                              ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

                              for (int ixx = 0; ixx < chunkWrappersToGenerate.size(); ixx++) {
                                 ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(ixx);
                                 if (chunkWrapper.getStatus() != class_2806.field_12798) {
                                    iChunkWrapperList.add(chunkWrapper);
                                 }
                              }

                              for (int ixxx = 0; ixxx < iChunkWrapperList.size(); ixxx++) {
                                 ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxx);
                                 if (centerChunkWrapper != null) {
                                    throwIfThreadInterrupted();
                                    centerChunkWrapper.createDhHeightMaps();
                                    if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                                       DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
                                    }

                                    List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
                                    if (!activeBeamList.isEmpty()) {
                                       this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
                                    }
                                 }
                              }

                              for (int ixxxx = 0; ixxxx < iChunkWrapperList.size(); ixxxx++) {
                                 ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxx);
                                 if (chunkWrapper != null) {
                                    this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                                       {
                                          this.val$chunkWrapper = chunkWrapper_fabric;
                                       }

                                       @Override
                                       public void run() {
                                          if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                                             BatchGenerationEnvironment_fabric.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                                          }
                                       }
                                    }, 5000L);
                                 }
                              }

                              return;
                           }

                           int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
                           ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

                           for (int ixxxxx = 0; ixxxxx < chunkWrappersToGenerate.size(); ixxxxx++) {
                              ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(ixxxxx);
                              if (chunkWrapper.getStatus() != class_2806.field_12798) {
                                 iChunkWrapperList.add(chunkWrapper);
                              }
                           }

                           for (int ixxxxxx = 0; ixxxxxx < iChunkWrapperList.size(); ixxxxxx++) {
                              ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxx);
                              if (centerChunkWrapper != null) {
                                 throwIfThreadInterrupted();
                                 centerChunkWrapper.createDhHeightMaps();
                                 if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                                    DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
                                 }

                                 List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
                                 if (!activeBeamList.isEmpty()) {
                                    this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
                                 }
                              }
                           }

                           for (int ixxxxxxx = 0; ixxxxxxx < iChunkWrapperList.size(); ixxxxxxx++) {
                              ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxx);
                              if (chunkWrapper != null) {
                                 this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                                    {
                                       this.val$chunkWrapper = chunkWrapper_fabric;
                                    }

                                    @Override
                                    public void run() {
                                       if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                                          BatchGenerationEnvironment_fabric.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                                       }
                                    }
                                 }, 5000L);
                              }
                           }

                           return;
                        }

                        int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
                        ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

                        for (int ixxxxxxxx = 0; ixxxxxxxx < chunkWrappersToGenerate.size(); ixxxxxxxx++) {
                           ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxx);
                           if (chunkWrapper.getStatus() != class_2806.field_12798) {
                              iChunkWrapperList.add(chunkWrapper);
                           }
                        }

                        for (int ixxxxxxxxx = 0; ixxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxx++) {
                           ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxx);
                           if (centerChunkWrapper != null) {
                              throwIfThreadInterrupted();
                              centerChunkWrapper.createDhHeightMaps();
                              if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                                 DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
                              }

                              List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
                              if (!activeBeamList.isEmpty()) {
                                 this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
                              }
                           }
                        }

                        for (int ixxxxxxxxxx = 0; ixxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxx++) {
                           ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxx);
                           if (chunkWrapper != null) {
                              this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                                 {
                                    this.val$chunkWrapper = chunkWrapper_fabric;
                                 }

                                 @Override
                                 public void run() {
                                    if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                                       BatchGenerationEnvironment_fabric.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                                    }
                                 }
                              }, 5000L);
                           }
                        }

                        return;
                     }

                     int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
                     ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

                     for (int ixxxxxxxxxxx = 0; ixxxxxxxxxxx < chunkWrappersToGenerate.size(); ixxxxxxxxxxx++) {
                        ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxx);
                        if (chunkWrapper.getStatus() != class_2806.field_12798) {
                           iChunkWrapperList.add(chunkWrapper);
                        }
                     }

                     for (int ixxxxxxxxxxxx = 0; ixxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxx++) {
                        ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxx);
                        if (centerChunkWrapper != null) {
                           throwIfThreadInterrupted();
                           centerChunkWrapper.createDhHeightMaps();
                           if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                              DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
                           }

                           List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
                           if (!activeBeamList.isEmpty()) {
                              this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
                           }
                        }
                     }

                     for (int ixxxxxxxxxxxxx = 0; ixxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxx++) {
                        ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxx);
                        if (chunkWrapper != null) {
                           this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                              {
                                 this.val$chunkWrapper = chunkWrapper_fabric;
                              }

                              @Override
                              public void run() {
                                 if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                                    BatchGenerationEnvironment_fabric.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                                 }
                              }
                           }, 5000L);
                        }
                     }

                     return;
                  }

                  int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
                  ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

                  for (int ixxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxx < chunkWrappersToGenerate.size(); ixxxxxxxxxxxxxx++) {
                     ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxxxxx);
                     if (chunkWrapper.getStatus() != class_2806.field_12798) {
                        iChunkWrapperList.add(chunkWrapper);
                     }
                  }

                  for (int ixxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxx++) {
                     ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxxxx);
                     if (centerChunkWrapper != null) {
                        throwIfThreadInterrupted();
                        centerChunkWrapper.createDhHeightMaps();
                        if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                           DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
                        }

                        List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
                        if (!activeBeamList.isEmpty()) {
                           this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
                        }
                     }
                  }

                  for (int ixxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxx++) {
                     ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxxxxx);
                     if (chunkWrapper != null) {
                        this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                           {
                              this.val$chunkWrapper = chunkWrapper_fabric;
                           }

                           @Override
                           public void run() {
                              if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                                 BatchGenerationEnvironment_fabric.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                              }
                           }
                        }, 5000L);
                     }
                  }

                  return;
               }

               int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
               ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

               for (int ixxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxx < chunkWrappersToGenerate.size(); ixxxxxxxxxxxxxxxxx++) {
                  ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxxxxxxxx);
                  if (chunkWrapper.getStatus() != class_2806.field_12798) {
                     iChunkWrapperList.add(chunkWrapper);
                  }
               }

               for (int ixxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxx++) {
                  ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxx);
                  if (centerChunkWrapper != null) {
                     throwIfThreadInterrupted();
                     centerChunkWrapper.createDhHeightMaps();
                     if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                        DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
                     }

                     List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
                     if (!activeBeamList.isEmpty()) {
                        this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
                     }
                  }
               }

               for (int ixxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxxx++) {
                  ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxx);
                  if (chunkWrapper != null) {
                     this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                        {
                           this.val$chunkWrapper = chunkWrapper_fabric;
                        }

                        @Override
                        public void run() {
                           if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                              BatchGenerationEnvironment_fabric.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                           }
                        }
                     }, 5000L);
                  }
               }

               return;
            }

            int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
            ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

            for (int ixxxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxxx < chunkWrappersToGenerate.size(); ixxxxxxxxxxxxxxxxxxxx++) {
               ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxxxxxxxxxxx);
               if (chunkWrapper.getStatus() != class_2806.field_12798) {
                  iChunkWrapperList.add(chunkWrapper);
               }
            }

            for (int ixxxxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxxxxx++) {
               ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxxxx);
               if (centerChunkWrapper != null) {
                  throwIfThreadInterrupted();
                  centerChunkWrapper.createDhHeightMaps();
                  if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                     DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
                  }

                  List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
                  if (!activeBeamList.isEmpty()) {
                     this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
                  }
               }
            }

            for (int ixxxxxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxxxxxx++) {
               ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxxxxx);
               if (chunkWrapper != null) {
                  this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                     {
                        this.val$chunkWrapper = chunkWrapper_fabric;
                     }

                     @Override
                     public void run() {
                        if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                           BatchGenerationEnvironment_fabric.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                        }
                     }
                  }, 5000L);
               }
            }

            return;
         }

         int maxSkyLight = this.dhServerLevel.getServerLevelWrapper().hasSkyLight() ? 15 : 0;
         ArrayList<IChunkWrapper> iChunkWrapperList = new ArrayList<>();

         for (int ixxxxxxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxxxxxx < chunkWrappersToGenerate.size(); ixxxxxxxxxxxxxxxxxxxxxxx++) {
            ChunkWrapper_fabric chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxxxxxxxxxxxxxx);
            if (chunkWrapper.getStatus() != class_2806.field_12798) {
               iChunkWrapperList.add(chunkWrapper);
            }
         }

         for (int ixxxxxxxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxxxxxxxx++) {
            ChunkWrapper_fabric centerChunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxxxxxxx);
            if (centerChunkWrapper != null) {
               throwIfThreadInterrupted();
               centerChunkWrapper.createDhHeightMaps();
               if (!centerChunkWrapper.isDhBlockLightingCorrect()) {
                  DhLightingEngine.INSTANCE.bakeChunkBlockLighting(centerChunkWrapper, iChunkWrapperList, maxSkyLight);
               }

               List<BeaconBeamDTO> activeBeamList = centerChunkWrapper.getAllActiveBeacons(iChunkWrapperList);
               if (!activeBeamList.isEmpty()) {
                  this.dhServerLevel.updateBeaconBeamsForChunkPos(centerChunkWrapper.getChunkPos(), activeBeamList);
               }
            }
         }

         for (int ixxxxxxxxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxxxxxxxxx++) {
            ChunkWrapper_fabric chunkWrapper = (ChunkWrapper_fabric)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxxxxxxxx);
            if (chunkWrapper != null) {
               this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                  {
                     this.val$chunkWrapper = chunkWrapper_fabric;
                  }

                  @Override
                  public void run() {
                     if (BatchGenerationEnvironment_fabric.this.updateManager != null) {
                        BatchGenerationEnvironment_fabric.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
                     }
                  }
               }, 5000L);
            }
         }
      }
   }

   private static <T> ArrayGridList<T> GetCutoutFrom(ArrayGridList<T> total, int border) {
      return new ArrayGridList<>(total, border, total.gridSize - border);
   }

   private static <T> ArrayGridList<T> GetCutoutFrom(ArrayGridList<T> total, EDhApiWorldGenerationStep step) {
      return GetCutoutFrom(total, (Integer)WORLD_GEN_CHUNK_BORDER_NEEDED_BY_GEN_STEP.get(step));
   }

   @Override
   public CompletableFuture<Void> queueGenEvent(
      int minX,
      int minZ,
      int chunkWidthCount,
      EDhApiDistantGeneratorMode generatorMode,
      EDhApiWorldGenerationStep targetStep,
      ExecutorService worldGeneratorThreadPool,
      Consumer<IChunkWrapper> resultConsumer
   ) {
      GenerationEvent_fabric genEvent = GenerationEvent_fabric.start(
         new DhChunkPos(minX, minZ), chunkWidthCount, this, generatorMode, targetStep, resultConsumer, worldGeneratorThreadPool
      );
      this.generationEventQueue.add(genEvent);
      return genEvent.future;
   }

   @Override
   public void close() {
      LOGGER.info("Closing [" + BatchGenerationEnvironment_fabric.class.getSimpleName() + "]");
      Iterator<GenerationEvent_fabric> genEventIter = this.generationEventQueue.iterator();

      while (genEventIter.hasNext()) {
         GenerationEvent_fabric event = genEventIter.next();
         event.future.cancel(true);
         genEventIter.remove();
      }

      this.chunkFileReader.close();
   }

   public static void throwIfThreadInterrupted() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException("[" + BatchGenerationEnvironment_fabric.class.getSimpleName() + "] task interrupted.");
      }
   }

   static {
      Builder<EDhApiWorldGenerationStep, Integer> builder = ImmutableMap.builder();
      builder.put(EDhApiWorldGenerationStep.EMPTY, 1);
      builder.put(EDhApiWorldGenerationStep.STRUCTURE_START, 0);
      builder.put(EDhApiWorldGenerationStep.STRUCTURE_REFERENCE, 0);
      builder.put(EDhApiWorldGenerationStep.BIOMES, 0);
      builder.put(EDhApiWorldGenerationStep.NOISE, 0);
      builder.put(EDhApiWorldGenerationStep.SURFACE, 0);
      builder.put(EDhApiWorldGenerationStep.CARVERS, 0);
      builder.put(EDhApiWorldGenerationStep.LIQUID_CARVERS, 0);
      builder.put(EDhApiWorldGenerationStep.FEATURES, 0);
      builder.put(EDhApiWorldGenerationStep.LIGHT, 0);
      WORLD_GEN_CHUNK_BORDER_NEEDED_BY_GEN_STEP = builder.build();
   }
}
