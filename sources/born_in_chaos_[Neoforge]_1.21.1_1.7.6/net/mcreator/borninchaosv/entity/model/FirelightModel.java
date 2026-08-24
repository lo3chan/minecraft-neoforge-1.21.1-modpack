package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.FirelightEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FirelightModel extends GeoModel<FirelightEntity> {
   public ResourceLocation getAnimationResource(FirelightEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/firelight.animation.json");
   }

   public ResourceLocation getModelResource(FirelightEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/firelight.geo.json");
   }

   public ResourceLocation getTextureResource(FirelightEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
