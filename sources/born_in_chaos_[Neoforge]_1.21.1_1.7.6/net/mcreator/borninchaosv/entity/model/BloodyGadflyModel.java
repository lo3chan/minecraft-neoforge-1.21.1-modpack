package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.BloodyGadflyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodyGadflyModel extends GeoModel<BloodyGadflyEntity> {
   public ResourceLocation getAnimationResource(BloodyGadflyEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/bloodygadfly.animation.json");
   }

   public ResourceLocation getModelResource(BloodyGadflyEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/bloodygadfly.geo.json");
   }

   public ResourceLocation getTextureResource(BloodyGadflyEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
