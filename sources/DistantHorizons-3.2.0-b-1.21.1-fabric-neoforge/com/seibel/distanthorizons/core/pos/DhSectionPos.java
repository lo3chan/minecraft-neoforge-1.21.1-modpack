package com.seibel.distanthorizons.core.pos;

import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.coreapi.util.BitShiftUtil;
import java.util.function.LongConsumer;

public class DhSectionPos {
   public static final byte SECTION_MINIMUM_DETAIL_LEVEL = 6;
   public static final byte SECTION_BLOCK_DETAIL_LEVEL = 6;
   public static final byte SECTION_CHUNK_DETAIL_LEVEL = 10;
   public static final byte SECTION_REGION_DETAIL_LEVEL = 15;
   public static final int DETAIL_LEVEL_WIDTH = 8;
   public static final int X_POS_WIDTH = 28;
   public static final int Z_POS_WIDTH = 28;
   public static final int X_POS_MISSING_WIDTH = 4;
   public static final int Z_POS_MISSING_WIDTH = 4;
   public static final int DETAIL_LEVEL_OFFSET = 0;
   public static final int POS_X_OFFSET = 8;
   public static final int POS_Z_OFFSET = 36;
   public static final long DETAIL_LEVEL_MASK = 127L;
   public static final int POS_X_MASK = (int)Math.pow(2.0, 28.0) - 1;
   public static final int POS_Z_MASK = (int)Math.pow(2.0, 28.0) - 1;

   private DhSectionPos() {
   }

   public static long encode(byte detailLevel, int x, int z) {
      long data = 0L;
      data |= detailLevel & 127L;
      data |= (long)(x & POS_X_MASK) << 8;
      return data | (long)(z & POS_Z_MASK) << 36;
   }

   public static long encodeContaining(byte outputSectionDetailLevel, DhBlockPos pos) {
      int sectionPosX = getXOrZSectionPosFromChunkOrBlockPos(pos.getX(), false);
      int sectionPosZ = getXOrZSectionPosFromChunkOrBlockPos(pos.getZ(), false);
      long blockPos = encode((byte)6, sectionPosX, sectionPosZ);
      return convertToDetailLevel(blockPos, outputSectionDetailLevel);
   }

   public static long encodeContaining(byte outputSectionDetailLevel, DhChunkPos pos) {
      int sectionPosX = getXOrZSectionPosFromChunkOrBlockPos(pos.getX(), true);
      int sectionPosZ = getXOrZSectionPosFromChunkOrBlockPos(pos.getZ(), true);
      long blockPos = encode((byte)6, sectionPosX, sectionPosZ);
      return convertToDetailLevel(blockPos, outputSectionDetailLevel);
   }

   private static int getXOrZSectionPosFromChunkOrBlockPos(int chunkXOrZPos, boolean isChunkPos) {
      int fullDataSourceWidth = isChunkPos ? 4 : 64;
      return chunkXOrZPos < 0 ? (chunkXOrZPos + 1) / fullDataSourceWidth - 1 : chunkXOrZPos / fullDataSourceWidth;
   }

   public static long convertToDetailLevel(long pos, byte newDetailLevel) {
      byte detailLevel = getDetailLevel(pos);
      int x = getX(pos);
      int z = getZ(pos);
      if (newDetailLevel >= detailLevel) {
         x = Math.floorDiv(x, BitShiftUtil.powerOfTwo(newDetailLevel - detailLevel));
         z = Math.floorDiv(z, BitShiftUtil.powerOfTwo(newDetailLevel - detailLevel));
      } else {
         x *= BitShiftUtil.powerOfTwo(detailLevel - newDetailLevel);
         z *= BitShiftUtil.powerOfTwo(detailLevel - newDetailLevel);
      }

      return encode(newDetailLevel, x, z);
   }

   public static byte getDetailLevel(long pos) {
      return (byte)(pos >> 0 & 127L);
   }

   public static int getX(long pos) {
      int x = (int)(pos >> 8 & POS_X_MASK);
      return x << 4 >> 4;
   }

   public static int getZ(long pos) {
      int Z = (int)(pos >> 36 & POS_Z_MASK);
      return Z << 4 >> 4;
   }

   public static int getMinCornerBlockX(long pos) {
      int halfBlockWidth = getDetailLevel(pos) != 1 ? getBlockWidth(pos) / 2 : 0;
      return getCenterBlockPosX(pos) - halfBlockWidth;
   }

