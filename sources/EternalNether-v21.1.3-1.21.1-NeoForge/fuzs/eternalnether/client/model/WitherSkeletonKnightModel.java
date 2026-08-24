package fuzs.eternalnether.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.eternalnether.world.entity.monster.WitherSkeletonKnight;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WitherSkeletonKnightModel extends HumanoidModel<WitherSkeletonKnight> {
   public WitherSkeletonKnightModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "hat",
         CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.getChild("body")
         .addOrReplaceChild(
            "bodywear",
            CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
      PartDefinition rightArm = partdefinition.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition leftArm = partdefinition.addOrReplaceChild(
         "left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition rightLeg = partdefinition.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition leftLeg = partdefinition.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      leftArm.addOrReplaceChild(
         "left_armwear",
         CubeListBuilder.create().texOffs(40, 32).mirror().addBox(3.0F, -4.0F, -1.5F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false),
         PartPose.offset(-3.0F, 2.0F, 0.0F)
      );
      rightArm.addOrReplaceChild(
         "right_armwear",
         CubeListBuilder.create().texOffs(40, 32).addBox(-9.0F, -4.0F, -1.5F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      leftLeg.addOrReplaceChild(
         "left_legwear",
         CubeListBuilder.create().texOffs(0, 32).mirror().addBox(0.0F, -12.0F, -2.1F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)).mirror(false),
         PartPose.offset(-2.0F, 12.0F, 0.1F)
      );
      rightLeg.addOrReplaceChild(
         "right_legwear",
         CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -12.0F, -2.1F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)),
         PartPose.offset(2.0F, 12.0F, 0.1F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void prepareMobModel(WitherSkeletonKnight witherSkeletonKnight, float pitch, float yaw, float roll) {
      this.rightArmPose = ArmPose.EMPTY;
      this.leftArmPose = ArmPose.EMPTY;
      ItemStack itemstack = witherSkeletonKnight.getItemInHand(InteractionHand.MAIN_HAND);
      if (itemstack.is(Items.BOW) && witherSkeletonKnight.isAggressive()) {
         if (witherSkeletonKnight.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = ArmPose.BOW_AND_ARROW;
         }
      }

      super.prepareMobModel(witherSkeletonKnight, pitch, yaw, roll);
   }

   public void setupAnim(WitherSkeletonKnight witherSkeletonKnight, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setupAnim(witherSkeletonKnight, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      ItemStack itemstack = witherSkeletonKnight.getMainHandItem();
      if (witherSkeletonKnight.isAggressive() && (itemstack.isEmpty() || !itemstack.is(Items.BOW))) {
         float f = Mth.sin(this.attackTime * 3.1415927F);
         float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * 3.1415927F);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - f * 0.6F);
         this.leftArm.yRot = 0.1F - f * 0.6F;
         this.rightArm.xRot = -1.5707964F;
         this.leftArm.xRot = -1.5707964F;
         this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;
         this.leftArm.xRot -= f * 1.2F - f1 * 0.4F;
         AnimationUtils.bobArms(this.rightArm, this.leftArm, ageInTicks);
      }

      if (witherSkeletonKnight.isAlive() && witherSkeletonKnight.isUsingShield()) {
         boolean flag = witherSkeletonKnight.getMainArm() == HumanoidArm.RIGHT;
         if (witherSkeletonKnight.getShieldHand() == InteractionHand.MAIN_HAND == flag) {
            this.poseRightArmShield();
         } else if (witherSkeletonKnight.getShieldHand() == InteractionHand.OFF_HAND == flag) {
            this.poseLeftArmShield();
         }
      }
   }

   private void poseRightArmShield() {
      this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F + 0.7853982F;
      this.rightArm.yRot = -0.5235988F;
   }

   private void poseLeftArmShield() {
      this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F + 0.7853982F;
      this.leftArm.yRot = 0.5235988F;
   }

   public void translateToHand(HumanoidArm arm, PoseStack pose) {
      float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      ModelPart modelpart = this.getArm(arm);
      modelpart.x += f;
      modelpart.translateAndRotate(pose);
      modelpart.x -= f;
   }
}
