package com.seibel.distanthorizons.api.enums.config;

import com.seibel.distanthorizons.coreapi.util.MathUtil;

public enum EDhApiVerticalQuality {
   HEIGHT_MAP(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}),
   LOW(new int[]{4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 1}),
   MEDIUM(new int[]{6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 1}),
   HIGH(new int[]{16, 16, 12, 12, 8, 8, 8, 8, 8, 8, 1}),
   VERY_HIGH(new int[]{32, 16, 12, 12, 12, 12, 12, 12, 12, 12, 1}),
   EXTREME(new int[]{64, 32, 32, 32, 16, 16, 16, 16, 16, 16, 1}),
   PIXEL_ART(new int[]{512, 256, 128, 64, 32, 32, 16, 16, 16, 16, 1});

   public final int[] maxVerticalData;

   private EDhApiVerticalQuality(int[] maxVerticalData) {
      this.maxVerticalData = maxVerticalData;
   }

   public int calculateMaxNumberOfVerticalSlicesAtDetailLevel(byte dataDetail) {
      int index = MathUtil.clamp(0, dataDetail, this.maxVerticalData.length - 1);
      return this.maxVerticalData[index];
   }
}
