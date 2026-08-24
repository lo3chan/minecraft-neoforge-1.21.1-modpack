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

public class Modelcloggercrown<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("undead_revamp2", "modelcloggercrown"), "main"
   );
   public final ModelPart helmet;
   public final ModelPart chestar;
   public final ModelPart rightarmar;
   public final ModelPart leftarmar;
   public final ModelPart rightlegma;
   public final ModelPart leftlegma;
   public final ModelPart rightlegma2;
   public final ModelPart leftlegma2;

   public Modelcloggercrown(ModelPart root) {
      this.helmet = root.getChild("helmet");
      this.chestar = root.getChild("chestar");
      this.rightarmar = root.getChild("rightarmar");
      this.leftarmar = root.getChild("leftarmar");
      this.rightlegma = root.getChild("rightlegma");
      this.leftlegma = root.getChild("leftlegma");
      this.rightlegma2 = root.getChild("rightlegma2");
      this.leftlegma2 = root.getChild("leftlegma2");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition helmet = partdefinition.addOrReplaceChild(
         "helmet",
         CubeListBuilder.create().texOffs(56, 107).addBox(-4.0F, -21.0F, -2.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.0873F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = helmet.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(0, 30)
            .addBox(-8.0F, -7.0F, -3.8F, 10.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(0, 47)
            .addBox(-9.0F, -2.0F, -5.0F, 12.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
            .texOffs(2, 61)
            .addBox(-13.0F, 0.0F, -8.8F, 20.0F, 0.0F, 18.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(3.0F, -6.0F, 0.0F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = helmet.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(1, 86).addBox(-3.0F, -3.0F, -1.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.0F, -5.0F, -4.0F, 0.0873F, 0.0F, 0.0F)
      );
      PartDefinition cube_r3 = helmet.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(0, 13).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0873F, 0.0F, 0.0F)
      );
      PartDefinition chestar = partdefinition.addOrReplaceChild(
         "chestar",
         CubeListBuilder.create().texOffs(66, 28).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition rightarmar = partdefinition.addOrReplaceChild(
         "rightarmar",
         CubeListBuilder.create().texOffs(50, 7).addBox(-1.0F, -2.5F, -3.0F, 5.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition leftarmar = partdefinition.addOrReplaceChild(
         "leftarmar",
         CubeListBuilder.create().texOffs(76, 7).addBox(-4.0F, -2.5F, -3.0F, 5.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition rightlegma = partdefinition.addOrReplaceChild(
         "rightlegma",
         CubeListBuilder.create().texOffs(1, 94).addBox(-2.0F, 1.0F, -3.0F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition leftlegma = partdefinition.addOrReplaceChild("leftlegma", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
      PartDefinition cube_r4 = leftlegma.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(1, 94).addBox(-2.0F, -1.0F, -3.0F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.2F, 2.0F, 0.0F, 0.0F, 3.098F, 0.0F)
      );
      PartDefinition rightlegma2 = partdefinition.addOrReplaceChild(
         "rightlegma2",
         CubeListBuilder.create().texOffs(4, 110).addBox(-1.9F, 9.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition leftlegma2 = partdefinition.addOrReplaceChild(
         "leftlegma2",
         CubeListBuilder.create().texOffs(4, 110).addBox(-3.1F, 9.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.helmet.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.chestar.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.rightarmar.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.leftarmar.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.rightlegma.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.leftlegma.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.rightlegma2.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.leftlegma2.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
