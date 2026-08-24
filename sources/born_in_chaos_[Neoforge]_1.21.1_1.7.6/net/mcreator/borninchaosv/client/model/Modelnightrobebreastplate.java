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

public class Modelnightrobebreastplate<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modelnightrobebreastplate"), "main"
   );
   public final ModelPart Body;
   public final ModelPart RightArm;
   public final ModelPart LeftArm;

   public Modelnightrobebreastplate(ModelPart root) {
      this.Body = root.getChild("Body");
      this.RightArm = root.getChild("RightArm");
      this.LeftArm = root.getChild("LeftArm");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "Body",
         CubeListBuilder.create()
            .texOffs(0, 1)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 18.0F, 4.0F, new CubeDeformation(0.9F))
            .texOffs(26, 26)
            .addBox(-4.0F, 0.0F, -2.2F, 8.0F, 18.0F, 4.0F, new CubeDeformation(1.0F))
            .texOffs(29, 0)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 14.0F, 4.0F, new CubeDeformation(0.8F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition RightArm = partdefinition.addOrReplaceChild(
         "RightArm",
         CubeListBuilder.create()
            .texOffs(112, 1)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.8F))
            .texOffs(90, 28)
            .addBox(-3.1F, -2.1F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(1.0F))
            .texOffs(91, 0)
            .addBox(-3.0F, -1.1F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.6F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition LeftArm = partdefinition.addOrReplaceChild(
         "LeftArm",
         CubeListBuilder.create()
            .texOffs(112, 1)
            .mirror()
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.8F))
            .mirror(false)
            .texOffs(91, 0)
            .mirror()
            .addBox(-1.0F, -1.1F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.6F))
            .mirror(false)
            .texOffs(90, 28)
            .mirror()
            .addBox(-0.9F, -2.1F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(1.0F))
            .mirror(false),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
