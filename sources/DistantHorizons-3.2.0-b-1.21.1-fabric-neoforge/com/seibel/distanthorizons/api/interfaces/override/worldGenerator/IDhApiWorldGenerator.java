package com.seibel.distanthorizons.api.interfaces.override.worldGenerator;

import com.seibel.distanthorizons.api.enums.EDhApiDetailLevel;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGeneratorReturnType;
import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;
import com.seibel.distanthorizons.api.objects.data.DhApiChunk;
import com.seibel.distanthorizons.api.objects.data.IDhApiFullDataSource;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public interface IDhApiWorldGenerator extends Closeable, IDhApiOverrideable {
   default byte getSmallestDataDetailLevel() {
      return EDhApiDetailLevel.BLOCK.detailLevel;
   }

   default byte getLargestDataDetailLevel() {
      return EDhApiDetailLevel.BLOCK.detailLevel;
   }

   default boolean runApiValidation() {
      return true;
   }

   default CompletableFuture<Void> generateChunks(
      int chunkPosMinX,
      int chunkPosMinZ,
      int generationRequestChunkWidthCount,
      byte targetDataDetail,
      EDhApiDistantGeneratorMode generatorMode,
      ExecutorService worldGeneratorThreadPool,
      Consumer<Object[]> resultConsumer
   ) {
      throw new UnsupportedOperationException();
   }

   default CompletableFuture<Void> generateApiChunks(
      int chunkPosMinX,
      int chunkPosMinZ,
      int generationRequestChunkWidthCount,
      byte targetDataDetail,
      EDhApiDistantGeneratorMode generatorMode,
      ExecutorService worldGeneratorThreadPool,
      Consumer<DhApiChunk> resultConsumer
   ) {
      throw new UnsupportedOperationException();
   }

   default CompletableFuture<Void> generateLod(
      int chunkPosMinX,
      int chunkPosMinZ,
      int lodPosX,
      int lodPosZ,
      byte detailLevel,
      IDhApiFullDataSource pooledFullDataSource,
      EDhApiDistantGeneratorMode generatorMode,
      ExecutorService worldGeneratorThreadPool,
      Consumer<IDhApiFullDataSource> resultConsumer
   ) {
      throw new UnsupportedOperationException();
   }

   default EDhApiWorldGeneratorReturnType getReturnType() {
      return EDhApiWorldGeneratorReturnType.VANILLA_CHUNKS;
   }

   void preGeneratorTaskStart();

   @Override
   void close();
}
