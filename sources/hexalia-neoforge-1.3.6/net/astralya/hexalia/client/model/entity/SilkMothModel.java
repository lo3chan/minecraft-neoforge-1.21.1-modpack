package net.astralya.hexalia.client.model.entity;

import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SilkMothModel extends GeoModel<SilkMothEntity> {
   public ResourceLocation getModelResource(SilkMothEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("hexalia", "geo/silk_moth.geo.json");
   }

   public ResourceLocation getTextureResource(SilkMothEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("hexalia", "textures/entity/silk_moth_default.png");
   }

   public ResourceLocation getAnimationResource(SilkMothEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("hexalia", "animations/silk_moth.animation.json");
   }
}
