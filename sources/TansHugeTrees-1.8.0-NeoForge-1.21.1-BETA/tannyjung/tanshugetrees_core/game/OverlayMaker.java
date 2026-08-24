package tannyjung.tanshugetrees_core.game;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tannyjung.tanshugetrees_core.Core;
import tannyjung.tanshugetrees_core.outside.OutsideUtils;

public class OverlayMaker {
   private static final Map<String, Integer> online_image_id = new HashMap<>();
   private static final Map<String, String> status = new HashMap<>();
   private static int online_image_count = 0;

   public static void createText(
      GuiGraphics graphic, int screen_width, int screen_height, String pos_style, int posX, int posZ, double scale, boolean shadow, String text
   ) {
      int[] pos = convertPos(screen_width, screen_height, posX, posZ, pos_style, scale);
      posX = pos[0];
      posZ = pos[1];
      graphic.pose().pushPose();
      graphic.pose().scale((float)scale, (float)scale, 1.0F);
      graphic.drawString(Minecraft.getInstance().font, text, posX, posZ, 0, shadow);
      graphic.pose().popPose();
   }

   public static void createImage(
      GuiGraphics graphic,
      boolean internet,
      String path,
      String path_load,
      String path_fail,
      int posX,
      int posZ,
      int sizeX,
      int sizeZ,
      int piece_countX,
      int piece_countZ,
      int choose
   ) {
      String name = "";
      if (internet) {
         if (!online_image_id.containsKey(path)) {
            online_image_count++;
            online_image_id.put(path, online_image_count);
         }

         name = "tannyjung:online_image_" + online_image_id.get(path) + ".png";
      } else {
         name = path;
      }

      ResourceLocation location = null;
      if (!status.containsKey(name)) {
         status.put(name, "load");
         if (internet) {
            Core.thread_main.submit(() -> {
               if (OutsideUtils.isURLAvailable(path)) {
                  try {
                     BufferedImage buffer = ImageIO.read(URI.create(path).toURL());
                     NativeImage native_image = new NativeImage(buffer.getWidth(), buffer.getHeight(), false);
                     int argb = 0;
                     int a = 0;
                     int r = 0;
                     int g = 0;
                     int b = 0;
                     int abgr = 0;

                     for (int scanY = 0; scanY < buffer.getHeight(); scanY++) {
                        for (int scanX = 0; scanX < buffer.getWidth(); scanX++) {
                           argb = buffer.getRGB(scanX, scanY);
                           a = argb >>> 24 & 0xFF;
                           r = argb >>> 16 & 0xFF;
                           g = argb >>> 8 & 0xFF;
                           b = argb & 0xFF;
                           abgr = a << 24 | b << 16 | g << 8 | r;
                           native_image.setPixelRGBA(scanX, scanY, abgr);
                        }
                     }

                     Minecraft.getInstance().getTextureManager().register(ResourceLocation.parse(name), new DynamicTexture(native_image));
                     status.put(name, "available");
                  } catch (Exception var12x) {
                     OutsideUtils.exception(new Exception(), var12x, "");
                     status.put(name, "fail");
                  }
               } else {
                  status.put(name, "fail");
               }
            });
         } else {
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(ResourceLocation.parse(name));
            if (texture.getId() == -1) {
               status.put(name, "fail");
            } else {
               status.put(name, "available");
            }
         }
      }

      if (status.get(name).equals("available")) {
         location = ResourceLocation.parse(name);
      } else if (status.get(name).equals("load")) {
         location = ResourceLocation.parse(path_load);
      } else {
         location = ResourceLocation.parse(path_fail);
      }

      int piece_sizeX = sizeX / piece_countX;
      int piece_sizeZ = sizeZ / piece_countZ;
      int startX = Mth.clamp(choose * piece_sizeX, 0, sizeX - piece_sizeX);
      int startZ = Mth.clamp(choose * piece_sizeZ, 0, sizeZ - piece_sizeZ);
      graphic.blit(location, posX, posZ, startX, startZ, piece_sizeX, piece_sizeZ, sizeX, sizeZ);
   }

   private static int[] convertPos(int screen_width, int screen_height, int posX, int posZ, String pos_style, double scale) {
      int[] pos = new int[2];
      if (pos_style.startsWith("top-")) {
         pos[1] = posZ;
      } else if (pos_style.startsWith("bottom-")) {
         pos[1] = screen_height - posZ;
      }

      if (pos_style.endsWith("-left")) {
         pos[0] = posX;
      } else if (pos_style.endsWith("-right")) {
         pos[0] = screen_width - posX;
      }

      pos[0] = (int)(pos[0] / scale);
      pos[1] = (int)(pos[1] / scale);
      return pos;
   }
}
