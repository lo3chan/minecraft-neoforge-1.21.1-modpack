package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.monster.Swet;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class GoldenSwetRenderer extends SwetRenderer {
   private static final ResourceLocation GOLDEN_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/swet/swet_golden.png");

   public GoldenSwetRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(Swet swet) {
      return GOLDEN_TEXTURE;
   }
}
