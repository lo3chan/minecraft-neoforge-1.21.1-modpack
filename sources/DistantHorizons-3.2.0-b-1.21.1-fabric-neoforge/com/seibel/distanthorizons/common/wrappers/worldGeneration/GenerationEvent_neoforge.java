package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_neoforge;
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

public final class GenerationEvent_neoforge {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final AtomicInteger DEBUG_ID_REF = new AtomicInteger(0);
   public final int id = DEBUG_ID_REF.getAndIncrement();
   public final ThreadWorldGenParams_neoforge threadedParam;
   public final DhChunkPos minPos;
   public final int widthInChunks;
   public final EDhApiWorldGenerationStep targetGenerationStep;
   public final EDhApiDistantGeneratorMode generatorMode;
   public final CompletableFuture<Void> future;
   public final Consumer<IChunkWrapper> resultConsumer;

   private GenerationEvent_neoforge(
      DhChunkPos minPos,
      int widthInChunks,
      BatchGenerationEnvironment_neoforge generationGroup,
      EDhApiDistantGeneratorMode generatorMode,
      EDhApiWorldGenerationStep targetGenerationStep,
      Consumer<IChunkWrapper> resultConsumer
   ) {
      this.minPos = minPos;
      this.widthInChunks = widthInChunks;
      this.targetGenerationStep = targetGenerationStep;
      this.generatorMode = generatorMode;
      this.threadedParam = ThreadWorldGenParams_neoforge.getOrMake(generationGroup.globalParams);
      this.future = new CompletableFuture<>();
      this.resultConsumer = resultConsumer;
   }

   public static GenerationEvent_neoforge start(
      DhChunkPos minPos,
      int widthInChunks,
      BatchGenerationEnvironment_neoforge genEnvironment,
      EDhApiDistantGeneratorMode generatorMode,
      EDhApiWorldGenerationStep target,
      Consumer<IChunkWrapper> resultConsumer,
      ExecutorService worldGeneratorThreadPool
   ) {
      GenerationEvent_neoforge genEvent = new GenerationEvent_neoforge(minPos, widthInChunks, genEnvironment, generatorMode, target, resultConsumer);

      try {
         worldGeneratorThreadPool.execute(() -> {
            try {
               BatchGenerationEnvironment_neoforge.isDhWorldGenThreadRef.set(true);
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
               BatchGenerationEnvironment_neoforge.isDhWorldGenThreadRef.remove();
            }
         });
      } catch (RejectedExecutionException var9) {
         genEvent.future.completeExceptionally(var9);
      }

      return genEvent;
   }

   private static void handleWorldGenThrowable(GenerationEvent_neoforge generationEvent, Throwable initialThrowable) {
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
