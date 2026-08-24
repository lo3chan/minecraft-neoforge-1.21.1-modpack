package com.leonardoinc22.shortgrass.client.render.iris;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;

public final class GrassIrisBrightness {
   private static final float BLADE_MULTIPLIER = 0.7F;
   private static final float PLANT_MULTIPLIER = 1.0F;

   private GrassIrisBrightness() {
   }

   private static float brightness(boolean plants) {
      return GrassConfig.grassBrightness * (plants ? 1.0F : 0.7F);
   }

   public static float colorMultiplier(boolean plants) {
      return Math.min(brightness(plants), 1.0F);
   }

   public static float lightBrightness(boolean plants) {
      return Math.max(brightness(plants), 1.0F);
   }

   public static int adjustLight(int light, boolean plants) {
      float blend = Mth.clamp(brightness(plants) - 1.0F, 0.0F, 1.0F);
      if (blend <= 0.0F) {
         return light;
      } else {
         int block = Mth.clamp(Math.round(Mth.lerp(blend, LightTexture.block(light), 15.0F)), 0, 15);
         int sky = Mth.clamp(Math.round(Mth.lerp(blend, LightTexture.sky(light), 15.0F)), 0, 15);
         return LightTexture.pack(block, sky);
      }
   }
}
