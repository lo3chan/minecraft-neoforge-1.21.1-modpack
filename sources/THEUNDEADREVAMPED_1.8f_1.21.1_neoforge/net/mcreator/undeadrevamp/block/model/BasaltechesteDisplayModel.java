package net.mcreator.undeadrevamp.block.model;

import net.mcreator.undeadrevamp.block.display.BasaltechesteDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BasaltechesteDisplayModel extends GeoModel<BasaltechesteDisplayItem> {
   public ResourceLocation getAnimationResource(BasaltechesteDisplayItem animatable) {
      return ResourceLocation.parse("undead_revamp2:animations/coffin.animation.json");
   }

   public ResourceLocation getModelResource(BasaltechesteDisplayItem animatable) {
      return ResourceLocation.parse("undead_revamp2:geo/coffin.geo.json");
   }

   public ResourceLocation getTextureResource(BasaltechesteDisplayItem entity) {
      return ResourceLocation.parse("undead_revamp2:textures/block/sarcophagus.png");
   }
}
