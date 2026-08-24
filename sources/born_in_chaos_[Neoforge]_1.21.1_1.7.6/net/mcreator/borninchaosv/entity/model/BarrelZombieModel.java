package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.BarrelZombieEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BarrelZombieModel extends GeoModel<BarrelZombieEntity> {
   public ResourceLocation getAnimationResource(BarrelZombieEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/zombiesinabarrel.animation.json");
   }

   public ResourceLocation getModelResource(BarrelZombieEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/zombiesinabarrel.geo.json");
   }

   public ResourceLocation getTextureResource(BarrelZombieEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(BarrelZombieEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("head");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
