package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThelurkerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThelurkerModel extends GeoModel<ThelurkerEntity> {
   public ResourceLocation getAnimationResource(ThelurkerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/lurker.animation.json");
   }

   public ResourceLocation getModelResource(ThelurkerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/lurker.geo.json");
   }

   public ResourceLocation getTextureResource(ThelurkerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
