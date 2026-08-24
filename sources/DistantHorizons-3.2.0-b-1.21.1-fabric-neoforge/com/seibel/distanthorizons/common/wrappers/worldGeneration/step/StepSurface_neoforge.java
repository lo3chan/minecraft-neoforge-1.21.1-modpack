package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_neoforge;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public final class StepSurface_neoforge extends AbstractWorldGenStep_neoforge {
   private static final ChunkStatus STATUS = ChunkStatus.SURFACE;
   private final BatchGenerationEnvironment_neoforge environment;

   public StepSurface_neoforge(BatchGenerationEnvironment_neoforge batchGenerationEnvironment) {
      this.environment = batchGenerationEnvironment;
   }

   @Override
   public ChunkStatus getChunkStatus() {
      return STATUS;
   }

   @Override
   public void generateGroup(
      ThreadWorldGenParams_neoforge tParams, DhLitWorldGenRegion_neoforge worldGenRegion, ArrayGridList<ChunkWrapper_neoforge> chunkWrappers
   ) {
      for (ChunkWrapper_neoforge chunkWrapper : this.getChunkWrappersToGenerate(chunkWrappers)) {
         ChunkAccess chunk = chunkWrapper.getChunk();
         this.environment
            .globalParams
            .generator
            .buildSurface(worldGenRegion, tParams.structFeatManager.forWorldGenRegion(worldGenRegion), this.environment.globalParams.randomState, chunk);
      }
   }
}
