package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityAnacondaPart;
import com.github.alexthe666.alexsmobs.entity.util.AnacondaPartIndex;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderAnacondaPart extends LivingEntityRenderer<EntityAnacondaPart, AdvancedEntityModel<EntityAnacondaPart>> {
   private final ModelAnaconda<EntityAnacondaPart> neckModel = new ModelAnaconda<>(AnacondaPartIndex.NECK);
   private final ModelAnaconda<EntityAnacondaPart> bodyModel = new ModelAnaconda<>(AnacondaPartIndex.BODY);
   private final ModelAnaconda<EntityAnacondaPart> tailModel = new ModelAnaconda<>(AnacondaPartIndex.TAIL);

   public RenderAnacondaPart(Context renderManagerIn) {
      super(renderManagerIn, new ModelAnaconda(AnacondaPartIndex.NECK), 0.3F);
   }

   protected void setupRotations(EntityAnacondaPart entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime, float scale) {
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

         stack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
      } else if (entity.hasCustomName()) {
         String s = ChatFormatting.stripFormatting(entity.getName().getString());
         if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
            stack.translate(0.0, entity.getBbHeight() + 0.1F, 0.0);
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
         }
      }
   }

   protected boolean shouldShowName(EntityAnacondaPart entity) {
      return false;
   }

   protected void scale(EntityAnacondaPart entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      this.model = this.getModelForType(entitylivingbaseIn.getPartType());
      matrixStackIn.scale(entitylivingbaseIn.getScale(), entitylivingbaseIn.getScale(), entitylivingbaseIn.getScale());
   }

   private AdvancedEntityModel<EntityAnacondaPart> getModelForType(AnacondaPartIndex partType) {
      switch (partType) {
         case BODY:
            return this.bodyModel;
         case NECK:
            return this.neckModel;
         case TAIL:
            return this.tailModel;
         default:
            return this.bodyModel;
      }
   }

   public ResourceLocation getTextureLocation(EntityAnacondaPart entity) {
      return RenderAnaconda.getAnacondaTexture(entity.isYellow(), entity.isShedding());
   }
}
