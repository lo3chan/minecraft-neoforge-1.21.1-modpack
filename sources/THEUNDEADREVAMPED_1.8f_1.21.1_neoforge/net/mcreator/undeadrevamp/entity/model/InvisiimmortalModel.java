package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.InvisiimmortalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InvisiimmortalModel extends GeoModel<InvisiimmortalEntity> {
   public ResourceLocation getAnimationResource(InvisiimmortalEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/bidy.animation.json");
   }

   public ResourceLocation getModelResource(InvisiimmortalEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/bidy.geo.json");
   }

   public ResourceLocation getTextureResource(InvisiimmortalEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
