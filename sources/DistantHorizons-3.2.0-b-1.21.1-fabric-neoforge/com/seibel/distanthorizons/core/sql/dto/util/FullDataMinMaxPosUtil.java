package com.seibel.distanthorizons.core.sql.dto.util;

import com.seibel.distanthorizons.core.enums.EDhDirection;

public class FullDataMinMaxPosUtil {
   private static final int ADJ_POS_MASK = (int)Math.pow(2.0, 16.0) - 1;
   private static final int MIN_X_OFFSET = 0;
   private static final int MAX_X_OFFSET = 16;
   private static final int MIN_Z_OFFSET = 32;
   private static final int MAX_Z_OFFSET = 48;

   public static long getEncodedMinMaxPos(EDhDirection direction) {
      short minX;
      short maxX;
      short minZ;
      short maxZ;
      switch (direction) {
         case NORTH:
            minX = 0;
            maxX = 64;
            minZ = 0;
            maxZ = 1;
            break;
         case SOUTH:
            minX = 0;
            maxX = 64;
            minZ = 63;
            maxZ = 64;
            break;
         case EAST:
            minX = 63;
            maxX = 64;
            minZ = 0;
            maxZ = 64;
            break;
         case WEST:
            minX = 0;
            maxX = 1;
            minZ = 0;
            maxZ = 64;
            break;
         default:
            throw new IllegalArgumentException("Unsupported direction [" + direction + "].");
      }

      return encodeAdjMinMaxPos(minX, maxX, minZ, maxZ);
   }

   public static long encodeAdjMinMaxPos(short minX, short maxX, short minZ, short maxZ) {
      long data = 0L;
      data |= (long)minX << 0;
      data |= (long)maxX << 16;
      data |= (long)minZ << 32;
      return data | (long)maxZ << 48;
   }

   public static int getAdjMinX(long encodedMinMaxPos) {
      return (int)(encodedMinMaxPos >> 0 & ADJ_POS_MASK);
   }

   public static int getAdjMaxX(long encodedMinMaxPos) {
      return (int)(encodedMinMaxPos >> 16 & ADJ_POS_MASK);
   }

   public static int getAdjMinZ(long encodedMinMaxPos) {
      return (int)(encodedMinMaxPos >> 32 & ADJ_POS_MASK);
   }

   public static int getAdjMaxZ(long encodedMinMaxPos) {
      return (int)(encodedMinMaxPos >> 48 & ADJ_POS_MASK);
   }
}
