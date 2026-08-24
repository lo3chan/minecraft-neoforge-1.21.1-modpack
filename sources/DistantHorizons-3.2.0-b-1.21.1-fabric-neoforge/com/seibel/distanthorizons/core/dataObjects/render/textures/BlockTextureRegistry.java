package com.seibel.distanthorizons.core.dataObjects.render.textures;

import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateFaceTextureProvider;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

public class BlockTextureRegistry {
   private static final IBlockStateFaceTextureProvider TEXTURE_PROVIDER = SingletonInjector.INSTANCE.get(IBlockStateFaceTextureProvider.class);
   public static final BlockTextureRegistry INSTANCE = new BlockTextureRegistry();
   public static final int TILE_HEIGHT_AND_WIDTH = 16;
   public static final int TILE_BYTE_COUNT = 1024;
   public static final int MAX_TILE_COUNT = 65536;
   public static final short UNTEXTURED_ID = 0;
   private final ArrayList<byte[]> tilePixelsById = new ArrayList<>();
   private final HashMap<BlockTextureRegistry.TileKey, Short> tileIdByContent = new HashMap<>();
   private int firstTileIdPendingUpload = 0;
   private final ArrayList<short[]> faceTileIdsBySetId = new ArrayList<>();
   private final ConcurrentHashMap<IBlockStateWrapper, Short> setIdByBlockStateWrapper = new ConcurrentHashMap<>();

   private BlockTextureRegistry() {
      byte[] flatPixels = new byte[1024];

      for (int i = 0; i < 1024; i += 4) {
         flatPixels[i] = -128;
         flatPixels[i + 1] = -128;
         flatPixels[i + 2] = -128;
         flatPixels[i + 3] = -1;
      }

      this.tilePixelsById.add(flatPixels);
      this.faceTileIdsBySetId.add(new short[6]);
   }

   public short getOrRegisterBlockStateSetId(IBlockStateWrapper blockState) {
      Short setId = this.setIdByBlockStateWrapper.get(blockState);
      if (setId == null) {
         setId = this.registerBlockState(blockState);
      }

      return setId;
   }

   @Nullable
   public synchronized short[] getFaceTileIds(int setId) {
      return setId > 0 && setId < this.faceTileIdsBySetId.size() ? this.faceTileIdsBySetId.get(setId) : null;
   }

   private short registerBlockState(IBlockStateWrapper blockState) {
      short[] faceTileIds = new short[6];
      boolean anyFaceTextured = false;

      for (EDhDirection direction : EDhDirection.ALL) {
         BlockFaceTexture faceTexture = TEXTURE_PROVIDER.getFaceTexture(blockState, direction);
         short tileId = faceTexture != null ? this.getOrCreateTileId(faceTexture) : 0;
         if (tileId == 0 && direction != EDhDirection.UP && direction != EDhDirection.DOWN) {
            faceTexture = TEXTURE_PROVIDER.getFaceTexture(blockState, EDhDirection.UP);
            tileId = faceTexture != null ? this.getOrCreateTileId(faceTexture) : 0;
         }

         faceTileIds[direction.faceIndex] = tileId;
         anyFaceTextured |= tileId != 0;
      }

      short textureId;
      if (!anyFaceTextured) {
         textureId = 0;
      } else {
         synchronized (this) {
            if (this.faceTileIdsBySetId.size() >= 65536) {
               textureId = 0;
            } else {
               textureId = (short)this.faceTileIdsBySetId.size();
               this.faceTileIdsBySetId.add(faceTileIds);
            }
         }
      }

      Short existingSetId = this.setIdByBlockStateWrapper.putIfAbsent(blockState, textureId);
      return existingSetId != null ? existingSetId : textureId;
   }

   private synchronized short getOrCreateTileId(BlockFaceTexture faceTexture) {
      byte[] ratioPixels = convertColorsToDifferenceRatios(faceTexture);
      if (ratioPixels == null) {
         return 0;
      } else {
         BlockTextureRegistry.TileKey key = new BlockTextureRegistry.TileKey(ratioPixels);
         Short existingId = this.tileIdByContent.get(key);
         if (existingId != null) {
            return existingId;
         } else if (this.tilePixelsById.size() >= 65536) {
            return 0;
         } else {
            short newId = (short)this.tilePixelsById.size();
            this.tilePixelsById.add(ratioPixels);
            this.tileIdByContent.put(key, newId);
            return newId;
         }
      }
   }

