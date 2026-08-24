package com.seibel.distanthorizons.core.util.math;

import com.seibel.distanthorizons.api.objects.math.DhApiVec3i;
import com.seibel.distanthorizons.coreapi.util.MathUtil;

public class DhVec3i extends DhApiVec3i {
   public static DhVec3i XNeg = new DhVec3i(-1, 0, 0);
   public static DhVec3i XPos = new DhVec3i(1, 0, 0);
   public static DhVec3i YNeg = new DhVec3i(0, -1, 0);
   public static DhVec3i YPos = new DhVec3i(0, 1, 0);
   public static DhVec3i ZNeg = new DhVec3i(0, 0, -1);
   public static DhVec3i ZPos = new DhVec3i(0, 0, 1);

   public DhVec3i() {
      this.x = 0;
      this.y = 0;
      this.z = 0;
   }

   public DhVec3i(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void mul(float scalar) {
      this.x = (int)(this.x * scalar);
      this.y = (int)(this.y * scalar);
      this.z = (int)(this.z * scalar);
   }

   public void mul(float x, float y, float z) {
      this.x = (int)(this.x * x);
      this.y = (int)(this.y * y);
      this.z = (int)(this.z * z);
   }

   public void clamp(int min, int max) {
      this.x = MathUtil.clamp(min, this.x, max);
      this.y = MathUtil.clamp(min, this.y, max);
      this.z = MathUtil.clamp(min, this.z, max);
   }

   public void set(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void add(int x, int y, int z) {
      this.x += x;
      this.y += y;
      this.z += z;
   }

   public void add(DhVec3i vector) {
      this.x = this.x + vector.x;
      this.y = this.y + vector.y;
      this.z = this.z + vector.z;
   }

   public void subtract(DhVec3i vector) {
      this.x = this.x - vector.x;
      this.y = this.y - vector.y;
      this.z = this.z - vector.z;
   }

   public double distSqr(double x, double y, double z, boolean centerOfBlock) {
      double offset = centerOfBlock ? 0.5 : 0.0;
      double xAdd = this.x + offset - x;
      double yAdd = this.y + offset - y;
      double zAdd = this.z + offset - z;
      return xAdd * xAdd + yAdd * yAdd + zAdd * zAdd;
   }

   public int distManhattan(DhVec3i otherVec) {
      float xSub = Math.abs(otherVec.x - this.x);
      float ySub = Math.abs(otherVec.y - this.y);
      float zSub = Math.abs(otherVec.z - this.z);
      return (int)(xSub + ySub + zSub);
   }

   public float dotProduct(DhVec3i vector) {
      return this.x * vector.x + this.y * vector.y + this.z * vector.z;
   }

   public DhVec3i cross(DhVec3i otherVec) {
      return new DhVec3i(this.y * otherVec.z - this.z * otherVec.y, this.z * otherVec.x - this.x * otherVec.z, this.x * otherVec.y - this.y * otherVec.x);
   }

   public DhVec3i copy() {
      return new DhVec3i(this.x, this.y, this.z);
   }

   public DhVec3i(int[] values) {
      this.set(values);
   }

   public void set(int[] values) {
      this.x = values[0];
      this.y = values[1];
      this.z = values[2];
   }
}
