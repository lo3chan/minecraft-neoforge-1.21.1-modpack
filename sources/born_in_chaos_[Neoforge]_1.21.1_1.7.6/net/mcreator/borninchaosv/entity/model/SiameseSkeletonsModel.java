package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.SiameseSkeletonsEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SiameseSkeletonsModel extends GeoModel<SiameseSkeletonsEntity> {
   public ResourceLocation getAnimationResource(SiameseSkeletonsEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/siameseskeletons.animation.json");
   }

   public ResourceLocation getModelResource(SiameseSkeletonsEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/siameseskeletons.geo.json");
   }

   public ResourceLocation getTextureResource(SiameseSkeletonsEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
