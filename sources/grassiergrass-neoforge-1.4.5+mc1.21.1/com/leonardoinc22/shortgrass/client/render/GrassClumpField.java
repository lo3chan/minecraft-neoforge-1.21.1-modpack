package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.InputStream;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

final class GrassClumpField {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String PATH = "/assets/grassiergrass/textures/effect/noise.png";
   private static final float RANDOM_PRIORITY_SHARE = 0.15F;
   private static final float PER_BLADE_HEIGHT_MIN = 0.88F;
   private static final float PER_BLADE_HEIGHT_MAX = 1.14F;
   private static final float HEIGHT_NOISE_SCALE = 1.0F;
   private static final float HEIGHT_NOISE_CONTRAST = 1.35F;
   private static final float HEIGHT_NOISE_BIAS = 0.5F;
   private static final float HEIGHT_NOISE_SHORT_MULTIPLIER = 0.1F;
   private static final float HEIGHT_NOISE_TALL_MULTIPLIER = 1.28F;
   private static final float REGIONAL_LENGTH_FRACTION = 0.59F;
   private static final float MIN_BLADE_LENGTH = 0.02F;
   private static byte[] snowCoverage;
   private static int width;
   private static int height;
   private static boolean loadFailed;

   private GrassClumpField() {
   }

   static boolean keepBlade(float noiseX, float noiseZ, float sparsity, float bladeRandom) {
      if (sparsity <= 0.0F) {
         return true;
      } else if (sparsity >= 1.0F) {
         return false;
      } else {
         if (snowCoverage == null) {
            load();
         }

         if (snowCoverage == null) {
            return Mth.clamp(bladeRandom, 0.0F, 1.0F) < 1.0F - sparsity;
         } else {
            float shortPriority = 1.0F - stationaryHeightNoise(noiseX, noiseZ);
            float removalPriority = shortPriority * 0.85F + Mth.clamp(bladeRandom, 0.0F, 1.0F) * 0.15F;
            return removalPriority < 1.0F - sparsity;
         }
      }
   }

   static float bladeLengthMultiplier(float noiseX, float noiseZ, float angle, int heightClass) {
      if (snowCoverage == null) {
         load();
      }

      if (snowCoverage == null) {
         return 1.0F;
      } else {
         float heightMultiplier = Mth.lerp(stationaryHeightNoise(noiseX, noiseZ), 0.1F, 1.28F);
         int angleBucket = Mth.clamp((int)(positiveAngle(angle) / 6.2831855F * 31.0F), 0, 31);
         float bladeRandom = hash2D(noiseX + angleBucket * 0.071F, noiseZ + heightClass * 13.37F);
         float shapedRandom = smoothstep(0.0F, 1.0F, bladeRandom);
         float perBladeHeight = Mth.lerp(shapedRandom, 0.88F, 1.14F);
         float variation = ((heightMultiplier - 1.0F) * 0.59F + (perBladeHeight - 1.0F)) * GrassConfig.heightVariation;
         return Math.max(1.0F + variation, 0.02F / GrassGeometry.visualBladeHeight());
      }
   }

   private static float stationaryHeightNoise(float noiseX, float noiseZ) {
      float noise = sampleSmoothNoise(noiseX * 1.0F, noiseZ * 1.0F);
      noise = Mth.clamp((noise - 0.5F) * 1.35F + 0.5F, 0.0F, 1.0F);
      return smoothstep(0.0F, 1.0F, noise);
   }

   static float snowCoverage(float noiseX, float noiseZ) {
      if (snowCoverage == null) {
         load();
      }

      return snowCoverage == null ? 0.5F : sampleSmoothNoise(noiseX, noiseZ);
   }

   private static float sampleSmoothNoise(float noiseX, float noiseZ) {
      float x = fract(noiseX) * width - 0.5F;
      float y = fract(noiseZ) * height - 0.5F;
      int x0 = Mth.floor(x);
      int y0 = Mth.floor(y);
      float tx = smoothstep(0.0F, 1.0F, x - x0);
      float ty = smoothstep(0.0F, 1.0F, y - y0);
      float bottom = Mth.lerp(tx, coverageAt(x0, y0), coverageAt(x0 + 1, y0));
      float top = Mth.lerp(tx, coverageAt(x0, y0 + 1), coverageAt(x0 + 1, y0 + 1));
      return Mth.lerp(ty, bottom, top);
   }

   private static float coverageAt(int x, int y) {
      int index = wrap(y, height) * width + wrap(x, width);
      return (snowCoverage[index] & 255) / 255.0F;
   }

   private static float smoothstep(float edge0, float edge1, float value) {
      float t = Mth.clamp((value - edge0) / (edge1 - edge0), 0.0F, 1.0F);
      return t * t * (3.0F - 2.0F * t);
   }

   private static float hash2D(float x, float y) {
      float px = fract(x * 0.1031F);
      float py = fract(y * 0.1031F);
      float pz = fract(x * 0.1031F);
      float dot = px * (py + 33.33F) + py * (pz + 33.33F) + pz * (px + 33.33F);
      px += dot;
      py += dot;
      pz += dot;
      return fract((px + py) * pz);
   }

   private static float fract(float value) {
      return value - Mth.floor(value);
   }

   private static float positiveAngle(float angle) {
      return angle - Mth.floor(angle / 6.2831855F) * 6.2831855F;
   }

   private static int wrap(int value, int size) {
      int wrapped = value % size;
      return wrapped < 0 ? wrapped + size : wrapped;
   }

   private static synchronized void load() {
      if (snowCoverage == null && !loadFailed) {
         try (InputStream in = GrassClumpField.class.getResourceAsStream("/assets/grassiergrass/textures/effect/noise.png")) {
            NativeImage image = NativeImage.read(in);

            try {
               width = image.getWidth();
               height = image.getHeight();
               byte[] loadedCoverage = new byte[width * height];

               for (int y = 0; y < height; y++) {
                  for (int x = 0; x < width; x++) {
                     int rgba = image.getPixelRGBA(x, y);
                     loadedCoverage[y * width + x] = (byte)(rgba & 0xFF);
                  }
               }

               snowCoverage = loadedCoverage;
            } catch (Throwable var8) {
               if (image != null) {
                  try {
                     image.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (image != null) {
               image.close();
            }
         } catch (Exception var10) {
            loadFailed = true;
            LOGGER.error("[grassiergrass] failed to load noise.png height field", var10);
         }
      }
   }
}
