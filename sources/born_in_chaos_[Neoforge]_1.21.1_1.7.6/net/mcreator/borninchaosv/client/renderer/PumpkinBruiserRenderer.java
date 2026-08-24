package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.PumpkinBruiserEntity;
import net.mcreator.borninchaosv.entity.layer.PumpkinBruiserLayer;
import net.mcreator.borninchaosv.entity.model.PumpkinBruiserModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PumpkinBruiserRenderer extends GeoEntityRenderer<PumpkinBruiserEntity> {
   public PumpkinBruiserRenderer(Context renderManager) {
      super(renderManager, new PumpkinBruiserModel());
      this.shadowRadius = 0.7F;
      this.addRenderLayer(new PumpkinBruiserLayer(this));
   }

   public RenderType getRenderType(PumpkinBruiserEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      PumpkinBruiserEntity entity,
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

   protected float getDeathMaxRotation(PumpkinBruiserEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
