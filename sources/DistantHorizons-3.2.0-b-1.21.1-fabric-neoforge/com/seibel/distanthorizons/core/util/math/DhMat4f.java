package com.seibel.distanthorizons.core.util.math;

import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class DhMat4f extends DhApiMat4f {
   public static final DhApiMat4f EMPTY = new DhApiMat4f();
   public static final DhApiMat4f IDENTITY = new DhApiMat4f();

   public DhMat4f() {
   }

   public DhMat4f(DhApiMat4f sourceMatrix) {
      super(sourceMatrix);
   }

   public DhMat4f(FloatBuffer buffer) {
      this(buffer.array());
   }

   public DhMat4f(float[] values) {
      super(values);
   }

   public DhMat4f(Matrix4fc sourceMatrix) {
      this.set(sourceMatrix);
   }

   public void set(Matrix4fc sourceMatrix) {
      this.m00 = sourceMatrix.m00();
      this.m01 = sourceMatrix.m10();
      this.m02 = sourceMatrix.m20();
      this.m03 = sourceMatrix.m30();
      this.m10 = sourceMatrix.m01();
      this.m11 = sourceMatrix.m11();
      this.m12 = sourceMatrix.m21();
      this.m13 = sourceMatrix.m31();
      this.m20 = sourceMatrix.m02();
      this.m21 = sourceMatrix.m12();
      this.m22 = sourceMatrix.m22();
      this.m23 = sourceMatrix.m32();
      this.m30 = sourceMatrix.m03();
      this.m31 = sourceMatrix.m13();
      this.m32 = sourceMatrix.m23();
      this.m33 = sourceMatrix.m33();
   }

   public static Matrix4f createJomlMatrix(DhApiMat4f matrix) {
      return new Matrix4f(
         matrix.m00,
         matrix.m10,
         matrix.m20,
         matrix.m30,
         matrix.m01,
         matrix.m11,
         matrix.m21,
         matrix.m31,
         matrix.m02,
         matrix.m12,
         matrix.m22,
         matrix.m32,
         matrix.m03,
         matrix.m13,
         matrix.m23,
         matrix.m33
      );
   }

   public Matrix4f createJomlMatrix() {
      return new Matrix4f(
         this.m00,
         this.m10,
         this.m20,
         this.m30,
         this.m01,
         this.m11,
         this.m21,
         this.m31,
         this.m02,
         this.m12,
         this.m22,
         this.m32,
         this.m03,
         this.m13,
         this.m23,
         this.m33
      );
   }

   public static DhMat4f perspective(double fov, float widthHeightRatio, float nearClipPlane, float farClipPlane) {
      float f = (float)(1.0 / Math.tan(fov * 0.01745329238474369 / 2.0));
      DhMat4f matrix = new DhMat4f();
      matrix.m00 = f / widthHeightRatio;
      matrix.m11 = f;
      matrix.m22 = (farClipPlane + nearClipPlane) / (nearClipPlane - farClipPlane);
      matrix.m32 = -1.0F;
      matrix.m23 = 2.0F * farClipPlane * nearClipPlane / (nearClipPlane - farClipPlane);
      return matrix;
   }

   public void multiplyTranslationMatrix(double x, double y, double z) {
      this.multiply(createTranslateMatrix((float)x, (float)y, (float)z));
   }

   public static DhMat4f createScaleMatrix(float x, float y, float z) {
      DhMat4f matrix = new DhMat4f();
      matrix.m00 = x;
      matrix.m11 = y;
      matrix.m22 = z;
      matrix.m33 = 1.0F;
      return matrix;
   }

   public static DhMat4f createTranslateMatrix(float x, float y, float z) {
      DhMat4f matrix = new DhMat4f();
      matrix.m00 = 1.0F;
      matrix.m11 = 1.0F;
      matrix.m22 = 1.0F;
      matrix.m33 = 1.0F;
      matrix.m03 = x;
      matrix.m13 = y;
      matrix.m23 = z;
      return matrix;
   }

   public void add(DhApiMat4f other) {
      this.m00 = this.m00 + other.m00;
      this.m01 = this.m01 + other.m01;
      this.m02 = this.m02 + other.m02;
      this.m03 = this.m03 + other.m03;
      this.m10 = this.m10 + other.m10;
      this.m11 = this.m11 + other.m11;
      this.m12 = this.m12 + other.m12;
      this.m13 = this.m13 + other.m13;
      this.m20 = this.m20 + other.m20;
      this.m21 = this.m21 + other.m21;
      this.m22 = this.m22 + other.m22;
      this.m23 = this.m23 + other.m23;
      this.m30 = this.m30 + other.m30;
      this.m31 = this.m31 + other.m31;
      this.m32 = this.m32 + other.m32;
      this.m33 = this.m33 + other.m33;
   }

   public void multiplyBackward(DhApiMat4f other) {
      DhApiMat4f copy = other.copy();
      copy.multiply(this);
      this.set(copy);
   }

   public void setTranslation(float x, float y, float z) {
      this.m00 = 1.0F;
      this.m11 = 1.0F;
      this.m22 = 1.0F;
      this.m33 = 1.0F;
      this.m03 = x;
      this.m13 = y;
      this.m23 = z;
   }

   public DhMat4f copy() {
      return new DhMat4f(this);
   }

   static {
      IDENTITY.setIdentity();
   }
}
