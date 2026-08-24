package jeresources.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import java.nio.FloatBuffer;
import jeresources.api.render.IMobRenderHook;
import jeresources.api.render.IScissorHook;
import jeresources.compatibility.api.MobRegistryImpl;
import jeresources.reference.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;

public class RenderHelper {
   public static void drawLine(GuiGraphics guiGraphics, int xBegin, int yBegin, int xEnd, int yEnd, int color) {
      xEnd += xBegin == xEnd ? 1 : 0;
      yEnd += yBegin == yEnd ? 1 : 0;
      guiGraphics.fill(xBegin, yBegin, xEnd, yEnd, color);
   }

   public static void renderEntity(GuiGraphics guiGraphics, int x, int y, double scale, double yaw, double pitch, LivingEntity livingEntity) {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix();
      modelViewStack.mul(guiGraphics.pose().last().pose());
      modelViewStack.translate(x, y, 50.0F);
      modelViewStack.scale((float)(-scale), (float)scale, (float)scale);
      PoseStack mobPoseStack = new PoseStack();
      mobPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      IMobRenderHook.RenderInfo renderInfo = MobRegistryImpl.applyRenderHooks(livingEntity, new IMobRenderHook.RenderInfo(x, y, scale, yaw, pitch));
      x = renderInfo.x;
      y = renderInfo.y;
      scale = renderInfo.scale;
      yaw = renderInfo.yaw;
      pitch = renderInfo.pitch;
      mobPoseStack.mulPose(Axis.XN.rotationDegrees((float)Math.atan(pitch / 40.0) * 20.0F));
      livingEntity.yo = (float)Math.atan(yaw / 40.0) * 20.0F;
      float yRot = (float)Math.atan(yaw / 40.0) * 40.0F;
      float xRot = -((float)Math.atan(pitch / 40.0)) * 20.0F;
      livingEntity.setYRot(yRot);
      livingEntity.setYRot(yRot);
      livingEntity.setXRot(xRot);
      livingEntity.yHeadRot = yRot;
      livingEntity.yHeadRotO = yRot;
      mobPoseStack.translate(0.0, livingEntity.getY(), 0.0);
      RenderSystem.applyModelViewMatrix();
      EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      entityRenderDispatcher.setRenderShadow(false);
      BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(livingEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, mobPoseStack, bufferSource, 15728880));
      bufferSource.endBatch();
      entityRenderDispatcher.setRenderShadow(true);
      modelViewStack.popMatrix();
      RenderSystem.applyModelViewMatrix();
   }

   public static void renderChest(GuiGraphics guiGraphics, float x, float y, float rotate, float scale, float lidAngle) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, Resources.Vanilla.CHEST);
      guiGraphics.pose().pushPose();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.pose().translate(x, y, 50.0F);
      guiGraphics.pose().mulPose(new Quaternionf(-160.0F, 1.0F, 0.0F, 0.0F));
      guiGraphics.pose().scale(scale, -scale, -scale);
      guiGraphics.pose().translate(0.5F, 0.5F, 0.5F);
      guiGraphics.pose().mulPose(new Quaternionf(rotate, 0.0F, 1.0F, 0.0F));
      guiGraphics.pose().translate(-0.5F, -0.5F, -0.5F);
      float lidAngleF = lidAngle / 180.0F;
      lidAngleF = 1.0F - lidAngleF;
      lidAngleF = 1.0F - lidAngleF * lidAngleF * lidAngleF;
      guiGraphics.pose().popPose();
   }

   public static void renderBlock(GuiGraphics guiGraphics, BlockState block, float x, float y, float z, float rotate, float scale) {
      Minecraft mc = Minecraft.getInstance();
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(x, y, z);
      guiGraphics.pose().scale(-scale, -scale, -scale);
      guiGraphics.pose().translate(-0.5F, -0.5F, 0.0F);
      guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-30.0F));
      guiGraphics.pose().translate(0.5F, 0.0F, -0.5F);
      guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(rotate));
      guiGraphics.pose().translate(-0.5F, 0.0F, 0.5F);
      guiGraphics.pose().pushPose();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.pose().translate(0.0F, 0.0F, -1.0F);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      BufferSource bufferSource = mc.renderBuffers().bufferSource();
      mc.getBlockRenderer().renderSingleBlock(block, guiGraphics.pose(), bufferSource, 15728880, OverlayTexture.NO_OVERLAY);
      bufferSource.endBatch();
      guiGraphics.pose().popPose();
      guiGraphics.pose().popPose();
   }

   public static void scissor(GuiGraphics guiGraphics, int x, int y, int w, int h) {
      double scale = Minecraft.getInstance().getWindow().getGuiScale();
      double[] xyzTranslation = getGLTranslation(guiGraphics, scale);
      x = (int)(x * scale);
      y = (int)(y * scale);
      w = (int)(w * scale);
      h = (int)(h * scale);
      int scissorX = Math.round((float)Math.round(xyzTranslation[0] + x));
      int scissorY = Math.round((float)Math.round(Minecraft.getInstance().getWindow().getHeight() - y - xyzTranslation[1]));
      int scissorW = Math.round((float)(w - x));
      int scissorH = Math.round((float)(h - y));
      IScissorHook.ScissorInfo scissorInfo = MobRegistryImpl.applyScissorHooks(new IScissorHook.ScissorInfo(scissorX, scissorY, scissorW, scissorH));
      RenderSystem.enableScissor(scissorInfo.x, scissorInfo.y, Math.max(0, scissorInfo.width), Math.max(0, scissorInfo.height));
   }

   public static void stopScissor() {
      RenderSystem.disableScissor();
   }

   public static void drawTexture(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height, ResourceLocation resource) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, resource);
      drawTexturedModalRect(guiGraphics, x, y, u, v, width, height, 0.0F);
   }

   public static double[] getGLTranslation(GuiGraphics guiGraphics, double scale) {
      Matrix4f matrix = guiGraphics.pose().last().pose();
      FloatBuffer buf = BufferUtils.createFloatBuffer(16);
      matrix.set(buf);
      return new double[]{buf.get(getIndexFloatBuffer(0, 3)) * scale, buf.get(getIndexFloatBuffer(1, 3)) * scale, buf.get(getIndexFloatBuffer(2, 3)) * scale};
   }

   private static int getIndexFloatBuffer(int x, int y) {
      return y * 4 + x;
   }

   public static double getGuiScaleFactor() {
      return Minecraft.getInstance().getWindow().getGuiScale();
   }

   public static void drawTexturedModalRect(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height, float zLevel) {
      float uScale = 0.00390625F;
      float vScale = 0.00390625F;
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder buffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      Matrix4f matrix = guiGraphics.pose().last().pose();
      buffer.addVertex(matrix, x, y + height, zLevel).setUv(u * 0.00390625F, (v + height) * 0.00390625F);
      buffer.addVertex(matrix, x + width, y + height, zLevel).setUv((u + width) * 0.00390625F, (v + height) * 0.00390625F);
      buffer.addVertex(matrix, x + width, y, zLevel).setUv((u + width) * 0.00390625F, v * 0.00390625F);
      buffer.addVertex(matrix, x, y, zLevel).setUv(u * 0.00390625F, v * 0.00390625F);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
   }
}
