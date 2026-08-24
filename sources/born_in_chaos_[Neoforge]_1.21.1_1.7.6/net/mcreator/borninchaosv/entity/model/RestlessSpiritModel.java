package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.RestlessSpiritEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RestlessSpiritModel extends GeoModel<RestlessSpiritEntity> {
   public ResourceLocation getAnimationResource(RestlessSpiritEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/restlessspirit.animation.json");
   }

   public ResourceLocation getModelResource(RestlessSpiritEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/restlessspirit.geo.json");
   }

   public ResourceLocation getTextureResource(RestlessSpiritEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(RestlessSpiritEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("head");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
