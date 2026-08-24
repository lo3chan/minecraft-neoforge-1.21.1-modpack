package vazkii.psi.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.jetbrains.annotations.NotNull;

public class ModelArmor extends HumanoidModel<LivingEntity> {
   public ModelArmor(ModelPart root) {
      super(root);
   }

   public void setupAnim(@NotNull LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      if (entity instanceof ArmorStand entityIn) {
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
         super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      }
   }
}
