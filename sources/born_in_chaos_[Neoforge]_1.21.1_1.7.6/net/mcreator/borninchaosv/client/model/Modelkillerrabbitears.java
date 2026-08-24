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

public class Modelkillerrabbitears<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modelkillerrabbitears"), "main"
   );
   public final ModelPart Head;

   public Modelkillerrabbitears(ModelPart root) {
      this.Head = root.getChild("Head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild(
         "Head",
         CubeListBuilder.create().texOffs(0, 120).addBox(-4.5F, -8.5F, -1.3F, 9.0F, 6.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = Head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(3, 96).mirror().addBox(-0.9684F, -10.7135F, -1.0F, 3.0F, 11.0F, 2.0F, new CubeDeformation(0.3F)).mirror(false),
         PartPose.offsetAndRotation(1.4F, -7.7F, -0.3F, 0.0F, 0.0F, 0.2094F)
      );
      PartDefinition cube_r2 = Head.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(16, 95).addBox(-2.0316F, -10.7135F, -1.0F, 3.0F, 11.0F, 2.0F, new CubeDeformation(0.3F)),
         PartPose.offsetAndRotation(-1.4F, -7.7F, -0.3F, 0.0F, 0.0F, -0.2094F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
