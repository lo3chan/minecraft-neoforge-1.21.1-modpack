package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.LordTheHeadlessEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LordTheHeadlessModel extends GeoModel<LordTheHeadlessEntity> {
   public ResourceLocation getAnimationResource(LordTheHeadlessEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/lordofpumpkinss3.animation.json");
   }

   public ResourceLocation getModelResource(LordTheHeadlessEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/lordofpumpkinss3.geo.json");
   }

   public ResourceLocation getTextureResource(LordTheHeadlessEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }
}
