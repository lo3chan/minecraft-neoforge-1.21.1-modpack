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

public class Modelbox<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("undead_revamp2", "modelbox"), "main");
   public final ModelPart box;

   public Modelbox(ModelPart root) {
      this.box = root.getChild("box");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition box = partdefinition.addOrReplaceChild(
         "box",
         CubeListBuilder.create().texOffs(21, 40).addBox(-5.0F, -10.0F, -3.0F, 9.0F, 9.0F, 7.0F, new CubeDeformation(0.5F)),
         PartPose.offsetAndRotation(0.0F, 24.0F, 1.0F, 0.0F, -1.6144F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.box.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.box.xRot = headPitch / 57.295776F;
   }
}
