package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFly;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderFly extends MobRenderer<EntityFly, ModelFly> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/fly.png");

   public RenderFly(Context renderManagerIn) {
      super(renderManagerIn, new ModelFly(), 0.2F);
   }

   protected void scale(EntityFly entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   protected boolean isShaking(EntityFly fly) {
      return fly.isInNether();
   }

   protected void setupRotations(EntityFly entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
      if (this.isShaking(entityLiving)) {
         rotationYaw += (float)(Math.cos(entityLiving.tickCount * 7.0) * 3.141592653589793 * 0.8999999761581421);
         float vibrate = 0.05F;
         matrixStackIn.translate(
            (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate,
            (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate,
            (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate
         );
      }

      super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks, scale);
   }

   public ResourceLocation getTextureLocation(EntityFly entity) {
      return TEXTURE;
   }
}
