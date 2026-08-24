package com.seibel.distanthorizons.common.wrappers.worldGeneration.step;

import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject.DhLitWorldGenRegion_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.ThreadWorldGenParams_neoforge;
import com.seibel.distanthorizons.core.util.gridList.ArrayGridList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public abstract class AbstractWorldGenStep_neoforge {
   public abstract void generateGroup(
      ThreadWorldGenParams_neoforge threadWorldGenParams_neoforge,
      DhLitWorldGenRegion_neoforge dhLitWorldGenRegion_neoforge,
      ArrayGridList<ChunkWrapper_neoforge> arrayGridList
   );

   public abstract ChunkStatus getChunkStatus();

   protected ArrayList<ChunkWrapper_neoforge> getChunkWrappersToGenerate(List<ChunkWrapper_neoforge> chunkWrappers) {
      ArrayList<ChunkWrapper_neoforge> chunkWrappersToGenerate = new ArrayList<>(chunkWrappers.size());

      for (ChunkWrapper_neoforge chunkWrapper : chunkWrappers) {
         ChunkAccess chunk = chunkWrapper.getChunk();
         if (!chunkWrapper.getStatus().isOrAfter(this.getChunkStatus()) && chunk instanceof ProtoChunk) {
            chunkWrapper.trySetStatus(this.getChunkStatus());
            chunkWrappersToGenerate.add(chunkWrapper);
         }
      }

      return chunkWrappersToGenerate;
   }
}
