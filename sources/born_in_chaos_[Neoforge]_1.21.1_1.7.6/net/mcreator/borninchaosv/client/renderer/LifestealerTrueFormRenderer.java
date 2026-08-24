package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.LifestealerTrueFormEntity;
import net.mcreator.borninchaosv.entity.layer.LifestealerTrueFormLayer;
import net.mcreator.borninchaosv.entity.model.LifestealerTrueFormModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LifestealerTrueFormRenderer extends GeoEntityRenderer<LifestealerTrueFormEntity> {
   public LifestealerTrueFormRenderer(Context renderManager) {
      super(renderManager, new LifestealerTrueFormModel());
      this.shadowRadius = 0.8F;
      this.addRenderLayer(new LifestealerTrueFormLayer(this));
   }

   public RenderType getRenderType(LifestealerTrueFormEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      LifestealerTrueFormEntity entity,
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

   protected float getDeathMaxRotation(LifestealerTrueFormEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
