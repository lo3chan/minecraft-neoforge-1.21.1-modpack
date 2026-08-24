package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThesomnolenceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ThesomnolenceModel extends GeoModel<ThesomnolenceEntity> {
   public ResourceLocation getAnimationResource(ThesomnolenceEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/tsetse.animation.json");
   }

   public ResourceLocation getModelResource(ThesomnolenceEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/tsetse.geo.json");
   }

   public ResourceLocation getTextureResource(ThesomnolenceEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(ThesomnolenceEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("heade");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
