package net.mcreator.borninchaosv.entity.model;

import net.mcreator.borninchaosv.entity.MrsPumpkinEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MrsPumpkinModel extends GeoModel<MrsPumpkinEntity> {
   public ResourceLocation getAnimationResource(MrsPumpkinEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:animations/mrspumpkin.animation.json");
   }

   public ResourceLocation getModelResource(MrsPumpkinEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:geo/mrspumpkin.geo.json");
   }

   public ResourceLocation getTextureResource(MrsPumpkinEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(MrsPumpkinEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("head");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
