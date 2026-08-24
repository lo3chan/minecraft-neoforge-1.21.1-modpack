package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.RidingFelsteedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RidingFelsteedModel extends GeoModel<RidingFelsteedEntity> {
   public ResourceLocation getAnimationResource(RidingFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/felsteedt.animation.json");
   }

   public ResourceLocation getModelResource(RidingFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/felsteedt.geo.json");
   }

   public ResourceLocation getTextureResource(RidingFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
