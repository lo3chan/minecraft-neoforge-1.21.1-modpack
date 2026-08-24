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

public class Modeldamneddemomanshat<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modeldamneddemomanshat"), "main"
   );
   public final ModelPart Head;

   public Modeldamneddemomanshat(ModelPart root) {
      this.Head = root.getChild("Head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild(
         "Head",
         CubeListBuilder.create()
            .texOffs(10, 90)
            .addBox(-5.0F, -8.9F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(-0.1F))
            .texOffs(0, 109)
            .addBox(-8.5F, -6.0F, -7.5F, 17.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 2.0F, 0.0F)
      );
      PartDefinition Helmet_r1 = Head.addOrReplaceChild(
         "Helmet_r1",
         CubeListBuilder.create().texOffs(6, 83).addBox(-0.6F, -1.0F, -0.6F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(6.2022F, -8.166F, -1.7691F, -0.5925F, 0.0226F, 0.4334F)
      );
      PartDefinition Helmet_r2 = Head.addOrReplaceChild(
         "Helmet_r2",
         CubeListBuilder.create().texOffs(7, 88).addBox(-0.9F, -2.4F, 0.1F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(5.5F, -6.0F, -3.0F, -0.2085F, 0.0226F, 0.4334F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
