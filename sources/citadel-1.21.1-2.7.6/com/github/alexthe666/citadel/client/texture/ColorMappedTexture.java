package com.github.alexthe666.citadel.client.texture;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.FastColor.ABGR32;

public class ColorMappedTexture extends SimpleTexture {
   private int[] colors;

   public ColorMappedTexture(ResourceLocation resourceLocation, int[] colors) {
      super(resourceLocation);
      this.colors = colors;
   }

   public void load(ResourceManager resourceManager) {
      NativeImage nativeimage = this.getNativeImage(resourceManager, this.location);
      if (nativeimage != null) {
         if (resourceManager.getResource(this.location).isPresent()) {
            Resource resource = (Resource)resourceManager.getResource(this.location).get();

            try {
               ColorMappedTexture.ColorsMetadataSection section = resource.metadata()
                  .getSection(ColorMappedTexture.ColorsMetadataSection.SERIALIZER)
                  .orElse(new ColorMappedTexture.ColorsMetadataSection(null));
               NativeImage nativeimage2 = this.getNativeImage(resourceManager, section.getColorRamp());
               if (nativeimage2 != null) {
                  this.processColorMap(nativeimage, nativeimage2);
               }
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }

         TextureUtil.prepareImage(this.getId(), nativeimage.getWidth(), nativeimage.getHeight());
         this.bind();
         nativeimage.upload(0, 0, 0, false);
      }
   }

   private NativeImage getNativeImage(ResourceManager resourceManager, @Nullable ResourceLocation resourceLocation) {
      Resource resource = null;
      if (resourceLocation == null) {
         return null;
      } else {
         try {
            resource = resourceManager.getResourceOrThrow(resourceLocation);
            InputStream inputstream = resource.open();
            NativeImage nativeimage = NativeImage.read(inputstream);
            inputstream.close();
            return nativeimage;
         } catch (Throwable var6) {
            return null;
         }
      }
   }

   private void processColorMap(NativeImage nativeImage, NativeImage colorMap) {
      int[] fromColorMap = new int[colorMap.getHeight()];

      for (int i = 0; i < fromColorMap.length; i++) {
         fromColorMap[i] = colorMap.getPixelRGBA(0, i);
      }

      for (int i = 0; i < nativeImage.getWidth(); i++) {
         for (int j = 0; j < nativeImage.getHeight(); j++) {
            int colorAt = nativeImage.getPixelRGBA(i, j);
            if (ABGR32.alpha(colorAt) != 0) {
               int replaceIndex = -1;

               for (int k = 0; k < fromColorMap.length; k++) {
                  if (colorAt == fromColorMap[k]) {
                     replaceIndex = k;
                  }
               }

               if (replaceIndex >= 0 && this.colors.length > replaceIndex) {
                  int r = this.colors[replaceIndex] >> 16 & 0xFF;
                  int g = this.colors[replaceIndex] >> 8 & 0xFF;
                  int b = this.colors[replaceIndex] & 0xFF;
                  nativeImage.setPixelRGBA(i, j, ABGR32.color(ABGR32.alpha(colorAt), b, g, r));
               }
            }
         }
      }
   }

   private static class ColorsMetadataSection {
      public static final ColorMappedTexture.ColorsMetadataSectionSerializer SERIALIZER = new ColorMappedTexture.ColorsMetadataSectionSerializer();
      private ResourceLocation colorRamp;

      public ColorsMetadataSection(ResourceLocation colorRamp) {
         this.colorRamp = colorRamp;
      }

      private boolean areColorsEqual(int color1, int color2) {
         int r1 = color1 >> 16 & 0xFF;
         int g1 = color1 >> 8 & 0xFF;
         int b1 = color1 & 0xFF;
         int r2 = color2 >> 16 & 0xFF;
         int g2 = color2 >> 8 & 0xFF;
         int b2 = color2 & 0xFF;
         return r1 == r2 && g1 == g2 && b1 == b2;
      }

      public ResourceLocation getColorRamp() {
         return this.colorRamp;
      }
   }

   private static class ColorsMetadataSectionSerializer implements MetadataSectionSerializer<ColorMappedTexture.ColorsMetadataSection> {
      public ColorMappedTexture.ColorsMetadataSection fromJson(JsonObject json) {
         return new ColorMappedTexture.ColorsMetadataSection(ResourceLocation.parse(GsonHelper.getAsString(json, "color_ramp")));
      }

      public String getMetadataSectionName() {
         return "colors";
      }
   }
}
