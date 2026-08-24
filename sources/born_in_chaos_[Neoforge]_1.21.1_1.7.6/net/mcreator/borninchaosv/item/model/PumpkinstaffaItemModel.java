package net.mcreator.borninchaosv.item.model;

import net.mcreator.borninchaosv.item.PumpkinstaffaItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PumpkinstaffaItemModel extends GeoModel<PumpkinstaffaItem> {
   public ResourceLocation getAnimationResource(PumpkinstaffaItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/pumpkinstaff.animation.json");
   }

   public ResourceLocation getModelResource(PumpkinstaffaItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/pumpkinstaff.geo.json");
   }

   public ResourceLocation getTextureResource(PumpkinstaffaItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/item/pumpkinstaff.png");
   }
}
