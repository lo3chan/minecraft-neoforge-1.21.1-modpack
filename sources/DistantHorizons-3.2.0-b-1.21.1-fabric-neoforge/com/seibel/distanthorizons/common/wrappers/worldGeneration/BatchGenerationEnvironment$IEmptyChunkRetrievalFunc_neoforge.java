package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import net.minecraft.world.level.chunk.ChunkAccess;

@FunctionalInterface
public interface BatchGenerationEnvironment$IEmptyChunkRetrievalFunc_neoforge {
   ChunkAccess getChunk(int i, int j);
}
