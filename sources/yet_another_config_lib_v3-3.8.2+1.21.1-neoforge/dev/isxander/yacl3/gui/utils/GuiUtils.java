package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import dev.isxander.yacl3.debug.DebugProperties;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GuiUtils {
   private static Function<ResourceLocation, RenderType> GUI_TEXTURED = Util.memoize(
      location -> RenderType.create(
         "yacl:gui_textured",
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         786432,
         CompositeState.builder()
            .setTextureState(new TextureStateShard(location, false, false))
            .setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
            .createCompositeState(false)
      )
   );

   public static void pushPose(GuiGraphics graphics) {
      graphics.pose().pushPose();
   }

   public static void popPose(GuiGraphics graphics) {
      graphics.pose().popPose();
   }

   public static void translate2D(GuiGraphics graphics, float x, float y) {
      graphics.pose().translate(x, y, 0.0F);
   }

   public static void translateZ(GuiGraphics graphics, float z) {
      graphics.pose().translate(0.0F, 0.0F, z);
   }

   public static void scale2D(GuiGraphics graphics, float x, float y) {
      graphics.pose().scale(x, y, 1.0F);
   }

   public static void rotate2D(GuiGraphics graphics, float angle) {
      graphics.pose().rotateAround(Axis.ZP.rotationDegrees(angle), 0.0F, 0.0F, 1.0F);
   }

   public static void blitGuiTex(
      GuiGraphics graphics, ResourceLocation texture, int x, int y, float u, float v, int textureWidth, int textureHeight, int width, int height
   ) {
      blitGuiTex(graphics, texture, x, y, u, v, textureWidth, textureHeight, width, height, false);
   }

   public static void blitGuiTex(
      GuiGraphics graphics,
      ResourceLocation texture,
      int x,
      int y,
      float u,
      float v,
      int textureWidth,
      int textureHeight,
      int width,
      int height,
      boolean linearFiltering
   ) {
      doTextureFiltering();
      graphics.blit(texture, x, y, u, v, textureWidth, textureHeight, width, height);
   }

   public static void blitGuiTexColor(
      GuiGraphics graphics, ResourceLocation texture, int x, int y, float u, float v, int textureWidth, int textureHeight, int width, int height, int color
   ) {
      float a = (color >> 24 & 0xFF) / 255.0F;
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      graphics.setColor(r, g, b, a);
      graphics.blit(texture, x, y, u, v, textureWidth, textureHeight, width, height);
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void blitSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
      graphics.blitSprite(sprite, x, y, width, height);
   }

   public static Function<ResourceLocation, RenderType> guiTextured(boolean textureFiltering) {
      return GUI_TEXTURED;
   }

   public static MutableComponent translatableFallback(String key, Component fallback) {
      return Language.getInstance().has(key) ? Component.translatable(key) : fallback.copy();
   }

   public static String shortenString(String string, Font font, int maxWidth, String suffix) {
      if (string.isEmpty()) {
         return string;
      } else {
         for (boolean firstIter = true; font.width(string) > maxWidth; firstIter = false) {
            string = string.substring(0, Math.max(string.length() - 1 - (firstIter ? 1 : suffix.length() + 1), 0)).trim();
            string = string + suffix;
            if (string.equals(suffix)) {
               break;
            }
         }

         return string;
      }
   }

   public static void setPixelARGB(NativeImage nativeImage, int x, int y, int argb) {
      int a = argb >> 24 & 0xFF;
      int r = argb >> 16 & 0xFF;
      int g = argb >> 8 & 0xFF;
      int b = argb & 0xFF;
      int abgr = a << 24 | b << 16 | g << 8 | r;
      nativeImage.setPixelRGBA(x, y, abgr);
   }

   public static void doTextureFiltering() {
      if (DebugProperties.IMAGE_FILTERING) {
         GlStateManager._texParameter(3553, 10240, 9729);
         GlStateManager._texParameter(3553, 10241, 9729);
      }
   }

   public static int extractAlpha(int argb) {
      return argb >> 24 & 0xFF;
   }

   public static int putAlpha(int rgb, int alpha) {
      return rgb & 16777215 | alpha << 24;
   }
}
