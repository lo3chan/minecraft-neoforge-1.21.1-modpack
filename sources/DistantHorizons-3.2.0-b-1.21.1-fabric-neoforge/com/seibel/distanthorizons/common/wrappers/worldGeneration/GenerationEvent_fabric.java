package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_fabric;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class GenerationEvent_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final AtomicInteger DEBUG_ID_REF = new AtomicInteger(0);
   public final int id = DEBUG_ID_REF.getAndIncrement();
   public final ThreadWorldGenParams_fabric threadedParam;
   public final DhChunkPos minPos;
   public final int widthInChunks;
   public final EDhApiWorldGenerationStep targetGenerationStep;
   public final EDhApiDistantGeneratorMode generatorMode;
   public final CompletableFuture<Void> future;
   public final Consumer<IChunkWrapper> resultConsumer;

   private GenerationEvent_fabric(
      DhChunkPos minPos,
      int widthInChunks,
      BatchGenerationEnvironment_fabric generationGroup,
      EDhApiDistantGeneratorMode generatorMode,
      EDhApiWorldGenerationStep targetGenerationStep,
      Consumer<IChunkWrapper> resultConsumer
   ) {
      this.minPos = minPos;
      this.widthInChunks = widthInChunks;
      this.targetGenerationStep = targetGenerationStep;
      this.generatorMode = generatorMode;
      this.threadedParam = ThreadWorldGenParams_fabric.getOrMake(generationGroup.globalParams);
      this.future = new CompletableFuture<>();
      this.resultConsumer = resultConsumer;
   }

   public static GenerationEvent_fabric start(
      DhChunkPos minPos,
      int widthInChunks,
      BatchGenerationEnvironment_fabric genEnvironment,
      EDhApiDistantGeneratorMode generatorMode,
      EDhApiWorldGenerationStep target,
      Consumer<IChunkWrapper> resultConsumer,
      ExecutorService worldGeneratorThreadPool
   ) {
      GenerationEvent_fabric genEvent = new GenerationEvent_fabric(minPos, widthInChunks, genEnvironment, generatorMode, target, resultConsumer);

      try {
         worldGeneratorThreadPool.execute(() -> {
            try {
               BatchGenerationEnvironment_fabric.isDhWorldGenThreadRef.set(true);
               if (genEvent.generatorMode == EDhApiDistantGeneratorMode.INTERNAL_SERVER) {
                  genEnvironment.internalServerGenerator.generateChunksViaInternalServer(genEvent);
                  genEvent.future.complete(null);
               } else {
                  try {
                     genEnvironment.generateEvent(genEvent);
                  } catch (Throwable var13) {
                     handleWorldGenThrowable(genEvent, var13);
                  } finally {
                     genEvent.future.complete(null);
                  }
               }
            } catch (Throwable var15) {
               handleWorldGenThrowable(genEvent, var15);
            } finally {
               BatchGenerationEnvironment_fabric.isDhWorldGenThreadRef.remove();
            }
         });
      } catch (RejectedExecutionException var9) {
         genEvent.future.completeExceptionally(var9);
      }

      return genEvent;
   }

   private static void handleWorldGenThrowable(GenerationEvent_fabric generationEvent, Throwable initialThrowable) {
      Throwable throwable = initialThrowable;

      while (throwable instanceof CompletionException) {
         throwable = throwable.getCause();
      }

      boolean isShutdownException = ExceptionUtil.isShutdownException(throwable);
      if (!isShutdownException) {
         generationEvent.future.completeExceptionally(throwable);
      }
   }

   @Override
   public String toString() {
      return this.id + ":" + this.widthInChunks + "@" + this.minPos + "(" + this.targetGenerationStep + ")";
   }
}
