package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.SirTheHeadlessEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SirTheHeadlessModel extends GeoModel<SirTheHeadlessEntity> {
   public ResourceLocation getAnimationResource(SirTheHeadlessEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/sertheheadless.animation.json");
   }

   public ResourceLocation getModelResource(SirTheHeadlessEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/sertheheadless.geo.json");
   }

   public ResourceLocation getTextureResource(SirTheHeadlessEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
