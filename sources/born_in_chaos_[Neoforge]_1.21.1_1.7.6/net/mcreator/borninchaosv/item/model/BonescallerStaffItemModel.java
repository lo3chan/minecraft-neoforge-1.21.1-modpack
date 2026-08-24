package net.mcreator.borninchaosv.item.model;

import net.mcreator.borninchaosv.item.BonescallerStaffItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BonescallerStaffItemModel extends GeoModel<BonescallerStaffItem> {
   public ResourceLocation getAnimationResource(BonescallerStaffItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/bonescallerstaff.animation.json");
   }

   public ResourceLocation getModelResource(BonescallerStaffItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/bonescallerstaff.geo.json");
   }

   public ResourceLocation getTextureResource(BonescallerStaffItem animatable) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/item/bonescallerstaff.png");
   }
}
