package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.MotherSpiderEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MotherSpiderModel extends GeoModel<MotherSpiderEntity> {
   public ResourceLocation getAnimationResource(MotherSpiderEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/spidersmother.animation.json");
   }

   public ResourceLocation getModelResource(MotherSpiderEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/spidersmother.geo.json");
   }

   public ResourceLocation getTextureResource(MotherSpiderEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
