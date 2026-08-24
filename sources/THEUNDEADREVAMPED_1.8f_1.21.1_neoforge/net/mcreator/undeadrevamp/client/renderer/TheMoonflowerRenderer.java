package net.mcreator.undeadrevamp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.undeadrevamp.entity.TheMoonflowerEntity;
import net.mcreator.undeadrevamp.entity.layer.TheMoonflowerLayer;
import net.mcreator.undeadrevamp.entity.model.TheMoonflowerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TheMoonflowerRenderer extends GeoEntityRenderer<TheMoonflowerEntity> {
   public TheMoonflowerRenderer(Context renderManager) {
      super(renderManager, new TheMoonflowerModel());
      this.shadowRadius = 0.5F;
      this.addRenderLayer(new TheMoonflowerLayer(this));
   }

   public RenderType getRenderType(TheMoonflowerEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      TheMoonflowerEntity entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      float scale = 0.95F;
      this.scaleHeight = scale;
      this.scaleWidth = scale;
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
   }
}
