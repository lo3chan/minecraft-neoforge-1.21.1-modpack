package net.joefoxe.hexerei.client.renderer.entity.model;

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

public class CandleHerbLayer<T extends Entity> extends EntityModel<T> {
   private final ModelPart bb_main;

   public CandleHerbLayer(ModelPart root) {
      this.bb_main = root.getChild("bb_main");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition bb_main = partdefinition.addOrReplaceChild(
         "bb_main",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.1F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 16, 16);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.bb_main.render(poseStack, buffer, packedLight, packedOverlay, color);
   }
}
