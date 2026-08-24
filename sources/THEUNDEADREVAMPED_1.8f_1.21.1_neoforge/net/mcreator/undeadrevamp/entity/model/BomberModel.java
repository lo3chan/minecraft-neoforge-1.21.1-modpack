package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.BomberEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BomberModel extends GeoModel<BomberEntity> {
   public ResourceLocation getAnimationResource(BomberEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/bomber.animation.json");
   }

   public ResourceLocation getModelResource(BomberEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/bomber.geo.json");
   }

   public ResourceLocation getTextureResource(BomberEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(BomberEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("head");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
