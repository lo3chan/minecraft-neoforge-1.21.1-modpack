package net.mcreator.undeadrevamp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.undeadrevamp.client.model.Modelskeeperbox;
import net.mcreator.undeadrevamp.entity.SkeeperthrowprojectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkeeperthrowprojectileRenderer extends EntityRenderer<SkeeperthrowprojectileEntity> {
   private static final ResourceLocation texture = ResourceLocation.parse("undead_revamp2:textures/entities/skeeperbox.png");
   private final Modelskeeperbox model;

   public SkeeperthrowprojectileRenderer(Context context) {
      super(context);
      this.model = new Modelskeeperbox(context.bakeLayer(Modelskeeperbox.LAYER_LOCATION));
   }

   public void render(
      SkeeperthrowprojectileEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn
   ) {
      VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
      poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
      this.model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
   }

   public ResourceLocation getTextureLocation(SkeeperthrowprojectileEntity entity) {
      return texture;
   }
}
