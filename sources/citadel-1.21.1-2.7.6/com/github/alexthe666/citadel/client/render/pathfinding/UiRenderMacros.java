package com.github.alexthe666.citadel.client.render.pathfinding;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class UiRenderMacros {
   public static final double HALF_BIAS = 0.5;

   public static void drawLineRectGradient(PoseStack ps, int x, int y, int w, int h, int argbColorStart, int argbColorEnd) {
      drawLineRectGradient(ps, x, y, w, h, argbColorStart, argbColorEnd, 1);
   }

   public static void drawLineRectGradient(PoseStack ps, int x, int y, int w, int h, int argbColorStart, int argbColorEnd, int lineWidth) {
      drawLineRectGradient(
         ps,
         x,
         y,
         w,
         h,
         argbColorStart >> 16 & 0xFF,
         argbColorEnd >> 16 & 0xFF,
         argbColorStart >> 8 & 0xFF,
         argbColorEnd >> 8 & 0xFF,
         argbColorStart & 0xFF,
         argbColorEnd & 0xFF,
         argbColorStart >> 24 & 0xFF,
         argbColorEnd >> 24 & 0xFF,
         lineWidth
      );
   }

   public static void drawLineRectGradient(
      PoseStack ps,
      int x,
      int y,
      int w,
      int h,
      int redStart,
      int redEnd,
      int greenStart,
      int greenEnd,
      int blueStart,
      int blueEnd,
      int alphaStart,
      int alphaEnd,
      int lineWidth
   ) {
      if (lineWidth >= 1 && (alphaStart != 0 || alphaEnd != 0)) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         if (alphaStart == 255 && alphaEnd == 255) {
            RenderSystem.disableBlend();
         } else {
            RenderSystem.enableBlend();
         }

         Matrix4f m = ps.last().pose();
         BufferBuilder buffer1 = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         buffer1.addVertex(m, x, y, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
         buffer1.addVertex(m, x, y + h, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
         buffer1.addVertex(m, x + lineWidth, y + h - lineWidth, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
         buffer1.addVertex(m, x + lineWidth, y + lineWidth, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
         buffer1.addVertex(m, x + w - lineWidth, y + lineWidth, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
         buffer1.addVertex(m, x + w, y, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
         BufferBuilder buffer2 = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         buffer2.addVertex(m, x + w, y + h, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
         buffer2.addVertex(m, x + w, y, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
         buffer2.addVertex(m, x + w - lineWidth, y + lineWidth, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
         buffer2.addVertex(m, x + w - lineWidth, y + h - lineWidth, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
         buffer2.addVertex(m, x + lineWidth, y + h - lineWidth, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
         buffer2.addVertex(m, x, y + h, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
         RenderSystem.disableBlend();
      }
   }

   public static void drawLineRect(PoseStack ps, int x, int y, int w, int h, int argbColor) {
      drawLineRect(ps, x, y, w, h, argbColor, 1);
   }

   public static void drawLineRect(PoseStack ps, int x, int y, int w, int h, int argbColor, int lineWidth) {
      drawLineRect(ps, x, y, w, h, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF, lineWidth);
   }

   public static void drawLineRect(PoseStack ps, int x, int y, int w, int h, int red, int green, int blue, int alpha, int lineWidth) {
      if (lineWidth >= 1 && alpha != 0) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         if (alpha != 255) {
            RenderSystem.enableBlend();
         } else {
            RenderSystem.disableBlend();
         }

         Matrix4f m = ps.last().pose();
         BufferBuilder buffer1 = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         buffer1.addVertex(m, x, y, 0.0F).setColor(red, green, blue, alpha);
         buffer1.addVertex(m, x, y + h, 0.0F).setColor(red, green, blue, alpha);
         buffer1.addVertex(m, x + lineWidth, y + h - lineWidth, 0.0F).setColor(red, green, blue, alpha);
         buffer1.addVertex(m, x + lineWidth, y + lineWidth, 0.0F).setColor(red, green, blue, alpha);
         buffer1.addVertex(m, x + w - lineWidth, y + lineWidth, 0.0F).setColor(red, green, blue, alpha);
         buffer1.addVertex(m, x + w, y, 0.0F).setColor(red, green, blue, alpha);
         BufferBuilder buffer2 = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         buffer2.addVertex(m, x + w, y + h, 0.0F).setColor(red, green, blue, alpha);
         buffer2.addVertex(m, x + w, y, 0.0F).setColor(red, green, blue, alpha);
         buffer2.addVertex(m, x + w - lineWidth, y + lineWidth, 0.0F).setColor(red, green, blue, alpha);
         buffer2.addVertex(m, x + w - lineWidth, y + h - lineWidth, 0.0F).setColor(red, green, blue, alpha);
         buffer2.addVertex(m, x + lineWidth, y + h - lineWidth, 0.0F).setColor(red, green, blue, alpha);
         buffer2.addVertex(m, x, y + h, 0.0F).setColor(red, green, blue, alpha);
         RenderSystem.disableBlend();
      }
   }

   public static void fill(PoseStack ps, int x, int y, int w, int h, int argbColor) {
      fill(ps, x, y, w, h, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF);
   }

   public static void fill(PoseStack ps, int x, int y, int w, int h, int red, int green, int blue, int alpha) {
      if (alpha != 0) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         if (alpha != 255) {
            RenderSystem.enableBlend();
         } else {
            RenderSystem.disableBlend();
         }

         Matrix4f m = ps.last().pose();
         BufferBuilder buffer = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         buffer.addVertex(m, x, y, 0.0F).setColor(red, green, blue, alpha);
         buffer.addVertex(m, x, y + h, 0.0F).setColor(red, green, blue, alpha);
         buffer.addVertex(m, x + w, y + h, 0.0F).setColor(red, green, blue, alpha);
         buffer.addVertex(m, x + w, y, 0.0F).setColor(red, green, blue, alpha);
         RenderSystem.disableBlend();
      }
   }

   public static void fillGradient(PoseStack ps, int x, int y, int w, int h, int argbColorStart, int argbColorEnd) {
      fillGradient(
         ps,
         x,
         y,
         w,
         h,
         argbColorStart >> 16 & 0xFF,
         argbColorEnd >> 16 & 0xFF,
         argbColorStart >> 8 & 0xFF,
         argbColorEnd >> 8 & 0xFF,
         argbColorStart & 0xFF,
         argbColorEnd & 0xFF,
         argbColorStart >> 24 & 0xFF,
         argbColorEnd >> 24 & 0xFF
      );
   }

   public static void fillGradient(
      PoseStack ps,
      int x,
      int y,
      int w,
      int h,
      int redStart,
      int redEnd,
      int greenStart,
      int greenEnd,
      int blueStart,
      int blueEnd,
      int alphaStart,
      int alphaEnd
   ) {
      if (alphaStart != 0 || alphaEnd != 0) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         if (alphaStart == 255 && alphaEnd == 255) {
            RenderSystem.disableBlend();
         } else {
            RenderSystem.enableBlend();
         }

         Matrix4f m = ps.last().pose();
         BufferBuilder buffer = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         buffer.addVertex(m, x, y, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
         buffer.addVertex(m, x, y + h, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
         buffer.addVertex(m, x + w, y + h, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
         buffer.addVertex(m, x + w, y, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
         RenderSystem.disableBlend();
      }
   }

   public static void hLine(PoseStack ps, int x, int xEnd, int y, int argbColor) {
      line(ps, x, y, xEnd, y, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF);
   }

   public static void hLine(PoseStack ps, int x, int xEnd, int y, int red, int green, int blue, int alpha) {
      line(ps, x, y, xEnd, y, red, green, blue, alpha);
   }

   public static void vLine(PoseStack ps, int x, int y, int yEnd, int argbColor) {
      line(ps, x, y, x, yEnd, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF);
   }

   public static void vLine(PoseStack ps, int x, int y, int yEnd, int red, int green, int blue, int alpha) {
      line(ps, x, y, x, yEnd, red, green, blue, alpha);
   }

   public static void line(PoseStack ps, int x, int y, int xEnd, int yEnd, int argbColor) {
      line(ps, x, y, xEnd, yEnd, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF);
   }

   public static void line(PoseStack ps, int x, int y, int xEnd, int yEnd, int red, int green, int blue, int alpha) {
      if (alpha != 0) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         if (alpha != 255) {
            RenderSystem.enableBlend();
         } else {
            RenderSystem.disableBlend();
         }

         Matrix4f m = ps.last().pose();
         BufferBuilder buffer = Tesselator.getInstance().begin(Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
         buffer.addVertex(m, x, y, 0.0F).setColor(red, green, blue, alpha);
         buffer.addVertex(m, xEnd, yEnd, 0.0F).setColor(red, green, blue, alpha);
         RenderSystem.disableBlend();
      }
   }

   public static void blit(PoseStack ps, ResourceLocation rl, int x, int y, int w, int h) {
      blit(ps, rl, x, y, w, h, 0.0F, 0.0F, 1.0F, 1.0F);
   }

   public static void blit(PoseStack ps, ResourceLocation rl, int x, int y, int w, int h, int u, int v, int mapW, int mapH) {
      blit(ps, rl, x, y, w, h, (float)u / mapW, (float)v / mapH, (float)(u + w) / mapW, (float)(v + h) / mapH);
   }

   public static void blit(PoseStack ps, ResourceLocation rl, int x, int y, int w, int h, int u, int v, int uW, int vH, int mapW, int mapH) {
      blit(ps, rl, x, y, w, h, (float)u / mapW, (float)v / mapH, (float)(u + uW) / mapW, (float)(v + vH) / mapH);
   }

   public static void blit(PoseStack ps, ResourceLocation rl, int x, int y, int w, int h, float uMin, float vMin, float uMax, float vMax) {
      Minecraft.getInstance().getTextureManager().bindForSetup(rl);
      RenderSystem.setShaderTexture(0, rl);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      Matrix4f m = ps.last().pose();
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);
      buffer.addVertex(m, x, y, 0.0F).setUv(uMin, vMin);
      buffer.addVertex(m, x, y + h, 0.0F).setUv(uMin, vMax);
      buffer.addVertex(m, x + w, y + h, 0.0F).setUv(uMax, vMax);
      buffer.addVertex(m, x + w, y, 0.0F).setUv(uMax, vMin);
   }

   protected static void blitRepeatable(
      PoseStack ps,
      ResourceLocation rl,
      int x,
      int y,
      int width,
      int height,
      int u,
      int v,
      int uWidth,
      int vHeight,
      int textureWidth,
      int textureHeight,
      int uRepeat,
      int vRepeat,
      int repeatWidth,
      int repeatHeight
   ) {
      if (uRepeat >= 0
         && vRepeat >= 0
         && uRepeat < uWidth
         && vRepeat < vHeight
         && repeatWidth >= 1
         && repeatHeight >= 1
         && repeatWidth <= uWidth - uRepeat
         && repeatHeight <= vHeight - vRepeat) {
         int repeatCountX = Math.max(1, Math.max(0, width - (uWidth - repeatWidth)) / repeatWidth);
         int repeatCountY = Math.max(1, Math.max(0, height - (vHeight - repeatHeight)) / repeatHeight);
         Matrix4f mat = ps.last().pose();
         BufferBuilder buffer = Tesselator.getInstance().begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);

         for (int i = 0; i < repeatCountX; i++) {
            int uAdjust = i == 0 ? 0 : uRepeat;
            int xStart = x + uAdjust + i * repeatWidth;
            int w = Math.min(repeatWidth + uRepeat - uAdjust, width - (uWidth - uRepeat - repeatWidth));
            float minU = (float)(u + uAdjust) / textureWidth;
            float maxU = (float)(u + uAdjust + w) / textureWidth;

            for (int j = 0; j < repeatCountY; j++) {
               int vAdjust = j == 0 ? 0 : vRepeat;
               int yStart = y + vAdjust + j * repeatHeight;
               int h = Math.min(repeatHeight + vRepeat - vAdjust, height - (vHeight - vRepeat - repeatHeight));
               float minV = (float)(v + vAdjust) / textureHeight;
               float maxV = (float)(v + vAdjust + h) / textureHeight;
               populateBlitTriangles(buffer, mat, xStart, xStart + w, yStart, yStart + h, minU, maxU, minV, maxV);
            }
         }

         int xEnd = x + Math.min(uRepeat + repeatCountX * repeatWidth, width - (uWidth - uRepeat - repeatWidth));
         int yEnd = y + Math.min(vRepeat + repeatCountY * repeatHeight, height - (vHeight - vRepeat - repeatHeight));
         int uLeft = width - (xEnd - x);
         int vLeft = height - (yEnd - y);
         float restMinU = (float)(u + uWidth - uLeft) / textureWidth;
         float restMaxU = (float)(u + uWidth) / textureWidth;
         float restMinV = (float)(v + vHeight - vLeft) / textureHeight;
         float restMaxV = (float)(v + vHeight) / textureHeight;

         for (int i = 0; i < repeatCountX; i++) {
            int uAdjust = i == 0 ? 0 : uRepeat;
            int xStart = x + uAdjust + i * repeatWidth;
            int w = Math.min(repeatWidth + uRepeat - uAdjust, width - uLeft);
            float minU = (float)(u + uAdjust) / textureWidth;
            float maxU = (float)(u + uAdjust + w) / textureWidth;
            populateBlitTriangles(buffer, mat, xStart, xStart + w, yEnd, yEnd + vLeft, minU, maxU, restMinV, restMaxV);
         }

         for (int j = 0; j < repeatCountY; j++) {
            int vAdjust = j == 0 ? 0 : vRepeat;
            int yStart = y + vAdjust + j * repeatHeight;
            int h = Math.min(repeatHeight + vRepeat - vAdjust, height - vLeft);
            float minV = (float)(v + vAdjust) / textureHeight;
            float maxV = (float)(v + vAdjust + h) / textureHeight;
            populateBlitTriangles(buffer, mat, xEnd, xEnd + uLeft, yStart, yStart + h, restMinU, restMaxU, minV, maxV);
         }

         populateBlitTriangles(buffer, mat, xEnd, xEnd + uLeft, yEnd, yEnd + vLeft, restMinU, restMaxU, restMinV, restMaxV);
         Minecraft.getInstance().getTextureManager().bindForSetup(rl);
         RenderSystem.setShaderTexture(0, rl);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
      } else {
         throw new IllegalArgumentException("Repeatable box is outside of texture box");
      }
   }

   public static void populateFillTriangles(Matrix4f m, BufferBuilder buffer, int x, int y, int w, int h, int red, int green, int blue, int alpha) {
      buffer.addVertex(m, x, y, 0.0F).setColor(red, green, blue, alpha);
      buffer.addVertex(m, x, y + h, 0.0F).setColor(red, green, blue, alpha);
      buffer.addVertex(m, x + w, y, 0.0F).setColor(red, green, blue, alpha);
      buffer.addVertex(m, x + w, y, 0.0F).setColor(red, green, blue, alpha);
      buffer.addVertex(m, x, y + h, 0.0F).setColor(red, green, blue, alpha);
      buffer.addVertex(m, x + w, y + h, 0.0F).setColor(red, green, blue, alpha);
   }

   public static void populateFillGradientTriangles(
      Matrix4f m,
      BufferBuilder buffer,
      int x,
      int y,
      int w,
      int h,
      int redStart,
      int redEnd,
      int greenStart,
      int greenEnd,
      int blueStart,
      int blueEnd,
      int alphaStart,
      int alphaEnd
   ) {
      buffer.addVertex(m, x, y, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
      buffer.addVertex(m, x, y + h, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
      buffer.addVertex(m, x + w, y, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
      buffer.addVertex(m, x + w, y, 0.0F).setColor(redStart, greenStart, blueStart, alphaStart);
      buffer.addVertex(m, x, y + h, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
      buffer.addVertex(m, x + w, y + h, 0.0F).setColor(redEnd, greenEnd, blueEnd, alphaEnd);
   }

   public static void populateBlitTriangles(
      BufferBuilder buffer, Matrix4f mat, float xStart, float xEnd, float yStart, float yEnd, float uMin, float uMax, float vMin, float vMax
   ) {
      buffer.addVertex(mat, xStart, yStart, 0.0F).setUv(uMin, vMin);
      buffer.addVertex(mat, xStart, yEnd, 0.0F).setUv(uMin, vMax);
      buffer.addVertex(mat, xEnd, yStart, 0.0F).setUv(uMax, vMin);
      buffer.addVertex(mat, xEnd, yStart, 0.0F).setUv(uMax, vMin);
      buffer.addVertex(mat, xStart, yEnd, 0.0F).setUv(uMin, vMax);
      buffer.addVertex(mat, xEnd, yEnd, 0.0F).setUv(uMax, vMax);
   }

   public static void drawEntity(PoseStack poseStack, int x, int y, double scale, float headYaw, float yaw, float pitch, Entity entity) {
      LivingEntity livingEntity = entity instanceof LivingEntity ? (LivingEntity)entity : null;
      Minecraft mc = Minecraft.getInstance();
      poseStack.pushPose();
      poseStack.translate(x, y, 1050.0F);
      poseStack.scale(1.0F, 1.0F, -1.0F);
      poseStack.translate(0.0, 0.0, 1000.0);
      poseStack.scale((float)scale, (float)scale, (float)scale);
      Quaternionf pitchRotation = Axis.XP.rotationDegrees(pitch);
      poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      poseStack.mulPose(pitchRotation);
      float oldYaw = entity.getYRot();
      float oldPitch = entity.getXRot();
      float oldYawOffset = livingEntity == null ? 0.0F : livingEntity.yBodyRot;
      float oldPrevYawHead = livingEntity == null ? 0.0F : livingEntity.yHeadRotO;
      float oldYawHead = livingEntity == null ? 0.0F : livingEntity.yHeadRot;
      entity.setYRot(180.0F + headYaw);
      entity.setXRot(-pitch);
      if (livingEntity != null) {
         livingEntity.yBodyRot = 180.0F + yaw;
         livingEntity.yHeadRot = entity.getYRot();
         livingEntity.yHeadRotO = entity.getYRot();
      }

      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
      pitchRotation.conjugate();
      dispatcher.overrideCameraOrientation(pitchRotation);
      dispatcher.setRenderShadow(false);
      BufferSource buffers = mc.renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, buffers, 15728880));
      buffers.endBatch();
      dispatcher.setRenderShadow(true);
      entity.setYRot(oldYaw);
      entity.setXRot(oldPitch);
      if (livingEntity != null) {
         livingEntity.yBodyRot = oldYawOffset;
         livingEntity.yHeadRotO = oldPrevYawHead;
         livingEntity.yHeadRot = oldYawHead;
      }

      poseStack.popPose();
      Lighting.setupFor3DItems();
   }
}
