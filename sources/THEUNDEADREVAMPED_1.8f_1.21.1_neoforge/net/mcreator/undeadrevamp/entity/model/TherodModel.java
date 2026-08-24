package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.TherodEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TherodModel extends GeoModel<TherodEntity> {
   public ResourceLocation getAnimationResource(TherodEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/the_rod.animation.json");
   }

   public ResourceLocation getModelResource(TherodEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/the_rod.geo.json");
   }

   public ResourceLocation getTextureResource(TherodEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
