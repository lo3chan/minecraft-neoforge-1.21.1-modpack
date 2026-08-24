package io.github.razordevs.deep_aether.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

public class VenomiteBubbleModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart bubble;

   public VenomiteBubbleModel(ModelPart root) {
      this.bubble = root.getChild("bubble");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "bubble",
         CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 2.0F, -5.1F, 6.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 19.0F, 2.6F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
      this.bubble.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
   }
}
