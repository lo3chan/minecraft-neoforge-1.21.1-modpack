package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.CrystalModel;
import com.aetherteam.aether.entity.projectile.crystal.AbstractCrystal;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class CloudCrystalRenderer<T extends AbstractCrystal> extends AbstractCrystalRenderer<T> {
   private static final ResourceLocation ICE_CRYSTAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/projectile/crystals/ice_ball.png"
   );

   public CloudCrystalRenderer(Context context) {
      super(context, new CrystalModel(context.bakeLayer(AetherModelLayers.CLOUD_CRYSTAL)));
   }

   public ResourceLocation getTextureLocation(T crystal) {
      return ICE_CRYSTAL_TEXTURE;
   }
}
