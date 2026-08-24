package com.seibel.distanthorizons.common.wrappers.chunk;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.misc.MutableBlockPosWrapper_fabric;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.ChunkLightStorage;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IMutableBlockPosWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2826;
import net.minecraft.class_2839;
import net.minecraft.class_5742;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_2902.class_2903;

public class ChunkWrapper_fabric implements IChunkWrapper {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final ThreadLocal<class_2339> MUTABLE_BLOCK_POS_REF = ThreadLocal.withInitial(() -> new class_2339());
   private static final ThreadLocal<MutableBlockPosWrapper_fabric> MUTABLE_BLOCK_POS_WRAPPER_REF = ThreadLocal.withInitial(
      () -> new MutableBlockPosWrapper_fabric()
   );
   public static final Set<String> LOGGED_BLOCK_GET_ERRORS = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private static boolean heightmapThreadWarningLogged = false;
   private final class_2791 chunk;
   private final DhChunkPos chunkPos;
   private final ILevelWrapper wrappedLevel;
   private boolean isDhBlockLightCorrect = false;
   private boolean isDhSkyLightCorrect = false;
   private ChunkLightStorage blockLightStorage;
   private ChunkLightStorage skyLightStorage;
   private ArrayList<DhBlockPos> blockLightPosList = null;
   private int minNonEmptyHeight = -2147483648;
   private int maxNonEmptyHeight = 2147483647;
   private int[][] solidHeightMap = null;
   private int[][] lightBlockingHeightMap = null;

   public ChunkWrapper_fabric(class_2791 chunk, ILevelWrapper wrappedLevel) {
      this.chunk = chunk;
      this.wrappedLevel = wrappedLevel;
      this.chunkPos = new DhChunkPos(chunk.method_12004().field_9181, chunk.method_12004().field_9180);
   }

   public ChunkWrapper_fabric copy() {
      return new ChunkWrapper_fabric(this.chunk, this.wrappedLevel);
   }

   public ChunkWrapper_fabric copyWithLevel(ILevelWrapper levelWrapper) {
      return new ChunkWrapper_fabric(this.chunk, levelWrapper);
   }

   @Override
   public int getHeight() {
      return getHeight(this.chunk);
   }

   public static int getHeight(class_2791 chunk) {
      return chunk.method_31605();
   }

   @Override
   public int getInclusiveMinBuildHeight() {
      return getInclusiveMinBuildHeight(this.chunk);
   }

   public static int getInclusiveMinBuildHeight(class_2791 chunk) {
      return chunk.method_31607();
   }

   @Override
   public int getExclusiveMaxBuildHeight() {
      return getExclusiveMaxBuildHeight(this.chunk);
   }

   public static int getExclusiveMaxBuildHeight(class_2791 chunk) {
      return chunk.method_31600();
   }

   @Override
   public int getMinNonEmptyHeight() {
      if (this.minNonEmptyHeight != -2147483648) {
         return this.minNonEmptyHeight;
      } else {
         this.minNonEmptyHeight = this.getInclusiveMinBuildHeight();
         class_2826[] sections = this.chunk.method_12006();

         for (int index = 0; index < sections.length; index++) {
            if (sections[index] != null && !isChunkSectionEmpty(sections[index])) {
               this.minNonEmptyHeight = this.getChunkSectionMinHeight(index);
               break;
            }
         }

         return this.minNonEmptyHeight;
      }
   }

   @Override
   public int getMaxNonEmptyHeight() {
      if (this.maxNonEmptyHeight != 2147483647) {
         return this.maxNonEmptyHeight;
      } else {
         this.maxNonEmptyHeight = this.getExclusiveMaxBuildHeight();
         class_2826[] sections = this.chunk.method_12006();

         for (int index = sections.length - 1; index >= 0; index--) {
            this.maxNonEmptyHeight = this.getChunkSectionMinHeight(index) + 16;
            if (sections[index] != null && !isChunkSectionEmpty(sections[index])) {
               break;
            }
         }

         return this.maxNonEmptyHeight;
      }
   }

   private static boolean isChunkSectionEmpty(class_2826 section) {
      return section.method_38292();
   }

   private int getChunkSectionMinHeight(int index) {
      return index * 16 + this.getInclusiveMinBuildHeight();
   }

