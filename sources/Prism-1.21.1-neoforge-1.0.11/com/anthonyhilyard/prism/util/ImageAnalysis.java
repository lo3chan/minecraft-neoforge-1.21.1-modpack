package com.anthonyhilyard.prism.util;

import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class ImageAnalysis {
   public static TextColor getDominantColor(ResourceLocation imageLocation, Rect2i region) {
      Minecraft minecraft = Minecraft.getInstance();
      ResourceManager resourceManager = minecraft.getResourceManager();

      try {
         TextColor var6;
         try (InputStream imageStream = resourceManager.getResource(imageLocation).isPresent()
               ? ((Resource)resourceManager.getResource(imageLocation).get()).open()
               : null) {
            BufferedImage image = ImageIO.read(imageStream);
            if (region != null) {
               image = image.getSubimage(region.getX(), region.getY(), region.getWidth(), region.getHeight());
            }

            var6 = getDominantColor(image);
         }

         return var6;
      } catch (Exception var9) {
         return null;
      }
   }

   public static TextColor getDominantColor(BufferedImage image) {
      record ImageSampleData(float weight, int count) {
      }

      Map<Integer, ImageSampleData> samples = Maps.newHashMap();
      Function<Integer, Float> getWeight = colorx -> {
         float a = (colorx >> 24 & 0xFF) / 255.0F;
         float r = (colorx >> 16 & 0xFF) / 255.0F;
         float g = (colorx >> 8 & 0xFF) / 255.0F;
         float b = (colorx >> 0 & 0xFF) / 255.0F;
         return (!(r <= 0.06F) || !(g <= 0.06F) || !(b <= 0.06F)) && !(a < 0.3F)
            ? (
                  1.0F
                     - (1.0F - a) * (1.0F - a)
                     + 1.0F
                     - (1.0F - r) * (1.0F - r)
                     + 1.0F
                     - (1.0F - g) * (1.0F - g)
                     + 1.0F
                     - (1.0F - r) * (1.0F - r)
                     + 1.0F
                     - (1.0F - g) * (1.0F - g)
                     + 1.0F
                     - (1.0F - b) * (1.0F - b)
                     + 1.0F
                     - (1.0F - b) * (1.0F - b)
               )
               / 7.0F
            : 0.0F;
      };

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            for (int degrade = 0; degrade < 8; degrade += 2) {
               int color = image.getRGB(x, y);
               int degradedColor = ColorUtil.combineARGB(
                  (color >> 24 & 0xFF) >> degrade, (color >> 16 & 0xFF) >> degrade, (color >> 8 & 0xFF) >> degrade, (color >> 0 & 0xFF) >> degrade
               );
               if (!samples.containsKey(degradedColor)) {
                  float weight = getWeight.apply(degradedColor);
                  if (weight != 0.0F) {
                     ImageSampleData data = new ImageSampleData(weight, 1);
                     samples.put(degradedColor, data);
                  }
               } else {
                  samples.replace(degradedColor, new ImageSampleData(samples.get(degradedColor).weight(), samples.get(degradedColor).count() + 1));
               }
            }
         }
      }

      Map<Integer, Float> groups = Maps.newHashMap();

      for (Integer color : samples.keySet()) {
         groups.put(color, samples.get(color).count() * samples.get(color).weight());
      }

      return groups.isEmpty() ? null : TextColor.fromRgb(groups.entrySet().stream().max((a, b) -> Float.compare(a.getValue(), b.getValue())).get().getKey());
   }
}
