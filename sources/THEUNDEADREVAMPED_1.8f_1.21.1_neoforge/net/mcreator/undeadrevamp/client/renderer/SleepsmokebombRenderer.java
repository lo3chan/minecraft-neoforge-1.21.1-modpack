package net.mcreator.undeadrevamp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.undeadrevamp.client.model.Modelsleepbomb;
import net.mcreator.undeadrevamp.entity.SleepsmokebombEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SleepsmokebombRenderer extends EntityRenderer<SleepsmokebombEntity> {
   private static final ResourceLocation texture = ResourceLocation.parse("undead_revamp2:textures/entities/smoke_bomb_entitytex.png");
   private final Modelsleepbomb model;

   public SleepsmokebombRenderer(Context context) {
      super(context);
      this.model = new Modelsleepbomb(context.bakeLayer(Modelsleepbomb.LAYER_LOCATION));
   }

   public void render(SleepsmokebombEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
      VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
      poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
      this.model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
   }

   public ResourceLocation getTextureLocation(SleepsmokebombEntity entity) {
      return texture;
   }
}
