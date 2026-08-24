package com.seibel.distanthorizons.core.wrapperInterfaces.chunk;

import com.seibel.distanthorizons.core.generation.AdjacentChunkHolder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IMutableBlockPosWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.ModInfo;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface IChunkWrapper extends IBindable {
   boolean RUN_RELATIVE_POS_INDEX_VALIDATION = ModInfo.IS_DEV_BUILD;

   DhChunkPos getChunkPos();

   default int getHeight() {
      return this.getExclusiveMaxBuildHeight() - this.getInclusiveMinBuildHeight();
   }

   int getInclusiveMinBuildHeight();

   int getExclusiveMaxBuildHeight();

   int getMinNonEmptyHeight();

   int getMaxNonEmptyHeight();

   void createDhHeightMaps();

   int getSolidHeightMapValue(int i, int j);

   int getLightBlockingHeightMapValue(int i, int j);

   int getMaxBlockX();

   int getMaxBlockZ();

   int getMinBlockX();

   int getMinBlockZ();

   void setIsDhBlockLightCorrect(boolean bl);

   void setIsDhSkyLightCorrect(boolean bl);

   boolean isDhBlockLightingCorrect();

   boolean isDhSkyLightCorrect();

   int getDhSkyLight(int i, int j, int k);

   void setDhSkyLight(int i, int j, int k, int l);

   void clearDhSkyLighting();

   int getDhBlockLight(int i, int j, int k);

   void setDhBlockLight(int i, int j, int k, int l);

   void clearDhBlockLighting();

   ArrayList<DhBlockPos> getWorldBlockLightPosList();

   default boolean blockPosInsideChunk(DhBlockPos blockPos) {
      return this.blockPosInsideChunk(blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   default boolean blockPosInsideChunk(int x, int y, int z) {
      return x >= this.getMinBlockX()
         && x <= this.getMaxBlockX()
         && y >= this.getInclusiveMinBuildHeight()
         && y < this.getExclusiveMaxBuildHeight()
         && z >= this.getMinBlockZ()
         && z <= this.getMaxBlockZ();
   }

   default boolean blockPosInsideChunk(DhBlockPos2D blockPos) {
      return blockPos.x >= this.getMinBlockX() && blockPos.x <= this.getMaxBlockX() && blockPos.z >= this.getMinBlockZ() && blockPos.z <= this.getMaxBlockZ();
   }

   @Override
   String toString();

   default IBlockStateWrapper getBlockState(DhBlockPos pos) {
      return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
   }

   IBlockStateWrapper getBlockState(int i, int j, int k);

   default IBlockStateWrapper getBlockState(DhBlockPos pos, IMutableBlockPosWrapper mcBlockPos, IBlockStateWrapper guess) {
      return this.getBlockState(pos.getX(), pos.getY(), pos.getZ(), mcBlockPos, guess);
   }

   IBlockStateWrapper getBlockState(int i, int j, int k, IMutableBlockPosWrapper iMutableBlockPosWrapper, IBlockStateWrapper iBlockStateWrapper);

   IMutableBlockPosWrapper getMutableBlockPosWrapper();

   IBiomeWrapper getBiome(int i, int j, int k);

   default void throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(int x, int y, int z) throws IndexOutOfBoundsException {
      if (RUN_RELATIVE_POS_INDEX_VALIDATION) {
         int minHeight = this.getInclusiveMinBuildHeight();
         int maxHeight = this.getExclusiveMaxBuildHeight() + 1;
         if (x < 0 || x >= 16 || z < 0 || z >= 16 || y < minHeight || y > maxHeight) {
            String errorMessage = "Relative position ["
               + x
               + ","
               + y
               + ","
               + z
               + "] out of bounds. \nX/Z must be between 0 and 15 (inclusive) \nY must be between ["
               + minHeight
               + "] and ["
               + maxHeight
               + "] (inclusive).";
            throw new IndexOutOfBoundsException(errorMessage);
         }
      }
   }

   default void throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(int x, int z) throws IndexOutOfBoundsException {
      if (RUN_RELATIVE_POS_INDEX_VALIDATION) {
         int minHeight = this.getInclusiveMinBuildHeight();
         int maxHeight = this.getExclusiveMaxBuildHeight() + 1;
         if (x < 0 || x >= 16 || z < 0 || z >= 16) {
            String errorMessage = "Relative position [" + x + "," + z + "] out of bounds. \nX/Z must be between 0 and 15 (inclusive).";
            throw new IndexOutOfBoundsException(errorMessage);
         }
      }
   }

   default int relativeBlockPosToIndex(int xRel, int y, int zRel) {
      int yRel = y - this.getInclusiveMinBuildHeight();
      return zRel * 16 * this.getHeight() + yRel * 16 + xRel;
   }

   default DhBlockPos indexToRelativeBlockPos(int index) {
      int zRel = index / (16 * this.getHeight());
      index -= zRel * 16 * this.getHeight();
      int y = index / 16;
      int yRel = y + this.getInclusiveMinBuildHeight();
      int xRel = index % 16;
      return new DhBlockPos(xRel, yRel, zRel);
   }

   default int roughHashCode() {
      int hash = 31;
      int primeMultiplier = 227;

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            hash = hash * primeMultiplier + Integer.hashCode(this.getLightBlockingHeightMapValue(x, z));
         }
      }

      return hash;
   }

   default int getBlockBiomeHashCode() {
      int hash = 31;
      int primeBlockMultiplier = 227;
      int primeBiomeMultiplier = 701;
      int primeHeightMultiplier = 137;
      int minBuildHeight = this.getMinNonEmptyHeight();
      int maxBuildHeight = this.getMaxNonEmptyHeight();
      IMutableBlockPosWrapper mcBlockPos = this.getMutableBlockPosWrapper();
      IBlockStateWrapper previousBlockState = null;

      for (int x = 0; x < 16; x += 2) {
         for (int z = 0; z < 16; z += 2) {
            for (int y = minBuildHeight; y < maxBuildHeight; y += 2) {
               previousBlockState = this.getBlockState(x, y, z, mcBlockPos, previousBlockState);
               hash = hash * primeBlockMultiplier + previousBlockState.hashCode();
               hash = hash * primeBiomeMultiplier + this.getBiome(x, y, z).hashCode();
               hash = hash * primeHeightMultiplier + y;
            }
         }
      }

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            int lightBlockingY = this.getLightBlockingHeightMapValue(x, z);
            previousBlockState = this.getBlockState(x, lightBlockingY, z, mcBlockPos, previousBlockState);
            hash = hash * primeBlockMultiplier + previousBlockState.hashCode();
            hash = hash * primeBiomeMultiplier + this.getBiome(x, lightBlockingY, z).hashCode();
            hash = hash * primeHeightMultiplier + lightBlockingY;
            int solidY = this.getSolidHeightMapValue(x, z);
            if (solidY != lightBlockingY) {
               previousBlockState = this.getBlockState(x, lightBlockingY, z, mcBlockPos, previousBlockState);
               hash = hash * primeBlockMultiplier + previousBlockState.hashCode();
               hash = hash * primeBiomeMultiplier + this.getBiome(x, solidY, z).hashCode();
               hash = hash * primeHeightMultiplier + solidY;
            }
         }
      }

      DhBlockPosMutable relPos = new DhBlockPosMutable();
      ArrayList<DhBlockPos> lightPosList = this.getWorldBlockLightPosList();

      for (int i = 0; i < lightPosList.size(); i++) {
         DhBlockPos pos = lightPosList.get(i);
         pos.mutateToChunkRelativePos(relPos);
         hash = hash * primeBlockMultiplier + this.getBlockState(relPos.getX(), relPos.getY(), relPos.getZ()).hashCode();
         hash = hash * primeHeightMultiplier + relPos.getY();
      }

      return hash;
   }

   default List<BeaconBeamDTO> getAllActiveBeacons(ArrayList<IChunkWrapper> neighbourChunkList) {
      ArrayList<BeaconBeamDTO> beaconBeamList = new ArrayList<>();
      AdjacentChunkHolder adjacentChunkHolder = new AdjacentChunkHolder(this, neighbourChunkList);
      DhBlockPosMutable relPos = new DhBlockPosMutable();
      ArrayList<DhBlockPos> blockPosList = this.getWorldBlockLightPosList();

      for (int i = 0; i < blockPosList.size(); i++) {
         DhBlockPos pos = blockPosList.get(i);
         pos.mutateToChunkRelativePos(relPos);
         IBlockStateWrapper block = this.getBlockState(relPos);
         if (block.isBeaconBlock()) {
            Color beaconColor = getBeaconColor(pos, adjacentChunkHolder);
            if (beaconColor != null) {
               BeaconBeamDTO beam = new BeaconBeamDTO(blockPosList.get(i), beaconColor);
               beaconBeamList.add(beam);
            }
         }
      }

      return beaconBeamList;
   }

   @Nullable
   static Color getBeaconColor(DhBlockPos beaconPos, AdjacentChunkHolder chunkHolder) {
      DhBlockPos beaconRelPos = beaconPos.createChunkRelativePos();
      DhBlockPosMutable baseRelPos = new DhBlockPosMutable(0, beaconRelPos.getY() - 1, 0);

      for (int x = -1; x <= 1; x++) {
         for (int z = -1; z <= 1; z++) {
            baseRelPos.setX(beaconRelPos.getX() + x);
            baseRelPos.setZ(beaconRelPos.getZ() + z);
            baseRelPos.mutateToChunkRelativePos(baseRelPos);
            IChunkWrapper chunk = chunkHolder.getByBlockPos(beaconPos.getX() + x, beaconPos.getZ() + z);
            if (chunk != null) {
               IBlockStateWrapper block = chunk.getBlockState(baseRelPos.getX(), baseRelPos.getY(), baseRelPos.getZ());
               if (!block.isBeaconBaseBlock()) {
                  return null;
               }
            }
         }
      }

      int red = 0;
      int green = 0;
      int blue = 0;
      boolean beaconTintBlockFound = false;
      IChunkWrapper centerChunk = chunkHolder.getByBlockPos(beaconPos.getX(), beaconPos.getZ());
      int maxY = centerChunk.getMaxNonEmptyHeight();

      for (int y = beaconRelPos.getY() + 1; y <= maxY; y++) {
         IBlockStateWrapper block = centerChunk.getBlockState(beaconRelPos.getX(), y, beaconRelPos.getZ());
         if (!block.allowsBeaconBeamPassage()) {
            return null;
         }

         if (block.isBeaconTintBlock()) {
            red += block.getBeaconTintColor().getRed();
            green += block.getBeaconTintColor().getGreen();
            blue += block.getBeaconTintColor().getBlue();
            if (beaconTintBlockFound) {
               red /= 2;
               green /= 2;
               blue /= 2;
            }

            beaconTintBlockFound = true;
         }
      }

      return beaconTintBlockFound ? new Color(red, green, blue) : Color.WHITE;
   }

   IChunkWrapper copy();

   IChunkWrapper copyWithLevel(ILevelWrapper iLevelWrapper);
}
