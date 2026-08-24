package com.seibel.distanthorizons.common.render.openGl.glObject.texture;

import com.seibel.distanthorizons.common.render.openGl.glObject.enums.EGlVersion;
import java.util.Locale;
import java.util.Optional;

public enum EGlDhInternalTextureFormat {
   RGBA(6408, EGlVersion.GL_11, EGlDhPixelFormat.RGBA),
   R8(33321, EGlVersion.GL_30, EGlDhPixelFormat.RED),
   RG8(33323, EGlVersion.GL_30, EGlDhPixelFormat.RG),
   RGB8(32849, EGlVersion.GL_11, EGlDhPixelFormat.RGB),
   RGBA8(32856, EGlVersion.GL_11, EGlDhPixelFormat.RGBA),
   R8_SNORM(36756, EGlVersion.GL_31, EGlDhPixelFormat.RED),
   RG8_SNORM(36757, EGlVersion.GL_31, EGlDhPixelFormat.RG),
   RGB8_SNORM(36758, EGlVersion.GL_31, EGlDhPixelFormat.RGB),
   RGBA8_SNORM(36759, EGlVersion.GL_31, EGlDhPixelFormat.RGBA),
   R16(33322, EGlVersion.GL_30, EGlDhPixelFormat.RED),
   RG16(33324, EGlVersion.GL_30, EGlDhPixelFormat.RG),
   RGB16(32852, EGlVersion.GL_11, EGlDhPixelFormat.RGB),
   RGBA16(32859, EGlVersion.GL_11, EGlDhPixelFormat.RGBA),
   R16_SNORM(36760, EGlVersion.GL_31, EGlDhPixelFormat.RED),
   RG16_SNORM(36761, EGlVersion.GL_31, EGlDhPixelFormat.RG),
   RGB16_SNORM(36762, EGlVersion.GL_31, EGlDhPixelFormat.RGB),
   RGBA16_SNORM(36763, EGlVersion.GL_31, EGlDhPixelFormat.RGBA),
   R16F(33325, EGlVersion.GL_30, EGlDhPixelFormat.RED),
   RG16F(33327, EGlVersion.GL_30, EGlDhPixelFormat.RG),
   RGB16F(34843, EGlVersion.GL_30, EGlDhPixelFormat.RGB),
   RGBA16F(34842, EGlVersion.GL_30, EGlDhPixelFormat.RGBA),
   R32F(33326, EGlVersion.GL_30, EGlDhPixelFormat.RED),
   RG32F(33328, EGlVersion.GL_30, EGlDhPixelFormat.RG),
   RGB32F(34837, EGlVersion.GL_30, EGlDhPixelFormat.RGB),
   RGBA32F(34836, EGlVersion.GL_30, EGlDhPixelFormat.RGBA),
   R8I(33329, EGlVersion.GL_30, EGlDhPixelFormat.RED_INTEGER),
   RG8I(33335, EGlVersion.GL_30, EGlDhPixelFormat.RG_INTEGER),
   RGB8I(36239, EGlVersion.GL_30, EGlDhPixelFormat.RGB_INTEGER),
   RGBA8I(36238, EGlVersion.GL_30, EGlDhPixelFormat.RGBA_INTEGER),
   R8UI(33330, EGlVersion.GL_30, EGlDhPixelFormat.RED_INTEGER),
   RG8UI(33336, EGlVersion.GL_30, EGlDhPixelFormat.RG_INTEGER),
   RGB8UI(36221, EGlVersion.GL_30, EGlDhPixelFormat.RGB_INTEGER),
   RGBA8UI(36220, EGlVersion.GL_30, EGlDhPixelFormat.RGBA_INTEGER),
   R16I(33331, EGlVersion.GL_30, EGlDhPixelFormat.RED_INTEGER),
   RG16I(33337, EGlVersion.GL_30, EGlDhPixelFormat.RG_INTEGER),
   RGB16I(36233, EGlVersion.GL_30, EGlDhPixelFormat.RGB_INTEGER),
   RGBA16I(36232, EGlVersion.GL_30, EGlDhPixelFormat.RGBA_INTEGER),
   R16UI(33332, EGlVersion.GL_30, EGlDhPixelFormat.RED_INTEGER),
   RG16UI(33338, EGlVersion.GL_30, EGlDhPixelFormat.RG_INTEGER),
   RGB16UI(36215, EGlVersion.GL_30, EGlDhPixelFormat.RGB_INTEGER),
   RGBA16UI(36214, EGlVersion.GL_30, EGlDhPixelFormat.RGBA_INTEGER),
   R32I(33333, EGlVersion.GL_30, EGlDhPixelFormat.RED_INTEGER),
   RG32I(33339, EGlVersion.GL_30, EGlDhPixelFormat.RG_INTEGER),
   RGB32I(36227, EGlVersion.GL_30, EGlDhPixelFormat.RGB_INTEGER),
   RGBA32I(36226, EGlVersion.GL_30, EGlDhPixelFormat.RGBA_INTEGER),
   R32UI(33334, EGlVersion.GL_30, EGlDhPixelFormat.RED_INTEGER),
   RG32UI(33340, EGlVersion.GL_30, EGlDhPixelFormat.RG_INTEGER),
   RGB32UI(36209, EGlVersion.GL_30, EGlDhPixelFormat.RGB_INTEGER),
   RGBA32UI(36208, EGlVersion.GL_30, EGlDhPixelFormat.RGBA_INTEGER),
   R3_G3_B2(10768, EGlVersion.GL_11, EGlDhPixelFormat.RGB),
   RGB5_A1(32855, EGlVersion.GL_11, EGlDhPixelFormat.RGBA),
   RGB10_A2(32857, EGlVersion.GL_11, EGlDhPixelFormat.RGBA),
   R11F_G11F_B10F(35898, EGlVersion.GL_30, EGlDhPixelFormat.RGB),
   RGB9_E5(35901, EGlVersion.GL_30, EGlDhPixelFormat.RGB);

   private final int glFormat;
   private final EGlVersion minimumGlVersion;
   private final EGlDhPixelFormat expectedPixelFormat;

   private EGlDhInternalTextureFormat(int glFormat, EGlVersion minimumGlVersion, EGlDhPixelFormat expectedPixelFormat) {
      this.glFormat = glFormat;
      this.minimumGlVersion = minimumGlVersion;
      this.expectedPixelFormat = expectedPixelFormat;
   }

   public static Optional<EGlDhInternalTextureFormat> fromString(String name) {
      try {
         return Optional.of(valueOf(name.toUpperCase(Locale.US)));
      } catch (IllegalArgumentException var2) {
         return Optional.empty();
      }
   }

   public int getGlFormat() {
      return this.glFormat;
   }

   public EGlDhPixelFormat getPixelFormat() {
      return this.expectedPixelFormat;
   }

   public EGlVersion getMinimumGlVersion() {
      return this.minimumGlVersion;
   }
}