   public static int getMinCornerBlockZ(long pos) {
      int halfBlockWidth = getDetailLevel(pos) != 1 ? getBlockWidth(pos) / 2 : 0;
      return getCenterBlockPosZ(pos) - halfBlockWidth;
   }

   public static int getWidthCountForLowerDetailedSection(long pos, byte returnDetailLevel) {
      byte detailLevel = getDetailLevel(pos);
      LodUtil.assertTrue(returnDetailLevel <= detailLevel, "returnDetailLevel must be less than sectionDetail");
      byte offset = (byte)(detailLevel - returnDetailLevel);
      return BitShiftUtil.powerOfTwo((int)offset);
   }

   public static int getChunkWidth(long pos) {
      return getBlockWidth(pos) / 16;
   }

   public static int getBlockWidth(long pos) {
      return getDetailLevelWidthInBlocks(getDetailLevel(pos));
   }

   public static int getDetailLevelWidthInBlocks(byte detailLevel) {
      return BitShiftUtil.powerOfTwo((int)detailLevel);
   }

   public static DhBlockPos2D getCenterBlockPos(long pos) {
      return new DhBlockPos2D(getCenterBlockPosX(pos), getCenterBlockPosZ(pos));
   }

   public static int getCenterBlockPosX(long pos) {
      return getCenterBlockPosXOrZ(pos, true);
   }

   public static int getCenterBlockPosZ(long pos) {
      return getCenterBlockPosXOrZ(pos, false);
   }

   private static int getCenterBlockPosXOrZ(long pos, boolean returnX) {
      byte detailLevel = getDetailLevel(pos);
      int x = getX(pos);
      int z = getZ(pos);
      int centerBlockPos = returnX ? x : z;
      if (detailLevel == 0) {
         return centerBlockPos;
      } else {
         int positionOffset = 0;
         if (detailLevel != 1) {
            positionOffset = BitShiftUtil.powerOfTwo(detailLevel - 1);
         }

         return centerBlockPos * BitShiftUtil.powerOfTwo((int)detailLevel) + positionOffset;
      }
   }

   public static int getManhattanBlockDistance(long pos, DhBlockPos2D blockPos) {
      return Math.abs(getCenterBlockPosX(pos) - blockPos.x) + Math.abs(getCenterBlockPosZ(pos) - blockPos.z);
   }

   public static int getChebyshevSignedBlockDistance(long pos, DhBlockPos blockPos) {
      return getChebyshevSignedBlockDistance(pos, blockPos.getX(), blockPos.getZ());
   }

   public static int getChebyshevSignedBlockDistance(long pos, DhBlockPos2D blockPos) {
      return getChebyshevSignedBlockDistance(pos, blockPos.x, blockPos.z);
   }

   public static int getChebyshevSignedBlockDistance(long pos, int blockPosX, int blockPosZ) {
      return Math.max(Math.abs(getCenterBlockPosX(pos) - blockPosX), Math.abs(getCenterBlockPosZ(pos) - blockPosZ)) - getBlockWidth(pos) / 2;
   }

   public static long getDhSectionRelativePositionForDetailLevel(long pos, byte outputDetailLevel) throws IllegalArgumentException {
      int xInputOriginal = getX(pos);
      int zInputOriginal = getZ(pos);
      byte detailLevelDifference = (byte)(outputDetailLevel - getDetailLevel(pos));
      if (outputDetailLevel < getDetailLevel(pos)) {
         throw new IllegalArgumentException(
            "The output Detail Level [" + outputDetailLevel + "] is less than the input pos's detail level [" + getDetailLevel(pos) + "]."
         );
      } else {
         int blockOffset = BitShiftUtil.powerOfTwo((int)detailLevelDifference) - 1;
         blockOffset = Math.max(1, blockOffset);
         int xInput = xInputOriginal + (xInputOriginal < 0 ? blockOffset : 0);
         int zInput = zInputOriginal + (zInputOriginal < 0 ? blockOffset : 0);
         int xRelativePos = xInput / BitShiftUtil.powerOfTwo((int)detailLevelDifference);
         int zRelativePos = zInput / BitShiftUtil.powerOfTwo((int)detailLevelDifference);
         int WIDTH_IN = 64;
         int WIDTH_EX = 63;
         xRelativePos = xInputOriginal >= 0 ? xRelativePos % 64 : 63 + xRelativePos % 64;
         zRelativePos = zInputOriginal >= 0 ? zRelativePos % 64 : 63 + zRelativePos % 64;
         return encode(outputDetailLevel, xRelativePos, zRelativePos);
      }
   }

