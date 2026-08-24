package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelCaveCentipede;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderCentipedeBody extends MobRenderer<EntityCentipedeBody, AdvancedEntityModel<EntityCentipedeBody>> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/cave_centipede.png");

   public RenderCentipedeBody(Context renderManagerIn) {
      super(renderManagerIn, new ModelCaveCentipede(1), 0.5F);
   }

   protected float getFlipDegrees(EntityCentipedeBody centipede) {
      return 180.0F;
   }

   protected void setupRotations(EntityCentipedeBody entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime, float scale) {
      float newYaw = entity.yHeadRot;
      if (this.isShaking(entity)) {
         newYaw += (float)(Math.cos(entity.tickCount * 3.25) * 3.141592653589793 * 0.4000000059604645);
      }

      Pose pose = entity.getPose();
      if (pose != Pose.SLEEPING) {
         stack.mulPose(Axis.YP.rotationDegrees(180.0F - newYaw));
         stack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
      }

      if (entity.deathTime > 0) {
         float f = (entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
         f = Mth.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         stack.translate(0.0F, f * 1.15F, 0.0F);
         stack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
      } else if (entity.hasCustomName()) {
         String s = ChatFormatting.stripFormatting(entity.getName().getString());
         if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
            stack.translate(0.0, entity.getBbHeight() + 0.1F, 0.0);
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
         }
      }
   }

   public ResourceLocation getTextureLocation(EntityCentipedeBody entity) {
      return TEXTURE;
   }
}
