package fuzs.eternalnether.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.eternalnether.world.entity.monster.Corpor;
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

public class CorporModel extends HumanoidModel<Corpor> {
   public CorporModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -9.0F, -4.0F, 9.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void prepareMobModel(Corpor corpor, float pitch, float yaw, float roll) {
      this.rightArmPose = ArmPose.EMPTY;
      this.leftArmPose = ArmPose.EMPTY;
      ItemStack itemstack = corpor.getItemInHand(InteractionHand.MAIN_HAND);
      if (itemstack.is(Items.BOW) && corpor.isAggressive()) {
         if (corpor.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = ArmPose.BOW_AND_ARROW;
         }
      }

      super.prepareMobModel(corpor, pitch, yaw, roll);
   }

   public void setupAnim(Corpor corpor, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setupAnim(corpor, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      ItemStack itemstack = corpor.getMainHandItem();
      if (corpor.isAggressive() && (itemstack.isEmpty() || !itemstack.is(Items.BOW))) {
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
   }

   public void translateToHand(HumanoidArm side, PoseStack poseStack) {
      float f = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      ModelPart modelpart = this.getArm(side);
      modelpart.x += f;
      modelpart.translateAndRotate(poseStack);
      modelpart.x -= f;
   }
}
