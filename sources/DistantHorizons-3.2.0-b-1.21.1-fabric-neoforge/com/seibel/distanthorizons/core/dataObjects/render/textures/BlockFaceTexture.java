package com.seibel.distanthorizons.core.dataObjects.render.textures;

import com.seibel.distanthorizons.coreapi.util.ColorUtil;

public class BlockFaceTexture {
   public final int width;
   public final int height;
   public final int[] argbPixels;
   @Deprecated
   public final boolean tinted;
   public final boolean uploadAsColorRatio;

   public static BlockFaceTexture createSolidColor(int argbColor) {
      return new BlockFaceTexture(1, 1, new int[]{argbColor}, false, false);
   }

   public static BlockFaceTexture createErrorGridTexture() {
      int[] argbPixels = new int[]{ColorUtil.HOT_PINK, ColorUtil.BLACK, ColorUtil.BLACK, ColorUtil.HOT_PINK};
      return new BlockFaceTexture(2, 2, argbPixels, false, false);
   }

   public static BlockFaceTexture createTexture(int width, int height, int[] argbPixels, boolean tinted) {
      return new BlockFaceTexture(width, height, argbPixels, tinted, true);
   }

   private BlockFaceTexture(int width, int height, int[] argbPixels, boolean tinted, boolean uploadAsColorRatio) {
      this.width = width;
      this.height = height;
      this.argbPixels = argbPixels;
      this.tinted = tinted;
      this.uploadAsColorRatio = uploadAsColorRatio;
   }
}
