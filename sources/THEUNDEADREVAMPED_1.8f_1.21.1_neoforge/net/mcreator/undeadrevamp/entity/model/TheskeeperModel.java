package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.TheskeeperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TheskeeperModel extends GeoModel<TheskeeperEntity> {
   public ResourceLocation getAnimationResource(TheskeeperEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/skeeper.animation.json");
   }

   public ResourceLocation getModelResource(TheskeeperEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/skeeper.geo.json");
   }

   public ResourceLocation getTextureResource(TheskeeperEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(TheskeeperEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("rec");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
