package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.SiameseSkeletonsleftEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SiameseSkeletonsleftModel extends GeoModel<SiameseSkeletonsleftEntity> {
   public ResourceLocation getAnimationResource(SiameseSkeletonsleftEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/siameseskeletonsleft.animation.json");
   }

   public ResourceLocation getModelResource(SiameseSkeletonsleftEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/siameseskeletonsleft.geo.json");
   }

   public ResourceLocation getTextureResource(SiameseSkeletonsleftEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