   @Override
   public void createDhHeightMaps() {
      if (heightmapThreadWarningLogged && !DhApi.isDhThread()) {
         heightmapThreadWarningLogged = true;
         LOGGER.warn("ChunkWrapper Height maps created on non-DH thread [" + Thread.currentThread().getName() + "]. This may cause stuttering.");
      }

      this.solidHeightMap = new int[16][16];
      this.lightBlockingHeightMap = new int[16][16];

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            int minInclusiveBuildHeight = this.getMinNonEmptyHeight();
            int solidHeight = minInclusiveBuildHeight;
            int lightBlockingHeight = minInclusiveBuildHeight;
            int y = this.getMaxNonEmptyHeight();

            for (IBlockStateWrapper block = this.getBlockState(x, y, z);
               y > minInclusiveBuildHeight && (solidHeight == minInclusiveBuildHeight || lightBlockingHeight == minInclusiveBuildHeight);
               block = this.getBlockState(x, --y, z)
            ) {
               if (solidHeight == minInclusiveBuildHeight && block.isSolid()) {
                  solidHeight = y;
               }

               if (lightBlockingHeight == minInclusiveBuildHeight && block.getOpacity() != 0) {
                  lightBlockingHeight = y;
               }
            }

            this.solidHeightMap[x][z] = solidHeight;
            this.lightBlockingHeightMap[x][z] = lightBlockingHeight;
         }
      }
   }

   @Override
   public int getSolidHeightMapValue(int xRel, int zRel) {
      this.throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(xRel, zRel);
      return this.solidHeightMap == null ? this.chunk.method_12032(class_2903.field_13202).method_12603(xRel, zRel) : this.solidHeightMap[xRel][zRel];
   }

   @Override
   public int getLightBlockingHeightMapValue(int xRel, int zRel) {
      this.throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(xRel, zRel);
      return this.lightBlockingHeightMap == null
         ? this.chunk.method_12032(class_2903.field_13197).method_12603(xRel, zRel)
         : this.lightBlockingHeightMap[xRel][zRel];
   }

   @Override
   public IBiomeWrapper getBiome(int relX, int relY, int relZ) {
      return BiomeWrapper_fabric.getBiomeWrapper(
         this.chunk.method_16359(class_5742.method_33100(relX), class_5742.method_33100(relY), class_5742.method_33100(relZ)), this.wrappedLevel
      );
   }

   @Override
   public IBlockStateWrapper getBlockState(int relX, int relY, int relZ) {
      this.throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(relX, relY, relZ);
      class_2339 blockPos = MUTABLE_BLOCK_POS_REF.get();
      blockPos.method_33097(relX);
      blockPos.method_33098(relY);
      blockPos.method_33099(relZ);

      try {
         return BlockStateWrapper_fabric.fromBlockState(this.chunk.method_8320(blockPos), this.wrappedLevel);
      } catch (Exception var6) {
         if (LOGGED_BLOCK_GET_ERRORS.add(var6.getMessage())) {
            LOGGER.warn(
               "Failed to get block from chunk ["
                  + this.chunkPos
                  + "] at relative block pos ["
                  + relX
                  + ","
                  + relY
                  + ","
                  + relZ
                  + "], air will be used instead. This error message will only be logged once. error: ["
                  + var6.getMessage()
                  + "].",
               var6
            );
         }

         return BlockStateWrapper_fabric.AIR;
      }
   }

   @Override
   public IBlockStateWrapper getBlockState(int relX, int relY, int relZ, IMutableBlockPosWrapper mcBlockPos, IBlockStateWrapper guess) {
      this.throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(relX, relY, relZ);
      class_2339 pos = (class_2339)mcBlockPos.getWrappedMcObject();
      pos.method_33097(relX);
      pos.method_33098(relY);
      pos.method_33099(relZ);

      try {
         return BlockStateWrapper_fabric.fromBlockState(this.chunk.method_8320(pos), this.wrappedLevel, guess);
      } catch (Exception var8) {
         if (LOGGED_BLOCK_GET_ERRORS.add(var8.getMessage())) {
            LOGGER.warn(
               "Failed to get block from chunk ["
                  + this.chunkPos
                  + "] at relative block pos ["
                  + relX
                  + ","
                  + relY
                  + ","
                  + relZ
                  + "], air will be used instead. This error message will only be logged once. error: ["
                  + var8.getMessage()
                  + "].",
               var8
            );
         }

         return BlockStateWrapper_fabric.AIR;
      }
   }

   @Override
   public IMutableBlockPosWrapper getMutableBlockPosWrapper() {
      return MUTABLE_BLOCK_POS_WRAPPER_REF.get();
   }

   @Override
   public DhChunkPos getChunkPos() {
      return this.chunkPos;
   }

   public class_2791 getChunk() {
      return this.chunk;
   }

   public void trySetStatus(class_2806 status) {
      trySetStatus(this.getChunk(), status);
   }

   public static void trySetStatus(class_2791 chunk, class_2806 status) {
      if (chunk instanceof class_2839) {
         ((class_2839)chunk).method_12308(status);
      }
   }

   public class_2806 getStatus() {
      return getStatus(this.getChunk());
   }

   public static class_2806 getStatus(class_2791 chunk) {
      return chunk.method_12009();
   }

   @Override
   public int getMaxBlockX() {
      return this.chunk.method_12004().method_8327();
   }

   @Override
   public int getMaxBlockZ() {
      return this.chunk.method_12004().method_8329();
   }

   @Override
   public int getMinBlockX() {
      return this.chunk.method_12004().method_8326();
   }

   @Override
   public int getMinBlockZ() {
      return this.chunk.method_12004().method_8328();
   }

   @Override
   public void setIsDhSkyLightCorrect(boolean isDhLightCorrect) {
      this.isDhSkyLightCorrect = isDhLightCorrect;
   }

   @Override
   public void setIsDhBlockLightCorrect(boolean isDhLightCorrect) {
      this.isDhBlockLightCorrect = isDhLightCorrect;
   }

   @Override
   public boolean isDhBlockLightingCorrect() {
      return this.isDhBlockLightCorrect;
   }

   @Override
   public boolean isDhSkyLightCorrect() {
      return this.isDhSkyLightCorrect;
   }

   @Override
   public int getDhBlockLight(int relX, int y, int relZ) {
      this.throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(relX, y, relZ);
      return this.getBlockLightStorage().get(relX, y, relZ);
   }

   @Override
   public void setDhBlockLight(int relX, int y, int relZ, int lightValue) {
      this.throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(relX, y, relZ);
      this.getBlockLightStorage().set(relX, y, relZ, lightValue);
   }

   private ChunkLightStorage getBlockLightStorage() {
      if (this.blockLightStorage == null) {
         this.blockLightStorage = ChunkLightStorage.createBlockLightStorage(this);
      }

      return this.blockLightStorage;
   }

   public void setBlockLightStorage(ChunkLightStorage lightStorage) {
      this.blockLightStorage = lightStorage;
   }

   @Override
   public void clearDhBlockLighting() {
      this.getBlockLightStorage().clear();
   }

   @Override
   public int getDhSkyLight(int relX, int y, int relZ) {
      this.throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(relX, y, relZ);
      return this.getSkyLightStorage().get(relX, y, relZ);
   }

   @Override
   public void setDhSkyLight(int relX, int y, int relZ, int lightValue) {
      this.throwIndexOutOfBoundsIfRelativePosOutsideChunkBounds(relX, y, relZ);
      this.getSkyLightStorage().set(relX, y, relZ, lightValue);
   }

   @Override
   public void clearDhSkyLighting() {
      this.getSkyLightStorage().clear();
   }

   private ChunkLightStorage getSkyLightStorage() {
      if (this.skyLightStorage == null) {
         this.skyLightStorage = ChunkLightStorage.createSkyLightStorage(this);
      }

      return this.skyLightStorage;
   }

   public void setSkyLightStorage(ChunkLightStorage lightStorage) {
      this.skyLightStorage = lightStorage;
   }

   @Override
   public synchronized ArrayList<DhBlockPos> getWorldBlockLightPosList() {
      if (this.blockLightPosList == null) {
         this.blockLightPosList = new ArrayList<>();
         this.chunk.method_51524((blockPos, blockState) -> {
            DhBlockPos pos = new DhBlockPos(blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260());
            this.blockLightPosList.add(pos);
         });
      }

      return this.blockLightPosList;
   }

   @Override
   public String toString() {
      return this.chunk.getClass().getSimpleName() + this.chunk.method_12004();
   }
}
