package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.FirelightNotDespawnEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FirelightNotDespawnModel extends GeoModel<FirelightNotDespawnEntity> {
   public ResourceLocation getAnimationResource(FirelightNotDespawnEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/firelight.animation.json");
   }

   public ResourceLocation getModelResource(FirelightNotDespawnEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/firelight.geo.json");
   }

   public ResourceLocation getTextureResource(FirelightNotDespawnEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
