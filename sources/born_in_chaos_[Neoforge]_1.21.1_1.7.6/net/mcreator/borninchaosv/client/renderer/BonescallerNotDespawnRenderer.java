package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.BonescallerNotDespawnEntity;
import net.mcreator.borninchaosv.entity.model.BonescallerNotDespawnModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BonescallerNotDespawnRenderer extends GeoEntityRenderer<BonescallerNotDespawnEntity> {
   public BonescallerNotDespawnRenderer(Context renderManager) {
      super(renderManager, new BonescallerNotDespawnModel());
      this.shadowRadius = 0.3F;
   }

   public RenderType getRenderType(BonescallerNotDespawnEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      BonescallerNotDespawnEntity entity,
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

   protected float getDeathMaxRotation(BonescallerNotDespawnEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
