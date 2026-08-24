package net.mcreator.undeadrevamp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.undeadrevamp.entity.TheheavyEntity;
import net.mcreator.undeadrevamp.entity.layer.TheheavyLayer;
import net.mcreator.undeadrevamp.entity.model.TheheavyModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TheheavyRenderer extends GeoEntityRenderer<TheheavyEntity> {
   public TheheavyRenderer(Context renderManager) {
      super(renderManager, new TheheavyModel());
      this.shadowRadius = 0.5F;
      this.addRenderLayer(new TheheavyLayer(this));
   }

   public RenderType getRenderType(TheheavyEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      TheheavyEntity entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      float scale = 1.45F;
      this.scaleHeight = scale;
      this.scaleWidth = scale;
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
   }

   protected float getDeathMaxRotation(TheheavyEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
