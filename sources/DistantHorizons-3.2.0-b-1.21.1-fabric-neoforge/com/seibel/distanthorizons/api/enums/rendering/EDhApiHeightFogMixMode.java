package com.seibel.distanthorizons.api.enums.rendering;

public enum EDhApiHeightFogMixMode {
   SPHERICAL(0),
   CYLINDRICAL(1),
   MAX(2),
   ADDITION(3),
   MULTIPLY(4),
   INVERSE_MULTIPLY(5),
   LIMITED_ADDITION(6),
   MULTIPLY_ADDITION(7),
   INVERSE_MULTIPLY_ADDITION(8),
   AVERAGE(9);

   public final int value;

   private EDhApiHeightFogMixMode(int value) {
      this.value = value;
   }
}
