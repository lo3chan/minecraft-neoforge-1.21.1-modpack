package com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute;

import com.seibel.distanthorizons.coreapi.util.MathUtil;

public final class GlVertexPointer {
   public final int elementCount;
   public final int glType;
   public final boolean normalized;
   public final int byteSize;
   public final boolean useInteger;

   public GlVertexPointer(int elementCount, int glType, boolean normalized, int byteSize, boolean useInteger) {
      this.elementCount = elementCount;
      this.glType = glType;
      this.normalized = normalized;
      this.byteSize = byteSize;
      this.useInteger = useInteger;
   }

   public GlVertexPointer(int elementCount, int glType, boolean normalized, int byteSize) {
      this(elementCount, glType, normalized, byteSize, false);
   }

   private static int _align(int bytes) {
      return MathUtil.ceilDiv(bytes, 4) * 4;
   }

   public static GlVertexPointer addFloatPointer(boolean normalized) {
      return new GlVertexPointer(1, 5126, normalized, 4);
   }

   public static GlVertexPointer addVec2Pointer(boolean normalized) {
      return new GlVertexPointer(2, 5126, normalized, 8);
   }

   public static GlVertexPointer addVec3Pointer(boolean normalized) {
      return new GlVertexPointer(3, 5126, normalized, 12);
   }

   public static GlVertexPointer addVec4Pointer(boolean normalized) {
      return new GlVertexPointer(4, 5126, normalized, 16);
   }

   public static GlVertexPointer addUnsignedBytePointer(boolean normalized, boolean useInteger) {
      return new GlVertexPointer(1, 5121, normalized, 4, useInteger);
   }

   public static GlVertexPointer addUnsignedBytesPointer(int elementCount, boolean normalized, boolean useInteger) {
      return new GlVertexPointer(elementCount, 5121, normalized, _align(elementCount), useInteger);
   }

   public static GlVertexPointer addUnsignedShortsPointer(int elementCount, boolean normalized, boolean useInteger) {
      return new GlVertexPointer(elementCount, 5123, normalized, _align(elementCount * 2), useInteger);
   }

   public static GlVertexPointer addShortsPointer(int elementCount, boolean normalized, boolean useInteger) {
      return new GlVertexPointer(elementCount, 5122, normalized, _align(elementCount * 2), useInteger);
   }

   public static GlVertexPointer addIntPointer(boolean normalized, boolean useInteger) {
      return new GlVertexPointer(1, 5124, normalized, 4, useInteger);
   }

   public static GlVertexPointer addIVec2Pointer(boolean normalized, boolean useInteger) {
      return new GlVertexPointer(2, 5124, normalized, 8, useInteger);
   }

   public static GlVertexPointer addIVec3Pointer(boolean normalized, boolean useInteger) {
      return new GlVertexPointer(3, 5124, normalized, 12, useInteger);
   }

   public static GlVertexPointer addIVec4Pointer(boolean normalized, boolean useInteger) {
      return new GlVertexPointer(4, 5124, normalized, 16, useInteger);
   }
}
