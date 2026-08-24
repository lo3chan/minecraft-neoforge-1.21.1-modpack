package net.irisshaders.iris.mixin.bettermipmaps;

import net.irisshaders.iris.helpers.ColorSRGB;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.util.FastColor.ABGR32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin({MipmapGenerator.class})
public class MixinMipmapGenerator {
   @Overwrite
   private static int alphaBlend(int one, int two, int three, int four, boolean checkAlpha) {
      return weightedAverageColor(weightedAverageColor(one, two), weightedAverageColor(three, four));
   }

   @Unique
   private static int weightedAverageColor(int one, int two) {
      int alphaOne = ABGR32.alpha(one);
      int alphaTwo = ABGR32.alpha(two);
      if (alphaOne == alphaTwo) {
         return averageRgb(one, two, alphaOne);
      } else if (alphaOne == 0) {
         return two & 16777215 | alphaTwo >> 2 << 24;
      } else if (alphaTwo == 0) {
         return one & 16777215 | alphaOne >> 2 << 24;
      } else {
         float scale = 1.0F / (alphaOne + alphaTwo);
         float relativeWeightOne = alphaOne * scale;
         float relativeWeightTwo = alphaTwo * scale;
         float oneR = ColorSRGB.srgbToLinear(ABGR32.red(one)) * relativeWeightOne;
         float oneG = ColorSRGB.srgbToLinear(ABGR32.green(one)) * relativeWeightOne;
         float oneB = ColorSRGB.srgbToLinear(ABGR32.blue(one)) * relativeWeightOne;
         float twoR = ColorSRGB.srgbToLinear(ABGR32.red(two)) * relativeWeightTwo;
         float twoG = ColorSRGB.srgbToLinear(ABGR32.green(two)) * relativeWeightTwo;
         float twoB = ColorSRGB.srgbToLinear(ABGR32.blue(two)) * relativeWeightTwo;
         float linearR = oneR + twoR;
         float linearG = oneG + twoG;
         float linearB = oneB + twoB;
         int averageAlpha = alphaOne + alphaTwo >> 1;
         return ColorSRGB.linearToSrgb(linearR, linearG, linearB, averageAlpha);
      }
   }

   @Unique
   private static int averageRgb(int a, int b, int alpha) {
      float ar = ColorSRGB.srgbToLinear(ABGR32.red(a));
      float ag = ColorSRGB.srgbToLinear(ABGR32.green(a));
      float ab = ColorSRGB.srgbToLinear(ABGR32.blue(a));
      float br = ColorSRGB.srgbToLinear(ABGR32.red(b));
      float bg = ColorSRGB.srgbToLinear(ABGR32.green(b));
      float bb = ColorSRGB.srgbToLinear(ABGR32.blue(b));
      return ColorSRGB.linearToSrgb((ar + br) * 0.5F, (ag + bg) * 0.5F, (ab + bb) * 0.5F, alpha);
   }
}
