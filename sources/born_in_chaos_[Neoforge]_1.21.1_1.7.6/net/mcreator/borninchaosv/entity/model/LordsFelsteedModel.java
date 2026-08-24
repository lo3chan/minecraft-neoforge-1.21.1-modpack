package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.LordsFelsteedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LordsFelsteedModel extends GeoModel<LordsFelsteedEntity> {
   public ResourceLocation getAnimationResource(LordsFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/felsteedlord.animation.json");
   }

   public ResourceLocation getModelResource(LordsFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/felsteedlord.geo.json");
   }

   public ResourceLocation getTextureResource(LordsFelsteedEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
