package com.seibel.distanthorizons.api.enums.rendering;

public enum EDhApiRendererMode {
   DEFAULT,
   DEBUG_TRIANGLE,
   DISABLED;

   public static EDhApiRendererMode next(EDhApiRendererMode type) {
      switch (type) {
         case DEFAULT:
            return DEBUG_TRIANGLE;
         case DEBUG_TRIANGLE:
            return DISABLED;
         default:
            return DEFAULT;
      }
   }

   public static EDhApiRendererMode previous(EDhApiRendererMode type) {
      switch (type) {
         case DEFAULT:
            return DISABLED;
         case DEBUG_TRIANGLE:
            return DEFAULT;
         default:
            return DEBUG_TRIANGLE;
      }
   }
}
