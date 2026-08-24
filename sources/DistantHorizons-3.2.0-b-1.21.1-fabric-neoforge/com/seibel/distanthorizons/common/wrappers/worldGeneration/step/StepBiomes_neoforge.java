package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_neoforge;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.blending.Blender;

public final class StepBiomes_neoforge extends AbstractWorldGenStep_neoforge {
   private final BatchGenerationEnvironment_neoforge environment;
   public static final ChunkStatus STATUS = ChunkStatus.BIOMES;

   public StepBiomes_neoforge(BatchGenerationEnvironment_neoforge batchGenerationEnvironment) {
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
         chunk = this.environment
            .confirmFutureWasRunSynchronously(
               this.environment
                  .globalParams
                  .generator
                  .createBiomes(
                     this.environment.globalParams.randomState, Blender.of(worldGenRegion), tParams.structFeatManager.forWorldGenRegion(worldGenRegion), chunk
                  )
            );
      }
   }
}
