package net.mcreator.borninchaosv.item.model;

import net.mcreator.borninchaosv.item.StaffOfMagicArrowsAItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StaffOfMagicArrowsAItemModel extends GeoModel<StaffOfMagicArrowsAItem> {
   public ResourceLocation getAnimationResource(StaffOfMagicArrowsAItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/staffmagicarrows.animation.json");
   }

   public ResourceLocation getModelResource(StaffOfMagicArrowsAItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/staffmagicarrows.geo.json");
   }

   public ResourceLocation getTextureResource(StaffOfMagicArrowsAItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/item/staffmagicarrows.png");
   }
}
