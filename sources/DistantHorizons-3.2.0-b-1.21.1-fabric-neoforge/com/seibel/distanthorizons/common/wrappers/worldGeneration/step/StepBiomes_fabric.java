package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_fabric;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_6748;

public final class StepBiomes_fabric extends AbstractWorldGenStep_fabric {
   private final BatchGenerationEnvironment_fabric environment;
   public static final class_2806 STATUS = class_2806.field_12794;

   public StepBiomes_fabric(BatchGenerationEnvironment_fabric batchGenerationEnvironment) {
      this.environment = batchGenerationEnvironment;
   }

   @Override
   public class_2806 getChunkStatus() {
      return STATUS;
   }

   @Override
   public void generateGroup(ThreadWorldGenParams_fabric tParams, DhLitWorldGenRegion_fabric worldGenRegion, ArrayGridList<ChunkWrapper_fabric> chunkWrappers) {
      for (ChunkWrapper_fabric chunkWrapper : this.getChunkWrappersToGenerate(chunkWrappers)) {
         class_2791 chunk = chunkWrapper.getChunk();
         chunk = this.environment
            .confirmFutureWasRunSynchronously(
               this.environment
                  .globalParams
                  .generator
                  .method_38275(
                     this.environment.globalParams.randomState,
                     class_6748.method_39342(worldGenRegion),
                     tParams.structFeatManager.forWorldGenRegion(worldGenRegion),
                     chunk
                  )
            );
      }
   }
}
