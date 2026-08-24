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

public class ModelHelmetDarkMetal18<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "model_helmet_dark_metal_18"), "main"
   );
   public final ModelPart Head;

   public ModelHelmetDarkMetal18(ModelPart root) {
      this.Head = root.getChild("Head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild(
         "Head",
         CubeListBuilder.create()
            .texOffs(0, 112)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
            .texOffs(33, 112)
            .addBox(-4.0F, -7.9F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition Helmet_r1 = Head.addOrReplaceChild(
         "Helmet_r1",
         CubeListBuilder.create()
            .texOffs(26, 98)
            .mirror()
            .addBox(0.8F, -3.15F, -0.2F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 94)
            .mirror()
            .addBox(0.8F, -3.15F, -3.2F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(-6.2746F, -8.4704F, -0.0786F, -2.7592F, 0.4708F, 1.9927F)
      );
      PartDefinition Helmet_r2 = Head.addOrReplaceChild(
         "Helmet_r2",
         CubeListBuilder.create()
            .texOffs(26, 98)
            .addBox(-2.8F, -3.15F, -0.2F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 94)
            .addBox(-2.8F, -3.15F, -3.2F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.2746F, -8.4704F, -0.0786F, -2.7592F, -0.4708F, -1.9927F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
