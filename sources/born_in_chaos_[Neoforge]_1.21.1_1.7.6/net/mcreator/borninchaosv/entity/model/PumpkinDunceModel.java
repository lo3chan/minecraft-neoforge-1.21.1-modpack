package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.PumpkinDunceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PumpkinDunceModel extends GeoModel<PumpkinDunceEntity> {
   public ResourceLocation getAnimationResource(PumpkinDunceEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/pumpkindunce.animation.json");
   }

   public ResourceLocation getModelResource(PumpkinDunceEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/pumpkindunce.geo.json");
   }

   public ResourceLocation getTextureResource(PumpkinDunceEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
