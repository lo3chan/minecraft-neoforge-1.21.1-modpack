package net.mcreator.undeadrevamp.entity.model;

import net.mcreator.undeadrevamp.entity.ThebeartamerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ThebeartamerModel extends GeoModel<ThebeartamerEntity> {
   public ResourceLocation getAnimationResource(ThebeartamerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:animations/thebear.animation.json");
   }

   public ResourceLocation getModelResource(ThebeartamerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:geo/thebear.geo.json");
   }

   public ResourceLocation getTextureResource(ThebeartamerEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(ThebeartamerEntity animatable, long instanceId, AnimationState animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("rote");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * 0.017453292F);
         head.setRotY(entityData.netHeadYaw() * 0.017453292F);
      }
   }
}
