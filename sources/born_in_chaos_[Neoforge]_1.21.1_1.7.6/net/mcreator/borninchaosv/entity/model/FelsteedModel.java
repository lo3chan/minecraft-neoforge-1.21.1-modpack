package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.FelsteedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FelsteedModel extends GeoModel<FelsteedEntity> {
   public ResourceLocation getAnimationResource(FelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/felsteed.animation.json");
   }

   public ResourceLocation getModelResource(FelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/felsteed.geo.json");
   }

   public ResourceLocation getTextureResource(FelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
