package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThedungeonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThedungeonModel extends GeoModel<ThedungeonEntity> {
   public ResourceLocation getAnimationResource(ThedungeonEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/thedugeon.animation.json");
   }

   public ResourceLocation getModelResource(ThedungeonEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/thedugeon.geo.json");
   }

   public ResourceLocation getTextureResource(ThedungeonEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
