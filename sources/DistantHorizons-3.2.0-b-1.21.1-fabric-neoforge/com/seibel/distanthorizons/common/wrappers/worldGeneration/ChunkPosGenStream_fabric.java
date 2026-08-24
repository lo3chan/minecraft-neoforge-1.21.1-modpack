package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.class_1923;

public class ChunkPosGenStream_fabric {
   public static Iterator<class_1923> getIterator(int genMinX, int genMinZ, int width, int extraRadius) {
      return getStream(genMinX, genMinZ, width, extraRadius).iterator();
   }

   public static Stream<class_1923> getStream(int genMinX, int genMinZ, int width, int extraRadius) {
      return StreamSupport.stream(new ChunkPosGenStream$InclusiveChunkPosIterator_fabric(genMinX, genMinZ, width, extraRadius), false);
   }
}
