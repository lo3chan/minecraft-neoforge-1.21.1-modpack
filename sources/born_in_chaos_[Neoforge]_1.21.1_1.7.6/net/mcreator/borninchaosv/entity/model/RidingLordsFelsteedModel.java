package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.RidingLordsFelsteedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RidingLordsFelsteedModel extends GeoModel<RidingLordsFelsteedEntity> {
   public ResourceLocation getAnimationResource(RidingLordsFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/felsteedlord.animation.json");
   }

   public ResourceLocation getModelResource(RidingLordsFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/felsteedlord.geo.json");
   }

   public ResourceLocation getTextureResource(RidingLordsFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
