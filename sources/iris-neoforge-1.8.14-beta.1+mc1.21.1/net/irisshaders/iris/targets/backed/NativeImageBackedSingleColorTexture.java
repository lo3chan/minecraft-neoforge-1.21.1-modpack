package net.irisshaders.iris.targets.backed;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.FastColor.ABGR32;

public class NativeImageBackedSingleColorTexture extends DynamicTexture {
   public NativeImageBackedSingleColorTexture(int red, int green, int blue, int alpha) {
      super(create(ABGR32.color(alpha, blue, green, red)));
   }

   public NativeImageBackedSingleColorTexture(int rgba) {
      this(rgba >> 24 & 0xFF, rgba >> 16 & 0xFF, rgba >> 8 & 0xFF, rgba & 0xFF);
   }

   private static NativeImage create(int color) {
      NativeImage image = new NativeImage(Format.RGBA, 1, 1, false);
      image.setPixelRGBA(0, 0, color);
      return image;
   }
}
