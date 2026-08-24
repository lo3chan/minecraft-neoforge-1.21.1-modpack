package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.entity.KrampusHenchmanEntity;
import net.mcreator.borninchaosv.entity.layer.KrampusHenchmanLayer;
import net.mcreator.borninchaosv.entity.model.KrampusHenchmanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KrampusHenchmanRenderer extends GeoEntityRenderer<KrampusHenchmanEntity> {
   public KrampusHenchmanRenderer(Context renderManager) {
      super(renderManager, new KrampusHenchmanModel());
      this.shadowRadius = 0.6F;
      this.addRenderLayer(new KrampusHenchmanLayer(this));
   }

   public RenderType getRenderType(KrampusHenchmanEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(this.getTextureLocation(animatable));
   }

   public void preRender(
      PoseStack poseStack,
      KrampusHenchmanEntity entity,
      BakedGeoModel model,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      float scale = 0.9F;
      this.scaleHeight = scale;
      this.scaleWidth = scale;
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
   }

   protected float getDeathMaxRotation(KrampusHenchmanEntity entityLivingBaseIn) {
      return 0.0F;
   }
}
