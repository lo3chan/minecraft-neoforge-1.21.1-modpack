package com.seibel.distanthorizons.api.enums.worldGeneration;

public enum EDhApiDistantGeneratorMode {
   PRE_EXISTING_ONLY((byte)1),
   SURFACE((byte)4),
   FEATURES((byte)5),
   INTERNAL_SERVER((byte)6);

   public final byte complexity;

   private EDhApiDistantGeneratorMode(byte complexity) {
      this.complexity = complexity;
   }
}
