package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSugarGlider;
import com.github.alexthe666.alexsmobs.entity.EntitySugarGlider;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;

public class RenderSugarGlider extends MobRenderer<EntitySugarGlider, ModelSugarGlider> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/sugar_glider.png");

   public RenderSugarGlider(Context renderManagerIn) {
      super(renderManagerIn, new ModelSugarGlider(), 0.35F);
   }

   private Direction rotate(Direction attachmentFacing) {
      return attachmentFacing.getAxis() == Axis.Y ? Direction.UP : attachmentFacing;
   }

   protected void setupRotations(EntitySugarGlider entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
      if (entityLiving.isPassenger()) {
         super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks, scale);
      } else {
         if (this.isShaking(entityLiving)) {
            rotationYaw += (float)(Math.cos(entityLiving.tickCount * 3.25) * 3.141592653589793 * 0.4000000059604645);
         }

         float trans = entityLiving.isBaby() ? 0.2F : 0.4F;
         Pose pose = entityLiving.getPose();
         if (pose != Pose.SLEEPING) {
            float prevProg = entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks;
            float yawMul = 0.0F;
            if (entityLiving.prevAttachDir == entityLiving.getAttachmentFacing() && entityLiving.getAttachmentFacing().getAxis() == Axis.Y) {
               yawMul = 1.0F;
            }

            matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yawMul * rotationYaw));
            if (entityLiving.getAttachmentFacing() == Direction.DOWN) {
               matrixStackIn.translate(0.0, trans, 0.0);
               if (entityLiving.yo <= entityLiving.getY()) {
                  matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F * prevProg));
               } else {
                  matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F * prevProg));
               }

               matrixStackIn.translate(0.0, -trans, 0.0);
            }

            matrixStackIn.translate(0.0, trans, 0.0);
            Quaternionf current = this.rotate(entityLiving.getAttachmentFacing()).getRotation();
            current.mul(1.0F - prevProg);
            matrixStackIn.mulPose(current);
            matrixStackIn.translate(0.0, -trans, 0.0);
         }

         if (entityLiving.deathTime > 0) {
            float f = (entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
               f = 1.0F;
            }

            matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entityLiving)));
         } else if (entityLiving.isAutoSpinAttack()) {
            matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F - entityLiving.getXRot()));
            matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees((entityLiving.tickCount + partialTicks) * -75.0F));
         } else if (entityLiving.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
            if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
               matrixStackIn.translate(0.0, entityLiving.getBbHeight() + 0.1F, 0.0);
               matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F));
            }
         }
      }
   }

   protected void scale(EntitySugarGlider mob, PoseStack matrixStackIn, float partialTickTime) {
      if (mob.isPassenger() && mob.getVehicle() != null && mob.getVehicle() instanceof Player) {
         Player mount = (Player)mob.getVehicle();
         EntityModel<?> playerModel = AMRenderCompat.rendererModel(mount);
         if ((Minecraft.getInstance().player != mount || Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON)
            && playerModel instanceof HumanoidModel) {
            matrixStackIn.translate(0.0F, 0.5F, 0.0F);
            ((HumanoidModel)playerModel).head.translateAndRotate(matrixStackIn);
            matrixStackIn.translate(0.0F, -0.5F, 0.0F);
         }
      }
   }

   public ResourceLocation getTextureLocation(EntitySugarGlider entity) {
      return TEXTURE;
   }
}
