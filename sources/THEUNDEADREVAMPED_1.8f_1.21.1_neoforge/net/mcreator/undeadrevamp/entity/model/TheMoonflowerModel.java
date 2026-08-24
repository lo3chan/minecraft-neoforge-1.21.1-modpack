package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.TheMoonflowerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TheMoonflowerModel extends GeoModel<TheMoonflowerEntity> {
   public ResourceLocation getAnimationResource(TheMoonflowerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/moonflower.animation.json");
   }

   public ResourceLocation getModelResource(TheMoonflowerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/moonflower.geo.json");
   }

   public ResourceLocation getTextureResource(TheMoonflowerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
