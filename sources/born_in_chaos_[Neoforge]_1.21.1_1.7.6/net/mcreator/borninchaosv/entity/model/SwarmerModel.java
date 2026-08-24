package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.SwarmerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SwarmerModel extends GeoModel<SwarmerEntity> {
   public ResourceLocation getAnimationResource(SwarmerEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/swarmer.animation.json");
   }

   public ResourceLocation getModelResource(SwarmerEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/swarmer.geo.json");
   }

   public ResourceLocation getTextureResource(SwarmerEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
