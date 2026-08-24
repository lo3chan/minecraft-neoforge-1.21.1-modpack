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

public class Modeldarkbib<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modeldarkbib"), "main"
   );
   public final ModelPart Body;
   public final ModelPart RightArm;
   public final ModelPart LeftArm;

   public Modeldarkbib(ModelPart root) {
      this.Body = root.getChild("Body");
      this.RightArm = root.getChild("RightArm");
      this.LeftArm = root.getChild("LeftArm");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "Body",
         CubeListBuilder.create()
            .texOffs(6, 3)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
            .texOffs(0, 22)
            .addBox(-4.5F, -0.1F, -2.5F, 9.0F, 12.0F, 5.0F, new CubeDeformation(1.0F))
            .texOffs(0, 40)
            .addBox(-4.5F, 9.1F, -2.5F, 9.0F, 8.0F, 5.0F, new CubeDeformation(0.8F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition RightArm = partdefinition.addOrReplaceChild(
         "RightArm",
         CubeListBuilder.create()
            .texOffs(90, 0)
            .addBox(-4.3F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .texOffs(112, 24)
            .addBox(-3.3F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.7F))
            .texOffs(84, 4)
            .addBox(-3.9F, -5.6F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-4.5F, 2.0F, 0.0F)
      );
      PartDefinition RightArmArmor_r1 = RightArm.addOrReplaceChild(
         "RightArmArmor_r1",
         CubeListBuilder.create().texOffs(84, 4).addBox(-0.238F, -1.3282F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-6.2007F, -3.4458F, 0.0F, 0.0F, 0.0F, -0.6109F)
      );
      PartDefinition RightArmArmor_r2 = RightArm.addOrReplaceChild(
         "RightArmArmor_r2", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.1F, -3.7F, 0.0F, 0.0F, 0.0F, -1.2217F)
      );
      PartDefinition RightArmArmor_r2_r1 = RightArmArmor_r2.addOrReplaceChild(
         "RightArmArmor_r2_r1",
         CubeListBuilder.create().texOffs(84, 4).addBox(-0.6125F, -1.5672F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.829F, 1.9698F, 0.0F, 0.0F, 0.0F, -0.1396F)
      );
      PartDefinition LeftArm = partdefinition.addOrReplaceChild(
         "LeftArm",
         CubeListBuilder.create()
            .texOffs(90, 0)
            .mirror()
            .addBox(0.3F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .mirror(false)
            .texOffs(112, 24)
            .mirror()
            .addBox(-0.8F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.7F))
            .mirror(false)
            .texOffs(84, 4)
            .mirror()
            .addBox(2.9F, -5.7F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(4.4F, 2.0F, 0.0F)
      );
      PartDefinition RightArmArmor_r3 = LeftArm.addOrReplaceChild(
         "RightArmArmor_r3", CubeListBuilder.create(), PartPose.offsetAndRotation(8.1F, -3.7F, 0.0F, 0.0F, 0.0F, 1.2217F)
      );
      PartDefinition RightArmArmor_r3_r1 = RightArmArmor_r3.addOrReplaceChild(
         "RightArmArmor_r3_r1",
         CubeListBuilder.create().texOffs(84, 4).mirror().addBox(-0.3904F, -1.6565F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(1.829F, 1.9698F, 0.0F, 0.0F, 0.0F, 0.0698F)
      );
      PartDefinition RightArmArmor_r4 = LeftArm.addOrReplaceChild(
         "RightArmArmor_r4",
         CubeListBuilder.create().texOffs(84, 4).mirror().addBox(-0.8768F, -1.492F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
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
