package com.seibel.distanthorizons.core.generation;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.api.interfaces.override.worldGenerator.IDhApiWorldGenerator;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.worldGeneration.IBatchGeneratorEnvironmentWrapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BatchGenerator implements IDhApiWorldGenerator {
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public IBatchGeneratorEnvironmentWrapper generationEnvironment;
   public IDhLevel targetDhLevel;

   public BatchGenerator(IDhLevel targetDhLevel) {
      this.targetDhLevel = targetDhLevel;
      this.generationEnvironment = WRAPPER_FACTORY.createBatchGenerator(targetDhLevel);
      LOGGER.info("Batch Chunk Generator initialized");
   }

   @Override
   public int getPriority() {
      return -1;
   }

   @Override
   public byte getSmallestDataDetailLevel() {
      return 0;
   }

   @Override
   public byte getLargestDataDetailLevel() {
      return 0;
   }

   @Override
   public CompletableFuture<Void> generateChunks(
      int chunkPosMinX,
      int chunkPosMinZ,
      int chunkWidthCount,
      byte targetDataDetail,
      EDhApiDistantGeneratorMode generatorMode,
      ExecutorService worldGeneratorThreadPool,
      Consumer<Object[]> resultConsumer
   ) {
      EDhApiWorldGenerationStep targetStep;
      switch (generatorMode) {
         case PRE_EXISTING_ONLY:
            targetStep = EDhApiWorldGenerationStep.EMPTY;
            break;
         case SURFACE:
            targetStep = EDhApiWorldGenerationStep.SURFACE;
            break;
         case FEATURES:
            targetStep = EDhApiWorldGenerationStep.FEATURES;
            break;
         case INTERNAL_SERVER:
            targetStep = EDhApiWorldGenerationStep.LIGHT;
            break;
         default:
            throw new IllegalArgumentException("no target step defined for generator mode: [" + generatorMode + "].");
      }

      Consumer<IChunkWrapper> consumerWrapper = chunkWrapper -> resultConsumer.accept(new Object[]{chunkWrapper});

      try {
         return this.generationEnvironment
            .queueGenEvent(chunkPosMinX, chunkPosMinZ, chunkWidthCount, generatorMode, targetStep, worldGeneratorThreadPool, consumerWrapper);
      } catch (Exception var12) {
         if (!ExceptionUtil.isInterruptOrReject(var12)) {
            LOGGER.error("Error starting future for chunk generation, error: [" + var12.getMessage() + "].", var12);
         }

         CompletableFuture<Void> future = new CompletableFuture<>();
         future.completeExceptionally(var12);
         return future;
      }
   }

   @Override
   public void preGeneratorTaskStart() {
      this.generationEnvironment.updateAllFutures();
   }

   @Override
   public void close() {
      LOGGER.info("[" + BatchGenerator.class.getSimpleName() + "] shutting down...");
      this.generationEnvironment.close();
   }
}
