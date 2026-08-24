package net.mehvahdjukaar.moonlight.api.client.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.BiConsumer;
import net.mehvahdjukaar.moonlight.api.client.util.platform.RenderUtilImpl;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.MoonlightClient;
import net.mehvahdjukaar.moonlight.core.client.MLRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class RenderUtil {
   public static void renderBlock(
      long seed, PoseStack poseStack, MultiBufferSource buffer, BlockState state, Level level, BlockPos pos, BlockRenderDispatcher dispatcher
   ) {
      BakedModel model = dispatcher.getBlockModel(state);
      renderBlock(model, seed, poseStack, buffer, state, level, pos, dispatcher);
   }

   public static void renderModel(
      ModelResourceLocation modelLocation,
      PoseStack matrixStack,
      MultiBufferSource buffer,
      BlockRenderDispatcher blockRenderer,
      int light,
      int overlay,
      boolean cutout
   ) {
      blockRenderer.getModelRenderer()
         .renderModel(
            matrixStack.last(),
            buffer.getBuffer(cutout ? Sheets.cutoutBlockSheet() : Sheets.solidBlockSheet()),
            null,
            ClientHelper.getModel(blockRenderer.getBlockModelShaper().getModelManager(), modelLocation),
            1.0F,
            1.0F,
            1.0F,
            light,
            overlay
         );
   }

   public static void renderGuiItemRelative(
      PoseStack poseStack, ItemStack stack, int x, int y, ItemRenderer renderer, BiConsumer<PoseStack, BakedModel> movement
   ) {
      renderGuiItemRelative(poseStack, stack, x, y, renderer, movement, 15728880, OverlayTexture.NO_OVERLAY);
   }

   public static void renderGuiItemRelative(
      PoseStack poseStack,
      ItemStack stack,
      int x,
      int y,
      ItemRenderer renderer,
      BiConsumer<PoseStack, BakedModel> movement,
      int combinedLight,
      int pCombinedOverlay
   ) {
      BakedModel model = renderer.getModel(stack, null, null, 0);
      int l = 0;
      poseStack.pushPose();
      poseStack.translate(x + 8, y + 8, 150 + (model.isGui3d() ? l : 0));
      poseStack.mulPose(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
      poseStack.scale(16.0F, 16.0F, 16.0F);
      BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      boolean flag = !model.usesBlockLight();
      if (flag) {
         Lighting.setupForFlatItems();
      } else {
         Lighting.setupFor3DItems();
      }

      ItemDisplayContext pTransformType = ItemDisplayContext.GUI;
      model = handleCameraTransforms(model, poseStack, pTransformType);
      movement.accept(poseStack, model);
      renderer.render(stack, ItemDisplayContext.NONE, false, poseStack, bufferSource, combinedLight, pCombinedOverlay, model);
      bufferSource.endBatch();
      RenderSystem.enableDepthTest();
      if (flag) {
         Lighting.setupFor3DItems();
      }

      poseStack.popPose();
   }

   @Deprecated(
      forRemoval = true
   )
   public static GuiGraphics getGuiDummy(PoseStack poseStack) {
      Minecraft mc = Minecraft.getInstance();
      GuiGraphics p = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
      p.pose().setIdentity();
      p.pose().mulPose(poseStack.last().pose());
      return p;
   }

   public static void blitSpriteSection(GuiGraphics graphics, int x, int y, int w, int h, float u, float v, int uW, int vH, TextureAtlasSprite sprite) {
      SpriteContents c = sprite.contents();
      int width = (int)(c.width() / (sprite.getU1() - sprite.getU0()));
      int height = (int)(c.height() / (sprite.getV1() - sprite.getV0()));
      graphics.blit(sprite.atlasLocation(), x, y, w, h, sprite.getU(u) * width, height * sprite.getV(v), uW, vH, width, height);
   }

   public static void renderSprite(PoseStack stack, VertexConsumer vertexBuilder, int light, int b, int g, int r, TextureAtlasSprite sprite) {
      renderSprite(stack, vertexBuilder, light, b, g, r, 255, sprite);
   }

   public static void renderSprite(PoseStack stack, VertexConsumer vertexBuilder, int light, int b, int g, int r, int a, TextureAtlasSprite sprite) {
      Matrix4f matrix4f1 = stack.last().pose();
      float u0 = sprite.getU(0.0F);
      float u1 = sprite.getU(1.0F);
      float h = (u0 + u1) / 2.0F;
      float v0 = sprite.getV(0.0F);
      float v1 = sprite.getV(1.0F);
      float k = (v0 + v1) / 2.0F;
      float shrink = sprite.uvShrinkRatio();
      float u0s = Mth.lerp(shrink, u0, h);
      float u1s = Mth.lerp(shrink, u1, h);
      float v0s = Mth.lerp(shrink, v0, k);
      float v1s = Mth.lerp(shrink, v1, k);
      vertexBuilder.addVertex(matrix4f1, -1.0F, 1.0F, 0.0F).setColor(r, g, b, a).setUv(u0s, v1s).setLight(light);
      vertexBuilder.addVertex(matrix4f1, 1.0F, 1.0F, 0.0F).setColor(r, g, b, a).setUv(u1s, v1s).setLight(light);
      vertexBuilder.addVertex(matrix4f1, 1.0F, -1.0F, 0.0F).setColor(r, g, b, a).setUv(u1s, v0s).setLight(light);
      vertexBuilder.addVertex(matrix4f1, -1.0F, -1.0F, 0.0F).setColor(r, g, b, a).setUv(u0s, v0s).setLight(light);
   }

   public static RenderType getTextMipmapRenderType(ResourceLocation texture) {
      return MLRenderTypes.TEXT_MIP.apply(texture);
   }

   public static RenderType getEntityCutoutMipmapRenderType(ResourceLocation texture) {
      return MLRenderTypes.ENTITY_CUTOUT_MIP.apply(texture);
   }

   public static RenderType getEntitySolidMipmapRenderType(ResourceLocation texture) {
      return MLRenderTypes.ENTITY_SOLID_MIP.apply(texture);
   }

   public static RenderType getColoredTextureRenderType(ResourceLocation texture) {
      return MLRenderTypes.COLOR_TEXT.apply(texture);
   }

   public static void setDynamicTexturesToUseMipmap(boolean mipMap) {
      MoonlightClient.setMipMap(mipMap);
   }

   public static ModelResourceLocation getStandaloneModelLocation(ResourceLocation location) {
      return new ModelResourceLocation(location, PlatHelper.getPlatform().isFabric() ? "fabric_resource" : "standalone");
   }

   public static void renderBlock(
      BakedModel var0, long var1, PoseStack var3, MultiBufferSource var4, BlockState var5, Level var6, BlockPos var7, BlockRenderDispatcher var8
   ) {
      RenderUtilImpl.renderBlock(var0, var1, var3, var4, var5, var6, var7, var8);
   }

   private static BakedModel handleCameraTransforms(BakedModel var0, PoseStack var1, ItemDisplayContext var2) {
      return RenderUtilImpl.handleCameraTransforms(var0, var1, var2);
   }
}
