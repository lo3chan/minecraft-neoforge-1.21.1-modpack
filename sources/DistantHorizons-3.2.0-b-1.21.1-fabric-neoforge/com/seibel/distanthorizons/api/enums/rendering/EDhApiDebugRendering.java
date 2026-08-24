package com.seibel.distanthorizons.api.enums.rendering;

public enum EDhApiDebugRendering {
   OFF,
   SHOW_DETAIL,
   SHOW_BLOCK_MATERIAL,
   SHOW_OVERLAPPING_QUADS;

   public static EDhApiDebugRendering next(EDhApiDebugRendering type) {
      switch (type) {
         case OFF:
            return SHOW_DETAIL;
         case SHOW_DETAIL:
            return SHOW_BLOCK_MATERIAL;
         case SHOW_BLOCK_MATERIAL:
            return SHOW_OVERLAPPING_QUADS;
         case SHOW_OVERLAPPING_QUADS:
            return OFF;
         default:
            return OFF;
      }
   }

   public static EDhApiDebugRendering previous(EDhApiDebugRendering type) {
      switch (type) {
         case OFF:
            return SHOW_OVERLAPPING_QUADS;
         case SHOW_OVERLAPPING_QUADS:
            return SHOW_DETAIL;
         default:
            return OFF;
      }
   }
}
