package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.entity.monster.dungeon.Sentry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SentryGlowLayer extends EyesLayer<Sentry, SlimeModel<Sentry>> {
   private static final RenderType SENTRY_EYE = RenderType.eyes(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/sentry/eye.png"));

   public SentryGlowLayer(RenderLayerParent<Sentry, SlimeModel<Sentry>> entityRenderer) {
      super(entityRenderer);
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Sentry sentry,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      VertexConsumer consumer = buffer.getBuffer(this.renderType());
      if (sentry.isAwake()) {
         ((SlimeModel)this.getParentModel()).renderToBuffer(poseStack, consumer, 15728640, OverlayTexture.NO_OVERLAY);
      }
   }

   public RenderType renderType() {
      return SENTRY_EYE;
   }
}
