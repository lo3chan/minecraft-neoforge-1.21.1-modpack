package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
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
import org.joml.Quaternionf;

public class RenderVoidWormShot extends EntityRenderer<EntityVoidWormShot> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/void_worm/void_worm_shot.png");
   private static final ModelVoidWormShot MODEL = new ModelVoidWormShot();

   public RenderVoidWormShot(Context renderManager) {
      super(renderManager);
   }

   public ResourceLocation getTextureLocation(EntityVoidWormShot entity) {
      return TEXTURE;
   }

   public void render(EntityVoidWormShot entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(new Quaternionf().rotateX(Maths.rad(180.0)));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
      matrixStackIn.pushPose();
      MODEL.animate(entityIn, entityIn.tickCount + partialTicks);
      float home = (entityIn.prevStopHomingProgress + (entityIn.getStopHomingProgress() - entityIn.prevStopHomingProgress) * partialTicks) / 40.0F;
      matrixStackIn.translate(0.0F, -1.5F, 0.0F);
      VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getFullBright(this.getTextureLocation(entityIn)));
      MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, 210, OverlayTexture.NO_OVERLAY, Math.max(home, 0.2F), Math.max(home, 0.2F), 1.0F, 1.0F);
      matrixStackIn.popPose();
      matrixStackIn.popPose();
   }
}
