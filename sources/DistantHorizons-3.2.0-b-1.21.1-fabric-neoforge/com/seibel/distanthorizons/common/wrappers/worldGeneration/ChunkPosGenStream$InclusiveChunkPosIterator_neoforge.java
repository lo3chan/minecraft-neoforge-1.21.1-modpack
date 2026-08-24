package com.seibel.distanthorizons.common.wrappers.worldGeneration;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import net.minecraft.world.level.ChunkPos;

class ChunkPosGenStream$InclusiveChunkPosIterator_neoforge extends AbstractSpliterator<ChunkPos> {
   private final int minX;
   private final int minZ;
   private final int maxX;
   private final int maxZ;
   int x;
   private int z;

   protected ChunkPosGenStream$InclusiveChunkPosIterator_neoforge(int genMinX, int genMinZ, int width, int extraRadius) {
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
   public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
      if (this.x == this.maxX && this.z == this.maxZ) {
         return false;
      } else {
         if (this.x == this.maxX) {
            this.x = this.minX;
            this.z++;
         } else {
            this.x++;
         }

         consumer.accept(new ChunkPos(this.x, this.z));
         return true;
      }
   }
}
