package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.FallenChaosKnightEntity;
import net.mcreator.borninchaosv.entity.layer.FallenChaosKnightLayer;
import net.mcreator.borninchaosv.entity.model.FallenChaosKnightModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FallenChaosKnightRenderer extends GeoEntityRenderer<FallenChaosKnightEntity> {
   public FallenChaosKnightRenderer(Context renderManager) {
      super(renderManager, new FallenChaosKnightModel());
      this.shadowRadius = 0.5F;
      this.addRenderLayer(new FallenChaosKnightLayer(this));
   }

   public RenderType getRenderType(FallenChaosKnightEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      FallenChaosKnightEntity entity,
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

   protected float getDeathMaxRotation(FallenChaosKnightEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
