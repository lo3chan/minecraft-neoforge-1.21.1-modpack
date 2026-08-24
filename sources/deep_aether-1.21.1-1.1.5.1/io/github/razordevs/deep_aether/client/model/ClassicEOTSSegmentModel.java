package io.github.razordevs.deep_aether.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.razordevs.deep_aether.entity.living.boss.eots.EOTSSegment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClassicEOTSSegmentModel extends HierarchicalModel<EOTSSegment> {
   private final ModelPart body;
   private final ModelPart head;
   private final ModelPart bb_main;
   private final ModelPart bb_segment;
   private final ModelPart upperMouth;
   private final ModelPart lowerMouth;

   public ClassicEOTSSegmentModel(ModelPart root) {
      this.body = root.getChild("body");
      this.head = root.getChild("head");
      this.bb_main = root.getChild("bb_main");
      this.bb_segment = root.getChild("bb_segment");
      this.upperMouth = this.head.getChild("upperMouth");
      this.lowerMouth = this.head.getChild("lowerMouth");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, 0.0F, 14.0F, 12.0F, 16.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 18.0F, -8.0F)
      );
      partdefinition.addOrReplaceChild(
         "bb_main",
         CubeListBuilder.create()
            .texOffs(26, 41)
            .addBox(-8.0F, -13.0F, 0.0F, 3.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 38)
            .addBox(5.0F, -13.0F, 0.0F, 3.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "bb_segment",
         CubeListBuilder.create()
            .texOffs(28, 28)
            .addBox(-9.0F, -13.0F, -6.0F, 2.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(0, 28)
            .addBox(7.0F, -13.0F, -6.0F, 2.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-3.5F, -9.0F, -10.0F, 11.0F, 13.0F, 13.0F, new CubeDeformation(0.0F))
            .texOffs(42, 30)
            .addBox(2.0F, -13.0F, -9.5F, 0.0F, 4.0F, 11.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 18.0F, 8.0F)
      );
      head.addOrReplaceChild(
         "upperMouth",
         CubeListBuilder.create()
            .texOffs(0, 26)
            .addBox(-5.0F, -3.0F, -9.0F, 10.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(1.0F, -5.0F, -7.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 4)
            .addBox(-3.0F, -5.0F, -7.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 1.0F, -10.0F)
      );
      head.addOrReplaceChild(
         "lowerMouth",
         CubeListBuilder.create().texOffs(29, 29).addBox(-3.0F, 0.0F, -9.0F, 10.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 1.0F, -10.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(EOTSSegment entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      if (entity.isControllingSegment()) {
         this.body.visible = false;
         this.bb_segment.visible = false;
         this.head.visible = true;
         this.bb_main.visible = true;
         this.upperMouth.visible = true;
         if (entity.isMouthOpen()) {
            if (this.upperMouth.xRot > -0.3F) {
               float mouthRotation = Mth.lerp(ageInTicks * 0.01F, this.upperMouth.xRot, -0.3F);
               this.upperMouth.xRot = mouthRotation;
               this.lowerMouth.xRot = -mouthRotation;
            }
         } else if (this.upperMouth.xRot < 0.0F) {
            float mouthRotation = Mth.lerp(ageInTicks * 0.01F, this.upperMouth.xRot, 0.0F);
            this.upperMouth.xRot = mouthRotation;
            this.lowerMouth.xRot = -mouthRotation;
         }
      } else {
         this.body.visible = true;
         this.bb_segment.visible = true;
         this.head.visible = false;
         this.bb_main.visible = false;
         this.upperMouth.visible = false;
      }
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
      this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.bb_segment.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
   }

   public ModelPart root() {
      return this.body;
   }
}
