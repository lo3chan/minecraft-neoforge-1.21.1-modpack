package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.BabySpiderEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BabySpiderModel extends GeoModel<BabySpiderEntity> {
   public ResourceLocation getAnimationResource(BabySpiderEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/babyspider.animation.json");
   }

   public ResourceLocation getModelResource(BabySpiderEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/babyspider.geo.json");
   }

   public ResourceLocation getTextureResource(BabySpiderEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
