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

public class Modelnightrobemasc<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modelnightrobemasc"), "main"
   );
   public final ModelPart Head;

   public Modelnightrobemasc(ModelPart root) {
      this.Head = root.getChild("Head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild(
         "Head",
         CubeListBuilder.create()
            .texOffs(0, 112)
            .addBox(-4.0F, -7.3F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.8F))
            .texOffs(0, 93)
            .addBox(-4.0F, -7.3F, -4.0F, 8.0F, 7.1F, 8.0F, new CubeDeformation(0.6F))
            .texOffs(33, 108)
            .addBox(-4.5F, -8.3F, -4.6F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.6F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = Head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(11, 55)
            .mirror()
            .addBox(-1.5F, 1.6F, 4.3F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(39, 58)
            .mirror()
            .addBox(-1.5F, 0.6F, 5.3F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(33, 65)
            .mirror()
            .addBox(-1.5F, 0.6F, -5.7F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 59)
            .mirror()
            .addBox(-1.5F, -1.4F, -5.7F, 3.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(33, 65)
            .addBox(7.3F, 0.6F, -5.7F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(11, 55)
            .addBox(7.3F, 1.6F, 4.3F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(39, 58)
            .addBox(7.3F, 0.6F, 5.3F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 59)
            .addBox(7.3F, -1.4F, -5.7F, 3.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.4F, -9.0F, 5.4F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition Helmet_r1 = Head.addOrReplaceChild(
         "Helmet_r1",
         CubeListBuilder.create()
            .texOffs(32, 76)
            .addBox(-2.45F, -3.5313F, -6.1322F, 5.0F, 5.0F, 6.0F, new CubeDeformation(-0.2F))
            .texOffs(32, 90)
            .addBox(-2.45F, 1.4687F, -6.1322F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(1, 75)
            .addBox(-2.45F, -3.5313F, -6.1322F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.05F, -1.1061F, -4.1657F, 0.0349F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
