package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.world.level.ChunkPos;

public class ChunkPosGenStream_neoforge {
   public static Iterator<ChunkPos> getIterator(int genMinX, int genMinZ, int width, int extraRadius) {
      return getStream(genMinX, genMinZ, width, extraRadius).iterator();
   }

   public static Stream<ChunkPos> getStream(int genMinX, int genMinZ, int width, int extraRadius) {
      return StreamSupport.stream(new ChunkPosGenStream$InclusiveChunkPosIterator_neoforge(genMinX, genMinZ, width, extraRadius), false);
   }
}
