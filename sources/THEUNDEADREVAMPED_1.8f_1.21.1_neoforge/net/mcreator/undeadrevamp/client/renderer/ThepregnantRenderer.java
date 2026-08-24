package net.mcreator.undeadrevamp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.undeadrevamp.entity.ThepregnantEntity;
import net.mcreator.undeadrevamp.entity.model.ThepregnantModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThepregnantRenderer extends GeoEntityRenderer<ThepregnantEntity> {
   public ThepregnantRenderer(Context renderManager) {
      super(renderManager, new ThepregnantModel());
      this.shadowRadius = 0.5F;
   }

   public RenderType getRenderType(ThepregnantEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      ThepregnantEntity entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      float scale = 1.25F;
      this.scaleHeight = scale;
      this.scaleWidth = scale;
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
   }

   protected float getDeathMaxRotation(ThepregnantEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
