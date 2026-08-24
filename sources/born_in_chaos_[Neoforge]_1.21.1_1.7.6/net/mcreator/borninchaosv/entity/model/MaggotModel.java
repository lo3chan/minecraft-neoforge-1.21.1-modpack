package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.MaggotEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MaggotModel extends GeoModel<MaggotEntity> {
   public ResourceLocation getAnimationResource(MaggotEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/maggot.animation.json");
   }

   public ResourceLocation getModelResource(MaggotEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/maggot.geo.json");
   }

   public ResourceLocation getTextureResource(MaggotEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
