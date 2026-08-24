package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAnt;
import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAntQueen;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerLeafcutterAntLeaf;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderLeafcutterAnt extends MobRenderer<EntityLeafcutterAnt, AdvancedEntityModel<EntityLeafcutterAnt>> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/leafcutter_ant.png");
   private static final ResourceLocation TEXTURE_QUEEN = AMCompat.rl("alexsmobs:textures/entity/leafcutter_ant_queen.png");
   private static final ResourceLocation TEXTURE_ANGRY = AMCompat.rl("alexsmobs:textures/entity/leafcutter_ant_angry.png");
   private static final ResourceLocation TEXTURE_QUEEN_ANGRY = AMCompat.rl("alexsmobs:textures/entity/leafcutter_ant_queen_angry.png");
   private final ModelLeafcutterAnt modelAnt = new ModelLeafcutterAnt();
   private final ModelLeafcutterAntQueen modelQueen = new ModelLeafcutterAntQueen();

   public RenderLeafcutterAnt(Context renderManagerIn) {
      super(renderManagerIn, new ModelLeafcutterAnt(), 0.25F);
      this.addLayer(new LayerLeafcutterAntLeaf(this));
   }

   protected void setupRotations(
      EntityLeafcutterAnt entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks, float scale
   ) {
      if (this.isShaking(entityLiving)) {
         rotationYaw += (float)(Math.cos(entityLiving.tickCount * 3.25) * 3.141592653589793 * 0.4000000059604645);
      }

      float trans = entityLiving.isBaby() ? 0.25F : 0.5F;
      Pose pose = entityLiving.getPose();
      if (pose != Pose.SLEEPING) {
         float progresso = 1.0F
            - (entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks);
         if (entityLiving.getAttachmentFacing() == Direction.DOWN) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
            matrixStackIn.translate(0.0, trans, 0.0);
            if (entityLiving.yo < entityLiving.getY()) {
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * (1.0F - progresso)));
            } else {
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F * (1.0F - progresso)));
            }

            matrixStackIn.translate(0.0, -trans, 0.0);
         } else if (entityLiving.getAttachmentFacing() == Direction.UP) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            matrixStackIn.translate(0.0, -trans, 0.0);
         } else {
            matrixStackIn.translate(0.0, trans, 0.0);
            switch (entityLiving.getAttachmentFacing()) {
               case NORTH:
                  matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * progresso));
                  matrixStackIn.mulPose(Axis.ZP.rotationDegrees(0.0F));
                  break;
               case SOUTH:
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
                  matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * progresso));
                  break;
               case WEST:
                  matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F - 90.0F * progresso));
                  matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                  break;
               case EAST:
                  matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F * progresso - 90.0F));
                  matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            if (entityLiving.getDeltaMovement().y <= -0.0010000000474974513) {
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180.0F));
            }

            matrixStackIn.translate(0.0, -trans, 0.0);
         }
      }

      if (entityLiving.deathTime > 0) {
         float f = (entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = Mth.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entityLiving)));
      } else if (entityLiving.isAutoSpinAttack()) {
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F - entityLiving.getXRot()));
         matrixStackIn.mulPose(Axis.YP.rotationDegrees((entityLiving.tickCount + partialTicks) * -75.0F));
      } else if (pose != Pose.SLEEPING && entityLiving.hasCustomName()) {
         String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
         if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
            matrixStackIn.translate(0.0, entityLiving.getBbHeight() + 0.1F, 0.0);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
         }
      }
   }

   protected void scale(EntityLeafcutterAnt entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      this.model = (EntityModel)(entitylivingbaseIn.isQueen() ? this.modelQueen : this.modelAnt);
   }

   public ResourceLocation getTextureLocation(EntityLeafcutterAnt entity) {
      if (entity.getRemainingPersistentAngerTime() > 0) {
         return entity.isQueen() ? TEXTURE_QUEEN_ANGRY : TEXTURE_ANGRY;
      } else {
         return entity.isQueen() ? TEXTURE_QUEEN : TEXTURE;
      }
   }
}
