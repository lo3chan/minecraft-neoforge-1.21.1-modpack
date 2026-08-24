package com.seibel.distanthorizons.common.wrappers;

import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import java.nio.FloatBuffer;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class McObjectConverter_fabric {
   public static DhMat4f convert(Matrix4f mcMatrix) {
      FloatBuffer buffer = FloatBuffer.allocate(16);
      storeMatrix(mcMatrix, buffer);
      return new DhMat4f(buffer);
   }

   private static void storeMatrix(Matrix4f matrix, FloatBuffer buffer) {
      buffer.put(bufferIndex(0, 0), matrix.m00());
      buffer.put(bufferIndex(0, 1), matrix.m01());
      buffer.put(bufferIndex(0, 2), matrix.m02());
      buffer.put(bufferIndex(0, 3), matrix.m03());
      buffer.put(bufferIndex(1, 0), matrix.m10());
      buffer.put(bufferIndex(1, 1), matrix.m11());
      buffer.put(bufferIndex(1, 2), matrix.m12());
      buffer.put(bufferIndex(1, 3), matrix.m13());
      buffer.put(bufferIndex(2, 0), matrix.m20());
      buffer.put(bufferIndex(2, 1), matrix.m21());
      buffer.put(bufferIndex(2, 2), matrix.m22());
      buffer.put(bufferIndex(2, 3), matrix.m23());
      buffer.put(bufferIndex(3, 0), matrix.m30());
      buffer.put(bufferIndex(3, 1), matrix.m31());
      buffer.put(bufferIndex(3, 2), matrix.m32());
      buffer.put(bufferIndex(3, 3), matrix.m33());
   }

   private static int bufferIndex(int x, int y) {
      return y * 4 + x;
   }

   @Nullable
   public static class_2350 convert(@Nullable EDhDirection dhDirection) {
      if (dhDirection == null) {
         return null;
      } else {
         switch (dhDirection) {
            case DOWN:
               return class_2350.field_11033;
            case UP:
               return class_2350.field_11036;
            case NORTH:
               return class_2350.field_11043;
            case SOUTH:
               return class_2350.field_11035;
            case WEST:
               return class_2350.field_11039;
            case EAST:
               return class_2350.field_11034;
            default:
               throw new IllegalArgumentException("No Minecraft direction defined for [" + dhDirection + "].");
         }
      }
   }

   public static class_2338 convert(DhBlockPos wrappedPos) {
      return new class_2338(wrappedPos.getX(), wrappedPos.getY(), wrappedPos.getZ());
   }

   public static class_1923 convert(DhChunkPos wrappedPos) {
      return new class_1923(wrappedPos.getX(), wrappedPos.getZ());
   }

   public static DhChunkPos convert(class_1923 mcPos) {
      return new DhChunkPos(mcPos.field_9181, mcPos.field_9180);
   }
}
