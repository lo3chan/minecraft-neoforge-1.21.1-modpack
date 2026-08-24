package net.mcreator.borninchaosv.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.SenorPumpkinEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SenorPumpkinLayer extends GeoRenderLayer<SenorPumpkinEntity> {
   private static final ResourceLocation LAYER = ResourceLocation.parse("born_in_chaos_v1:textures/entities/senorpumpkin_e.png");

   public SenorPumpkinLayer(GeoRenderer<SenorPumpkinEntity> entityRenderer) {
      super(entityRenderer);
   }

   public void render(
      PoseStack poseStack,
      SenorPumpkinEntity animatable,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      RenderType glowRenderType = RenderType.eyes(LAYER);
      this.getRenderer()
         .reRender(
            this.getDefaultBakedModel(animatable),
            poseStack,
            bufferSource,
            animatable,
            glowRenderType,
            bufferSource.getBuffer(glowRenderType),
            partialTick,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            -1
         );
   }
}
