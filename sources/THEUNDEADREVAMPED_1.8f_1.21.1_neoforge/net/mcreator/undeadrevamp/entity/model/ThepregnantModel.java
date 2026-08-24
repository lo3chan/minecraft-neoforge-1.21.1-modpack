package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThepregnantEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ThepregnantModel extends GeoModel<ThepregnantEntity> {
   public ResourceLocation getAnimationResource(ThepregnantEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/pregnant.animation.json");
   }

   public ResourceLocation getModelResource(ThepregnantEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/pregnant.geo.json");
   }

   public ResourceLocation getTextureResource(ThepregnantEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(ThepregnantEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("heade");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
