package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.BabySpiderControlledEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BabySpiderControlledModel extends GeoModel<BabySpiderControlledEntity> {
   public ResourceLocation getAnimationResource(BabySpiderControlledEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/babyspider.animation.json");
   }

   public ResourceLocation getModelResource(BabySpiderControlledEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/babyspider.geo.json");
   }

   public ResourceLocation getTextureResource(BabySpiderControlledEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
