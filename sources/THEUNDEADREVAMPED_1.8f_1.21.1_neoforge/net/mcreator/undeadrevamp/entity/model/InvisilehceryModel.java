package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.InvisilehceryEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InvisilehceryModel extends GeoModel<InvisilehceryEntity> {
   public ResourceLocation getAnimationResource(InvisilehceryEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/bidy.animation.json");
   }

   public ResourceLocation getModelResource(InvisilehceryEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/bidy.geo.json");
   }

   public ResourceLocation getTextureResource(InvisilehceryEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
