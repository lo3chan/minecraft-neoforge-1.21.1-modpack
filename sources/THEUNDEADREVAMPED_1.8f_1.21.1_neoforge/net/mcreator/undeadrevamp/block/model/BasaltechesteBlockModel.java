package net.mcreator.undeadrevamp.block.model;

import net.mcreator.undeadrevamp.block.entity.BasaltechesteTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BasaltechesteBlockModel extends GeoModel<BasaltechesteTileEntity> {
   public ResourceLocation getAnimationResource(BasaltechesteTileEntity animatable) {
      return ResourceLocation.parse("undead_revamp2:animations/coffin.animation.json");
   }

   public ResourceLocation getModelResource(BasaltechesteTileEntity animatable) {
      return ResourceLocation.parse("undead_revamp2:geo/coffin.geo.json");
   }

   public ResourceLocation getTextureResource(BasaltechesteTileEntity animatable) {
      return ResourceLocation.parse("undead_revamp2:textures/block/sarcophagus.png");
   }
}
