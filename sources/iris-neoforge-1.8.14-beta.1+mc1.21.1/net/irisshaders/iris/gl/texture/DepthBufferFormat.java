package net.irisshaders.iris.gl.texture;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public enum DepthBufferFormat {
   DEPTH(false),
   DEPTH16(false),
   DEPTH24(false),
   DEPTH32(false),
   DEPTH32F(false),
   DEPTH_STENCIL(true),
   DEPTH24_STENCIL8(true),
   DEPTH32F_STENCIL8(true);

   private final boolean combinedStencil;

   private DepthBufferFormat(boolean combinedStencil) {
      this.combinedStencil = combinedStencil;
   }

   @Nullable
   public static DepthBufferFormat fromGlEnum(int glenum) {
      return switch (glenum) {
         case 6402 -> DEPTH;
         case 33189 -> DEPTH16;
         case 33190 -> DEPTH24;
         case 33191 -> DEPTH32;
         case 34041 -> DEPTH_STENCIL;
         case 35056 -> DEPTH24_STENCIL8;
         case 36012 -> DEPTH32F;
         case 36013 -> DEPTH32F_STENCIL8;
         default -> null;
      };
   }

   public static DepthBufferFormat fromGlEnumOrDefault(int glenum) {
      DepthBufferFormat format = fromGlEnum(glenum);
      return Objects.requireNonNullElse(format, DEPTH);
   }

   public int getGlInternalFormat() {
      return switch (this) {
         case DEPTH -> 'ᤂ';
         case DEPTH16 -> '膥';
         case DEPTH24 -> '膦';
         case DEPTH32 -> '膧';
         case DEPTH32F -> '責';
         case DEPTH_STENCIL -> '蓹';
         case DEPTH24_STENCIL8 -> '裰';
         case DEPTH32F_STENCIL8 -> '貭';
      };
   }

   public int getGlType() {
      return this.isCombinedStencil() ? 34041 : 6402;
   }

   public int getGlFormat() {
      return switch (this) {
         case DEPTH, DEPTH16 -> 'ᐃ';
         case DEPTH24, DEPTH32 -> 'ᐅ';
         case DEPTH32F -> 'ᐆ';
         case DEPTH_STENCIL, DEPTH24_STENCIL8 -> '蓺';
         case DEPTH32F_STENCIL8 -> '趭';
      };
   }

   public boolean isCombinedStencil() {
      return this.combinedStencil;
   }
}
