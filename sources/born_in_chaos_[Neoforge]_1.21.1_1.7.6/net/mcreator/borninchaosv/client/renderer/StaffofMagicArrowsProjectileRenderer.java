package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.borninchaosv.client.model.Modelmagic_arrow18;
import net.mcreator.borninchaosv.entity.StaffofMagicArrowsProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class StaffofMagicArrowsProjectileRenderer extends EntityRenderer<StaffofMagicArrowsProjectileEntity> {
   private static final ResourceLocation texture = ResourceLocation.parse("born_in_chaos_v1:textures/entities/magicarrow.png");
   private final Modelmagic_arrow18 model;

   public StaffofMagicArrowsProjectileRenderer(Context context) {
      super(context);
      this.model = new Modelmagic_arrow18(context.bakeLayer(Modelmagic_arrow18.LAYER_LOCATION));
   }

   public void render(
      StaffofMagicArrowsProjectileEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn
   ) {
      VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
      poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
      this.model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
   }

   public ResourceLocation getTextureLocation(StaffofMagicArrowsProjectileEntity entity) {
      return texture;
   }
}
