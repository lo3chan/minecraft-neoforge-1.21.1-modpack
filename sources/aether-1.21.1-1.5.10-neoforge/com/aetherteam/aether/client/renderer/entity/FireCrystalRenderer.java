package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.CrystalModel;
import com.aetherteam.aether.entity.projectile.crystal.FireCrystal;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class FireCrystalRenderer extends AbstractCrystalRenderer<FireCrystal> {
   private static final ResourceLocation FIRE_CRYSTAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/projectile/crystals/fire_ball.png"
   );

   public FireCrystalRenderer(Context context) {
      super(context, new CrystalModel(context.bakeLayer(AetherModelLayers.CLOUD_CRYSTAL)));
   }

   public ResourceLocation getTextureLocation(FireCrystal crystal) {
      return FIRE_CRYSTAL_TEXTURE;
   }
}
