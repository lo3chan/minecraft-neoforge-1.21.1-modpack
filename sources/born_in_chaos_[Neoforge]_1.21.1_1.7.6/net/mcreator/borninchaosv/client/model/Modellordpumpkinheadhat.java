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

public class Modellordpumpkinheadhat<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modellordpumpkinheadhat"), "main"
   );
   public final ModelPart Head;

   public Modellordpumpkinheadhat(ModelPart root) {
      this.Head = root.getChild("Head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild(
         "Head",
         CubeListBuilder.create()
            .texOffs(0, 113)
            .addBox(-5.0022F, -9.2023F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(-0.1F))
            .texOffs(0, 93)
            .addBox(-8.6022F, -4.7023F, -8.5F, 17.0F, 1.0F, 17.0F, new CubeDeformation(-0.4F))
            .texOffs(36, 63)
            .addBox(-8.8022F, -5.7023F, -8.5F, 1.0F, 2.0F, 17.0F, new CubeDeformation(-0.4F))
            .texOffs(4, 53)
            .addBox(7.5978F, -7.7023F, -8.5F, 1.0F, 4.0F, 17.0F, new CubeDeformation(-0.4F))
            .texOffs(54, 87)
            .addBox(6.5978F, -7.9023F, -8.5F, 2.0F, 1.0F, 17.0F, new CubeDeformation(-0.4F)),
         PartPose.offset(0.0F, 2.0F, 0.0F)
      );
      PartDefinition cube_r1 = Head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(46, 105).addBox(0.2F, -4.9F, -6.5F, 0.0F, 10.0F, 13.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(5.7899F, -9.7696F, 1.4355F, 0.0874F, 0.0522F, 0.1776F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
