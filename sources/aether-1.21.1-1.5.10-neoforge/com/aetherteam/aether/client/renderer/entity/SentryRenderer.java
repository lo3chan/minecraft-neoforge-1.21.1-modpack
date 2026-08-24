package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.SentryGlowLayer;
import com.aetherteam.aether.entity.monster.dungeon.Sentry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class SentryRenderer extends MobRenderer<Sentry, SlimeModel<Sentry>> {
   private static final ResourceLocation SENTRY_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/sentry/sentry.png");
   private static final ResourceLocation SENTRY_LIT_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/sentry/sentry_lit.png");

   public SentryRenderer(Context context) {
      super(context, new SlimeModel(context.bakeLayer(AetherModelLayers.SENTRY)), 0.3F);
      this.addLayer(new SentryGlowLayer(this));
   }

   protected void scale(Sentry sentry, PoseStack poseStack, float partialTicks) {
      float f = 0.879F;
      poseStack.scale(f, f, f);
      float f1 = sentry.getSize() + 1.0F;
      float f2 = 0.0F;
      float f3 = 1.0F / (f2 + 1.0F);
      poseStack.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
   }

   public ResourceLocation getTextureLocation(Sentry sentry) {
      return sentry.isAwake() ? SENTRY_LIT_TEXTURE : SENTRY_TEXTURE;
   }
}
