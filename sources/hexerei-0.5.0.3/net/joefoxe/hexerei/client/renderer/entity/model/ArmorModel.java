package net.joefoxe.hexerei.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorModel extends HumanoidModel<LivingEntity> {
   public EquipmentSlot slot;
   public LivingEntity entity;

   public ArmorModel(ModelPart root, EquipmentSlot slot) {
      super(root);
      this.slot = slot;
   }

   public void setupAnim(LivingEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      if (pEntity instanceof ArmorStand entityIn) {
         this.head.xRot = 0.017453292F * entityIn.getHeadPose().getX();
         this.head.yRot = 0.017453292F * entityIn.getHeadPose().getY();
         this.head.zRot = 0.017453292F * entityIn.getHeadPose().getZ();
         this.head.setPos(0.0F, 1.0F, 0.0F);
         this.body.xRot = 0.017453292F * entityIn.getBodyPose().getX();
         this.body.yRot = 0.017453292F * entityIn.getBodyPose().getY();
         this.body.zRot = 0.017453292F * entityIn.getBodyPose().getZ();
         this.leftArm.xRot = 0.017453292F * entityIn.getLeftArmPose().getX();
         this.leftArm.yRot = 0.017453292F * entityIn.getLeftArmPose().getY();
         this.leftArm.zRot = 0.017453292F * entityIn.getLeftArmPose().getZ();
         this.rightArm.xRot = 0.017453292F * entityIn.getRightArmPose().getX();
         this.rightArm.yRot = 0.017453292F * entityIn.getRightArmPose().getY();
         this.rightArm.zRot = 0.017453292F * entityIn.getRightArmPose().getZ();
         this.leftLeg.xRot = 0.017453292F * entityIn.getLeftLegPose().getX();
         this.leftLeg.yRot = 0.017453292F * entityIn.getLeftLegPose().getY();
         this.leftLeg.zRot = 0.017453292F * entityIn.getLeftLegPose().getZ();
         this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
         this.rightLeg.xRot = 0.017453292F * entityIn.getRightLegPose().getX();
         this.rightLeg.yRot = 0.017453292F * entityIn.getRightLegPose().getY();
         this.rightLeg.zRot = 0.017453292F * entityIn.getRightLegPose().getZ();
         this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
         this.hat.copyFrom(this.head);
      } else {
         super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      }
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.setPartVisibility(this.slot);
      super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
   }

   private void setPartVisibility(EquipmentSlot slot) {
      this.setAllVisible(false);
      switch (slot) {
         case HEAD:
            this.head.visible = true;
            this.hat.visible = true;
            break;
         case CHEST:
            this.body.visible = true;
            this.rightArm.visible = true;
            this.leftArm.visible = true;
            break;
         case LEGS:
            this.body.visible = true;
            this.rightLeg.visible = true;
            this.leftLeg.visible = true;
            break;
         case FEET:
            this.rightLeg.visible = true;
            this.leftLeg.visible = true;
      }
   }
}