   public static long getChildByIndex(long pos, int child0to3) throws IllegalArgumentException, IllegalStateException {
      byte detailLevel = getDetailLevel(pos);
      int x = getX(pos);
      int z = getZ(pos);
      if (child0to3 < 0 || child0to3 > 3) {
         throw new IllegalArgumentException("child0to3 must be between 0 and 3");
      } else if (detailLevel <= 0) {
         throw new IllegalStateException("section detail must be greater than 0");
      } else {
         return encode((byte)(detailLevel - 1), x * 2 + (child0to3 & 1), z * 2 + BitShiftUtil.half(child0to3 & 2));
      }
   }

   public static int getChildIndexOfParent(long pos) {
      return (getX(pos) & 1) + BitShiftUtil.square(getZ(pos) & 1);
   }

   public static long getParentPos(long pos) {
      return encode((byte)(getDetailLevel(pos) + 1), BitShiftUtil.half(getX(pos)), BitShiftUtil.half(getZ(pos)));
   }

   public static long getAdjacentPos(long pos, EDhDirection dir) throws IllegalArgumentException {
      if (dir != EDhDirection.UP && dir != EDhDirection.DOWN) {
         return encode(getDetailLevel(pos), getX(pos) + dir.normal.x, getZ(pos) + dir.normal.z);
      } else {
         throw new IllegalArgumentException("getAdjacentPos can't be UP or DOWN, direction given: [" + dir.name() + "].");
      }
   }

   public static boolean contains(long aPos, long bPos) {
      int aMinX = getMinCornerBlockX(aPos);
      int aMinZ = getMinCornerBlockZ(aPos);
      int bMinX = getMinCornerBlockX(bPos);
      int bMinZ = getMinCornerBlockZ(bPos);
      int aBlockWidth = getBlockWidth(aPos) - 1;
      int aMaxX = aMinX + aBlockWidth;
      int aMaxZ = aMinZ + aBlockWidth;
      return aMinX <= bMinX && bMinX <= aMaxX && aMinZ <= bMinZ && bMinZ <= aMaxZ;
   }

   public static boolean contains(long aPos, DhBlockPos blockPos) {
      int sectionMinX = getMinCornerBlockX(aPos);
      int sectionMinZ = getMinCornerBlockZ(aPos);
      int blockX = blockPos.getX();
      int blockZ = blockPos.getZ();
      int sectionBlockWidth = getBlockWidth(aPos) - 1;
      int sectionMaxX = sectionMinX + sectionBlockWidth;
      int sectionMaxZ = sectionMinZ + sectionBlockWidth;
      return sectionMinX <= blockX && blockX <= sectionMaxX && sectionMinZ <= blockZ && blockZ <= sectionMaxZ;
   }

   public static void forEachChild(long pos, LongConsumer callback) throws IllegalArgumentException, IllegalStateException {
      for (int i = 0; i < 4; i++) {
         callback.accept(getChildByIndex(pos, i));
      }
   }

   public static void forEachChildDownToDetailLevel(long pos, byte minSectionDetailLevel, DhSectionPos.ICancelablePrimitiveLongConsumer callback) throws IllegalArgumentException, IllegalStateException {
      boolean stop = callback.accept(pos);
      if (!stop && minSectionDetailLevel != getDetailLevel(pos)) {
         for (int i = 0; i < 4; i++) {
            forEachChildDownToDetailLevel(getChildByIndex(pos, i), minSectionDetailLevel, callback);
         }
      }
   }

   public static void forEachChildAtDetailLevel(long pos, byte sectionDetailLevel, LongConsumer callback) throws IllegalArgumentException, IllegalStateException {
      if (sectionDetailLevel == getDetailLevel(pos)) {
         callback.accept(pos);
      } else {
         for (int i = 0; i < 4; i++) {
            forEachChildAtDetailLevel(getChildByIndex(pos, i), sectionDetailLevel, callback);
         }
      }
   }

   public static void forEachPosUpToDetailLevel(long pos, byte maxSectionDetailLevel, LongConsumer callback) {
      callback.accept(pos);
      if (maxSectionDetailLevel != getDetailLevel(pos)) {
         forEachPosUpToDetailLevel(getParentPos(pos), maxSectionDetailLevel, callback);
      }
   }

   public static String toString(long pos) {
      return getDetailLevel(pos) + "*" + getX(pos) + "," + getZ(pos);
   }

   public static int hashCode(long pos) {
      return Long.hashCode(pos);
   }

   @FunctionalInterface
   public interface ICancelablePrimitiveLongConsumer {
      boolean accept(long l);
   }
}
