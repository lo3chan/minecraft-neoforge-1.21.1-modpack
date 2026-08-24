package net.mcreator.undeadrevamp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.undeadrevamp.entity.ThesomnolenceEntity;
import net.mcreator.undeadrevamp.entity.layer.ThesomnolenceLayer;
import net.mcreator.undeadrevamp.entity.model.ThesomnolenceModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThesomnolenceRenderer extends GeoEntityRenderer<ThesomnolenceEntity> {
   public ThesomnolenceRenderer(Context renderManager) {
      super(renderManager, new ThesomnolenceModel());
      this.shadowRadius = 0.3F;
      this.addRenderLayer(new ThesomnolenceLayer(this));
   }

   public RenderType getRenderType(ThesomnolenceEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      ThesomnolenceEntity entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      float scale = 0.25F;
      this.scaleHeight = scale;
      this.scaleWidth = scale;
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
   }

   protected float getDeathMaxRotation(ThesomnolenceEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
