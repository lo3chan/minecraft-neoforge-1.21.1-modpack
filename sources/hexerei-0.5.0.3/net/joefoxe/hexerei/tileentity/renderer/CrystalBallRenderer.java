package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.Collection;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.CrystalBall;
import net.joefoxe.hexerei.client.renderer.ModRenderTypes;
import net.joefoxe.hexerei.data.recipes.MoonPhases;
import net.joefoxe.hexerei.tileentity.CrystalBallTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CrystalBallRenderer implements BlockEntityRenderer<CrystalBallTile> {
   public static ModelResourceLocation ORB = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hexerei", "block/crystal_ball_orb1"));
   public static ModelResourceLocation ORB2 = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hexerei", "block/crystal_ball_orb2"));

   public void render(
      CrystalBallTile tileEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
         && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CrystalBallTile) {
         this.renderMoon(tileEntityIn, poseStack, partialTicks, bufferIn);
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.5F, 0.5F);
         poseStack.translate(0.0F, tileEntityIn.orbOffset / 16.0F, 0.0F);
         poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.rotLerp(partialTicks, tileEntityIn.degreesSpunOld, tileEntityIn.degreesSpun) * 4.0F));
         if (bufferIn instanceof BufferSource bufferSource) {
            bufferSource.endBatch();
         }

         BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ORB2);
         if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            int i = -1;
            float f = (i >> 16 & 0xFF) / 255.0F;
            float f1 = (i >> 8 & 0xFF) / 255.0F;
            float f2 = (i & 0xFF) / 255.0F;

            for (RenderType rt : baseModel.getRenderTypes(
               ((CrystalBall)ModBlocks.CRYSTAL_BALL.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
            )) {
               dispatcher.getModelRenderer()
                  .renderModel(
                     poseStack.last(),
                     bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                     ((CrystalBall)ModBlocks.CRYSTAL_BALL.get()).defaultBlockState(),
                     baseModel,
                     f,
                     f1,
                     f2,
                     combinedLightIn,
                     combinedOverlayIn,
                     ModelData.EMPTY,
                     rt
                  );
            }
         }

         if (bufferIn instanceof BufferSource bufferSource) {
            bufferSource.endBatch();
         }

         baseModel = Minecraft.getInstance().getModelManager().getModel(ORB);
         if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            int i = -1;
            float f = (i >> 16 & 0xFF) / 255.0F;
            float f1 = (i >> 8 & 0xFF) / 255.0F;
            float f2 = (i & 0xFF) / 255.0F;

            for (RenderType rt : baseModel.getRenderTypes(
               ((CrystalBall)ModBlocks.CRYSTAL_BALL.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
            )) {
               dispatcher.getModelRenderer()
                  .renderModel(
                     poseStack.last(),
                     bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                     ((CrystalBall)ModBlocks.CRYSTAL_BALL.get()).defaultBlockState(),
                     baseModel,
                     f,
                     f1,
                     f2,
                     combinedLightIn,
                     combinedOverlayIn,
                     ModelData.EMPTY,
                     rt
                  );
            }
         }

         if (bufferIn instanceof BufferSource bufferSource) {
            bufferSource.endBatch();
         }

         poseStack.popPose();
         poseStack.pushPose();
         this.renderBlock(poseStack, bufferIn, combinedLightIn, ((Block)ModBlocks.CRYSTAL_BALL_STAND.get()).defaultBlockState());
         poseStack.popPose();
      }
   }

   public void renderMoon(CrystalBallTile tileEntityIn, PoseStack poseStack, float partialTicks, MultiBufferSource bufferIn) {
      int xOffset = 0;
      int yOffset = 0;
      switch (MoonPhases.MoonCondition.getMoonPhase(tileEntityIn.getLevel())) {
         case NONE:
            xOffset = 12;
            yOffset = 76;
            break;
         case NEW_MOON:
            xOffset = 12;
            yOffset = 44;
            break;
         case WAXING_CRESCENT:
            xOffset = 44;
            yOffset = 44;
            break;
         case FIRST_QUARTER:
            xOffset = 76;
            yOffset = 44;
            break;
         case WAXING_GIBBOUS:
            xOffset = 108;
            yOffset = 44;
            break;
         case FULL_MOON:
            xOffset = 12;
            yOffset = 12;
            break;
         case WANING_GIBBOUS:
            xOffset = 44;
            yOffset = 12;
            break;
         case LAST_QUARTER:
            xOffset = 76;
            yOffset = 12;
            break;
         case WANING_CRESCENT:
            xOffset = 108;
            yOffset = 12;
      }

      this.renderQuad(
         tileEntityIn,
         poseStack,
         xOffset,
         yOffset,
         bufferIn.getBuffer(ModRenderTypes.entityTranslucent(HexereiUtil.getResource("textures/gui/moon_phases.png"))),
         partialTicks
      );
      if (bufferIn instanceof BufferSource bufferSource) {
         bufferSource.endBatch();
      }
   }

   public void renderQuad(CrystalBallTile tileEntityIn, PoseStack poseStack, int xOffset, int yOffset, VertexConsumer consumer, float partialTicks) {
      Vector3f[] bottomVertices = new Vector3f[]{
         new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 0.0F)
      };
      Vector3f[] topVertices = new Vector3f[]{
         new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(1.0F, 1.0F, 0.0F)
      };
      applyWobble(bottomVertices, 0.0F);
      applyWobble(topVertices, 0.5F);
      Collection<Vector3f[]> offsetMap = new ArrayList<>();

      for (int i = 0; i < 4; i++) {
         offsetMap.add(new Vector3f[]{bottomVertices[(i + 1) % 4], bottomVertices[i], topVertices[i % 4], topVertices[(i + 1) % 4]});
      }

      poseStack.pushPose();
      poseStack.translate(0.5F, 0.5F, 0.5F);
      poseStack.translate(0.0F, tileEntityIn.orbOffset / 16.0F, 0.0F);
      float inc = Math.max(0.0F, Math.abs(tileEntityIn.centerYawIncrement) - 10.0F) / 90.0F;
      float vscale = 1.0F - inc * 0.59F;
      float hscale = 1.0F + inc * 0.59F;
      poseStack.mulPose(Axis.YP.rotationDegrees(-lerpAngle(tileEntityIn.centerYawO, tileEntityIn.centerYaw, partialTicks)));
      poseStack.mulPose(Axis.XP.rotationDegrees(lerpAngle(tileEntityIn.centerPitchO, tileEntityIn.centerPitch, partialTicks)));
      poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      float scale = 0.18F + 0.07F * (1.0F - inc);
      poseStack.scale(scale, scale, scale);
      drawWobblyCube(
         poseStack, 0.8F * vscale, 0.8F * hscale, 0.86F * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset
      );
      drawWobblyCube(
         poseStack, 0.68F * vscale, 0.68F * hscale, 0.5F * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset
      );
      drawWobblyCube(
         poseStack, 0.896F * vscale, 0.896F * hscale, 0.6F * tileEntityIn.moonAlpha, offsetMap, bottomVertices, topVertices, consumer, xOffset, yOffset
      );
      poseStack.popPose();
   }

   public static float lerpAngle(float startAngle, float endAngle, float alpha) {
      startAngle = normalizeAngle(startAngle);
      endAngle = normalizeAngle(endAngle);
      float difference = endAngle - startAngle;
      if (difference > 180.0F) {
         difference -= 360.0F;
      } else if (difference < -180.0F) {
         difference += 360.0F;
      }

      return normalizeAngle(startAngle + alpha * difference);
   }

   private static float normalizeAngle(float angle) {
      while (angle > 180.0F) {
         angle -= 360.0F;
      }

      while (angle < -180.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public static void drawWobblyCube(
      PoseStack poseStack,
      float vscale,
      float hscale,
      float alpha,
      Collection<Vector3f[]> offsetMap,
      Vector3f[] bottomVertices,
      Vector3f[] topVertices,
      VertexConsumer consumer,
      int xOffset,
      int yOffset
   ) {
      poseStack.pushPose();
      poseStack.scale(hscale, vscale, hscale);
      drawSide(poseStack, alpha, offsetMap.stream().toList().get(0), consumer, xOffset, yOffset);
      drawSide(poseStack, alpha, offsetMap.stream().toList().get(1), consumer, xOffset + 8, yOffset);
      drawSide(poseStack, alpha, offsetMap.stream().toList().get(2), consumer, xOffset + 8, yOffset + 8);
      drawSide(poseStack, alpha, offsetMap.stream().toList().get(3), consumer, xOffset + 16, yOffset);
      drawSide(
         poseStack, alpha, new Vector3f[]{bottomVertices[3], bottomVertices[2], bottomVertices[1], bottomVertices[0]}, consumer, xOffset + 16, yOffset + 8
      );
      drawSide(poseStack, alpha, topVertices, consumer, xOffset, yOffset + 8);
      poseStack.popPose();
   }

   public static void drawSide(PoseStack poseStack, float alpha, Vector3f[] offsets, VertexConsumer consumer, int xOffset, int yOffset) {
      poseStack.pushPose();
      poseStack.translate(-0.5F, -0.5F, -0.5F);
      Matrix4f matrix = poseStack.last().pose();
      consumer.addVertex(matrix, offsets[0].x(), offsets[0].y(), offsets[0].z())
         .setColor(1.0F, 1.0F, 1.0F, alpha)
         .setUv((xOffset + 8) / 256.0F, (yOffset + 8) / 256.0F)
         .setNormal(0.0F, 1.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880);
      consumer.addVertex(matrix, offsets[1].x(), offsets[1].y(), offsets[1].z())
         .setColor(1.0F, 1.0F, 1.0F, alpha)
         .setUv(xOffset / 256.0F, (yOffset + 8) / 256.0F)
         .setNormal(0.0F, 1.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880);
      consumer.addVertex(matrix, offsets[2].x(), offsets[2].y(), offsets[2].z())
         .setColor(1.0F, 1.0F, 1.0F, alpha)
         .setUv(xOffset / 256.0F, yOffset / 256.0F)
         .setNormal(0.0F, 1.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880);
      consumer.addVertex(matrix, offsets[3].x(), offsets[3].y(), offsets[3].z())
         .setColor(1.0F, 1.0F, 1.0F, alpha)
         .setUv((xOffset + 8) / 256.0F, yOffset / 256.0F)
         .setNormal(0.0F, 1.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880);
      poseStack.popPose();
   }

   public static void applyWobble(Vector3f[] offsets, float initialOffset) {
      applyWobble(offsets, initialOffset, 0.025F);
   }

   public static void applyWobble(Vector3f[] offsets, float initialOffset, float strength) {
      float value = initialOffset;

      for (Vector3f vector3f : offsets) {
         float sine = Mth.sin((float)((float)Minecraft.getInstance().level.getGameTime() / 10.0F + value * 3.141592653589793 * 2.0)) * strength;
         vector3f.add(sine, -sine, sine);
         value += 0.25F;
      }
   }

   private void renderItem(ItemStack stack, Level level, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, poseStack, bufferIn, level, 1);
   }

   private void renderBlock(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
      Minecraft.getInstance()
         .getBlockRenderer()
         .renderSingleBlock(state, poseStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
   }

   private void renderBlock(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, float red, float green, float blue) {
      this.renderSingleBlock(state, poseStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, red, green, blue);
   }

   private void renderBlock(
      PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BlockState state, RenderType renderType, int color
   ) {
      this.renderSingleBlock(state, poseStack, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType, color);
   }

   public void renderSingleBlock(
      BlockState p_110913_,
      PoseStack p_110914_,
      MultiBufferSource p_110915_,
      int p_110916_,
      int p_110917_,
      ModelData modelData,
      RenderType renderType,
      int color
   ) {
      RenderShape rendershape = p_110913_.getRenderShape();
      if (rendershape != RenderShape.INVISIBLE) {
         switch (rendershape) {
            case MODEL:
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
               float f = (color >> 16 & 0xFF) / 255.0F;
               float f1 = (color >> 8 & 0xFF) / 255.0F;
               float f2 = (color & 0xFF) / 255.0F;

               for (RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42L), modelData)) {
                  dispatcher.getModelRenderer()
                     .renderModel(
                        p_110914_.last(),
                        p_110915_.getBuffer(renderType != null ? renderType : RenderTypeHelper.getEntityRenderType(rt, false)),
                        p_110913_,
                        bakedmodel,
                        f,
                        f1,
                        f2,
                        p_110916_,
                        p_110917_,
                        modelData,
                        rt
                     );
               }
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
         }
      }
   }

   public void renderSingleBlock(
      BlockState p_110913_,
      PoseStack p_110914_,
      MultiBufferSource p_110915_,
      int p_110916_,
      int p_110917_,
      ModelData modelData,
      float red,
      float green,
      float blue
   ) {
      RenderShape rendershape = p_110913_.getRenderShape();
      if (rendershape != RenderShape.INVISIBLE) {
         switch (rendershape) {
            case MODEL:
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);

               for (RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42L), modelData)) {
                  dispatcher.getModelRenderer()
                     .renderModel(
                        p_110914_.last(),
                        p_110915_.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                        p_110913_,
                        bakedmodel,
                        red,
                        green,
                        blue,
                        p_110916_,
                        p_110917_,
                        modelData,
                        null
                     );
               }
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void renderSingleBlockC(
      BlockState p_110913_,
      PoseStack poseStack,
      MultiBufferSource p_110915_,
      int p_110916_,
      int p_110917_,
      ModelData modelData,
      float red,
      float green,
      float blue
   ) {
      RenderShape rendershape = p_110913_.getRenderShape();
      if (rendershape != RenderShape.INVISIBLE) {
         switch (rendershape) {
            case MODEL:
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
               dispatcher.getModelRenderer()
                  .renderModel(
                     poseStack.last(),
                     p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)),
                     p_110913_,
                     bakedmodel,
                     0.0F,
                     0.0F,
                     0.0F,
                     p_110916_,
                     p_110917_,
                     modelData,
                     null
                  );
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               poseStack.translate(0.2, -0.1, -0.1);
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
