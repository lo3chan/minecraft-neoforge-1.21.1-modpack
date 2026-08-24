package com.seibel.distanthorizons.core.pos;

import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.util.math.DhVec3d;

public class DhChunkPos {
   private final int x;
   private final int z;
   public final int hashCode;

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public DhChunkPos(int x, int z) {
      this.x = x;
      this.z = z;
      this.hashCode = this.x * 7309 + this.z;
   }

   public DhChunkPos(DhBlockPos blockPos) {
      this(blockPos.getX() >> 4, blockPos.getZ() >> 4);
   }

   public DhChunkPos(DhBlockPos2D blockPos) {
      this(blockPos.x >> 4, blockPos.z >> 4);
   }

   public DhChunkPos(DhVec3d pos) {
      this((int)pos.x >> 4, (int)pos.z >> 4);
   }

   public DhBlockPos centerBlockPos() {
      return new DhBlockPos(8 + this.x << 4, 0, 8 + this.z << 4);
   }

   public DhBlockPos minCornerBlockPos() {
      return new DhBlockPos(this.x << 4, 0, this.z << 4);
   }

   public int getMinBlockX() {
      return this.x << 4;
   }

   public int getMinBlockZ() {
      return this.z << 4;
   }

   public int getMaxBlockX() {
      int minBlockPos = this.getMinBlockX() + 16;
      return minBlockPos + (minBlockPos < 0 ? -1 : 0);
   }

   public int getMaxBlockZ() {
      int minBlockPos = this.getMinBlockZ() + 16;
      return minBlockPos + (minBlockPos < 0 ? -1 : 0);
   }

   public DhBlockPos2D getMinBlockPos() {
      return new DhBlockPos2D(this.x << 4, this.z << 4);
   }

   public boolean contains(DhBlockPos pos) {
      int minBlockX = this.getMinBlockX();
      int minBlockZ = this.getMinBlockZ();
      int maxBlockX = minBlockX + 16;
      int maxBlockZ = minBlockZ + 16;
      return minBlockX >= pos.getX() && pos.getX() < maxBlockX && minBlockZ >= pos.getZ() && pos.getZ() < maxBlockZ;
   }

   public double distance(DhChunkPos other) {
      return Math.sqrt(Math.pow(this.x - other.x, 2.0) + Math.pow(this.z - other.z, 2.0));
   }

   public double squaredDistance(DhChunkPos other) {
      return Math.pow(this.x - other.x, 2.0) + Math.pow(this.z - other.z, 2.0);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         DhChunkPos that = (DhChunkPos)obj;
         return this.x == that.x && this.z == that.z;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public String toString() {
      return "C[" + this.x + "," + this.z + "]";
   }
}
