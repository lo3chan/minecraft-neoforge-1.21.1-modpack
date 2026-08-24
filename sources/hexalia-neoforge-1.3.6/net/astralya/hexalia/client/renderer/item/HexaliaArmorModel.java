package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.item.custom.armor.HexaliaGeoArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HexaliaArmorModel extends GeoModel<HexaliaGeoArmorItem> {
   private final HexaliaGeoArmorItem item;

   public HexaliaArmorModel(HexaliaGeoArmorItem item) {
      this.item = item;
   }

   public ResourceLocation getModelResource(HexaliaGeoArmorItem animatable) {
      return this.item.modelResource();
   }

   public ResourceLocation getTextureResource(HexaliaGeoArmorItem animatable) {
      return this.item.textureResource();
   }

   public ResourceLocation getAnimationResource(HexaliaGeoArmorItem animatable) {
      return this.item.animationResource();
   }
}
