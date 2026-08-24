package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.projectile.dart.PoisonDart;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class PoisonDartRenderer extends ArrowRenderer<PoisonDart> {
   private static final ResourceLocation POISON_DART_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/projectile/dart/poison_dart.png"
   );

   public PoisonDartRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(PoisonDart dart) {
      return POISON_DART_TEXTURE;
   }
}
