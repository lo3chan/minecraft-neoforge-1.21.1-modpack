package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelPollenBall;
import com.github.alexthe666.alexsmobs.entity.EntityPollenBall;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderPollenBall extends EntityRenderer<EntityPollenBall> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/pollen_ball.png");
   private static final ModelPollenBall MODEL_POLLEN_BALL = new ModelPollenBall();

   public RenderPollenBall(Context renderManager) {
      super(renderManager);
   }

   public ResourceLocation getTextureLocation(EntityPollenBall entity) {
      return TEXTURE;
   }

   public void render(EntityPollenBall entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, -0.25, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180.0F));
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0F, 0.5F, 0.0F);
      matrixStackIn.scale(1.0F, 1.0F, 1.0F);
      VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getFullBright(this.getTextureLocation(entityIn)));
      MODEL_POLLEN_BALL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.popPose();
      matrixStackIn.popPose();
      matrixStackIn.popPose();
   }
}
