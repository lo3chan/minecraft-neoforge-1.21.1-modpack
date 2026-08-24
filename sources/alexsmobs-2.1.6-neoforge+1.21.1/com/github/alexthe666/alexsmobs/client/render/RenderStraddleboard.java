package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelStraddleboard;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityStraddleboard;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class RenderStraddleboard extends EntityRenderer<EntityStraddleboard> {
   private static final ResourceLocation TEXTURE_OVERLAY = AMCompat.rl("alexsmobs:textures/entity/straddleboard_overlay.png");
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/straddleboard.png");
   private static final ModelStraddleboard BOARD_MODEL = new ModelStraddleboard();

   public RenderStraddleboard(Context renderManager) {
      super(renderManager);
   }

   public ResourceLocation getTextureLocation(EntityStraddleboard entity) {
      return TEXTURE;
   }

   public void render(EntityStraddleboard entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(new Quaternionf().rotateY(3.1415927F));
      matrixStackIn.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) + 180.0F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
      matrixStackIn.pushPose();
      boolean lava = entityIn.isVehicle();
      float f2 = entityIn.getRockingAngle(partialTicks);
      if (!Mth.equal(f2, 0.0F)) {
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(entityIn.getRockingAngle(partialTicks)));
      }

      int k = entityIn.isDefaultColor() ? AMConfig.straddleboardPanelColor : entityIn.getColor();
      float r = (k >> 16 & 0xFF) / 255.0F;
      float g = (k >> 8 & 0xFF) / 255.0F;
      float b = (k & 0xFF) / 255.0F;
      int base = AMConfig.straddleboardBaseColor;
      float baseR = (base >> 16 & 0xFF) / 255.0F;
      float baseG = (base >> 8 & 0xFF) / 255.0F;
      float baseB = (base & 0xFF) / 255.0F;
      float boardRot = entityIn.prevBoardRot + partialTicks * (entityIn.getBoardRot() - entityIn.prevBoardRot);
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(boardRot));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
      matrixStackIn.translate(0.0F, -1.5F - Math.abs(boardRot * 0.007F) - (lava ? 0.0F : 0.25F), 0.0F);
      BOARD_MODEL.animateBoard(entityIn, entityIn.tickCount + partialTicks);
      VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_OVERLAY));
      BOARD_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
      VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
      BOARD_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, baseR, baseG, baseB, 1.0F);
      matrixStackIn.popPose();
      matrixStackIn.popPose();
   }
}
