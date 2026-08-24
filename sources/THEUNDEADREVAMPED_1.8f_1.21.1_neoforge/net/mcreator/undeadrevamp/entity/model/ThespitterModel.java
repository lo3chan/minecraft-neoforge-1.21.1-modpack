package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThespitterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThespitterModel extends GeoModel<ThespitterEntity> {
   public ResourceLocation getAnimationResource(ThespitterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/spitter.animation.json");
   }

   public ResourceLocation getModelResource(ThespitterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/spitter.geo.json");
   }

   public ResourceLocation getTextureResource(ThespitterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
