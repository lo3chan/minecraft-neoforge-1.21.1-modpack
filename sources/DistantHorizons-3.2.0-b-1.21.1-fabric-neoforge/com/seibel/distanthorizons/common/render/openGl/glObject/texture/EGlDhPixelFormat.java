package com.seibel.distanthorizons.common.render.openGl.glObject.texture;

import com.seibel.distanthorizons.common.render.openGl.glObject.enums.EGlVersion;
import java.util.Locale;
import java.util.Optional;

public enum EGlDhPixelFormat {
   RED(6403, EGlVersion.GL_11, false),
   RG(33319, EGlVersion.GL_30, false),
   RGB(6407, EGlVersion.GL_11, false),
   BGR(32992, EGlVersion.GL_12, false),
   RGBA(6408, EGlVersion.GL_11, false),
   BGRA(32993, EGlVersion.GL_12, false),
   RED_INTEGER(36244, EGlVersion.GL_30, true),
   RG_INTEGER(33320, EGlVersion.GL_30, true),
   RGB_INTEGER(36248, EGlVersion.GL_30, true),
   BGR_INTEGER(36250, EGlVersion.GL_30, true),
   RGBA_INTEGER(36249, EGlVersion.GL_30, true),
   BGRA_INTEGER(36251, EGlVersion.GL_30, true);

   private final int glFormat;
   private final EGlVersion minimumGlVersion;
   private final boolean isInteger;

   private EGlDhPixelFormat(int glFormat, EGlVersion minimumGlVersion, boolean isInteger) {
      this.glFormat = glFormat;
      this.minimumGlVersion = minimumGlVersion;
      this.isInteger = isInteger;
   }

   public static Optional<EGlDhPixelFormat> fromString(String name) {
      try {
         return Optional.of(valueOf(name.toUpperCase(Locale.US)));
      } catch (IllegalArgumentException var2) {
         return Optional.empty();
      }
   }

   public int getGlFormat() {
      return this.glFormat;
   }

   public EGlVersion getMinimumGlVersion() {
      return this.minimumGlVersion;
   }

   public boolean isInteger() {
      return this.isInteger;
   }
}
