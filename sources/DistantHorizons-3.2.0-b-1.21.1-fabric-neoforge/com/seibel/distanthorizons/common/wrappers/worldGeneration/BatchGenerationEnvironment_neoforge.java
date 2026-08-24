package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling.ChunkFileReader_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DummyLightEngine_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.LightGetterAdaptor_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.GlobalWorldGenParams_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepBiomes_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepFeatures_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepNoise_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepStructureReference_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepStructureStart_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.step.StepSurface_neoforge;
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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BatchGenerationEnvironment_neoforge implements IBatchGeneratorEnvironmentWrapper {
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
   public final InternalServerGenerator_neoforge internalServerGenerator;
   public final ChunkFileReader_neoforge chunkFileReader;
   private final Timer chunkSaveIgnoreTimer = TimerUtil.CreateTimer("ChunkSaveIgnoreTimer");
   public final LinkedBlockingQueue<GenerationEvent_neoforge> generationEventQueue = new LinkedBlockingQueue<>();
   public final GlobalWorldGenParams_neoforge globalParams;
   public final StepStructureStart_neoforge stepStructureStart = new StepStructureStart_neoforge(this);
   public final StepStructureReference_neoforge stepStructureReference = new StepStructureReference_neoforge(this);
   public final StepBiomes_neoforge stepBiomes = new StepBiomes_neoforge(this);
   public final StepNoise_neoforge stepNoise = new StepNoise_neoforge(this);
   public final StepSurface_neoforge stepSurface = new StepSurface_neoforge(this);
   public final StepFeatures_neoforge stepFeatures = new StepFeatures_neoforge(this);
   public boolean unsafeThreadingRecorded = false;
   public boolean generatedChunkWithoutBiomeWarningLogged = false;
   public int unknownExceptionCount = 0;
   public long lastExceptionTriggerTime = 0L;
   public static ThreadLocal<Boolean> isDhWorldGenThreadRef = new ThreadLocal<>();

   public static boolean isThisDhWorldGenThread() {
      return isDhWorldGenThreadRef.get() != null;
   }

   public BatchGenerationEnvironment_neoforge(IDhServerLevel dhServerLevel) {
      this.dhServerLevel = dhServerLevel;
      this.updateManager = WorldChunkUpdateManager.INSTANCE.getByLevelWrapper(this.dhServerLevel.getServerLevelWrapper());
      this.globalParams = new GlobalWorldGenParams_neoforge(dhServerLevel);
      this.internalServerGenerator = new InternalServerGenerator_neoforge(this.globalParams, this.dhServerLevel);
      this.chunkFileReader = new ChunkFileReader_neoforge(this.globalParams);
      ChunkGenerator generator = ((ServerLevelWrapper_neoforge)dhServerLevel.getServerLevelWrapper()).getLevel().getChunkSource().getGenerator();
      boolean isMcGenerator = generator instanceof NoiseBasedChunkGenerator || generator instanceof DebugLevelSource || generator instanceof FlatLevelSource;
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

      Iterator<GenerationEvent_neoforge> iter = this.generationEventQueue.iterator();

      while (iter.hasNext()) {
         GenerationEvent_neoforge event = iter.next();
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

   public void generateEvent(GenerationEvent_neoforge genEvent) throws RejectedExecutionException {
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
         LightGetterAdaptor_neoforge lightGetterAdaptor = new LightGetterAdaptor_neoforge(this.globalParams.mcServerLevel);
         DummyLightEngine_neoforge dummyLightEngine = new DummyLightEngine_neoforge(lightGetterAdaptor);
         Map<DhChunkPos, ChunkLightStorage> chunkSkyLightingByDhPos = Collections.synchronizedMap(new HashMap<>());
         Map<DhChunkPos, ChunkLightStorage> chunkBlockLightingByDhPos = Collections.synchronizedMap(new HashMap<>());
         Map<DhChunkPos, ChunkWrapper_neoforge> chunkWrappersByDhPos = Collections.synchronizedMap(new HashMap<>());
         HashMap<DhChunkPos, CompletableFuture<ChunkWrapper_neoforge>> readFutureByDhChunkPos = new HashMap<>();
         Iterator<ChunkPos> existingChunkPosIterator = ChunkPosGenStream_neoforge.getIterator(
            genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0
         );

         while (existingChunkPosIterator.hasNext()) {
            ChunkPos chunkPos = existingChunkPosIterator.next();
            DhChunkPos dhChunkPos = McObjectConverter_neoforge.convert(chunkPos);
            CompletableFuture<ChunkWrapper_neoforge> getExistingChunkFuture = this.chunkFileReader
               .createEmptyOrPreExistingChunkWrapperAsync(
                  dhChunkPos.getX(), dhChunkPos.getZ(), chunkSkyLightingByDhPos, chunkBlockLightingByDhPos, chunkWrappersByDhPos
               );
            readFutureByDhChunkPos.put(dhChunkPos, getExistingChunkFuture);
         }

         for (CompletableFuture<ChunkWrapper_neoforge> readChunkFuture : readFutureByDhChunkPos.values()) {
            readChunkFuture.join();
         }

         Iterator<ChunkPos> emptyChunkPosIterator = ChunkPosGenStream_neoforge.getIterator(
            genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 8
         );

         while (emptyChunkPosIterator.hasNext()) {
            ChunkPos chunkPos = emptyChunkPosIterator.next();
            DhChunkPos dhChunkPos = McObjectConverter_neoforge.convert(chunkPos);
            if (!readFutureByDhChunkPos.containsKey(dhChunkPos)) {
               ChunkWrapper_neoforge chunkWrapper = this.chunkFileReader.CreateProtoChunkWrapper(this.globalParams.mcServerLevel, chunkPos);
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
                  BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_neoforge fallbackChunkGetterFunc = (chunkPosX, chunkPosZ) -> Objects.requireNonNull(
                     chunkWrappersByDhPos.get(new DhChunkPos(chunkPosX, chunkPosZ)).getChunk(),
                     () -> String.format("Requested chunk [%d, %d] unavailable during world generation", chunkPosX, chunkPosZ)
                  );
                  ArrayGridList<ChunkAccess> regionChunks = new ArrayGridList<>(
                     refSize, (relX, relZ) -> fallbackChunkGetterFunc.getChunk(relX + refPosX + xOffsetFinal, relZ + refPosZ + zOffsetFinal)
                  );
                  ChunkAccess centerChunk = regionChunks.stream()
                     .filter(chunk -> chunk.getPos().x == centerX && chunk.getPos().z == centerZ)
                     .findFirst()
                     .orElseGet(() -> regionChunks.getFirst());
                  DhLitWorldGenRegion_neoforge region = new DhLitWorldGenRegion_neoforge(
                     centerX,
                     centerZ,
                     centerChunk,
                     this.globalParams.mcServerLevel,
                     dummyLightEngine,
                     regionChunks,
                     ChunkStatus.STRUCTURE_STARTS,
                     radius,
                     fallbackChunkGetterFunc
                  );
                  lightGetterAdaptor.setRegion(region);
                  genEvent.threadedParam.makeStructFeatManager(region, this.globalParams);
                  ArrayGridList<ChunkWrapper_neoforge> chunkWrapperList = new ArrayGridList<>(regionChunks.gridSize);
                  regionChunks.forEachPos((relX, relZ) -> {
                     DhChunkPos chunkPos = new DhChunkPos(relX + refPosX + xOffsetFinal, relZ + refPosZ + zOffsetFinal);
                     ChunkAccess chunk = regionChunks.get(relX, relZ);
                     if (chunkWrappersByDhPos.containsKey(chunkPos)) {
                        chunkWrapperList.set(relX, relZ, chunkWrappersByDhPos.get(chunkPos));
                     } else if (chunk != null) {
                        ChunkWrapper_neoforge chunkWrapper = new ChunkWrapper_neoforge(chunk, this.dhServerLevel.getLevelWrapper());
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

            Iterator<ChunkPos> iterator = ChunkPosGenStream_neoforge.getIterator(genEvent.minPos.getX(), genEvent.minPos.getZ(), genEvent.widthInChunks, 0);

            while (iterator.hasNext()) {
               ChunkPos chunkPos = iterator.next();
               DhChunkPos dhChunkPos = McObjectConverter_neoforge.convert(chunkPos);
               ChunkWrapper_neoforge wrappedChunk = chunkWrappersByDhPos.get(dhChunkPos);
               if (wrappedChunk.getStatus().isOrAfter(ChunkStatus.BIOMES)) {
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
   public void generateDirect(
      GenerationEvent_neoforge genEvent, ArrayGridList<ChunkWrapper_neoforge> chunkWrappersToGenerate, DhLitWorldGenRegion_neoforge region
   ) throws InterruptedException {
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
                                    if (chunkWrapperx.getChunk() instanceof ProtoChunk protoChunk) {
                                       protoChunk.setLightEngine(region.getLightEngine());
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

                                 for (ChunkWrapper_neoforge chunkWrapper : chunkWrappersToGenerate) {
                                    ChunkAccess chunk = chunkWrapper.getChunk();
                                    Heightmap.primeHeightmaps(chunk, ChunkStatus.CARVERS.heightmapsAfter());
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
                                       ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(i);
                                       if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
                                          iChunkWrapperList.add(chunkWrapper);
                                       }
                                    }

                                    for (int ix = 0; ix < iChunkWrapperList.size(); ix++) {
                                       ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ix);
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
                                          ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixx);
                                          if (chunkWrapper != null) {
                                             this.chunkSaveIgnoreTimer
                                                .schedule(
                                                   new TimerTask(chunkWrapper) {
                                                      {
                                                         this.val$chunkWrapper = chunkWrapper_neoforge;
                                                      }

                                                      @Override
                                                      public void run() {
                                                         if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                                                            BatchGenerationEnvironment_neoforge.this.updateManager
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
                                 ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(ixx);
                                 if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
                                    iChunkWrapperList.add(chunkWrapper);
                                 }
                              }

                              for (int ixxx = 0; ixxx < iChunkWrapperList.size(); ixxx++) {
                                 ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxx);
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
                                 ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxx);
                                 if (chunkWrapper != null) {
                                    this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                                       {
                                          this.val$chunkWrapper = chunkWrapper_neoforge;
                                       }

                                       @Override
                                       public void run() {
                                          if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                                             BatchGenerationEnvironment_neoforge.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
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
                              ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(ixxxxx);
                              if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
                                 iChunkWrapperList.add(chunkWrapper);
                              }
                           }

                           for (int ixxxxxx = 0; ixxxxxx < iChunkWrapperList.size(); ixxxxxx++) {
                              ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxx);
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
                              ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxx);
                              if (chunkWrapper != null) {
                                 this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                                    {
                                       this.val$chunkWrapper = chunkWrapper_neoforge;
                                    }

                                    @Override
                                    public void run() {
                                       if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                                          BatchGenerationEnvironment_neoforge.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
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
                           ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxx);
                           if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
                              iChunkWrapperList.add(chunkWrapper);
                           }
                        }

                        for (int ixxxxxxxxx = 0; ixxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxx++) {
                           ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxx);
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
                           ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxx);
                           if (chunkWrapper != null) {
                              this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                                 {
                                    this.val$chunkWrapper = chunkWrapper_neoforge;
                                 }

                                 @Override
                                 public void run() {
                                    if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                                       BatchGenerationEnvironment_neoforge.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
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
                        ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxx);
                        if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
                           iChunkWrapperList.add(chunkWrapper);
                        }
                     }

                     for (int ixxxxxxxxxxxx = 0; ixxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxx++) {
                        ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxx);
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
                        ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxx);
                        if (chunkWrapper != null) {
                           this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                              {
                                 this.val$chunkWrapper = chunkWrapper_neoforge;
                              }

                              @Override
                              public void run() {
                                 if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                                    BatchGenerationEnvironment_neoforge.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
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
                     ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxxxxx);
                     if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
                        iChunkWrapperList.add(chunkWrapper);
                     }
                  }

                  for (int ixxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxx++) {
                     ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxxxx);
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
                     ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxxxxx);
                     if (chunkWrapper != null) {
                        this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                           {
                              this.val$chunkWrapper = chunkWrapper_neoforge;
                           }

                           @Override
                           public void run() {
                              if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                                 BatchGenerationEnvironment_neoforge.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
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
                  ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxxxxxxxx);
                  if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
                     iChunkWrapperList.add(chunkWrapper);
                  }
               }

               for (int ixxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxx++) {
                  ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxx);
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
                  ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxx);
                  if (chunkWrapper != null) {
                     this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                        {
                           this.val$chunkWrapper = chunkWrapper_neoforge;
                        }

                        @Override
                        public void run() {
                           if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                              BatchGenerationEnvironment_neoforge.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
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
               ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxxxxxxxxxxx);
               if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
                  iChunkWrapperList.add(chunkWrapper);
               }
            }

            for (int ixxxxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxxxxx++) {
               ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxxxx);
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
               ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxxxxx);
               if (chunkWrapper != null) {
                  this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                     {
                        this.val$chunkWrapper = chunkWrapper_neoforge;
                     }

                     @Override
                     public void run() {
                        if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                           BatchGenerationEnvironment_neoforge.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
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
            ChunkWrapper_neoforge chunkWrapper = chunkWrappersToGenerate.get(ixxxxxxxxxxxxxxxxxxxxxxx);
            if (chunkWrapper.getStatus() != ChunkStatus.EMPTY) {
               iChunkWrapperList.add(chunkWrapper);
            }
         }

         for (int ixxxxxxxxxxxxxxxxxxxxxxxx = 0; ixxxxxxxxxxxxxxxxxxxxxxxx < iChunkWrapperList.size(); ixxxxxxxxxxxxxxxxxxxxxxxx++) {
            ChunkWrapper_neoforge centerChunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxxxxxxx);
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
            ChunkWrapper_neoforge chunkWrapper = (ChunkWrapper_neoforge)iChunkWrapperList.get(ixxxxxxxxxxxxxxxxxxxxxxxxx);
            if (chunkWrapper != null) {
               this.chunkSaveIgnoreTimer.schedule(new TimerTask(chunkWrapper) {
                  {
                     this.val$chunkWrapper = chunkWrapper_neoforge;
                  }

                  @Override
                  public void run() {
                     if (BatchGenerationEnvironment_neoforge.this.updateManager != null) {
                        BatchGenerationEnvironment_neoforge.this.updateManager.removePosToIgnore(this.val$chunkWrapper.getChunkPos());
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
      GenerationEvent_neoforge genEvent = GenerationEvent_neoforge.start(
         new DhChunkPos(minX, minZ), chunkWidthCount, this, generatorMode, targetStep, resultConsumer, worldGeneratorThreadPool
      );
      this.generationEventQueue.add(genEvent);
      return genEvent.future;
   }

   @Override
   public void close() {
      LOGGER.info("Closing [" + BatchGenerationEnvironment_neoforge.class.getSimpleName() + "]");
      Iterator<GenerationEvent_neoforge> genEventIter = this.generationEventQueue.iterator();

      while (genEventIter.hasNext()) {
         GenerationEvent_neoforge event = genEventIter.next();
         event.future.cancel(true);
         genEventIter.remove();
      }

      this.chunkFileReader.close();
   }

   public static void throwIfThreadInterrupted() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException("[" + BatchGenerationEnvironment_neoforge.class.getSimpleName() + "] task interrupted.");
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
