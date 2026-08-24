package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;

public class LodUtil {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final int[] DEBUG_DETAIL_LEVEL_COLORS = new int[]{
      ColorUtil.rgbToInt(255, 0, 0),
      ColorUtil.rgbToInt(255, 127, 0),
      ColorUtil.rgbToInt(255, 255, 0),
      ColorUtil.rgbToInt(127, 255, 0),
      ColorUtil.rgbToInt(0, 255, 0),
      ColorUtil.rgbToInt(0, 255, 127),
      ColorUtil.rgbToInt(0, 255, 255),
      ColorUtil.rgbToInt(0, 127, 255),
      ColorUtil.rgbToInt(0, 0, 255),
      ColorUtil.rgbToInt(127, 0, 255),
      ColorUtil.rgbToInt(255, 0, 255),
      ColorUtil.rgbToInt(255, 127, 255),
      ColorUtil.rgbToInt(255, 255, 255)
   };
   public static final byte REGION_DETAIL_LEVEL = 9;
   public static final byte CHUNK_DETAIL_LEVEL = 4;
   public static final byte BLOCK_DETAIL_LEVEL = 0;
   public static final short REGION_WIDTH = 512;
   public static final short CHUNK_WIDTH = 16;
   public static final int REGION_WIDTH_IN_CHUNKS = 32;
   public static final byte MAX_MC_LIGHT = 15;
   public static final byte MIN_MC_LIGHT = 0;
   public static final int BLOCK_FULLY_TRANSPARENT = 0;
   public static final int BLOCK_FULLY_OPAQUE = 16;
   public static final double WALKING_SPEED_IN_BLOCKS_PER_SEC = 4.1;
   public static final double SPRINTING_SPEED_IN_BLOCKS_PER_SEC = 7.1;
   public static final double ROCKET_ELYTRA_SPEED_IN_BLOCKS_PER_SEC = 30.0;
   public static final double MAX_SPECTATOR_SPEED_IN_BLOCKS_PER_SEC = 100.0;
   public static final String INVALID_FILE_CHARACTERS_REGEX = "[\\\\/:*?\"<>|]";
   public static final int MAX_ALLOCATABLE_DIRECT_MEMORY = 67108864;

   public static int getChunkPosFromDouble(double value) {
      return (int)Math.floor(value / 16.0);
   }

   public static float getSubChunkPosFromDouble(double value) {
      double chunkPos = Math.floor(value / 16.0);
      return (float)(value - chunkPos * 16.0);
   }

   public static boolean checkRamUsage(double minFreeMemoryPercent, int minFreeMemoryMB) {
      long freeMem = Runtime.getRuntime().freeMemory() + Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory();
      if (freeMem < minFreeMemoryMB * 1024L * 1024L) {
         return false;
      } else {
         long maxMem = Runtime.getRuntime().maxMemory();
         return !((double)freeMem / maxMem < minFreeMemoryPercent);
      }
   }

   public static void assertTrue(boolean condition) {
      if (!condition) {
         throw new LodUtil.AssertFailureException("Assertion failed");
      }
   }

   public static void assertTrue(boolean condition, String message) {
      if (!condition) {
         throw new LodUtil.AssertFailureException("Assertion failed:\n " + message);
      }
   }

   public static void assertNotReach(String message) {
      throw new LodUtil.AssertFailureException("Assert Not Reach failed:\n " + message);
   }

   public static class AssertFailureException extends RuntimeException {
      public AssertFailureException(String message) {
         super(message);
      }
   }
}
