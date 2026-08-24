package com.seibel.distanthorizons.common.wrappers.worldGeneration.chunkFileHandling;

import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.ChunkLightStorage;

public class ChunkCompoundTagParser$CombinedChunkLightStorage_fabric {
   public ChunkLightStorage blockLightStorage;
   public ChunkLightStorage skyLightStorage;

   public ChunkCompoundTagParser$CombinedChunkLightStorage_fabric(int minY, int maxY) {
      this.blockLightStorage = ChunkLightStorage.createBlockLightStorage(minY, maxY);
      this.skyLightStorage = ChunkLightStorage.createSkyLightStorage(minY, maxY);
   }
}
