package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.ModInfo;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;

public class RenderDataPointUtil {
   public static final boolean RUN_VALIDATION = ModInfo.IS_DEV_BUILD;
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final int EMPTY_DATA = 0;
   public static final int MAX_WORLD_Y_SIZE = 6095;
   public static final int ALPHA_DOWNSIZE_SHIFT = 4;
   public static final int IRIS_BLOCK_MATERIAL_ID_SHIFT = 60;
   public static final int COLOR_SHIFT = 32;
   public static final int BLUE_SHIFT = 32;
   public static final int GREEN_SHIFT = 40;
   public static final int RED_SHIFT = 48;
   public static final int ALPHA_SHIFT = 56;
   public static final int HEIGHT_SHIFT = 20;
   public static final int DEPTH_SHIFT = 8;
   public static final int BLOCK_LIGHT_SHIFT = 4;
   public static final int SKY_LIGHT_SHIFT = 0;
   public static final long ALPHA_MASK = 15L;
   public static final long RED_MASK = 255L;
   public static final long GREEN_MASK = 255L;
   public static final long BLUE_MASK = 255L;
   public static final long COLOR_MASK = 16777215L;
   public static final long HEIGHT_MASK = 4095L;
   public static final long DEPTH_MASK = 4095L;
   public static final long HEIGHT_DEPTH_MASK = 16777215L;
   public static final long BLOCK_LIGHT_MASK = 15L;
   public static final long SKY_LIGHT_MASK = 15L;
   public static final long IRIS_BLOCK_MATERIAL_ID_MASK = 15L;
   public static final long COMPARE_SHIFT = 7L;
   public static final long HEIGHT_SHIFTED_MASK = 4293918720L;
   public static final long DEPTH_SHIFTED_MASK = 1048320L;
   public static final long VOID_SETTER = 4294967040L;

   public static long createDataPoint(int height, int depth, int color, int lightSky, int lightBlock, int irisBlockMaterialId) {
      return createDataPoint(
         ColorUtil.getAlpha(color),
         ColorUtil.getRed(color),
         ColorUtil.getGreen(color),
         ColorUtil.getBlue(color),
         height,
         depth,
         lightSky,
         lightBlock,
         irisBlockMaterialId
      );
   }

   public static long createDataPoint(int alpha, int red, int green, int blue, int height, int depth, int lightSky, int lightBlock, int irisBlockMaterialId) {
      if (RUN_VALIDATION) {
         if (height < 0 || height >= 6095) {
            LodUtil.assertNotReach("Trying to create datapoint with height[" + height + "] out of range!");
         }

         if (depth < 0 || depth >= 6095) {
            LodUtil.assertNotReach("Trying to create datapoint with depth[" + depth + "] out of range!");
         }

         if (lightSky < 0 || lightSky >= 16) {
            LodUtil.assertNotReach("Trying to create datapoint with lightSky[" + lightSky + "] out of range!");
         }

         if (lightBlock < 0 || lightBlock >= 16) {
            LodUtil.assertNotReach("Trying to create datapoint with lightBlock[" + lightBlock + "] out of range!");
         }

         if (irisBlockMaterialId < 0 || irisBlockMaterialId >= 256) {
            LodUtil.assertNotReach("Trying to create datapoint with irisBlockMaterialId[" + irisBlockMaterialId + "] out of range!");
         }

         if (alpha < 0 || alpha >= 256) {
            LodUtil.assertNotReach("Trying to create datapoint with alpha[" + alpha + "] out of range!");
         }

         if (red < 0 || red >= 256) {
            LodUtil.assertNotReach("Trying to create datapoint with red[" + red + "] out of range!");
         }

         if (green < 0 || green >= 256) {
            LodUtil.assertNotReach("Trying to create datapoint with green[" + green + "] out of range!");
         }

         if (blue < 0 || blue >= 256) {
            LodUtil.assertNotReach("Trying to create datapoint with blue[" + blue + "] out of range!");
         }

         if (depth > height) {
            LodUtil.assertNotReach("Trying to create datapoint with depth[" + depth + "] greater than height[" + height + "]!");
         }
      }

      return (long)(alpha >>> 4) << 56
         | (red & 255L) << 48
         | (green & 255L) << 40
         | (blue & 255L) << 32
         | (height & 4095L) << 20
         | (depth & 4095L) << 8
         | (lightBlock & 15L) << 4
         | (lightSky & 15L) << 0
         | (irisBlockMaterialId & 15L) << 60;
   }

   public static long shiftHeightAndDepth(long dataPoint, short offset) {
      long height = dataPoint + ((long)offset << 20) & 4293918720L;
      long depth = dataPoint + (offset << 8) & 1048320L;
      return dataPoint & -4294967041L | height | depth;
   }

   public static short getYMax(long dataPoint) {
      return (short)(dataPoint >>> 20 & 4095L);
   }

   public static short getYMin(long dataPoint) {
      return (short)(dataPoint >>> 8 & 4095L);
   }

   public static long setYMin(long dataPoint, int depth) {
      return dataPoint & -1048321L | (depth & 4095L) << 8;
   }

   public static short getAlpha(long dataPoint) {
      return (short)((dataPoint >>> 56 & 15L) << 4 | 15L);
   }

   public static short getRed(long dataPoint) {
      return (short)(dataPoint >>> 48 & 255L);
   }

   public static short getGreen(long dataPoint) {
      return (short)(dataPoint >>> 40 & 255L);
   }

   public static short getBlue(long dataPoint) {
      return (short)(dataPoint >>> 32 & 255L);
   }

   public static byte getLightSky(long dataPoint) {
      return (byte)(dataPoint >>> 0 & 15L);
   }

   public static byte getLightBlock(long dataPoint) {
      return (byte)(dataPoint >>> 4 & 15L);
   }

   public static byte getBlockMaterialId(long dataPoint) {
      return (byte)(dataPoint >>> 60 & 15L);
   }

   public static boolean hasZeroHeight(long dataPoint) {
      return (dataPoint >>> 8 & 16777215L) == 0L;
   }

   public static boolean doesDataPointExist(long dataPoint) {
      return dataPoint != 0L;
   }

   public static int getColor(long dataPoint) {
      long alpha = getAlpha(dataPoint);
      return (int)(dataPoint >>> 32 & 16777215L | alpha << 24);
   }

   public static int compareDatapointPriority(long dataA, long dataB) {
      return (int)((dataA >> 7) - (dataB >> 7));
   }

   public static String toString(long dataPoint) {
      if (!doesDataPointExist(dataPoint)) {
         return "null";
      } else {
         return hasZeroHeight(dataPoint)
            ? "void"
            : "Y+:"
               + getYMax(dataPoint)
               + " Y-:"
               + getYMin(dataPoint)
               + " argb:"
               + getAlpha(dataPoint)
               + ","
               + getRed(dataPoint)
               + ","
               + getGreen(dataPoint)
               + ","
               + getBlue(dataPoint)
               + " BL:"
               + getLightBlock(dataPoint)
               + " SL:"
               + getLightSky(dataPoint)
               + " MAT:"
               + getBlockMaterialId(dataPoint)
               + "["
               + EDhApiBlockMaterial.getFromIndex(getBlockMaterialId(dataPoint))
               + "]";
      }
   }
}
