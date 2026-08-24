package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.DeadcloggerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DeadcloggerModel extends GeoModel<DeadcloggerEntity> {
   public ResourceLocation getAnimationResource(DeadcloggerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/clogger.animation.json");
   }

   public ResourceLocation getModelResource(DeadcloggerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/clogger.geo.json");
   }

   public ResourceLocation getTextureResource(DeadcloggerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(DeadcloggerEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("head");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
