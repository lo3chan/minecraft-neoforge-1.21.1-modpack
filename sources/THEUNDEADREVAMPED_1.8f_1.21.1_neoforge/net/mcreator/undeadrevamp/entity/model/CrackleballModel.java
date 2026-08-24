package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.CrackleballEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CrackleballModel extends GeoModel<CrackleballEntity> {
   public ResourceLocation getAnimationResource(CrackleballEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/ordure.animation.json");
   }

   public ResourceLocation getModelResource(CrackleballEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/ordure.geo.json");
   }

   public ResourceLocation getTextureResource(CrackleballEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
