package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.LecheryEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LecheryModel extends GeoModel<LecheryEntity> {
   public ResourceLocation getAnimationResource(LecheryEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/lechery.animation.json");
   }

   public ResourceLocation getModelResource(LecheryEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/lechery.geo.json");
   }

   public ResourceLocation getTextureResource(LecheryEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
