package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.CorpseFlyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CorpseFlyModel extends GeoModel<CorpseFlyEntity> {
   public ResourceLocation getAnimationResource(CorpseFlyEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/corpsefly.animation.json");
   }

   public ResourceLocation getModelResource(CorpseFlyEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/corpsefly.geo.json");
   }

   public ResourceLocation getTextureResource(CorpseFlyEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
