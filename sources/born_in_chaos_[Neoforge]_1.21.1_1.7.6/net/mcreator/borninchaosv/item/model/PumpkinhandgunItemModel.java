package net.mcreator.borninchaosv.item.model;

import net.mcreator.borninchaosv.item.PumpkinhandgunItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PumpkinhandgunItemModel extends GeoModel<PumpkinhandgunItem> {
   public ResourceLocation getAnimationResource(PumpkinhandgunItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/pumpkinhandgun.animation.json");
   }

   public ResourceLocation getModelResource(PumpkinhandgunItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/pumpkinhandgun.geo.json");
   }

   public ResourceLocation getTextureResource(PumpkinhandgunItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/item/pumpkinhandgun.png");
   }
}
