package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import net.minecraft.class_1923;

class ChunkPosGenStream$InclusiveChunkPosIterator_fabric extends AbstractSpliterator<class_1923> {
   private final int minX;
   private final int minZ;
   private final int maxX;
   private final int maxZ;
   int x;
   private int z;

   protected ChunkPosGenStream$InclusiveChunkPosIterator_fabric(int genMinX, int genMinZ, int width, int extraRadius) {
      super(getCount(width, extraRadius), 64);
      this.minX = genMinX - extraRadius;
      this.minZ = genMinZ - extraRadius;
      this.maxX = genMinX + (width - 1) + extraRadius;
      this.maxZ = genMinZ + (width - 1) + extraRadius;
      this.x = this.minX - 1;
      this.z = this.minZ;
   }

   private static int getCount(int width, int extraRadius) {
      int widthPlusExtra = width + extraRadius * 2;
      return widthPlusExtra * widthPlusExtra;
   }

   @Override
   public boolean tryAdvance(Consumer<? super class_1923> consumer) {
      if (this.x == this.maxX && this.z == this.maxZ) {
         return false;
      } else {
         if (this.x == this.maxX) {
            this.x = this.minX;
            this.z++;
         } else {
            this.x++;
         }

         consumer.accept(new class_1923(this.x, this.z));
         return true;
      }
   }
}
