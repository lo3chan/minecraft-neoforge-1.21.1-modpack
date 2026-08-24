package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThebidyupsideEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThebidyupsideModel extends GeoModel<ThebidyupsideEntity> {
   public ResourceLocation getAnimationResource(ThebidyupsideEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/bidy.animation.json");
   }

   public ResourceLocation getModelResource(ThebidyupsideEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/bidy.geo.json");
   }

   public ResourceLocation getTextureResource(ThebidyupsideEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
