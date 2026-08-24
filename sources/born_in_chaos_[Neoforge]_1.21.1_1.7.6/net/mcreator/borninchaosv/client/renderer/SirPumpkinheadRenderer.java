package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.SirPumpkinheadEntity;
import net.mcreator.borninchaosv.entity.layer.SirPumpkinheadLayer;
import net.mcreator.borninchaosv.entity.model.SirPumpkinheadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SirPumpkinheadRenderer extends GeoEntityRenderer<SirPumpkinheadEntity> {
   public SirPumpkinheadRenderer(Context renderManager) {
      super(renderManager, new SirPumpkinheadModel());
      this.shadowRadius = 0.9F;
      this.addRenderLayer(new SirPumpkinheadLayer(this));
   }

   public RenderType getRenderType(SirPumpkinheadEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      SirPumpkinheadEntity entity,
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

   protected float getDeathMaxRotation(SirPumpkinheadEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
