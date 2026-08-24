package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.BloodyGadflyEntity;
import net.mcreator.borninchaosv.entity.layer.BloodyGadflyLayer;
import net.mcreator.borninchaosv.entity.model.BloodyGadflyModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodyGadflyRenderer extends GeoEntityRenderer<BloodyGadflyEntity> {
   public BloodyGadflyRenderer(Context renderManager) {
      super(renderManager, new BloodyGadflyModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new BloodyGadflyLayer(this));
   }

   public RenderType getRenderType(BloodyGadflyEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      BloodyGadflyEntity entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      float scale = 1.0F;
      this.scaleHeight = scale;
      this.scaleWidth = scale;
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
   }

   protected float getDeathMaxRotation(BloodyGadflyEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
