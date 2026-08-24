package net.joefoxe.hexerei.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collections;
import java.util.Map;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
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

public class CrowModel<T extends CrowEntity> extends ColorableAgeableListModel<T> {
   public final ModelPart body;
   public final ModelPart head;
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(HexereiUtil.getResource("crow"), "main");
   public static final ModelLayerLocation POWER_LAYER_LOCATION = new ModelLayerLocation(HexereiUtil.getResource("crow_power_layer"), "main");

   public CrowModel(ModelPart root) {
      this.body = root.getChild("body");
      this.head = this.body.getChild("head");
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
      PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition chest = body.addOrReplaceChild(
         "chest",
         CubeListBuilder.create()
            .texOffs(1, 6)
            .addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 5.0F, cube)
            .texOffs(0, 14)
            .addBox(-1.5F, -2.308F, -0.4665F, 3.0F, 2.0F, 0.0F, cube),
         PartPose.offsetAndRotation(0.0F, -4.0F, -2.5F, -0.5236F, 0.0F, 0.0F)
      );
      PartDefinition bandana_r1 = chest.addOrReplaceChild(
         "bandana_r1",
         CubeListBuilder.create().texOffs(4, 16).addBox(-0.5F, -5.225F, -0.425F, 1.0F, 1.0F, 0.0F, cube),
         PartPose.offsetAndRotation(0.0F, 4.0F, 2.5F, 0.5236F, 0.0F, 0.0F)
      );
      PartDefinition amulet_r1 = body.addOrReplaceChild(
         "amulet_r1",
         CubeListBuilder.create()
            .texOffs(1, 6)
            .addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 5.0F, cube)
            .texOffs(24, 23)
            .addBox(-2.0F, -1.9205F, -0.0217F, 4.0F, 2.0F, 0.0F, cube),
         PartPose.offsetAndRotation(0.0F, -4.0F, -2.5F, -0.5236F, 0.0F, 0.0F)
      );
      PartDefinition amulet_r2 = amulet_r1.addOrReplaceChild(
         "amulet_r2",
         CubeListBuilder.create()
            .texOffs(30, 26)
            .addBox(-1.0F, -4.225F, -0.2F, 2.0F, 1.0F, 0.0F, cube)
            .texOffs(26, 25)
            .addBox(-1.5F, -5.225F, -0.185F, 3.0F, 1.0F, 0.0F, cube),
         PartPose.offsetAndRotation(0.0F, 4.5F, 2.75F, 0.5236F, 0.0F, 0.0F)
      );
      PartDefinition rightLeg = body.addOrReplaceChild(
         "rightLeg", CubeListBuilder.create().texOffs(12, 7).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, cube), PartPose.offset(-1.0F, -3.0F, 0.5F)
      );
      PartDefinition leftLeg = body.addOrReplaceChild(
         "leftLeg", CubeListBuilder.create().texOffs(12, 7).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, cube), PartPose.offset(1.0F, -3.0F, 0.5F)
      );
      PartDefinition rightWing = body.addOrReplaceChild("rightWing", CubeListBuilder.create(), PartPose.offset(-1.0F, -5.5F, -1.5F));
      PartDefinition rightWing_r1 = rightWing.addOrReplaceChild(
         "rightWing_r1",
         CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-11.0F, -5.5F, -2.5F, 10.0F, 0.0F, 6.0F, cube).mirror(false),
         PartPose.offsetAndRotation(1.0F, 5.5F, -0.5F, 0.0F, 0.1745F, 0.0F)
      );
      PartDefinition leftWing = body.addOrReplaceChild("leftWing", CubeListBuilder.create(), PartPose.offset(1.0F, -5.5F, -1.5F));
      PartDefinition leftWing_r1 = leftWing.addOrReplaceChild(
         "leftWing_r1",
         CubeListBuilder.create().texOffs(0, 25).addBox(1.0F, -5.5F, -2.5F, 10.0F, 0.0F, 6.0F, cube),
         PartPose.offsetAndRotation(-1.0F, 5.5F, -0.5F, 0.0F, -0.1745F, 0.0F)
      );
      PartDefinition rightTail = body.addOrReplaceChild(
         "rightTail",
         CubeListBuilder.create().texOffs(16, 14).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 6.0F, cube),
         PartPose.offsetAndRotation(-0.75F, -3.0F, 2.5F, -0.2618F, -0.2618F, 0.0F)
      );
      PartDefinition rightTail_dyed = body.addOrReplaceChild(
         "rightTail_dyed",
         CubeListBuilder.create().texOffs(20, 14).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 6.0F, cube),
         PartPose.offsetAndRotation(-0.75F, -3.0F, 2.5F, -0.2618F, -0.2618F, 0.0F)
      );
      PartDefinition leftTail = body.addOrReplaceChild(
         "leftTail",
         CubeListBuilder.create().texOffs(16, 14).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 6.0F, cube).mirror(false),
         PartPose.offsetAndRotation(0.75F, -3.0F, 2.5F, -0.2618F, 0.2618F, 0.0F)
      );
      PartDefinition leftTail_dyed = body.addOrReplaceChild(
         "leftTail_dyed",
         CubeListBuilder.create().texOffs(20, 14).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 6.0F, cube).mirror(false),
         PartPose.offsetAndRotation(0.75F, -3.0F, 2.5F, -0.2618F, 0.2618F, 0.0F)
      );
      PartDefinition tailMid = body.addOrReplaceChild(
         "tailMid",
         CubeListBuilder.create().texOffs(13, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 7.0F, cube),
         PartPose.offsetAndRotation(0.0F, -3.5F, 3.0F, -0.2618F, 0.0F, 0.0F)
      );
      PartDefinition tailMid_dyed = body.addOrReplaceChild(
         "tailMid_dyed",
         CubeListBuilder.create().texOffs(17, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 7.0F, cube),
         PartPose.offsetAndRotation(0.0F, -3.5F, 3.0F, -0.2618F, 0.0F, 0.0F)
      );
      PartDefinition wings = body.addOrReplaceChild(
         "wings",
         CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, -3.5F, -0.5F, 4.0F, 4.0F, 7.0F, cube),
         PartPose.offsetAndRotation(0.0F, -4.0F, -2.5F, -0.5236F, 0.0F, 0.0F)
      );
      PartDefinition head = body.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3.0F, -3.0F, 3.0F, 3.0F, 3.0F, cube), PartPose.offset(0.0F, -5.0F, -1.5F)
      );
      PartDefinition head_eyes_closed = body.addOrReplaceChild(
         "head_eyes_closed", CubeListBuilder.create().texOffs(20, 7).addBox(-1.5F, -3.0F, -3.0F, 3.0F, 3.0F, 3.0F, cube), PartPose.offset(0.0F, -5.0F, -1.5F)
      );
      PartDefinition head_r1 = head.addOrReplaceChild(
         "head_r1",
         CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -0.0434F, -2.0F, 1.0F, 1.0F, 2.0F, cube),
         PartPose.offsetAndRotation(0.0F, -0.924F, -2.7521F, -0.1309F, 0.0F, 0.0F)
      );
      PartDefinition head_r2 = head.addOrReplaceChild(
         "head_r2",
         CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -0.25F, -0.925F, 1.0F, 1.0F, 2.0F, cube),
         PartPose.offsetAndRotation(0.0F, -1.75F, -4.0F, 0.1309F, 0.0F, 0.0F)
      );
      PartDefinition head_eyes_closed_r1 = head_eyes_closed.addOrReplaceChild(
         "head_eyes_closed_r1",
         CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -0.0434F, -2.0F, 1.0F, 1.0F, 2.0F, cube),
         PartPose.offsetAndRotation(0.0F, -0.924F, -2.7521F, -0.1309F, 0.0F, 0.0F)
      );
      PartDefinition head_eyes_closed_r2 = head_eyes_closed.addOrReplaceChild(
         "head_eyes_closed_r2",
         CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -0.25F, -0.925F, 1.0F, 1.0F, 2.0F, cube),
         PartPose.offsetAndRotation(0.0F, -1.75F, -4.0F, 0.1309F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void renderOnShoulder(
      PoseStack matrixStackIn,
      VertexConsumer bufferIn,
      int packedLightIn,
      int packedOverlayIn,
      float p_228284_5_,
      float p_228284_6_,
      float p_228284_7_,
      float p_228284_8_,
      int p_228284_9_
   ) {
      this.body.getChild("leftWing").visible = false;
      this.body.getChild("rightWing").visible = false;
      this.body.getChild("wings").visible = true;
      this.body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
   }

   public void setupAnim(CrowEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.setupInitialAnimationValues(entity, netHeadYaw, headPitch);
      ModelPart leftWing = this.body.getChild("leftWing");
      ModelPart rightWing = this.body.getChild("rightWing");
      ModelPart wings = this.body.getChild("wings");
      ModelPart rightLeg = this.body.getChild("rightLeg");
      ModelPart leftLeg = this.body.getChild("leftLeg");
      ModelPart head = this.body.getChild("head");
      ModelPart head_eyes_closed = this.body.getChild("head_eyes_closed");
      ModelPart rightTail = this.body.getChild("rightTail");
      ModelPart rightTail_dyed = this.body.getChild("rightTail_dyed");
      ModelPart leftTail = this.body.getChild("leftTail");
      ModelPart leftTail_dyed = this.body.getChild("leftTail_dyed");
      ModelPart tailMid = this.body.getChild("tailMid");
      ModelPart tailMid_dyed = this.body.getChild("tailMid_dyed");
      ModelPart head_r1 = head.getChild("head_r1");
      ModelPart head_eyes_closed_r1 = head_eyes_closed.getChild("head_eyes_closed_r1");
      if (!entity.onGround() || !entity.isInSittingPose()) {
         this.body.y = 24.0F;
      }

      if (entity.onGround() || !entity.isFlying()) {
         leftWing.visible = false;
         rightWing.visible = false;
         wings.visible = true;
         if (entity.isTame() && entity.isInSittingPose()) {
            rightLeg.xRot = -0.5235988F;
            leftLeg.xRot = -0.5235988F;
            rightLeg.y = -4.0F;
            rightLeg.z = 1.5F;
            leftLeg.y = -4.0F;
            leftLeg.z = 1.5F;
            this.body.y = 25.5F;
         } else {
            rightLeg.y = -3.0F;
            rightLeg.z = 0.5F;
            leftLeg.y = -3.0F;
            leftLeg.z = 0.5F;
            rightLeg.xRot = Mth.cos(limbSwing * 2.0F + 3.1415927F) * 2.0F * limbSwingAmount;
            leftLeg.xRot = Mth.cos(limbSwing * 2.0F) * 2.0F * limbSwingAmount;
         }

         head.xRot = (float)Math.toRadians(headPitch);
         if (!entity.isPlayingDead()) {
            head.xRot = head.xRot + Mth.sin(ClientEvents.getClientTicks() / 25.0F) * 0.1F;
            rightTail.xRot = Mth.sin(ClientEvents.getClientTicks() / 25.0F) * 0.1F;
            leftTail.xRot = Mth.sin(ClientEvents.getClientTicks() / 25.0F) * 0.1F;
            tailMid.xRot = Mth.sin(ClientEvents.getClientTicks() / 25.0F) * 0.1F;
         }

         rightTail.yRot = -Mth.sin(0.05F);
         leftTail.yRot = Mth.sin(0.05F);
         rightTail.yRot = rightTail.yRot + Mth.sin(entity.tailWagTiltAngleActual / 100.0F) * 0.2F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.tailWagTiltAngleActual / 100.0F) * 0.2F;
         tailMid.yRot = Mth.sin(entity.tailWagTiltAngleActual / 100.0F) * 0.2F;
         if (entity.tailWag) {
            rightTail.yRot = rightTail.yRot + Mth.sin(0.15F) * 0.5F;
            leftTail.yRot = leftTail.yRot - Mth.sin(0.15F) * 0.5F;
         }

         rightTail.yRot = rightTail.yRot - Mth.sin(entity.tailFanTiltAngleActual / 100.0F) * 0.5F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.tailFanTiltAngleActual / 100.0F) * 0.5F;
      } else if (entity.isPassenger()) {
         leftWing.visible = false;
         rightWing.visible = false;
         wings.visible = true;
         rightLeg.xRot = -0.5235988F;
         leftLeg.xRot = -0.5235988F;
         rightLeg.y = -4.0F;
         rightLeg.z = 1.5F;
         leftLeg.y = -4.0F;
         leftLeg.z = 1.5F;
         this.body.y = 25.5F;
         head.xRot = (float)Math.toRadians(headPitch);
         if (!entity.isPlayingDead()) {
            head.xRot = head.xRot + Mth.sin(ClientEvents.getClientTicks() / 25.0F) * 0.1F;
            rightTail.xRot = Mth.sin(ClientEvents.getClientTicks() / 25.0F) * 0.1F;
            leftTail.xRot = Mth.sin(ClientEvents.getClientTicks() / 25.0F) * 0.1F;
            tailMid.xRot = Mth.sin(ClientEvents.getClientTicks() / 25.0F) * 0.1F;
         }

         rightTail.yRot = -Mth.sin(0.05F);
         leftTail.yRot = Mth.sin(0.05F);
         rightTail.yRot = rightTail.yRot + Mth.sin(entity.tailWagTiltAngleActual / 100.0F) * 0.2F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.tailWagTiltAngleActual / 100.0F) * 0.2F;
         tailMid.yRot = Mth.sin(entity.tailWagTiltAngleActual / 100.0F) * 0.2F;
         if (entity.tailWag) {
            rightTail.yRot = rightTail.yRot + Mth.sin(0.15F) * 0.5F;
            leftTail.yRot = leftTail.yRot - Mth.sin(0.15F) * 0.5F;
         }

         rightTail.yRot = rightTail.yRot - Mth.sin(entity.tailFanTiltAngleActual / 100.0F) * 0.5F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.tailFanTiltAngleActual / 100.0F) * 0.5F;
      } else {
         leftWing.visible = true;
         rightWing.visible = true;
         wings.visible = false;
         rightLeg.xRot = Mth.sin(20.0F);
         leftLeg.xRot = Mth.sin(20.0F);
         rightWing.zRot = entity.rightWingAngleActual;
         leftWing.zRot = entity.leftWingAngleActual;
         head.xRot = (float)Math.toRadians(headPitch);
         rightTail.yRot = -Mth.sin(0.15F);
         leftTail.yRot = Mth.sin(0.15F);
      }

      this.body.xRot = Mth.sin(entity.peckTiltAngleActual / 100.0F);
      head.yRot = (float)Math.toRadians(netHeadYaw);
      head.zRot = Mth.sin(entity.headZTiltAngleActual / 100.0F) / 2.0F;
      head.xRot = head.xRot + Mth.sin(entity.headXTiltAngleActual / 100.0F) / 2.0F;
      if ((entity.onGround() || entity.isPassenger()) && entity.dance) {
         head.zRot = 0.0F;
         head.xRot = (float)Math.toRadians(headPitch) + Mth.sin(entity.animationCounter / 1.5F) / 12.0F;
         head.yRot = (float)Math.toRadians(netHeadYaw) + Mth.sin(entity.animationCounter / 3.0F) / 4.0F;
         rightTail.xRot = Mth.sin(entity.animationCounter / 3.0F) * 0.1F;
         leftTail.xRot = Mth.sin(entity.animationCounter / 3.0F) * 0.1F;
         tailMid.xRot = Mth.sin(entity.animationCounter / 3.0F) * 0.1F;
         this.body.yRot = Mth.sin(entity.animationCounter / 3.0F) * 0.2F;
         rightTail.yRot = Mth.sin(0.1F);
         leftTail.yRot = -Mth.sin(0.1F);
         this.body.y = 24.0F + Mth.abs(Mth.sin(entity.animationCounter / 6.0F));
         rightTail.yRot = rightTail.yRot + Mth.sin(entity.animationCounter / 3.0F) * 0.4F;
         leftTail.yRot = leftTail.yRot + Mth.sin(entity.animationCounter / 3.0F) * 0.4F;
         tailMid.yRot = Mth.sin(entity.animationCounter / 3.0F) * 0.4F;
         rightLeg.y = -3.0F - Mth.abs(Mth.sin(entity.animationCounter / 6.0F));
         rightLeg.z = 0.5F;
         leftLeg.y = -3.0F - Mth.abs(Mth.sin(entity.animationCounter / 6.0F));
         leftLeg.z = 0.5F;
         rightLeg.xRot = Mth.cos(limbSwing * 2.0F + 3.1415927F) * 2.0F * limbSwingAmount;
         leftLeg.xRot = Mth.cos(limbSwing * 2.0F) * 2.0F * limbSwingAmount;
      } else {
         this.body.yRot = 0.0F;
      }

      head_r1.xRot = Mth.sin(entity.cawTiltAngleActual / 100.0F);
      if (entity.peckTiltAngleActual > 0.0F) {
         head_r1.xRot = Mth.sin(entity.peckTiltAngleActual / 100.0F);
      }

      head_eyes_closed.copyFrom(head);
      head_eyes_closed_r1.copyFrom(head_r1);
      tailMid_dyed.copyFrom(tailMid);
      leftTail_dyed.copyFrom(leftTail);
      rightTail_dyed.copyFrom(rightTail);
      if (entity.playingDead > 0 && !entity.isDeadOrDying()) {
         this.body.zRot = HexereiUtil.moveTo(this.body.zRot, 1.3962634F, 0.025F);
         this.body.y = this.body.y - 4.0F * this.body.zRot / 3.1415927F;
         head.visible = false;
         head_eyes_closed.visible = true;
         leftWing.visible = false;
         rightWing.visible = false;
         wings.visible = true;
      } else {
         if (this.body.zRot != 0.0F) {
            this.body.zRot = HexereiUtil.moveTo(this.body.zRot, 0.0F, 0.075F);
            this.body.y = this.body.y - 4.0F * this.body.zRot / 3.1415927F;
         }

         head.visible = true;
         head_eyes_closed.visible = false;
      }

      this.saveAnimationValues(entity);
   }

   private Vector3f getRotationVector(ModelPart pModelPart) {
      return new Vector3f(pModelPart.xRot, pModelPart.yRot, pModelPart.zRot);
   }

   private void setRotationFromVector(ModelPart pModelPart, Vector3f pRotationVector) {
      pModelPart.setRotation(pRotationVector.x(), pRotationVector.y(), pRotationVector.z());
   }

   private void saveAnimationValues(CrowEntity crow) {
      Map<String, Vector3f> map = crow.getModelRotationValues();
      map.put("body", this.getRotationVector(this.body));
   }

   private void setupInitialAnimationValues(CrowEntity crow, float pNetHeadYaw, float pHeadPitch) {
      this.body.x = 0.0F;
      this.body.y = 20.0F;
      Map<String, Vector3f> map = crow.getModelRotationValues();
      if (map.isEmpty()) {
         this.body.setRotation(pHeadPitch * 0.017453292F, pNetHeadYaw * 0.017453292F, 0.0F);
      } else {
         this.setRotationFromVector(this.body, map.get("body"));
      }
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.body.render(poseStack, buffer, packedLight, packedOverlay, color);
   }

   protected Iterable<ModelPart> headParts() {
      return Collections.singleton(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return Collections.singleton(this.body);
   }

   public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
      modelRenderer.xRot = x;
      modelRenderer.yRot = y;
      modelRenderer.zRot = z;
   }
}
