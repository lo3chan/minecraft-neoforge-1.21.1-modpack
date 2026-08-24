package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;

public class WorldGenUtil {
   public static boolean isPosInWorldGenRange(long requestedPos, int centerChunkX, int centerChunkZ, int maxChunkRadius) {
      if (Config.Common.WorldGenerator.generationMaxChunkRadius.get() <= 0) {
         return true;
      } else {
         DhBlockPos centerBlockPos = new DhChunkPos(centerChunkX, centerChunkZ).centerBlockPos();
         int blockDistanceFromCenter = DhSectionPos.getChebyshevSignedBlockDistance(requestedPos, centerBlockPos);
         int maxBlockRadius = maxChunkRadius * 16;
         return blockDistanceFromCenter <= maxBlockRadius;
      }
   }
}
