package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class PoisonNeedleRenderer extends ArrowRenderer<PoisonNeedle> {
   private static final ResourceLocation POISON_NEEDLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/projectile/dart/poison_needle.png"
   );

   public PoisonNeedleRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(PoisonNeedle dart) {
      return POISON_NEEDLE_TEXTURE;
   }
}
