package vazkii.psi.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ModelPsimetalExosuit {
   public static MeshDefinition createInsideMesh() {
      CubeDeformation deformation = new CubeDeformation(0.01F);
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, deformation), PartPose.ZERO);
      PartDefinition belt = body.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(0, 53).addBox(-4.5F, 8.0F, -3.0F, 9.0F, 5.0F, 6.0F), PartPose.ZERO);
      PartDefinition legL = root.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(0, 64).addBox(-1.39F, 1.0F, -2.49F, 4.0F, 5.0F, 5.0F), PartPose.ZERO
      );
      PartDefinition legR = root.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(0, 64).addBox(-2.61F, 1.0F, -2.51F, 4.0F, 5.0F, 5.0F), PartPose.ZERO
      );
      return mesh;
   }

   public static MeshDefinition createOutsideMesh() {
      CubeDeformation deformation = new CubeDeformation(0.01F);
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).mirror(), PartPose.ZERO);
      PartDefinition helm = head.addOrReplaceChild(
         "helm", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-4.5F, -9.0F, -5.0F, 9.0F, 9.0F, 10.0F), PartPose.ZERO
      );
      PartDefinition helmDetailL = helm.addOrReplaceChild(
         "helmDetailL", CubeListBuilder.create().texOffs(0, 0).addBox(4.5F, -5.0F, 0.0F, 1.0F, 3.0F, 3.0F), PartPose.ZERO
      );
      PartDefinition helmDetailR = helm.addOrReplaceChild(
         "helmDetailR", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -5.0F, 0.0F, 1.0F, 3.0F, 3.0F), PartPose.ZERO
      );
      PartDefinition sensor = helm.addOrReplaceChild(
         "sensor", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(4.5F, -8.0F, -2.0F, 1.0F, 3.0F, 5.0F), PartPose.ZERO
      );
      PartDefinition sensorColor = helm.addOrReplaceChild(
         "sensorColor", CubeListBuilder.create().texOffs(38, 8).mirror().addBox(4.51F, -7.01F, -1.0F, 1.0F, 2.0F, 3.0F), PartPose.ZERO
      );
      PartDefinition body = root.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(0, 19).addBox(-4.5F, -0.5F, -3.0F, 9.0F, 7.0F, 6.0F, deformation), PartPose.ZERO
      );
      PartDefinition leftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 0).mirror(), PartPose.ZERO);
      PartDefinition armL = leftArm.addOrReplaceChild(
         "armL", CubeListBuilder.create().texOffs(0, 44).mirror().addBox(0.5F, 6.0F, -2.5F, 3.0F, 4.0F, 5.0F, deformation), PartPose.ZERO
      );
      PartDefinition armLpauldron = armL.addOrReplaceChild(
         "armLpauldron",
         CubeListBuilder.create().texOffs(0, 32).mirror().addBox(1.0F, -2.5F, -2.5F, 3.0F, 7.0F, 5.0F, deformation),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.17453292F)
      );
      PartDefinition rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).mirror(), PartPose.ZERO);
      PartDefinition armR = rightArm.addOrReplaceChild(
         "armR", CubeListBuilder.create().texOffs(0, 44).addBox(-3.5F, 6.0F, -2.51F, 3.0F, 4.0F, 5.0F, deformation), PartPose.ZERO
      );
      PartDefinition armRpauldron = armR.addOrReplaceChild(
         "armRpauldron",
         CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -2.5F, -2.5F, 3.0F, 7.0F, 5.0F, deformation),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.17453292F)
      );
      PartDefinition belt = root.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(0, 53).addBox(-4.5F, 8.0F, -3.0F, 9.0F, 5.0F, 6.0F), PartPose.ZERO);
      PartDefinition legL = root.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(0, 64).mirror().addBox(-1.39F, 1.0F, -2.49F, 4.0F, 5.0F, 5.0F), PartPose.ZERO
      );
      PartDefinition legR = root.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(0, 64).addBox(-2.61F, 1.0F, -2.51F, 4.0F, 5.0F, 5.0F), PartPose.ZERO
      );
      PartDefinition bootL = legL.addOrReplaceChild(
         "bootL", CubeListBuilder.create().texOffs(0, 74).mirror().addBox(-2.39F, 8.0F, -2.49F, 5.0F, 4.0F, 5.0F), PartPose.ZERO
      );
      PartDefinition bootR = legR.addOrReplaceChild(
         "bootR", CubeListBuilder.create().texOffs(0, 74).addBox(-2.61F, 8.0F, -2.51F, 5.0F, 4.0F, 5.0F), PartPose.ZERO
      );
      root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      return mesh;
   }
}
