package net.mcreator.undeadrevamp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.undeadrevamp.entity.ThelurkerEntity;
import net.mcreator.undeadrevamp.entity.layer.ThelurkerLayer;
import net.mcreator.undeadrevamp.entity.model.ThelurkerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThelurkerRenderer extends GeoEntityRenderer<ThelurkerEntity> {
   public ThelurkerRenderer(Context renderManager) {
      super(renderManager, new ThelurkerModel());
      this.shadowRadius = 0.5F;
      this.addRenderLayer(new ThelurkerLayer(this));
   }

   public RenderType getRenderType(ThelurkerEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      ThelurkerEntity entity,
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

   protected float getDeathMaxRotation(ThelurkerEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
