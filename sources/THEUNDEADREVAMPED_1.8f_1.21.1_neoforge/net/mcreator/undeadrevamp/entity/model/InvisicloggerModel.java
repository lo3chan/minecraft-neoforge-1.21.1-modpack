package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.InvisicloggerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InvisicloggerModel extends GeoModel<InvisicloggerEntity> {
   public ResourceLocation getAnimationResource(InvisicloggerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/bidy.animation.json");
   }

   public ResourceLocation getModelResource(InvisicloggerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/bidy.geo.json");
   }

   public ResourceLocation getTextureResource(InvisicloggerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
