package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.Propball1Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Propball1Model extends GeoModel<Propball1Entity> {
   public ResourceLocation getAnimationResource(Propball1Entity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/ordure.animation.json");
   }

   public ResourceLocation getModelResource(Propball1Entity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/ordure.geo.json");
   }

   public ResourceLocation getTextureResource(Propball1Entity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
