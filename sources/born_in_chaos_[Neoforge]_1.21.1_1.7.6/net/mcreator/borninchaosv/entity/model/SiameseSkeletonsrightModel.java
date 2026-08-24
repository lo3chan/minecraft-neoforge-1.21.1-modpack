package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.SiameseSkeletonsrightEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SiameseSkeletonsrightModel extends GeoModel<SiameseSkeletonsrightEntity> {
   public ResourceLocation getAnimationResource(SiameseSkeletonsrightEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/siameseskeletonsright.animation.json");
   }

   public ResourceLocation getModelResource(SiameseSkeletonsrightEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/siameseskeletonsright.geo.json");
   }

   public ResourceLocation getTextureResource(SiameseSkeletonsrightEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
