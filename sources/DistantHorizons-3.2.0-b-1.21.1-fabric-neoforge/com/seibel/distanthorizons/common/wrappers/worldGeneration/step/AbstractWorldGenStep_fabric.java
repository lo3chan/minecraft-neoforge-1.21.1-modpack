package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_fabric;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2839;

public abstract class AbstractWorldGenStep_fabric {
   public abstract void generateGroup(
      ThreadWorldGenParams_fabric threadWorldGenParams_fabric,
      DhLitWorldGenRegion_fabric dhLitWorldGenRegion_fabric,
      ArrayGridList<ChunkWrapper_fabric> arrayGridList
   );

   public abstract class_2806 getChunkStatus();

   protected ArrayList<ChunkWrapper_fabric> getChunkWrappersToGenerate(List<ChunkWrapper_fabric> chunkWrappers) {
      ArrayList<ChunkWrapper_fabric> chunkWrappersToGenerate = new ArrayList<>(chunkWrappers.size());

      for (ChunkWrapper_fabric chunkWrapper : chunkWrappers) {
         class_2791 chunk = chunkWrapper.getChunk();
         if (!chunkWrapper.getStatus().method_12165(this.getChunkStatus()) && chunk instanceof class_2839) {
            chunkWrapper.trySetStatus(this.getChunkStatus());
            chunkWrappersToGenerate.add(chunkWrapper);
         }
      }

      return chunkWrappersToGenerate;
   }
}
