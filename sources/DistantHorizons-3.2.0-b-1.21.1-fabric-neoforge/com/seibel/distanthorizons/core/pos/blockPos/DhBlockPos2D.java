package com.seibel.distanthorizons.core.pos.blockPos;

import com.seibel.distanthorizons.coreapi.util.MathUtil;

public class DhBlockPos2D {
   public static final DhBlockPos2D ZERO = new DhBlockPos2D(0, 0);
   public final int x;
   public final int z;

   public DhBlockPos2D(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public DhBlockPos2D(DhBlockPos blockPos) {
      this.x = blockPos.getX();
      this.z = blockPos.getZ();
   }

   public DhBlockPos2D add(DhBlockPos2D other) {
      return new DhBlockPos2D(this.x + other.x, this.z + other.z);
   }

   public DhBlockPos2D add(int offsetX, int offsetZ) {
      return new DhBlockPos2D(this.x + offsetX, this.z + offsetZ);
   }

   public DhBlockPos2D subtract(DhBlockPos2D other) {
      return new DhBlockPos2D(this.x - other.x, this.z - other.z);
   }

   public double dist(DhBlockPos2D other) {
      return this.dist(other.x, other.z);
   }

   public double dist(int x, int z) {
      return Math.sqrt(Math.pow(this.x - x, 2.0) + Math.pow(this.z - z, 2.0));
   }

   public long distSquared(DhBlockPos2D other) {
      return this.distSquared(other.x, other.z);
   }

   public long distSquared(int x, int z) {
      return MathUtil.pow2((long)this.x - x) + MathUtil.pow2((long)this.z - z);
   }

   public int chebyshevDist(DhBlockPos2D other) {
      return Math.max(Math.abs(this.x - other.x), Math.abs(this.z - other.z));
   }

   public int manhattanDist(DhBlockPos2D other) {
      return Math.abs(this.x - other.x) + Math.abs(this.z - other.z);
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.z + ")";
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof DhBlockPos2D)) {
         return false;
      } else {
         DhBlockPos2D other = (DhBlockPos2D)obj;
         return this.x == other.x && this.z == other.z;
      }
   }

   @Override
   public int hashCode() {
      return Integer.hashCode(this.x) ^ Integer.hashCode(this.z);
   }
}
