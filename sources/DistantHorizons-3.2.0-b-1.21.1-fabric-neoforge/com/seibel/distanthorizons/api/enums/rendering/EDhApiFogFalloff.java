package com.seibel.distanthorizons.api.enums.rendering;

public enum EDhApiFogFalloff {
   LINEAR(0),
   EXPONENTIAL(1),
   EXPONENTIAL_SQUARED(2);

   public final int value;

   private EDhApiFogFalloff(int value) {
      this.value = value;
   }
}
