package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ThehunterModel extends GeoModel<ThehunterEntity> {
   public ResourceLocation getAnimationResource(ThehunterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/thehunter.animation.json");
   }

   public ResourceLocation getModelResource(ThehunterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/thehunter.geo.json");
   }

   public ResourceLocation getTextureResource(ThehunterEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(ThehunterEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("heade");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
