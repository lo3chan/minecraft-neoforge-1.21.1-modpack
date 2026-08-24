package com.github.alexthe666.alexsmobs.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class AMRenderCompat {
   public static void renderToBuffer(
      Model model, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      model.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, packColor(red, green, blue, alpha));
   }

   public static int packColor(float red, float green, float blue, float alpha) {
      return ((int)(alpha * 255.0F) & 0xFF) << 24 | ((int)(red * 255.0F) & 0xFF) << 16 | ((int)(green * 255.0F) & 0xFF) << 8 | (int)(blue * 255.0F) & 0xFF;
   }

   public static float[] dyeColorArray(DyeColor color) {
      int packed = Sheep.getColor(color);
      return new float[]{ARGB32.red(packed) / 255.0F, ARGB32.green(packed) / 255.0F, ARGB32.blue(packed) / 255.0F};
   }

   public static EntityModel<?> rendererModel(Entity entity) {
      return Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof LivingEntityRenderer living ? living.getModel() : null;
   }

   public static <E extends Entity> void renderEntity(E entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
      Object render = null;

      try {
         render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
         if (render != null) {
            ((EntityRenderer)render).render(entity, yaw, partialTick, poseStack, buffers, packedLight);
         }
      } catch (Throwable var10) {
         CrashReport crashreport = CrashReport.forThrowable(var10, "Rendering entity in world");
         entity.fillCrashReportCategory(crashreport.addCategory("Entity being rendered"));
         CrashReportCategory category = crashreport.addCategory("Renderer details");
         category.setDetail("Assigned renderer", render);
         category.setDetail("Rotation", yaw);
         category.setDetail("Delta", partialTick);
         throw new ReportedException(crashreport);
      }
   }

   public static Quaternionf cameraOrientation(EntityRenderDispatcher dispatcher) {
      return dispatcher.cameraOrientation();
   }

   public static void renderSingleBlock(BlockState state, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, buffers, packedLight, packedOverlay);
   }

   public static int blockTintColor(BlockState state, int layer) {
      return Minecraft.getInstance().getBlockColors().getColor(state, null, null, layer);
   }

   public static TextureAtlasSprite blockParticleSprite(BlockState state) {
      return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state).getParticleIcon();
   }

   public static void runAsFancy(Runnable runnable) {
      RenderSystem.runAsFancy(runnable);
   }

   public static void blit(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
      guiGraphics.blit(texture, x, y, uOffset, vOffset, uWidth, vHeight);
   }

   public static void translateGui(GuiGraphics guiGraphics, double x, double y, double z) {
      guiGraphics.pose().translate(x, y, z);
   }

   public static void scaleGui(GuiGraphics guiGraphics, float x, float y, float z) {
      guiGraphics.pose().scale(x, y, z);
   }

   public static void guiEntityFullBright(Object renderState) {
   }

   public static void blitTinted(
      GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight, int argb
   ) {
      guiGraphics.blit(texture, x, y, (int)u, (int)v, width, height, texWidth, texHeight);
   }

   public static void blitTintedUV(
      GuiGraphics guiGraphics, ResourceLocation texture, int startX, int startY, int endX, int endY, float u0, float u1, float v0, float v1, int argb
   ) {
      int ref = 4096;
      float u = u0 * 4096.0F;
      float v = v0 * 4096.0F;
      int srcWidth = Math.round((u1 - u0) * 4096.0F);
      int srcHeight = Math.round((v1 - v0) * 4096.0F);
      int width = endX - startX;
      int height = endY - startY;
      guiGraphics.blit(texture, startX, startY, width, height, u, v, srcWidth, srcHeight, 4096, 4096);
   }

   public static VertexConsumer armorFoilBuffer(MultiBufferSource source, RenderType renderType, boolean withGlint) {
      return ItemRenderer.getArmorFoilBuffer(source, renderType, withGlint);
   }

   public static void drawTextInBatch(
      Font font,
      String text,
      float x,
      float y,
      int color,
      boolean dropShadow,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int backgroundColor,
      int packedLight
   ) {
      font.drawInBatch(text, x, y, color, dropShadow, poseStack.last().pose(), buffer, DisplayMode.NORMAL, backgroundColor, packedLight);
   }

   public static void renderItemInHand(
      ItemInHandRenderer renderer, LivingEntity entity, ItemStack stack, ItemDisplayContext ctx, boolean left, PoseStack ps, MultiBufferSource buf, int light
   ) {
      renderer.renderItem(entity, stack, ctx, left, ps, buf, light);
   }

   public static void renderItemStatic(
      ItemStack stack, ItemDisplayContext ctx, int packedLight, int packedOverlay, PoseStack ps, MultiBufferSource buf, Level level, int seed
   ) {
      Minecraft.getInstance().getItemRenderer().renderStatic(stack, ctx, packedLight, packedOverlay, ps, buf, level, seed);
   }

   public static void setAllVisible(HumanoidModel<?> model, boolean visible) {
      model.setAllVisible(visible);
   }

   public static ModelLayerLocation armorStandArmorLayer(EquipmentSlot slot) {
      return slot == EquipmentSlot.LEGS ? ModelLayers.ARMOR_STAND_INNER_ARMOR : ModelLayers.ARMOR_STAND_OUTER_ARMOR;
   }
}
