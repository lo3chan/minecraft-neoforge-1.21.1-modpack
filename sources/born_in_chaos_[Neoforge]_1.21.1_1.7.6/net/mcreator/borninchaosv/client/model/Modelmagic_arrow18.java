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

public class Modelmagic_arrow18<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modelmagic_arrow_18"), "main"
   );
   public final ModelPart group;

   public Modelmagic_arrow18(ModelPart root) {
      this.group = root.getChild("group");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition group = partdefinition.addOrReplaceChild(
         "group",
         CubeListBuilder.create().texOffs(14, 7).addBox(5.0F, -11.8F, -8.0F, 5.0F, 18.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-7.5F, 17.0F, 8.0F)
      );
      PartDefinition cube_r1 = group.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(14, 7).addBox(-2.5F, -16.5F, 0.0F, 5.0F, 18.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.5F, 4.7F, -8.0F, 0.0F, 1.5708F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.group.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
