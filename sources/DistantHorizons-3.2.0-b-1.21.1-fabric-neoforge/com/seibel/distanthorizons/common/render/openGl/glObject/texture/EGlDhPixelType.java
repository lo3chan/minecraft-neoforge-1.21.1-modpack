package com.seibel.distanthorizons.common.render.openGl.glObject.texture;

import com.seibel.distanthorizons.common.render.openGl.glObject.enums.EGlVersion;
import java.util.Locale;
import java.util.Optional;

public enum EGlDhPixelType {
   BYTE(5120, EGlVersion.GL_11),
   SHORT(5122, EGlVersion.GL_11),
   INT(5124, EGlVersion.GL_11),
   HALF_FLOAT(5131, EGlVersion.GL_30),
   FLOAT(5126, EGlVersion.GL_11),
   UNSIGNED_BYTE(5121, EGlVersion.GL_11),
   UNSIGNED_BYTE_3_3_2(32818, EGlVersion.GL_12),
   UNSIGNED_BYTE_2_3_3_REV(33634, EGlVersion.GL_12),
   UNSIGNED_SHORT(5123, EGlVersion.GL_11),
   UNSIGNED_SHORT_5_6_5(33635, EGlVersion.GL_12),
   UNSIGNED_SHORT_5_6_5_REV(33636, EGlVersion.GL_12),
   UNSIGNED_SHORT_4_4_4_4(32819, EGlVersion.GL_12),
   UNSIGNED_SHORT_4_4_4_4_REV(33637, EGlVersion.GL_12),
   UNSIGNED_SHORT_5_5_5_1(32820, EGlVersion.GL_12),
   UNSIGNED_SHORT_1_5_5_5_REV(33638, EGlVersion.GL_12),
   UNSIGNED_INT(5125, EGlVersion.GL_11),
   UNSIGNED_INT_8_8_8_8(32821, EGlVersion.GL_12),
   UNSIGNED_INT_8_8_8_8_REV(33639, EGlVersion.GL_12),
   UNSIGNED_INT_10_10_10_2(32822, EGlVersion.GL_12),
   UNSIGNED_INT_2_10_10_10_REV(33640, EGlVersion.GL_12);

   private final int glFormat;
   private final EGlVersion minimumGlVersion;

   private EGlDhPixelType(int glFormat, EGlVersion minimumGlVersion) {
      this.glFormat = glFormat;
      this.minimumGlVersion = minimumGlVersion;
   }

   public static Optional<EGlDhPixelType> fromString(String name) {
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
}
