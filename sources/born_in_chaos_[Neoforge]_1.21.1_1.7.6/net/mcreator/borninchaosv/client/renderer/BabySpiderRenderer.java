package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.BabySpiderEntity;
import net.mcreator.borninchaosv.entity.layer.BabySpiderLayer;
import net.mcreator.borninchaosv.entity.model.BabySpiderModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BabySpiderRenderer extends GeoEntityRenderer<BabySpiderEntity> {
   public BabySpiderRenderer(Context renderManager) {
      super(renderManager, new BabySpiderModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new BabySpiderLayer(this));
   }

   public RenderType getRenderType(BabySpiderEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      BabySpiderEntity entity,
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

   protected float getDeathMaxRotation(BabySpiderEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
