package io.github.razordevs.deep_aether.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.razordevs.deep_aether.entity.living.AerglowFish;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class AerglowFishModel extends EntityModel<AerglowFish> {
   private final ModelPart body;
   private final ModelPart head;
   private final ModelPart fin_left;
   private final ModelPart fin_right;

   public AerglowFishModel(ModelPart root) {
      this.body = root.getChild("body");
      this.head = root.getChild("head");
      this.fin_left = root.getChild("fin_left");
      this.fin_right = root.getChild("fin_right");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 22.0F, 0.0F)
      );
      body.addOrReplaceChild(
         "right_fin",
         CubeListBuilder.create().texOffs(29, 4).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
      );
      body.addOrReplaceChild(
         "left_fin",
         CubeListBuilder.create().texOffs(24, 4).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.7854F)
      );
      PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 7.0F));
      tail.addOrReplaceChild(
         "tail_sub_0",
         CubeListBuilder.create().texOffs(22, 3).mirror().addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(0.0F, 0.0F, 2.0F)
      );
      body.addOrReplaceChild(
         "fin_back",
         CubeListBuilder.create().texOffs(20, -6).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -2.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(13, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 22.0F, 0.0F)
      );
      head.addOrReplaceChild(
         "nose",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, -3.0F)
      );
      partdefinition.addOrReplaceChild("fin_left", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      partdefinition.addOrReplaceChild("fin_right", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(AerglowFish entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      float f = 1.0F;
      float f1 = 1.0F;
      if (!entity.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      this.body.yRot = -f * 0.25F * Mth.sin(f1 * 0.6F * pAgeInTicks);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.body.render(poseStack, buffer, packedLight, packedOverlay, color);
      this.head.render(poseStack, buffer, packedLight, packedOverlay, color);
      this.fin_left.render(poseStack, buffer, packedLight, packedOverlay, color);
      this.fin_right.render(poseStack, buffer, packedLight, packedOverlay, color);
   }
}
