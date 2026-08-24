package net.mcreator.undeadrevamp.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class Modelpromodialarmor<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("undead_revamp2", "modelpromodialarmor"), "main"
   );
   public final ModelPart helmet;
   public final ModelPart bone;
   public final ModelPart rightshoulder;
   public final ModelPart leftshoulder;
   public final ModelPart bodyarmor;
   public final ModelPart rightshoe;
   public final ModelPart leftshoe;

   public Modelpromodialarmor(ModelPart root) {
      this.helmet = root.getChild("helmet");
      this.bone = this.helmet.getChild("bone");
      this.rightshoulder = root.getChild("rightshoulder");
      this.leftshoulder = root.getChild("leftshoulder");
      this.bodyarmor = root.getChild("bodyarmor");
      this.rightshoe = root.getChild("rightshoe");
      this.leftshoe = root.getChild("leftshoe");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition helmet = partdefinition.addOrReplaceChild("helmet", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition bone = helmet.addOrReplaceChild(
         "bone",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-13.0F, -2.0F, -10.0F, 18.0F, 3.0F, 19.0F, new CubeDeformation(0.0F))
            .texOffs(20, 3)
            .addBox(5.5F, -3.0F, -11.0F, 0.0F, 3.0F, 21.0F, new CubeDeformation(0.0F))
            .texOffs(20, 3)
            .addBox(-13.5F, -3.0F, -11.0F, 0.0F, 3.0F, 21.0F, new CubeDeformation(0.0F))
            .texOffs(74, 0)
            .addBox(-8.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.0F, -3.0F, 0.0F, 0.1745F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = bone.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(36, 22).addBox(1.0F, -18.0F, -9.0F, 0.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.0F, -9.0F, 1.0F, -2.682F, -0.36F, -2.7896F)
      );
      PartDefinition cube_r2 = bone.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(36, 22).addBox(1.0F, -18.0F, -9.0F, 0.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.0F, -9.0F, 1.0F, -2.5762F, 0.1152F, 2.6959F)
      );
      PartDefinition cube_r3 = bone.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(36, 22).addBox(1.0F, -18.0F, -9.0F, 0.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.0F, -9.0F, 1.0F, -1.5708F, 1.3526F, -1.5708F)
      );
      PartDefinition cube_r4 = bone.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(36, 22).addBox(1.0F, -18.0F, -9.0F, 0.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.0F, -9.0F, 3.0F, -1.5708F, 0.48F, -1.5708F)
      );
      PartDefinition cube_r5 = bone.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(44, 113).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, -9.0F, 0.0F, -0.5236F, 0.0F, 0.0F)
      );
      PartDefinition cube_r6 = bone.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create().texOffs(22, 107).addBox(-3.0F, -2.0F, -2.0F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, -7.0F, 0.0F, -0.1745F, 0.0F, 0.0F)
      );
      PartDefinition cube_r7 = bone.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create()
            .texOffs(20, 10)
            .addBox(10.5F, -5.0F, -10.0F, 0.0F, 3.0F, 20.0F, new CubeDeformation(0.0F))
            .texOffs(20, 10)
            .addBox(10.5F, -5.0F, -10.0F, 0.0F, 3.0F, 20.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, 2.0F, 0.0F, 0.0F, 1.5708F, 0.0F)
      );
      PartDefinition cube_r8 = bone.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create().texOffs(20, 10).addBox(10.5F, -5.0F, -10.0F, 0.0F, 3.0F, 20.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, 2.0F, 20.0F, 0.0F, 1.5708F, 0.0F)
      );
      PartDefinition cube_r9 = bone.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create().texOffs(106, 32).addBox(-4.0F, -13.0F, -3.0F, 8.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, 3.0F, 9.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r10 = bone.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create().texOffs(88, 58).addBox(-4.0F, -13.0F, -1.0F, 8.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, 3.0F, -7.0F, -0.3491F, 0.0F, 0.0F)
      );
      PartDefinition cube_r11 = bone.addOrReplaceChild(
         "cube_r11",
         CubeListBuilder.create().texOffs(0, 90).addBox(-1.0F, -7.0F, -5.0F, 2.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F)
      );
      PartDefinition cube_r12 = bone.addOrReplaceChild(
         "cube_r12",
         CubeListBuilder.create().texOffs(0, 90).addBox(-1.0F, -7.0F, -5.0F, 2.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F)
      );
      PartDefinition rightshoulder = partdefinition.addOrReplaceChild(
         "rightshoulder",
         CubeListBuilder.create().texOffs(72, 22).addBox(-8.0F, -2.8F, -3.5F, 10.0F, 11.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition cube_r13 = rightshoulder.addOrReplaceChild(
         "cube_r13",
         CubeListBuilder.create().texOffs(56, 82).addBox(0.0F, 0.0F, -2.5F, 10.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 3.1416F, 0.0F, 3.0543F)
      );
      PartDefinition leftshoulder = partdefinition.addOrReplaceChild(
         "leftshoulder",
         CubeListBuilder.create().texOffs(72, 40).addBox(-4.0F, -3.8F, -3.5F, 10.0F, 11.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offset(7.0F, 3.0F, 0.0F)
      );
      PartDefinition cube_r14 = leftshoulder.addOrReplaceChild(
         "cube_r14",
         CubeListBuilder.create().texOffs(56, 82).addBox(0.0F, 0.0F, -2.5F, 10.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.0873F)
      );
      PartDefinition bodyarmor = partdefinition.addOrReplaceChild(
         "bodyarmor",
         CubeListBuilder.create()
            .texOffs(56, 58)
            .addBox(-5.0F, -0.5F, -3.0F, 10.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(88, 74)
            .addBox(-5.0F, 9.0F, -2.5F, 10.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition rightshoe = partdefinition.addOrReplaceChild(
         "rightshoe",
         CubeListBuilder.create().texOffs(106, 43).addBox(-3.0F, 9.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition leftshoe = partdefinition.addOrReplaceChild(
         "leftshoe",
         CubeListBuilder.create().texOffs(0, 107).addBox(-2.0F, 9.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.helmet.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.rightshoulder.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.leftshoulder.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.bodyarmor.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.rightshoe.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.leftshoe.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }
}
