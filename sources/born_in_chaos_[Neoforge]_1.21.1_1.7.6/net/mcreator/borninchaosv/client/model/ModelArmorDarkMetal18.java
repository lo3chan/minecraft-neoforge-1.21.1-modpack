package net.mcreator.borninchaosv.client.model;

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

public class ModelArmorDarkMetal18<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "model_armor_dark_metal_18"), "main"
   );
   public final ModelPart Body;
   public final ModelPart RightArm;
   public final ModelPart LeftArm;

   public ModelArmorDarkMetal18(ModelPart root) {
      this.Body = root.getChild("Body");
      this.RightArm = root.getChild("RightArm");
      this.LeftArm = root.getChild("LeftArm");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "Body",
         CubeListBuilder.create().texOffs(6, 3).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition Chestplate_r1 = Body.addOrReplaceChild(
         "Chestplate_r1",
         CubeListBuilder.create().texOffs(4, 29).addBox(-4.0F, -7.4F, -0.5F, 8.0F, 11.0F, 0.0F, new CubeDeformation(1.01F)),
         PartPose.offsetAndRotation(0.0F, 16.6F, 4.1F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition RightArm = partdefinition.addOrReplaceChild(
         "RightArm",
         CubeListBuilder.create()
            .texOffs(90, 0)
            .addBox(-5.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .texOffs(84, 4)
            .addBox(-4.4F, -6.1F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition RightArmArmor_r1 = RightArm.addOrReplaceChild(
         "RightArmArmor_r1",
         CubeListBuilder.create().texOffs(84, 4).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-6.2007F, -3.4458F, 0.0F, 0.0F, 0.0F, -0.6109F)
      );
      PartDefinition RightArmArmor_r2 = RightArm.addOrReplaceChild(
         "RightArmArmor_r2",
         CubeListBuilder.create().texOffs(84, 4).addBox(-2.5F, -0.5F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.1F, -3.7F, 0.0F, 0.0F, 0.0F, -1.2217F)
      );
      PartDefinition LeftArm = partdefinition.addOrReplaceChild(
         "LeftArm",
         CubeListBuilder.create()
            .texOffs(90, 0)
            .mirror()
            .addBox(1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .mirror(false)
            .texOffs(84, 4)
            .mirror()
            .addBox(3.4F, -6.1F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition RightArmArmor_r3 = LeftArm.addOrReplaceChild(
         "RightArmArmor_r3",
         CubeListBuilder.create().texOffs(84, 4).mirror().addBox(1.5F, -0.5F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(8.1F, -3.7F, 0.0F, 0.0F, 0.0F, 1.2217F)
      );
      PartDefinition RightArmArmor_r4 = LeftArm.addOrReplaceChild(
         "RightArmArmor_r4",
         CubeListBuilder.create().texOffs(84, 4).mirror().addBox(-0.5F, -2.0F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(6.2007F, -3.4458F, 0.0F, 0.0F, 0.0F, 0.6109F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
