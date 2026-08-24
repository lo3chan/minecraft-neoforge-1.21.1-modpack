package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.AxestromEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AxestromModel extends GeoModel<AxestromEntity> {
   public ResourceLocation getAnimationResource(AxestromEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/screamer.animation.json");
   }

   public ResourceLocation getModelResource(AxestromEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/screamer.geo.json");
   }

   public ResourceLocation getTextureResource(AxestromEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
