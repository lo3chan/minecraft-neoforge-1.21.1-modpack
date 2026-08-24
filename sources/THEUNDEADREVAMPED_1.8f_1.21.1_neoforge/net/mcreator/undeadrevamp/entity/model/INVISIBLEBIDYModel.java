package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.INVISIBLEBIDYEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class INVISIBLEBIDYModel extends GeoModel<INVISIBLEBIDYEntity> {
   public ResourceLocation getAnimationResource(INVISIBLEBIDYEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/bidy.animation.json");
   }

   public ResourceLocation getModelResource(INVISIBLEBIDYEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/bidy.geo.json");
   }

   public ResourceLocation getTextureResource(INVISIBLEBIDYEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }
}
