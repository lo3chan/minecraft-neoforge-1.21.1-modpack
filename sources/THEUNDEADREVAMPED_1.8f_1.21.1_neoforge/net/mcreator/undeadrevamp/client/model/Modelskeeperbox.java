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

public class Modelskeeperbox<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("undead_revamp2", "modelskeeperbox"), "main"
   );
   public final ModelPart bag;

   public Modelskeeperbox(ModelPart root) {
      this.bag = root.getChild("bag");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition bag = partdefinition.addOrReplaceChild(
         "bag",
         CubeListBuilder.create().texOffs(0, 37).addBox(-4.6F, -7.0F, 0.6F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 21.0F, -6.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.bag.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.bag.yRot = netHeadYaw / 57.295776F;
      this.bag.xRot = headPitch / 57.295776F;
   }
}
