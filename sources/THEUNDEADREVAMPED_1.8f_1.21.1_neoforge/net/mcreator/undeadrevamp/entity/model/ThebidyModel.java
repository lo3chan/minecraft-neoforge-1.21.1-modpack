package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThebidyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThebidyModel extends GeoModel<ThebidyEntity> {
   public ResourceLocation getAnimationResource(ThebidyEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/bidy.animation.json");
   }

   public ResourceLocation getModelResource(ThebidyEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/bidy.geo.json");
   }

   public ResourceLocation getTextureResource(ThebidyEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
