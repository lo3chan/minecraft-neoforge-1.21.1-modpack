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

public class Modelsleepbomb<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("undead_revamp2", "modelsleepbomb"), "main"
   );
   public final ModelPart bne2;
   public final ModelPart bone;

   public Modelsleepbomb(ModelPart root) {
      this.bne2 = root.getChild("bne2");
      this.bone = root.getChild("bone");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition bne2 = partdefinition.addOrReplaceChild(
         "bne2",
         CubeListBuilder.create()
            .texOffs(0, 20)
            .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 8)
            .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.25F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition bone = partdefinition.addOrReplaceChild(
         "bone",
         CubeListBuilder.create().texOffs(1, 1).addBox(-0.5F, -3.0F, 0.0F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 18.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.bne2.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
      this.bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }
}
