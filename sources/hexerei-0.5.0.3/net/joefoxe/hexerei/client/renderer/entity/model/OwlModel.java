package net.joefoxe.hexerei.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collections;
import java.util.Map;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.model.ColorableAgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class OwlModel<T extends OwlEntity> extends ColorableAgeableListModel<T> {
   public final ModelPart owl;
   public final ModelPart head;
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(HexereiUtil.getResource("owl"), "main");

   public OwlModel(ModelPart root) {
      this.owl = root.getChild("owl");
      this.head = this.owl.getChild("head");
   }

   public static LayerDefinition createBodyLayerNone() {
      return createBodyLayer(CubeDeformation.NONE);
   }

   public static LayerDefinition createBodyLayerEnlarge() {
      return createBodyLayer(new CubeDeformation(0.1F));
   }

   public static LayerDefinition createBodyLayer(CubeDeformation cube) {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition owl = partdefinition.addOrReplaceChild(
         "owl", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 18.2166F, -0.3823F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition body = owl.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -2.0F, -2.5F, 7.0F, 4.0F, 6.0F, cube), PartPose.offset(0.0F, -0.9668F, -0.6345F)
      );
      PartDefinition chest_r1 = body.addOrReplaceChild(
         "chest_r1",
         CubeListBuilder.create().texOffs(0, 11).addBox(-3.0F, -3.25F, -1.75F, 6.0F, 7.0F, 5.0F, cube),
         PartPose.offsetAndRotation(0.0F, 1.75F, 0.0F, 0.0873F, 0.0F, 0.0F)
      );
      PartDefinition tailMid = body.addOrReplaceChild(
         "tailMid", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.2502F, 3.0167F, -0.2618F, 0.0F, 0.0F)
      );
      PartDefinition tailMid_r1 = tailMid.addOrReplaceChild(
         "tailMid_r1",
         CubeListBuilder.create()
            .texOffs(13, 39)
            .addBox(-1.0F, -1.9983F, 2.6101F, 2.0F, 0.0F, 7.0F, cube)
            .texOffs(0, 24)
            .addBox(-1.0F, -2.0959F, 2.5885F, 2.0F, 0.0F, 7.0F, cube),
         PartPose.offsetAndRotation(0.0F, 1.6F, -3.0F, -0.1745F, 0.0F, 0.0F)
      );
      PartDefinition leftTail = body.addOrReplaceChild(
         "leftTail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.75F, 4.7502F, 3.5167F, -0.2618F, 0.2618F, 0.0F)
      );
      PartDefinition leftTail_r1 = leftTail.addOrReplaceChild(
         "leftTail_r1",
         CubeListBuilder.create().texOffs(9, 40).addBox(-0.0531F, -1.8441F, 2.0488F, 2.0F, 0.0F, 6.0F, cube),
         PartPose.offsetAndRotation(-0.75F, 1.5F, -2.5F, -0.1688F, 0.008F, -0.0444F)
      );
      PartDefinition leftTail_r2 = leftTail.addOrReplaceChild(
         "leftTail_r2",
         CubeListBuilder.create().texOffs(0, 32).addBox(-0.0531F, -1.8441F, 2.0488F, 2.0F, 0.0F, 6.0F, cube),
         PartPose.offsetAndRotation(-0.75F, 1.4F, -2.5F, -0.1688F, 0.008F, -0.0444F)
      );
      PartDefinition rightTail = body.addOrReplaceChild(
         "rightTail", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.75F, 4.7502F, 3.5167F, -0.2618F, -0.2618F, 0.0F)
      );
      PartDefinition rightTail_r1 = rightTail.addOrReplaceChild(
         "rightTail_r1",
         CubeListBuilder.create()
            .texOffs(19, 40)
            .addBox(-1.9481F, -1.7465F, 2.0706F, 2.0F, 0.0F, 6.0F, cube)
            .texOffs(-5, 32)
            .addBox(-1.9469F, -1.8441F, 2.0488F, 2.0F, 0.0F, 6.0F, cube),
         PartPose.offsetAndRotation(0.75F, 1.4F, -2.5F, -0.1688F, -0.008F, 0.0444F)
      );
      PartDefinition leftWingBase = body.addOrReplaceChild(
         "leftWingBase",
         CubeListBuilder.create()
            .texOffs(14, 30)
            .mirror()
            .addBox(-0.5F, 0.0F, -1.5F, 4.0F, 0.0F, 5.0F, cube)
            .mirror(false)
            .texOffs(-4, 41)
            .mirror()
            .addBox(-0.5F, 0.1F, -1.5F, 4.0F, 0.0F, 5.0F, cube)
            .mirror(false),
         PartPose.offsetAndRotation(3.5F, -0.7498F, -0.4833F, 0.0F, 0.0F, 1.4835F)
      );
      PartDefinition leftWing_r1 = leftWingBase.addOrReplaceChild(
         "leftWing_r1",
         CubeListBuilder.create().texOffs(46, 33).mirror().addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, cube).mirror(false),
         PartPose.offsetAndRotation(1.5F, -0.1F, -1.5F, 0.5236F, 0.0F, 0.0F)
      );
      PartDefinition leftWingCloseMiddle = leftWingBase.addOrReplaceChild(
         "leftWingCloseMiddle",
         CubeListBuilder.create()
            .texOffs(21, 5)
            .mirror()
            .addBox(-0.25F, 0.0F, 0.5F, 4.0F, 0.0F, 6.0F, cube)
            .mirror(false)
            .texOffs(-5, 47)
            .mirror()
            .addBox(-0.25F, 0.1F, 0.5F, 4.0F, 0.0F, 6.0F, cube)
            .mirror(false),
         PartPose.offsetAndRotation(3.5F, 0.0F, -2.0F, -0.0873F, -0.5236F, 0.1745F)
      );
      PartDefinition leftWing_r2 = leftWingCloseMiddle.addOrReplaceChild(
         "leftWing_r2",
         CubeListBuilder.create().texOffs(54, 9).mirror().addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, cube).mirror(false),
         PartPose.offsetAndRotation(1.75F, -0.1F, 0.5F, 0.5236F, 0.0F, 0.0F)
      );
      PartDefinition leftWingFarMiddle = leftWingCloseMiddle.addOrReplaceChild(
         "leftWingFarMiddle",
         CubeListBuilder.create()
            .texOffs(20, 0)
            .mirror()
            .addBox(-0.25F, 0.0F, 0.5F, 4.0F, 0.0F, 5.0F, cube)
            .mirror(false)
            .texOffs(-4, 55)
            .mirror()
            .addBox(-0.25F, 0.1F, 0.5F, 4.0F, 0.0F, 5.0F, cube)
            .mirror(false),
         PartPose.offsetAndRotation(3.75F, 0.0F, 0.0F, -0.1745F, -1.0472F, 0.1745F)
      );
      PartDefinition leftWing_r3 = leftWingFarMiddle.addOrReplaceChild(
         "leftWing_r3",
         CubeListBuilder.create().texOffs(53, 3).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, cube).mirror(false),
         PartPose.offsetAndRotation(1.25F, -0.1F, 0.5F, 0.5236F, 0.0F, 0.0F)
      );
      PartDefinition leftWingTip = leftWingFarMiddle.addOrReplaceChild(
         "leftWingTip", CubeListBuilder.create(), PartPose.offsetAndRotation(3.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6109F)
      );
      PartDefinition rightWingBase = body.addOrReplaceChild(
         "rightWingBase",
         CubeListBuilder.create()
            .texOffs(14, 30)
            .addBox(-3.5F, 0.0F, -1.5F, 4.0F, 0.0F, 5.0F, cube)
            .texOffs(-4, 41)
            .addBox(-3.5F, 0.1F, -1.5F, 4.0F, 0.0F, 5.0F, cube),
         PartPose.offsetAndRotation(-3.5F, -0.7498F, -0.4833F, 0.0F, 0.0F, -1.4835F)
      );
      PartDefinition rightWing_r1 = rightWingBase.addOrReplaceChild(
         "rightWing_r1",
         CubeListBuilder.create().texOffs(46, 33).addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, cube),
         PartPose.offsetAndRotation(-1.5F, -0.1F, -1.5F, 0.5236F, 0.0F, 0.0F)
      );
      PartDefinition rightWingCloseMiddle = rightWingBase.addOrReplaceChild(
         "rightWingCloseMiddle",
         CubeListBuilder.create()
            .texOffs(21, 5)
            .addBox(-3.75F, 0.0F, 0.5F, 4.0F, 0.0F, 6.0F, cube)
            .texOffs(-5, 47)
            .addBox(-3.75F, 0.1F, 0.5F, 4.0F, 0.0F, 6.0F, cube),
         PartPose.offsetAndRotation(-3.5F, 0.0F, -2.0F, -0.0873F, 0.5236F, -0.1745F)
      );
      PartDefinition rightWing_r2 = rightWingCloseMiddle.addOrReplaceChild(
         "rightWing_r2",
         CubeListBuilder.create().texOffs(54, 9).addBox(-2.0F, 0.0F, -0.5F, 4.0F, 0.0F, 1.0F, cube),
         PartPose.offsetAndRotation(-1.75F, -0.1F, 0.5F, 0.5236F, 0.0F, 0.0F)
      );
      PartDefinition rightWingFarMiddle = rightWingCloseMiddle.addOrReplaceChild(
         "rightWingFarMiddle",
         CubeListBuilder.create()
            .texOffs(20, 0)
            .addBox(-3.75F, 0.0F, 0.5F, 4.0F, 0.0F, 5.0F, cube)
            .texOffs(-4, 55)
            .addBox(-3.75F, 0.1F, 0.5F, 4.0F, 0.0F, 5.0F, cube),
         PartPose.offsetAndRotation(-3.75F, 0.0F, 0.0F, -0.1745F, 1.0472F, -0.1745F)
      );
      PartDefinition rightWing_r3 = rightWingFarMiddle.addOrReplaceChild(
         "rightWing_r3",
         CubeListBuilder.create().texOffs(53, 3).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, cube),
         PartPose.offsetAndRotation(-1.25F, -0.2F, 0.5F, 0.5236F, 0.0F, 0.0F)
      );
      PartDefinition rightWingTip = rightWingFarMiddle.addOrReplaceChild(
         "rightWingTip", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.75F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6109F)
      );
      PartDefinition rightLeg = owl.addOrReplaceChild(
         "rightLeg",
         CubeListBuilder.create()
            .texOffs(0, 11)
            .addBox(-1.0F, 0.0F, 0.25F, 1.0F, 2.0F, 1.0F, cube)
            .texOffs(12, 29)
            .addBox(-1.5F, 2.0F, -0.75F, 2.0F, 0.0F, 1.0F, cube),
         PartPose.offsetAndRotation(-1.0F, 3.567F, -1.094F, -0.2182F, 0.0F, 0.0F)
      );
      PartDefinition leftLeg = owl.addOrReplaceChild(
         "leftLeg",
         CubeListBuilder.create()
            .texOffs(12, 24)
            .addBox(0.0F, 0.0F, 0.25F, 1.0F, 2.0F, 1.0F, cube)
            .texOffs(37, 15)
            .addBox(-0.5F, 2.0F, -0.75F, 2.0F, 0.0F, 1.0F, cube),
         PartPose.offsetAndRotation(1.0F, 3.567F, -1.094F, -0.2182F, 0.0F, 0.0F)
      );
      PartDefinition head = owl.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(18, 19)
            .addBox(-3.0F, -3.5126F, -2.7186F, 6.0F, 5.0F, 5.0F, cube)
            .texOffs(10, 50)
            .addBox(-3.0F, -3.5126F, -2.7186F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(0.0F, -3.9001F, 0.3282F, -0.1745F, 0.0F, 0.0F)
      );
      PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create(), PartPose.offset(0.0F, 0.6835F, -2.9962F));
      PartDefinition beak_r1 = beak.addOrReplaceChild(
         "beak_r1",
         CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.75F, 1.0F, 2.0F, 1.0F, cube),
         PartPose.offsetAndRotation(0.0F, -0.6961F, 0.0276F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition rightBrow = head.addOrReplaceChild("rightBrow", CubeListBuilder.create(), PartPose.offset(-0.85F, -3.2626F, -2.8186F));
      PartDefinition rightBrow_r1 = rightBrow.addOrReplaceChild(
         "rightBrow_r1",
         CubeListBuilder.create().texOffs(0, 27).addBox(-1.2462F, -1.4566F, 0.0F, 3.0F, 2.0F, 0.0F, cube),
         PartPose.offsetAndRotation(-1.15F, 1.0F, -0.15F, 0.0F, 0.0F, 0.1745F)
      );
      PartDefinition rightBrow_r2 = rightBrow.addOrReplaceChild(
         "rightBrow_r2",
         CubeListBuilder.create().texOffs(11, 32).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, cube),
         PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0451F, 0.0834F, 0.1725F)
      );
      PartDefinition rightBrow_r3 = rightBrow.addOrReplaceChild(
         "rightBrow_r3",
         CubeListBuilder.create().texOffs(36, 20).addBox(-1.6F, -0.5F, -0.6F, 3.0F, 1.0F, 1.0F, cube),
         PartPose.offsetAndRotation(-2.2437F, -0.5276F, 1.2532F, 0.3589F, 1.0414F, 0.6033F)
      );
      PartDefinition rightBrow_r4 = rightBrow.addOrReplaceChild(
         "rightBrow_r4",
         CubeListBuilder.create().texOffs(11, 36).addBox(-2.1F, 0.0F, 0.4F, 4.0F, 0.0F, 1.0F, cube),
         PartPose.offsetAndRotation(-2.2437F, -0.7776F, 1.2532F, 0.3589F, 1.0414F, 0.6033F)
      );
      PartDefinition leftBrow = head.addOrReplaceChild("leftBrow", CubeListBuilder.create(), PartPose.offset(0.85F, -3.2626F, -2.8186F));
      PartDefinition leftBrow_r1 = leftBrow.addOrReplaceChild(
         "leftBrow_r1",
         CubeListBuilder.create().texOffs(0, 24).addBox(-1.7538F, -1.4566F, 0.0F, 3.0F, 2.0F, 0.0F, cube),
         PartPose.offsetAndRotation(1.15F, 1.0F, -0.15F, 0.0F, 0.0F, -0.1745F)
      );
      PartDefinition leftBrow_r2 = leftBrow.addOrReplaceChild(
         "leftBrow_r2",
         CubeListBuilder.create().texOffs(21, 37).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, cube),
         PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0451F, -0.0834F, -0.1725F)
      );
      PartDefinition leftBrow_r3 = leftBrow.addOrReplaceChild(
         "leftBrow_r3",
         CubeListBuilder.create().texOffs(37, 12).addBox(-1.4F, -0.5F, -0.6F, 3.0F, 1.0F, 1.0F, cube),
         PartPose.offsetAndRotation(2.2437F, -0.5276F, 1.2532F, 0.3589F, -1.0414F, -0.6033F)
      );
      PartDefinition leftBrow_r4 = leftBrow.addOrReplaceChild(
         "leftBrow_r4",
         CubeListBuilder.create().texOffs(36, 18).addBox(-1.9F, 0.0F, 0.4F, 4.0F, 0.0F, 1.0F, cube),
         PartPose.offsetAndRotation(2.2437F, -0.7776F, 1.2532F, 0.3589F, -1.0414F, -0.6033F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(OwlEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.setupInitialAnimationValues(entity, netHeadYaw, headPitch);
      ModelPart body = this.owl.getChild("body");
      ModelPart leftWingBase = body.getChild("leftWingBase");
      ModelPart leftWingCloseMiddle = leftWingBase.getChild("leftWingCloseMiddle");
      ModelPart leftWingFarMiddle = leftWingCloseMiddle.getChild("leftWingFarMiddle");
      ModelPart rightWingBase = body.getChild("rightWingBase");
      ModelPart rightWingCloseMiddle = rightWingBase.getChild("rightWingCloseMiddle");
      ModelPart rightWingFarMiddle = rightWingCloseMiddle.getChild("rightWingFarMiddle");
      ModelPart rightLeg = this.owl.getChild("rightLeg");
      ModelPart leftLeg = this.owl.getChild("leftLeg");
      ModelPart head = this.owl.getChild("head");
      ModelPart rightTail = body.getChild("rightTail");
      ModelPart leftTail = body.getChild("leftTail");
      ModelPart tailMid = body.getChild("tailMid");
      ModelPart beak = head.getChild("beak");
      ModelPart rightBrow = head.getChild("rightBrow");
      ModelPart leftBrow = head.getChild("leftBrow");
      if (!entity.onGround() || !entity.isInSittingPose()) {
         this.owl.y = 18.5F;
      }

      float ticks = entity.tickCount + entity.getId() * 235.0F + ClientEvents.getPartial();
      float partial = ClientEvents.getPartial();
      rightWingBase.zRot = Mth.lerp(partial, entity.rightWingAngleLast, entity.rightWingAngle);
      leftWingBase.zRot = Mth.lerp(partial, entity.leftWingAngleLast, entity.leftWingAngle);
      rightWingCloseMiddle.zRot = Mth.lerp(partial, entity.rightWingMiddleAngleLast, entity.rightWingMiddleAngle);
      leftWingCloseMiddle.zRot = Mth.lerp(partial, entity.leftWingMiddleAngleLast, entity.leftWingMiddleAngle);
      rightWingBase.xRot = 0.0F;
      leftWingBase.xRot = 0.0F;
      rightWingBase.yRot = entity.rightWingFoldAngle;
      leftWingBase.yRot = entity.leftWingFoldAngle;
      rightWingCloseMiddle.yRot = entity.rightWingMiddleFoldAngle;
      leftWingCloseMiddle.yRot = entity.leftWingMiddleFoldAngle;
      rightWingFarMiddle.yRot = entity.rightWingTipAngle;
      leftWingFarMiddle.yRot = entity.leftWingTipAngle;
      this.owl.xRot = Mth.sin(entity.peckAnimation.getPeckRot() / 100.0F);
      this.owl.zRot = 0.0F;
      this.owl.yRot = 0.0F;
      body.yRot = 0.0F;
      head.yRot = 0.0F;
      head.zRot = 0.0F;
      leftBrow.zRot = 0.0F;
      rightBrow.zRot = 0.0F;
      leftBrow.yRot = 0.0F;
      rightBrow.yRot = 0.0F;
      leftBrow.z = -2.81F;
      rightBrow.z = -2.81F;
      leftBrow.y = -3.26F;
      rightBrow.y = -3.26F;
      leftBrow.x = 0.85F;
      rightBrow.x = -0.85F;
      if (entity.browAnimation.getBrowAnim() == OwlEntity.BrowAnim.BOTH) {
         leftBrow.zRot = leftBrow.zRot + Mth.sin(entity.browAnimation.getBrowRot() / 100.0F) * 0.2F;
         rightBrow.zRot = rightBrow.zRot - Mth.sin(entity.browAnimation.getBrowRot() / 100.0F) * 0.2F;
         leftBrow.y = leftBrow.y + Mth.sin(entity.browAnimation.getBrowRot() / 50.0F) * 0.3F;
         rightBrow.y = rightBrow.y + Mth.sin(entity.browAnimation.getBrowRot() / 50.0F) * 0.3F;
      }

      if (entity.browAnimation.getBrowAnim() == OwlEntity.BrowAnim.RIGHT) {
         rightBrow.zRot = rightBrow.zRot - Mth.sin(entity.browAnimation.getBrowRot() / 100.0F) * 0.2F;
         rightBrow.y = rightBrow.y + Mth.sin(entity.browAnimation.getBrowRot() / 50.0F) * 0.3F;
      }

      if (entity.browAnimation.getBrowAnim() == OwlEntity.BrowAnim.LEFT) {
         leftBrow.zRot = leftBrow.zRot + Mth.sin(entity.browAnimation.getBrowRot() / 100.0F) * 0.2F;
         leftBrow.y = leftBrow.y + Mth.sin(entity.browAnimation.getBrowRot() / 50.0F) * 0.3F;
      }

      if (entity.emotionState != null) {
         leftBrow.zRot = (float)(leftBrow.zRot + Math.toRadians(entity.emotionState.getzRot()));
         rightBrow.zRot = (float)(rightBrow.zRot - Math.toRadians(entity.emotionState.getzRot()));
         leftBrow.y = leftBrow.y + entity.emotionState.getyOffset();
         rightBrow.y = rightBrow.y + entity.emotionState.getyOffset();
         leftBrow.x = leftBrow.x + entity.emotionState.getxOffset();
         rightBrow.x = rightBrow.x - entity.emotionState.getxOffset();
      }

      rightLeg.z = 0.0F;
      leftLeg.z = 0.0F;
      if (entity.onGround() || !entity.isFlying()) {
         rightTail.xRot = Mth.sin(ticks / 15.0F) * 0.1F;
         leftTail.xRot = Mth.sin(ticks / 15.0F) * 0.1F;
         tailMid.xRot = Mth.sin(ticks / 15.0F) * 0.1F;
         if (entity.isInSittingPose()) {
            rightLeg.xRot = -0.7853982F;
            leftLeg.xRot = -0.7853982F;
            rightLeg.y = 2.25F;
            rightLeg.z = -2.0F;
            leftLeg.y = 2.25F;
            leftLeg.z = -2.0F;
            this.owl.y = 20.0F;
            rightTail.xRot += 0.3926991F;
            leftTail.xRot += 0.3926991F;
            tailMid.xRot += 0.3926991F;
         } else {
            rightLeg.y = 3.5F;
            leftLeg.y = 3.5F;
            rightLeg.xRot = Mth.cos(limbSwing * 2.0F + 3.1415927F) * 2.0F * limbSwingAmount;
            leftLeg.xRot = Mth.cos(limbSwing * 2.0F) * 2.0F * limbSwingAmount;
            if (entity.onGround()) {
               this.owl.zRot = Mth.cos(limbSwing * 2.0F) * 0.1F * (float)Math.max(0.08, (double)limbSwingAmount);
               this.owl.xRot = this.owl.xRot + Mth.cos(limbSwing * 4.0F) * 0.1F * (float)Math.max(0.08, (double)limbSwingAmount);
            }
         }

         head.xRot = (float)Math.toRadians(headPitch);
         head.xRot = head.xRot + Mth.sin(ticks / 15.0F) * 0.1F;
         leftBrow.zRot = leftBrow.zRot + -Mth.sin(ticks / 15.0F) * 0.05F;
         rightBrow.zRot = rightBrow.zRot - -Mth.sin(ticks / 15.0F) * 0.05F;
         leftBrow.y = leftBrow.y + -Mth.sin(ticks / 15.0F) * 0.15F;
         rightBrow.y = rightBrow.y + -Mth.sin(ticks / 15.0F) * 0.15F;
         rightTail.yRot = -Mth.sin(0.05F);
         leftTail.yRot = Mth.sin(0.05F);
         rightTail.yRot = rightTail.yRot + Mth.sin(entity.tailWagAnimation.getWagRot() / 100.0F) * 0.2F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.tailWagAnimation.getWagRot() / 100.0F) * 0.2F;
         tailMid.yRot = Mth.sin(entity.tailWagAnimation.getWagRot() / 100.0F) * 0.2F;
         if (entity.tailWagAnimation.isActive()) {
            rightTail.yRot = rightTail.yRot + Mth.sin(0.15F) * 0.5F;
            leftTail.yRot = leftTail.yRot - Mth.sin(0.15F) * 0.5F;
         }

         rightTail.yRot = rightTail.yRot - Mth.sin(entity.tailFanAnimation.getFanRot() / 100.0F) * 0.5F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.tailFanAnimation.getFanRot() / 100.0F) * 0.5F;
      } else if (entity.isPassenger()) {
         rightLeg.xRot = -0.5235988F;
         leftLeg.xRot = -0.5235988F;
         rightLeg.y = 2.5F;
         rightLeg.z = 1.5F;
         leftLeg.y = 2.5F;
         leftLeg.z = 1.5F;
         this.owl.y = 20.0F;
         head.xRot = (float)Math.toRadians(headPitch);
         head.xRot = head.xRot + Mth.sin(ticks / 15.0F) * 0.1F;
         leftBrow.zRot = leftBrow.zRot + -Mth.sin(ticks / 15.0F) * 0.05F;
         rightBrow.zRot = rightBrow.zRot - -Mth.sin(ticks / 15.0F) * 0.05F;
         leftBrow.y = leftBrow.y + -Mth.sin(ticks / 15.0F) * 0.15F;
         rightBrow.y = rightBrow.y + -Mth.sin(ticks / 15.0F) * 0.15F;
         rightTail.xRot = Mth.sin(ticks / 15.0F) * 0.1F;
         leftTail.xRot = Mth.sin(ticks / 15.0F) * 0.1F;
         tailMid.xRot = Mth.sin(ticks / 15.0F) * 0.1F;
         rightTail.yRot = -Mth.sin(0.05F);
         leftTail.yRot = Mth.sin(0.05F);
         rightTail.yRot = rightTail.yRot + Mth.sin(entity.tailWagAnimation.getWagRot() / 100.0F) * 0.2F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.tailWagAnimation.getWagRot() / 100.0F) * 0.2F;
         tailMid.yRot = Mth.sin(entity.tailWagAnimation.getWagRot() / 100.0F) * 0.2F;
         if (entity.tailWagAnimation.isActive()) {
            rightTail.yRot = rightTail.yRot + Mth.sin(0.15F) * 0.5F;
            leftTail.yRot = leftTail.yRot - Mth.sin(0.15F) * 0.5F;
         }

         rightTail.yRot = rightTail.yRot - Mth.sin(entity.tailFanAnimation.getFanRot() / 100.0F) * 0.5F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.tailFanAnimation.getFanRot() / 100.0F) * 0.5F;
      } else {
         rightLeg.xRot = Mth.sin(20.0F);
         leftLeg.xRot = Mth.sin(20.0F);
         head.xRot = (float)Math.toRadians(headPitch);
         rightTail.yRot = -Mth.sin(0.15F);
         leftTail.yRot = Mth.sin(0.15F);
         this.owl.y = 18.5F + Mth.lerp(partial, entity.bodyYOffsetLast, entity.bodyYOffset);
         this.owl.xRot = this.owl.xRot
            + (float)(
               Mth.lerp(partial, entity.bodyXRotLast, entity.bodyXRot)
                  + Math.toRadians(Mth.lerp(partial, entity.itemHeldSwingLast, entity.itemHeldSwing) / 4.0F)
            );
         head.xRot = head.xRot - this.owl.xRot / 1.5F;
         head.zRot = head.zRot - (float)(Math.toRadians(netHeadYaw) / 2.0);
         leftLeg.z = -1.0F;
         rightLeg.z = -1.0F;
         leftLeg.y = 4.5F;
         rightLeg.y = 4.5F;
         rightWingBase.xRot = -this.owl.xRot / 1.15F;
         leftWingBase.xRot = -this.owl.xRot / 1.15F;
         tailMid.xRot = -this.owl.xRot / 1.15F;
         leftTail.xRot = -this.owl.xRot / 1.15F;
         rightTail.xRot = -this.owl.xRot / 1.15F;
         rightWingBase.yRot = rightWingBase.yRot + Mth.lerp(partial, entity.rightWingAngleLast, entity.rightWingAngle) / 2.0F;
         leftWingBase.yRot = leftWingBase.yRot + Mth.lerp(partial, entity.leftWingAngleLast, entity.leftWingAngle) / 2.0F;
      }

      if (entity.emotions.isHappy()) {
         leftBrow.yRot = (float)(leftBrow.yRot - Math.toRadians(10.0));
         rightBrow.yRot = (float)(rightBrow.yRot + Math.toRadians(10.0));
         if (entity.browHappyAnimation.getBrowAnim() == OwlEntity.BrowAnim.BOTH) {
            leftBrow.yRot = leftBrow.yRot + Mth.sin(entity.browHappyAnimation.getBrowRot() / 100.0F) * 0.2F;
            rightBrow.yRot = rightBrow.yRot - Mth.sin(entity.browHappyAnimation.getBrowRot() / 100.0F) * 0.2F;
         }

         if (entity.browHappyAnimation.getBrowAnim() == OwlEntity.BrowAnim.RIGHT) {
            rightBrow.yRot = rightBrow.yRot - Mth.sin(entity.browHappyAnimation.getBrowRot() / 100.0F) * 0.2F;
            body.yRot = body.yRot
               + Mth.sin(Mth.lerp(partial, entity.browHappyAnimation.getBrowRotLast(), entity.browHappyAnimation.getBrowRot()) / 90.0F) * 0.2F;
         }

         if (entity.browHappyAnimation.getBrowAnim() == OwlEntity.BrowAnim.LEFT) {
            leftBrow.yRot = leftBrow.yRot + Mth.sin(entity.browHappyAnimation.getBrowRot() / 100.0F) * 0.2F;
            body.yRot = body.yRot
               + Mth.sin(Mth.lerp(partial, entity.browHappyAnimation.getBrowRotLast(), entity.browHappyAnimation.getBrowRot()) / 90.0F) * 0.2F;
         }

         if (entity.isInSittingPose() || entity.isFlying()) {
            rightLeg.xRot = rightLeg.xRot + Mth.sin(entity.browHappyAnimation.getBrowRot() / 100.0F);
            leftLeg.xRot = leftLeg.xRot - Mth.sin(entity.browHappyAnimation.getBrowRot() / 100.0F);
         }

         leftBrow.y = (float)(leftBrow.y - 0.25);
         rightBrow.y = (float)(rightBrow.y - 0.25);
         leftBrow.z = (float)(leftBrow.z - 0.25);
         rightBrow.z = (float)(rightBrow.z - 0.25);
      }

      head.yRot = head.yRot + (float)Math.toRadians(netHeadYaw);
      head.zRot = head.zRot + Mth.sin(entity.headTiltAnimation.getzTilt() / 100.0F) / 2.0F;
      head.xRot = head.xRot + Mth.sin(entity.headTiltAnimation.getxTilt() / 100.0F) / 2.0F;
      if ((entity.onGround() || entity.isPassenger()) && entity.dance) {
         head.zRot = 0.0F;
         head.xRot = (float)Math.toRadians(headPitch) + Mth.sin(entity.animationCounter / 1.5F) / 12.0F;
         head.yRot = (float)Math.toRadians(netHeadYaw) + Mth.sin(entity.animationCounter / 3.0F) / 4.0F;
         rightTail.xRot = Mth.sin(entity.animationCounter / 3.0F) * 0.1F;
         leftTail.xRot = Mth.sin(entity.animationCounter / 3.0F) * 0.1F;
         tailMid.xRot = Mth.sin(entity.animationCounter / 3.0F) * 0.1F;
         this.owl.yRot = this.owl.yRot + Mth.sin(entity.animationCounter / 3.0F) * 0.2F;
         rightTail.yRot = Mth.sin(0.1F);
         leftTail.yRot = -Mth.sin(0.1F);
         this.owl.y = 18.5F + Mth.abs(Mth.sin(entity.animationCounter / 6.0F));
         rightTail.yRot = rightTail.yRot + Mth.sin(entity.animationCounter / 3.0F) * 0.4F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.animationCounter / 3.0F) * 0.4F;
         tailMid.yRot = Mth.sin(entity.animationCounter / 3.0F) * 0.4F;
         rightLeg.y = 3.5F - Mth.abs(Mth.sin(entity.animationCounter / 6.0F));
         rightLeg.z = 0.5F;
         leftLeg.y = 3.5F - Mth.abs(Mth.sin(entity.animationCounter / 6.0F));
         leftLeg.z = 0.5F;
         rightLeg.xRot = Mth.cos(limbSwing * 2.0F + 3.1415927F) * 2.0F * limbSwingAmount;
         leftLeg.xRot = Mth.cos(limbSwing * 2.0F) * 2.0F * limbSwingAmount;
      }

      beak.xRot = Mth.sin(-entity.hootAnimation.getHootRot() / 100.0F);
      if (entity.peckAnimation.getPeckRot() > 0.0F) {
         beak.xRot = Mth.sin(-entity.peckAnimation.getPeckRot() / 100.0F);
      }

      head.visible = true;
      head.yRot = head.yRot + Mth.sin(Mth.lerp(partial, entity.headShakeAnimation.getzTiltLast(), entity.headShakeAnimation.getzTilt()) / 100.0F) * 1.5F;
      this.saveAnimationValues(entity);
   }

   private Vector3f getRotationVector(ModelPart pModelPart) {
      return new Vector3f(pModelPart.xRot, pModelPart.yRot, pModelPart.zRot);
   }

   private void setRotationFromVector(ModelPart pModelPart, Vector3f pRotationVector) {
      pModelPart.setRotation(pRotationVector.x(), pRotationVector.y(), pRotationVector.z());
   }

   private void saveAnimationValues(OwlEntity crow) {
      Map<String, Vector3f> map = crow.getModelRotationValues();
      map.put("owl", this.getRotationVector(this.owl));
   }

   private void setupInitialAnimationValues(OwlEntity crow, float pNetHeadYaw, float pHeadPitch) {
      this.owl.x = 0.0F;
      this.owl.y = 19.0F;
      Map<String, Vector3f> map = crow.getModelRotationValues();
      if (map.isEmpty()) {
         this.owl.setRotation(pHeadPitch * 0.017453292F, pNetHeadYaw * 0.017453292F, 0.0F);
      } else {
         this.setRotationFromVector(this.owl, map.get("owl"));
      }
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.owl.render(poseStack, buffer, packedLight, packedOverlay, color);
   }

   protected Iterable<ModelPart> headParts() {
      return Collections.singleton(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return Collections.singleton(this.owl);
   }

   public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
      modelRenderer.xRot = x;
      modelRenderer.yRot = y;
      modelRenderer.zRot = z;
   }
}
