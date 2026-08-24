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

public class ModelPumpkin_Bl<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "model_pumpkin_bl"), "main"
   );
   public final ModelPart Body;

   public ModelPumpkin_Bl(ModelPart root) {
      this.Body = root.getChild("Body");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "Body", CubeListBuilder.create(), PartPose.offsetAndRotation(2.4218F, 14.7639F, 0.245F, 0.0F, 1.5708F, 0.0F)
      );
      PartDefinition Body_r1 = Body.addOrReplaceChild(
         "Body_r1",
         CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, 3.8F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.25F, -0.4139F, 6.4718F, -2.3562F, 0.0F, 0.0F)
      );
      PartDefinition Body_r2 = Body.addOrReplaceChild(
         "Body_r2",
         CubeListBuilder.create()
            .texOffs(0, 5)
            .addBox(-0.25F, -3.75F, 3.9F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.4F))
            .texOffs(0, 42)
            .addBox(-5.25F, -4.65F, -1.1F, 12.0F, 10.0F, 12.0F, new CubeDeformation(-2.2F)),
         PartPose.offsetAndRotation(-0.5F, -5.3139F, -2.2782F, -1.5708F, 0.0F, 0.0F)
      );
      PartDefinition Body_r3 = Body.addOrReplaceChild(
         "Body_r3",
         CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0F, -6.0F, 12.0F, 10.0F, 12.0F, new CubeDeformation(-2.0F)),
         PartPose.offsetAndRotation(0.25F, -0.4139F, -2.6282F, -1.5708F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
