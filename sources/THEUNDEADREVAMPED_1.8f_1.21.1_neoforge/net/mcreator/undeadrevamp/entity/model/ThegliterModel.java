package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThegliterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThegliterModel extends GeoModel<ThegliterEntity> {
   public ResourceLocation getAnimationResource(ThegliterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/gliter.animation.json");
   }

   public ResourceLocation getModelResource(ThegliterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/gliter.geo.json");
   }

   public ResourceLocation getTextureResource(ThegliterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
