package net.mehvahdjukaar.moonlight.api.fluids;

import net.mehvahdjukaar.moonlight.api.client.TextureCache;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.DynamicTextureRenderer;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.RenderedTexturesManager;
import net.mehvahdjukaar.moonlight.api.fluids.platform.SoftFluidColorsImpl;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.resources.textures.PalettedPermutationsHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.level.BlockAndTintGetter;

public class SoftFluidColors implements ResourceManagerReloadListener {
   public void onResourceManagerReload(ResourceManager resourceManager) {
      RenderedTexturesManager.clearCache();
      DynamicTextureRenderer.clearCache();
      TextureCache.clear();
      PalettedPermutationsHelper.invalidate();
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         refreshParticleColors(SoftFluidRegistry.get(level.registryAccess()));
      }
   }

   public static void refreshParticleColors(Registry<SoftFluid> reg) {
      for (SoftFluid fluid : reg) {
         ResourceLocation location = fluid.getStillTexture();
         int averageColor = -1;
         int tint = fluid.getTintMethod().appliesToStill() ? fluid.getTintColor() : -1;
         TextureAtlas textureMap = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
         TextureAtlasSprite sprite = textureMap.getSprite(location);

         try {
            averageColor = getAverageColor(sprite, tint);
         } catch (Exception var9) {
            Moonlight.LOGGER.warn("Failed to load particle color for {} using current resource pack. might be a broken png.mcmeta", sprite);
         }

         fluid.averageTextureTint = averageColor;
      }
   }

   private static int getAverageColor(TextureAtlasSprite sprite, int tint) {
      SpriteContents c = sprite.contents();
      if (sprite != null && c.getFrameCount() != 0) {
         int tintR = tint >> 16 & 0xFF;
         int tintG = tint >> 8 & 0xFF;
         int tintB = tint & 0xFF;
         int total = 0;
         int totalR = 0;
         int totalB = 0;
         int totalG = 0;

         for (int tryFrame = 0; tryFrame < c.getFrameCount(); tryFrame++) {
            try {
               for (int x = 0; x < c.width(); x++) {
                  for (int y = 0; y < c.height(); y++) {
                     int pixel = ClientHelper.getPixelRGBA(sprite, tryFrame, x, y);
                     int pixelB = pixel >> 16 & 0xFF;
                     int pixelG = pixel >> 8 & 0xFF;
                     int pixelR = pixel & 0xFF;
                     total++;
                     totalR += pixelR;
                     totalG += pixelG;
                     totalB += pixelB;
                  }
               }
               break;
            } catch (Exception var17) {
               total = 0;
               totalR = 0;
               totalB = 0;
               totalG = 0;
            }
         }

         return total <= 0 ? -1 : ARGB32.color(255, totalR / total * tintR / 255, totalG / total * tintG / 255, totalB / total * tintB / 255);
      } else {
         return -1;
      }
   }

   public static int getSpecialColor(SoftFluidStack var0, BlockAndTintGetter var1, BlockPos var2) {
      return SoftFluidColorsImpl.getSpecialColor(var0, var1, var2);
   }
}
