package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.PumpkinBombEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PumpkinBombModel extends GeoModel<PumpkinBombEntity> {
   public ResourceLocation getAnimationResource(PumpkinBombEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/pumpkinbomb.animation.json");
   }

   public ResourceLocation getModelResource(PumpkinBombEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/pumpkinbomb.geo.json");
   }

   public ResourceLocation getTextureResource(PumpkinBombEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
