package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrimsonMosquito;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCrimsonMosquitoBlood;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderCrimsonMosquito extends MobRenderer<EntityCrimsonMosquito, ModelCrimsonMosquito> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/crimson_mosquito.png");
   private static final ResourceLocation TEXTURE_SICK = AMCompat.rl("alexsmobs:textures/entity/crimson_mosquito_blue.png");
   private static final ResourceLocation TEXTURE_FLY = AMCompat.rl("alexsmobs:textures/entity/crimson_mosquito_fly.png");
   private static final ResourceLocation TEXTURE_SICK_FLY = AMCompat.rl("alexsmobs:textures/entity/crimson_mosquito_fly_blue.png");

   public RenderCrimsonMosquito(Context renderManagerIn) {
      super(renderManagerIn, new ModelCrimsonMosquito(), 0.6F);
      this.addLayer(new LayerCrimsonMosquitoBlood(this));
   }

   protected void scale(EntityCrimsonMosquito entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      float mosScale = entitylivingbaseIn.prevMosquitoScale + (entitylivingbaseIn.getMosquitoScale() - entitylivingbaseIn.prevMosquitoScale) * partialTickTime;
      matrixStackIn.scale(mosScale * 1.2F, mosScale * 1.2F, mosScale * 1.2F);
   }

   protected boolean isShaking(EntityCrimsonMosquito fly) {
      return fly.isSick() || fly.getFleeingEntityId() != -1;
   }

   protected void setupRotations(
      EntityCrimsonMosquito entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks, float scale
   ) {
      if (this.isShaking(entityLiving)) {
         rotationYaw += (float)(Math.cos(entityLiving.tickCount * 7.0) * 3.141592653589793 * 0.8999999761581421);
         float vibrate = 0.05F * entityLiving.getMosquitoScale();
         matrixStackIn.translate(
            (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate,
            (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate,
            (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate
         );
      }

      super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks, scale);
   }

   public ResourceLocation getTextureLocation(EntityCrimsonMosquito entity) {
      if (entity.isSick()) {
         return entity.isFromFly() ? TEXTURE_SICK_FLY : TEXTURE_SICK;
      } else {
         return entity.isFromFly() ? TEXTURE_FLY : TEXTURE;
      }
   }
}
