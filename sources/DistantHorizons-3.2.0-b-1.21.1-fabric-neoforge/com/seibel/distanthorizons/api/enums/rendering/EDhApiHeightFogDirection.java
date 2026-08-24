package com.seibel.distanthorizons.api.enums.rendering;

public enum EDhApiHeightFogDirection {
   ABOVE_CAMERA(true, true, false),
   BELOW_CAMERA(true, false, true),
   ABOVE_AND_BELOW_CAMERA(true, true, true),
   ABOVE_SET_HEIGHT(false, true, false),
   BELOW_SET_HEIGHT(false, false, true),
   ABOVE_AND_BELOW_SET_HEIGHT(false, true, true);

   public final boolean basedOnCamera;
   public final boolean fogAppliesUp;
   public final boolean fogAppliesDown;

   private EDhApiHeightFogDirection(boolean basedOnCamera, boolean fogAppliesUp, boolean fogAppliesDown) {
      this.basedOnCamera = basedOnCamera;
      this.fogAppliesUp = fogAppliesUp;
      this.fogAppliesDown = fogAppliesDown;
   }
}
