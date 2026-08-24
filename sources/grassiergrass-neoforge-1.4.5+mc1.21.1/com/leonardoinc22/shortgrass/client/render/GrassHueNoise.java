package com.leonardoinc22.shortgrass.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.awt.Color;
import java.io.InputStream;
import org.slf4j.Logger;

final class GrassHueNoise {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String PATH = "/assets/grassiergrass/textures/effect/grainy.png";
   static final float HUE_VARIATION_RANGE = 0.04F;
   static final float HUE_TILE_BLOCKS = 96.0F;
   private static byte[] luminance;
   private static int size;
   private static boolean loadFailed;

   private GrassHueNoise() {
   }

   static int shiftHue(int tint, float worldX, float worldZ, float bladeRandom, float bladeHueJitterDegrees) {
      float grainy = sample(worldX / 96.0F, worldZ / 96.0F);
      float delta = (grainy - 0.5F) * 0.04F + (bladeRandom - 0.5F) * (bladeHueJitterDegrees / 180.0F);
      if (delta == 0.0F) {
         return tint;
      } else {
         float[] hsb = Color.RGBtoHSB(tint >> 16 & 0xFF, tint >> 8 & 0xFF, tint & 0xFF, null);
         hsb[0] = (hsb[0] + delta) % 1.0F;
         if (hsb[0] < 0.0F) {
            hsb[0]++;
         }

         return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 16777215;
      }
   }

   private static float sample(float u, float v) {
      if (luminance == null) {
         load();
      }

      if (luminance == null) {
         return 0.5F;
      } else {
         int x = wrap((int)Math.floor(u * size));
         int y = wrap((int)Math.floor(v * size));
         return (luminance[y * size + x] & 255) / 255.0F;
      }
   }

   private static int wrap(int i) {
      i %= size;
      return i < 0 ? i + size : i;
   }

   private static synchronized void load() {
      if (luminance == null && !loadFailed) {
         try (InputStream in = GrassHueNoise.class.getResourceAsStream("/assets/grassiergrass/textures/effect/grainy.png")) {
            NativeImage image = NativeImage.read(in);

            try {
               size = image.getWidth();
               byte[] lum = new byte[size * image.getHeight()];

               for (int y = 0; y < image.getHeight(); y++) {
                  for (int x = 0; x < size; x++) {
                     lum[y * size + x] = (byte)(image.getPixelRGBA(x, y) & 0xFF);
                  }
               }

               luminance = lum;
            } catch (Throwable var7) {
               if (image != null) {
                  try {
                     image.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (image != null) {
               image.close();
            }
         } catch (Exception var9) {
            loadFailed = true;
            LOGGER.error("[grassiergrass] failed to load grainy.png hue noise", var9);
         }
      }
   }
}
