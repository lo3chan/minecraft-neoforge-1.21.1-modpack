package com.seibel.distanthorizons.common.render.openGl.glObject.texture;

import org.jetbrains.annotations.Nullable;

public enum EGlDhDepthBufferFormat {
   DEPTH(false),
   DEPTH16(false),
   DEPTH24(false),
   DEPTH32(false),
   DEPTH32F(false),
   DEPTH_STENCIL(true),
   DEPTH24_STENCIL8(true),
   DEPTH32F_STENCIL8(true);

   private final boolean combinedStencil;

   private EGlDhDepthBufferFormat(boolean combinedStencil) {
      this.combinedStencil = combinedStencil;
   }

   @Nullable
   public static EGlDhDepthBufferFormat fromGlEnum(int glenum) {
      switch (glenum) {
         case 6402:
            return DEPTH;
         case 33189:
            return DEPTH16;
         case 33190:
            return DEPTH24;
         case 33191:
            return DEPTH32;
         case 34041:
            return DEPTH_STENCIL;
         case 35056:
            return DEPTH24_STENCIL8;
         case 36012:
            return DEPTH32F;
         case 36013:
            return DEPTH32F_STENCIL8;
         default:
            return null;
      }
   }

   public static EGlDhDepthBufferFormat fromGlEnumOrDefault(int glenum) {
      EGlDhDepthBufferFormat format = fromGlEnum(glenum);
      return format == null ? DEPTH : format;
   }

   public int getGlInternalFormat() {
      switch (this) {
         case DEPTH:
            return 6402;
         case DEPTH16:
            return 33189;
         case DEPTH24:
            return 33190;
         case DEPTH32:
            return 33191;
         case DEPTH32F:
            return 36012;
         case DEPTH_STENCIL:
            return 34041;
         case DEPTH24_STENCIL8:
            return 35056;
         case DEPTH32F_STENCIL8:
            return 36013;
         default:
            throw new AssertionError("unreachable");
      }
   }

   public int getGlType() {
      return this.isCombinedStencil() ? 34041 : 6402;
   }

   public int getGlFormat() {
      switch (this) {
         case DEPTH:
         case DEPTH16:
            return 5123;
         case DEPTH24:
         case DEPTH32:
            return 5125;
         case DEPTH32F:
            return 5126;
         case DEPTH_STENCIL:
         case DEPTH24_STENCIL8:
            return 34042;
         case DEPTH32F_STENCIL8:
            return 36269;
         default:
            throw new AssertionError("unreachable");
      }
   }

   public boolean isCombinedStencil() {
      return this.combinedStencil;
   }
}
