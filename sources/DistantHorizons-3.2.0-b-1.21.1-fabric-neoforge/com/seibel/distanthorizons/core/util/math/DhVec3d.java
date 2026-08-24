package com.seibel.distanthorizons.core.util.math;

import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.coreapi.util.MathUtil;

public class DhVec3d extends DhApiVec3d {
   public static DhVec3d XNeg = new DhVec3d(-1.0, 0.0, 0.0);
   public static DhVec3d XPos = new DhVec3d(1.0, 0.0, 0.0);
   public static DhVec3d YNeg = new DhVec3d(0.0, -1.0, 0.0);
   public static DhVec3d YPos = new DhVec3d(0.0, 1.0, 0.0);
   public static DhVec3d ZNeg = new DhVec3d(0.0, 0.0, -1.0);
   public static DhVec3d ZPos = new DhVec3d(0.0, 0.0, 1.0);
   public static final DhVec3d ZERO_VECTOR = new DhVec3d(0.0, 0.0, 0.0);

   public DhVec3d() {
   }

   public DhVec3d(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public DhVec3d(DhApiVec3d that) {
      this.x = that.x;
      this.y = that.y;
      this.z = that.z;
   }

   public DhVec3d(double[] values) {
      this.set(values);
   }

   public DhVec3d copy() {
      return new DhVec3d(this);
   }

   public void multiply(double scalar) {
      this.x *= scalar;
      this.y *= scalar;
      this.z *= scalar;
   }

   public void multiply(double x, double y, double z) {
      this.x *= x;
      this.y *= y;
      this.z *= z;
   }

   public void clamp(double min, double max) {
      this.x = MathUtil.clamp(min, this.x, max);
      this.y = MathUtil.clamp(min, this.y, max);
      this.z = MathUtil.clamp(min, this.z, max);
   }

   public void set(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void add(double x, double y, double z) {
      this.x += x;
      this.y += y;
      this.z += z;
   }

   public void add(DhVec3d vector) {
      this.x = this.x + vector.x;
      this.y = this.y + vector.y;
      this.z = this.z + vector.z;
   }

   public void subtract(DhVec3d vector) {
      this.x = this.x - vector.x;
      this.y = this.y - vector.y;
      this.z = this.z - vector.z;
   }

   public double dotProduct(DhVec3d vector) {
      return this.x * vector.x + this.y * vector.y + this.z * vector.z;
   }

   public DhVec3d normalize() {
      double value = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return value < 1.0E-4 ? ZERO_VECTOR : new DhVec3d(this.x / value, this.y / value, this.z / value);
   }

   public void crossProduct(DhVec3d vector) {
      double f = this.x;
      double f1 = this.y;
      double f2 = this.z;
      double f3 = vector.x;
      double f4 = vector.y;
      double f5 = vector.z;
      this.x = f1 * f5 - f2 * f4;
      this.y = f2 * f3 - f * f5;
      this.z = f * f4 - f1 * f3;
   }

   public void set(double[] values) {
      this.x = values[0];
      this.y = values[1];
      this.z = values[2];
   }

   public double getManhattanDistance(DhApiVec3d other) {
      return getManhattanDistance(this, other);
   }

   public static double getManhattanDistance(DhApiVec3d a, DhApiVec3d b) {
      return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z);
   }

   public double getDistance(DhApiVec3d other) {
      return getDistance(this, other);
   }

   public static double getDistance(DhApiVec3d a, DhApiVec3d b) {
      return Math.sqrt(Math.pow(a.x - b.x, 2.0) + Math.pow(a.y - b.y, 2.0) + Math.pow(a.z - b.z, 2.0));
   }

   public double getSquaredDistance(DhApiVec3d other) {
      return getSquaredDistance(this, other);
   }

   public static double getSquaredDistance(DhApiVec3d a, DhApiVec3d b) {
      return Math.pow(a.x - b.x, 2.0) + Math.pow(a.y - b.y, 2.0) + Math.pow(a.z - b.z, 2.0);
   }

   public double getHorizontalDistance(DhApiVec3d other) {
      return getHorizontalDistance(this, other);
   }

   public static double getHorizontalDistance(DhApiVec3d a, DhApiVec3d b) {
      return Math.sqrt(Math.pow(a.x - b.x, 2.0) + Math.pow(a.z - b.z, 2.0));
   }
}
