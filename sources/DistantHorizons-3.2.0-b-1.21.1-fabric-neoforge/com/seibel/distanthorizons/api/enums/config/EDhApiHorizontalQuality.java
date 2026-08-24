package com.seibel.distanthorizons.api.enums.config;

public enum EDhApiHorizontalQuality {
   LOWEST(2.0, 4),
   LOW(2.0, 8),
   MEDIUM(2.0, 12),
   HIGH(2.200000047683716, 16),
   EXTREME(2.4000000953674316, 32);

   public final double quadraticBase;
   public final int distanceUnitInBlocks;

   private EDhApiHorizontalQuality(double quadraticBase, int distanceUnitInBlocks) {
      this.quadraticBase = quadraticBase;
      this.distanceUnitInBlocks = distanceUnitInBlocks;
   }
}
