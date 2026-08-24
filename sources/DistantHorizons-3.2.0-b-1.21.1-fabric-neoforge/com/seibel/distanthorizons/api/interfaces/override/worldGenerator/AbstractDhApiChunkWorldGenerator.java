package com.seibel.distanthorizons.api.interfaces.override.worldGenerator;

import com.seibel.distanthorizons.api.enums.EDhApiDetailLevel;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;
import com.seibel.distanthorizons.api.objects.data.DhApiChunk;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public abstract class AbstractDhApiChunkWorldGenerator implements Closeable, IDhApiOverrideable, IDhApiWorldGenerator {
   @Override
   public final byte getSmallestDataDetailLevel() {
      return EDhApiDetailLevel.BLOCK.detailLevel;
   }

   @Override
   public final byte getLargestDataDetailLevel() {
      return EDhApiDetailLevel.BLOCK.detailLevel;
   }

   @Override
   public final CompletableFuture<Void> generateChunks(
      int chunkPosMinX,
      int chunkPosMinZ,
      int generationRequestChunkWidthCount,
      byte targetDataDetail,
      EDhApiDistantGeneratorMode generatorMode,
      ExecutorService worldGeneratorThreadPool,
      Consumer<Object[]> resultConsumer
   ) throws ClassCastException {
      return CompletableFuture.runAsync(() -> {
         for (int chunkX = chunkPosMinX; chunkX < chunkPosMinX + generationRequestChunkWidthCount; chunkX++) {
            for (int chunkZ = chunkPosMinZ; chunkZ < chunkPosMinZ + generationRequestChunkWidthCount; chunkZ++) {
               Object[] rawMcObjectArray = this.generateChunk(chunkX, chunkZ, generatorMode);
               resultConsumer.accept(rawMcObjectArray);
            }
         }
      }, worldGeneratorThreadPool);
   }

   @Override
   public final CompletableFuture<Void> generateApiChunks(
      int chunkPosMinX,
      int chunkPosMinZ,
      int generationRequestChunkWidthCount,
      byte targetDataDetail,
      EDhApiDistantGeneratorMode generatorMode,
      ExecutorService worldGeneratorThreadPool,
      Consumer<DhApiChunk> resultConsumer
   ) {
      return CompletableFuture.runAsync(() -> {
         for (int chunkX = chunkPosMinX; chunkX < chunkPosMinX + generationRequestChunkWidthCount; chunkX++) {
            for (int chunkZ = chunkPosMinZ; chunkZ < chunkPosMinZ + generationRequestChunkWidthCount; chunkZ++) {
               DhApiChunk apiChunk = this.generateApiChunk(chunkX, chunkZ, generatorMode);
               resultConsumer.accept(apiChunk);
            }
         }
      }, worldGeneratorThreadPool);
   }

   public abstract Object[] generateChunk(int i, int j, EDhApiDistantGeneratorMode eDhApiDistantGeneratorMode);

   public abstract DhApiChunk generateApiChunk(int i, int j, EDhApiDistantGeneratorMode eDhApiDistantGeneratorMode);
}
