package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.monster.Swet;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class BlueSwetRenderer extends SwetRenderer {
   private static final ResourceLocation BLUE_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/swet/swet_blue.png");

   public BlueSwetRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(Swet swet) {
      return BLUE_TEXTURE;
   }
}
