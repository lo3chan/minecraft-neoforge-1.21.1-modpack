package net.irisshaders.iris.mixin.bettermipmaps;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.util.Locale;
import net.irisshaders.iris.helpers.ColorSRGB;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ABGR32;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({SpriteContents.class})
public class MixinTextureAtlasSprite {
   @Unique
   private static final float[] SRGB_TO_LINEAR = new float[256];
   @Mutable
   @Shadow
   @Final
   private NativeImage originalImage;

   @Unique
   private static void iris$fillInTransparentPixelColors(NativeImage nativeImage) {
      long ppPixel = getPointerRGBA(nativeImage);
      int pixelCount = nativeImage.getHeight() * nativeImage.getWidth();
      float r = 0.0F;
      float g = 0.0F;
      float b = 0.0F;
      float totalWeight = 0.0F;

      for (int pixelIndex = 0; pixelIndex < pixelCount; pixelIndex++) {
         long pPixel = ppPixel + pixelIndex * 4L;
         int color = MemoryUtil.memGetInt(pPixel);
         int alpha = ABGR32.alpha(color);
         if (alpha != 0) {
            float weight = alpha;
            r += ColorSRGB.srgbToLinear(ABGR32.red(color)) * weight;
            g += ColorSRGB.srgbToLinear(ABGR32.green(color)) * weight;
            b += ColorSRGB.srgbToLinear(ABGR32.blue(color)) * weight;
            totalWeight += weight;
         }
      }

      if (totalWeight != 0.0F) {
         r /= totalWeight;
         g /= totalWeight;
         b /= totalWeight;
         int averageColor = ColorSRGB.linearToSrgb(r, g, b, 0);

         for (int pixelIndexx = 0; pixelIndexx < pixelCount; pixelIndexx++) {
            long pPixel = ppPixel + pixelIndexx * 4L;
            int color = MemoryUtil.memGetInt(pPixel);
            int alpha = ABGR32.alpha(color);
            if (alpha == 0) {
               MemoryUtil.memPutInt(pPixel, averageColor);
            }
         }
      }
   }

   @Unique
   private static long getPointerRGBA(NativeImage nativeImage) {
      if (nativeImage.format() != Format.RGBA) {
         throw new IllegalArgumentException(
            String.format(Locale.ROOT, "Tried to get pointer to RGBA pixel data on NativeImage of wrong format; have %s", nativeImage.format())
         );
      } else {
         return nativeImage.pixels;
      }
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/client/renderer/texture/SpriteContents;originalImage:Lcom/mojang/blaze3d/platform/NativeImage;",
         opcode = 181
      )
   )
   private void iris$beforeGenerateMipLevels(SpriteContents instance, NativeImage nativeImage, ResourceLocation resourceLocation) {
      if (resourceLocation.getPath().contains("leaves")) {
         this.originalImage = nativeImage;
      } else {
         iris$fillInTransparentPixelColors(nativeImage);
         this.originalImage = nativeImage;
      }
   }

   static {
      for (int i = 0; i < 256; i++) {
         SRGB_TO_LINEAR[i] = (float)Math.pow(i / 255.0, 2.2);
      }
   }
}
