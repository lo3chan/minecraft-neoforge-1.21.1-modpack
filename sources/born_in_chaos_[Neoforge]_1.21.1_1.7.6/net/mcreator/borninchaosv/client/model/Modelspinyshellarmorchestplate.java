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

public class Modelspinyshellarmorchestplate<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modelspinyshellarmorchestplate"), "main"
   );
   public final ModelPart Body;
   public final ModelPart RightArm;
   public final ModelPart LeftArm;

   public Modelspinyshellarmorchestplate(ModelPart root) {
      this.Body = root.getChild("Body");
      this.RightArm = root.getChild("RightArm");
      this.LeftArm = root.getChild("LeftArm");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "Body",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.1F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.1F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition RightArm = partdefinition.addOrReplaceChild(
         "RightArm",
         CubeListBuilder.create()
            .texOffs(106, 1)
            .addBox(-3.2F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.8F))
            .texOffs(100, 20)
            .addBox(-6.0F, -3.1F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(101, 41)
            .addBox(-3.9F, -7.1F, 0.1F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-4.8F, 2.0F, 0.0F)
      );
      PartDefinition RightArmArmor_r1 = RightArm.addOrReplaceChild(
         "RightArmArmor_r1",
         CubeListBuilder.create().texOffs(101, 54).addBox(-4.1F, -3.5F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-6.5F, -2.1F, 0.1F, 0.0F, 0.0F, -1.5708F)
      );
      PartDefinition RightArmArmor_r2 = RightArm.addOrReplaceChild(
         "RightArmArmor_r2",
         CubeListBuilder.create().texOffs(101, 47).addBox(1.0F, -1.9F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -1.6F, 0.1F, 0.0F, 0.0F, -0.7854F)
      );
      PartDefinition LeftArm = partdefinition.addOrReplaceChild(
         "LeftArm",
         CubeListBuilder.create()
            .texOffs(87, 0)
            .mirror()
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.8F))
            .mirror(false)
            .texOffs(72, 21)
            .mirror()
            .addBox(-1.9F, -3.1F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(71, 36)
            .mirror()
            .addBox(-1.9F, 1.7F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(5.0F, 2.0F, 0.0F)
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
