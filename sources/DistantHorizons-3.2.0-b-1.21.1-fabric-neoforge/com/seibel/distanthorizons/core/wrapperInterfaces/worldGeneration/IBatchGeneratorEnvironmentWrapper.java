package com.seibel.distanthorizons.core.wrapperInterfaces.worldGeneration;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public interface IBatchGeneratorEnvironmentWrapper extends AutoCloseable {
   void updateAllFutures();

   CompletableFuture<Void> queueGenEvent(
      int i,
      int j,
      int k,
      EDhApiDistantGeneratorMode eDhApiDistantGeneratorMode,
      EDhApiWorldGenerationStep eDhApiWorldGenerationStep,
      ExecutorService executorService,
      Consumer<IChunkWrapper> consumer
   );

   @Override
   void close();
}