   private static byte[] convertColorsToDifferenceRatios(BlockFaceTexture faceTexture) {
      int[] argbPixels = faceTexture.argbPixels;
      long redSum = 0L;
      long greenSum = 0L;
      long blueSum = 0L;
      int visibleCount = 0;

      for (int i = 0; i < argbPixels.length; i++) {
         if (ColorUtil.getAlpha(argbPixels[i]) != 0) {
            redSum += ColorUtil.getRed(argbPixels[i]);
            greenSum += ColorUtil.getGreen(argbPixels[i]);
            blueSum += ColorUtil.getBlue(argbPixels[i]);
            visibleCount++;
         }
      }

      if (visibleCount == 0) {
         return null;
      } else {
         float averageRed = Math.max((float)redSum / visibleCount, 1.0F);
         float averageGreen = Math.max((float)greenSum / visibleCount, 1.0F);
         float averageBlue = Math.max((float)blueSum / visibleCount, 1.0F);
         byte[] uploadPixels = new byte[1024];
         boolean anyPixelDiffersFromAverage = false;

         for (int v = 0; v < 16; v++) {
            for (int u = 0; u < 16; u++) {
               int sourceIndex = v * faceTexture.height / 16 * faceTexture.width + u * faceTexture.width / 16;
               int argb = argbPixels[sourceIndex];
               byte red;
               byte green;
               byte blue;
               byte alpha;
               if (faceTexture.uploadAsColorRatio) {
                  red = encodeRatio(ColorUtil.getRed(argb), averageRed);
                  green = encodeRatio(ColorUtil.getGreen(argb), averageGreen);
                  blue = encodeRatio(ColorUtil.getBlue(argb), averageBlue);
                  alpha = (byte)ColorUtil.getAlpha(argb);
               } else {
                  red = (byte)ColorUtil.getRed(argb);
                  green = (byte)ColorUtil.getGreen(argb);
                  blue = (byte)ColorUtil.getBlue(argb);
                  alpha = (byte)ColorUtil.getAlpha(argb);
               }

               int outIndex = (v * 16 + u) * 4;
               uploadPixels[outIndex] = red;
               uploadPixels[outIndex + 1] = green;
               uploadPixels[outIndex + 2] = blue;
               uploadPixels[outIndex + 3] = alpha;
               if (alpha != 0) {
                  byte averageGray = -128;
                  anyPixelDiffersFromAverage |= red != averageGray || green != averageGray || blue != averageGray;
               }
            }
         }

         return anyPixelDiffersFromAverage ? uploadPixels : null;
      }
   }

   private static byte encodeRatio(int channel, float average) {
      int encoded = Math.round(channel / average * 127.5F);
      return (byte)Math.min(encoded, 255);
   }

   public synchronized BlockTextureRegistry.PendingTiles getAndClearPendingUploadTiles() {
      int firstId = this.firstTileIdPendingUpload;
      int tileCount = this.tilePixelsById.size() - firstId;
      if (tileCount <= 0) {
         return null;
      } else {
         byte[][] pixelArrays = new byte[tileCount][];

         for (int i = 0; i < tileCount; i++) {
            pixelArrays[i] = this.tilePixelsById.get(firstId + i);
         }

         this.firstTileIdPendingUpload = this.tilePixelsById.size();
         return new BlockTextureRegistry.PendingTiles(firstId, pixelArrays);
      }
   }

   public synchronized int getTileCount() {
      return this.tilePixelsById.size();
   }

   public synchronized void resetPendingUploads() {
      this.firstTileIdPendingUpload = 0;
   }

   public synchronized void clear() {
      this.setIdByBlockStateWrapper.clear();
      short[] flatSet = this.faceTileIdsBySetId.get(0);
      this.faceTileIdsBySetId.clear();
      this.faceTileIdsBySetId.add(flatSet);
      this.tileIdByContent.clear();
      byte[] flatTile = this.tilePixelsById.get(0);
      this.tilePixelsById.clear();
      this.tilePixelsById.add(flatTile);
      this.firstTileIdPendingUpload = 0;
      IBlockStateFaceTextureProvider textureProvider = SingletonInjector.INSTANCE.get(IBlockStateFaceTextureProvider.class);
      if (textureProvider != null) {
         textureProvider.clear();
      }
   }

   public static class PendingTiles {
      public final int firstTileId;
      public final byte[][] tilePixels;

      public PendingTiles(int firstTileId, byte[][] tilePixels) {
         this.firstTileId = firstTileId;
         this.tilePixels = tilePixels;
      }
   }

   private static class TileKey {
      private final byte[] pixels;
      private final int hash;

      public TileKey(byte[] pixels) {
         this.pixels = pixels;
         this.hash = Arrays.hashCode(pixels);
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else {
            return !(obj instanceof BlockTextureRegistry.TileKey) ? false : Arrays.equals(this.pixels, ((BlockTextureRegistry.TileKey)obj).pixels);
         }
      }
   }
}
