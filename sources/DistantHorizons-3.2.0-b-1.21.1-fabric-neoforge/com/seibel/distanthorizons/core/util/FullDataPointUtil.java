package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.coreapi.ModInfo;
import org.jetbrains.annotations.Contract;

public class FullDataPointUtil {
   public static final boolean RUN_VALIDATION = ModInfo.IS_DEV_BUILD;
   public static final int EMPTY_DATA_POINT = 0;
   public static final int ID_WIDTH = 32;
   public static final int HEIGHT_WIDTH = 12;
   public static final int MIN_Y_WIDTH = 12;
   public static final int SKY_LIGHT_WIDTH = 4;
   public static final int BLOCK_LIGHT_WIDTH = 4;
   public static final int ID_OFFSET = 0;
   public static final int HEIGHT_OFFSET = 32;
   public static final int MIN_Y_OFFSET = 44;
   public static final int SKY_LIGHT_OFFSET = 56;
   public static final int BLOCK_LIGHT_OFFSET = 60;
   public static final long ID_MASK = 2147483647L;
   public static final long INVERSE_ID_MASK = -2147483648L;
   public static final int HEIGHT_MASK = (int)Math.pow(2.0, 12.0) - 1;
   public static final int MIN_Y_MASK = (int)Math.pow(2.0, 12.0) - 1;
   public static final int SKY_LIGHT_MASK = (int)Math.pow(2.0, 4.0) - 1;
   public static final int BLOCK_LIGHT_MASK = (int)Math.pow(2.0, 4.0) - 1;

   public static long encode(int id, int height, int relMinY, byte blockLight, byte skyLight) throws DataCorruptedException {
      if (RUN_VALIDATION) {
         validateData(id, height, relMinY, blockLight, skyLight);
      }

      long data = 0L;
      data |= id & 2147483647L;
      data |= (long)(height & HEIGHT_MASK) << 32;
      data |= (long)(relMinY & MIN_Y_MASK) << 44;
      data |= (long)blockLight << 60;
      data |= (long)skyLight << 56;
      if (RUN_VALIDATION
         && (
            getId(data) != id
               || getHeight(data) != height
               || getBottomY(data) != relMinY
               || getBlockLight(data) != Byte.toUnsignedInt(blockLight)
               || getSkyLight(data) != Byte.toUnsignedInt(skyLight)
         )) {
         LodUtil.assertNotReach(
            "Trying to create datapoint with id["
               + id
               + "], height["
               + height
               + "], minY["
               + relMinY
               + "], blockLight["
               + blockLight
               + "], skyLight["
               + skyLight
               + "] but got id["
               + getId(data)
               + "], height["
               + getHeight(data)
               + "], minY["
               + getBottomY(data)
               + "], blockLight["
               + getBlockLight(data)
               + "], skyLight["
               + getSkyLight(data)
               + "]!"
         );
      }

      return data;
   }

   public static void validateDatapoint(long datapoint) throws DataCorruptedException {
      validateData(getId(datapoint), getHeight(datapoint), getBottomY(datapoint), (byte)getBlockLight(datapoint), (byte)getSkyLight(datapoint));
   }

   public static void validateData(int id, int height, int relMinY, byte blockLight, byte skyLight) throws DataCorruptedException {
      if (id < 0) {
         throw new DataCorruptedException("Full datapoint ID [" + relMinY + "] must be greater than zero.");
      } else if (relMinY < 0 || relMinY >= 6095) {
         throw new DataCorruptedException("Full datapoint relative min y [" + relMinY + "] must be in the range [0 - " + 6095 + "] (inclusive).");
      } else if (height > 0 && height < 6095) {
         if (relMinY + height > 6095) {
            throw new DataCorruptedException("Full datapoint y+depth [" + (relMinY + height) + "] is higher than the maximum world Y height [" + 6095 + "].");
         } else if (blockLight < 0 || blockLight > 15) {
            throw new DataCorruptedException("Full datapoint block light [" + blockLight + "] must be in the range [" + 0 + " - " + 15 + "] (inclusive).");
         } else if (skyLight < 0 || skyLight > 15) {
            throw new DataCorruptedException("Full datapoint sky light [" + skyLight + "] must be in the range [" + 0 + " - " + 15 + "] (inclusive).");
         }
      } else {
         throw new DataCorruptedException("Full datapoint height [" + height + "] must be in the range [1 - " + 6095 + "] (inclusive).");
      }
   }

   public static int getId(long data) {
      return (int)(data & 2147483647L);
   }

   public static int getHeight(long data) {
      return (int)(data >> 32 & HEIGHT_MASK);
   }

   public static int getBottomY(long data) {
      return (int)(data >> 44 & MIN_Y_MASK);
   }

   public static int getBlockLight(long data) {
      return (int)(data >> 60 & BLOCK_LIGHT_MASK);
   }

   public static int getSkyLight(long data) {
      return (int)(data >> 56 & SKY_LIGHT_MASK);
   }

   public static long setId(long data, int id) {
      return data & -2147483648L | (long)id << 0;
   }

   public static long setHeight(long data, int height) {
      return data & ~((long)HEIGHT_MASK << 32) | (long)height << 32;
   }

   public static long setBottomY(long data, int bottomY) {
      return data & ~((long)MIN_Y_MASK << 44) | (long)bottomY << 44;
   }

   public static long setBlockLight(long data, byte blockLight) {
      return data & ~((long)BLOCK_LIGHT_MASK << 60) | (long)blockLight << 60;
   }

   public static long setSkyLight(long data, int skyLight) {
      return data & ~((long)SKY_LIGHT_MASK << 56) | (long)skyLight << 56;
   }

   public static String toString(long data) {
      return "[ID:"
         + getId(data)
         + ",Y:"
         + getBottomY(data)
         + ",Height:"
         + getHeight(data)
         + ",BlockLight:"
         + getBlockLight(data)
         + ",SkyLight:"
         + getSkyLight(data)
         + "]";
   }

   @Contract(
      pure = true
   )
   public static long remap(int[] newIdByOldId, long data) throws IndexOutOfBoundsException {
      int currentId = getId(data);

      try {
         int newId = newIdByOldId[currentId];
         return setId(data, newId);
      } catch (IndexOutOfBoundsException var5) {
         throw new RuntimeException(var5);
      }
   }
}
