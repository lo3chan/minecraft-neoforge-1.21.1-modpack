package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.projectile.dart.EnchantedDart;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class EnchantedDartRenderer extends ArrowRenderer<EnchantedDart> {
   private static final ResourceLocation ENCHANTED_DART_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/projectile/dart/enchanted_dart.png"
   );

   public EnchantedDartRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(EnchantedDart dart) {
      return ENCHANTED_DART_TEXTURE;
   }
}
