package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.TheordureEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TheordureModel extends GeoModel<TheordureEntity> {
   public ResourceLocation getAnimationResource(TheordureEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/ordure.animation.json");
   }

   public ResourceLocation getModelResource(TheordureEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/ordure.geo.json");
   }

   public ResourceLocation getTextureResource(TheordureEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
