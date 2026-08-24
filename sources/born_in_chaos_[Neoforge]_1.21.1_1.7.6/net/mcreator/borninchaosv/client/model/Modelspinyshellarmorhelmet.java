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

public class Modelspinyshellarmorhelmet<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "modelspinyshellarmorhelmet"), "main"
   );
   public final ModelPart Head;

   public Modelspinyshellarmorhelmet(ModelPart root) {
      this.Head = root.getChild("Head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition Helmet_r1 = Head.addOrReplaceChild(
         "Helmet_r1",
         CubeListBuilder.create()
            .texOffs(22, 92)
            .addBox(-6.3F, 4.0F, -3.95F, 5.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 85)
            .addBox(-7.3F, -4.0F, -3.95F, 1.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 116)
            .addBox(1.7F, -4.0F, -3.95F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 104)
            .addBox(-7.3F, -5.0F, -3.95F, 10.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(14, 116)
            .addBox(-7.3F, 4.0F, -4.95F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(21, 126)
            .addBox(-0.3F, 0.0F, -4.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(40, 105)
            .addBox(-0.3F, -5.0F, -4.95F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(30, 118)
            .addBox(-7.3F, -5.0F, -4.95F, 7.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(21, 126)
            .addBox(-0.3F, 0.0F, -4.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(40, 105)
            .addBox(-0.3F, -5.0F, -4.95F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(14, 116)
            .addBox(-7.3F, 4.0F, -4.95F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(30, 118)
            .addBox(-7.3F, -5.0F, -4.95F, 7.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(21, 126)
            .addBox(-0.3F, 0.0F, -4.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(40, 105)
            .addBox(-0.3F, -5.0F, -4.95F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(14, 116)
            .addBox(-7.3F, 4.0F, -4.95F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(30, 118)
            .addBox(-7.3F, -5.0F, -4.95F, 7.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.05F, -4.0F, -2.25F, 0.0F, 1.5708F, 0.0F)
      );
      PartDefinition Helmet_r2 = Head.addOrReplaceChild(
         "Helmet_r2",
         CubeListBuilder.create().texOffs(53, 121).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -0.3F, 6.25F, -1.4312F, 0.0F, -3.1416F)
      );
      PartDefinition Helmet_r3 = Head.addOrReplaceChild(
         "Helmet_r3",
         CubeListBuilder.create().texOffs(53, 121).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -0.3F, 6.25F, -1.5708F, -0.1396F, -1.5708F)
      );
      PartDefinition Helmet_r4 = Head.addOrReplaceChild(
         "Helmet_r4",
         CubeListBuilder.create().texOffs(53, 121).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -4.6F, 6.95F, -1.5708F, 0.0F, -3.1416F)
      );
      PartDefinition Helmet_r5 = Head.addOrReplaceChild(
         "Helmet_r5",
         CubeListBuilder.create().texOffs(53, 121).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -4.6F, 6.95F, -1.5708F, 0.0F, -1.5708F)
      );
      PartDefinition Helmet_r6 = Head.addOrReplaceChild(
         "Helmet_r6",
         CubeListBuilder.create().texOffs(53, 121).addBox(-1.5F, -2.95F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -10.0416F, -3.7325F, 2.8449F, 0.0F, -3.1416F)
      );
      PartDefinition Helmet_r7 = Head.addOrReplaceChild(
         "Helmet_r7",
         CubeListBuilder.create().texOffs(53, 121).addBox(-1.5F, -2.95F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -10.0416F, -3.7325F, 1.5708F, 1.2741F, 1.5708F)
      );
      PartDefinition Helmet_r8 = Head.addOrReplaceChild(
         "Helmet_r8",
         CubeListBuilder.create().texOffs(53, 120).addBox(-1.5F, -3.7F, 0.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -9.5234F, 5.5857F, -2.3562F, 0.0F, -3.1416F)
      );
      PartDefinition Helmet_r9 = Head.addOrReplaceChild(
         "Helmet_r9",
         CubeListBuilder.create().texOffs(53, 120).addBox(-1.5F, -3.7F, 0.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -9.5234F, 5.5857F, -1.5708F, 0.7854F, -1.5708F)
      );
      PartDefinition Helmet_r10 = Head.addOrReplaceChild(
         "Helmet_r10",
         CubeListBuilder.create().texOffs(53, 120).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -11.5951F, 0.9696F, -3.002F, 0.0F, -3.1416F)
      );
      PartDefinition Helmet_r11 = Head.addOrReplaceChild(
         "Helmet_r11",
         CubeListBuilder.create().texOffs(53, 120).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -11.5951F, 0.9696F, -1.5708F, 1.4312F, -1.5708F)
      );
      PartDefinition Helmet_r12 = Head.addOrReplaceChild(
         "Helmet_r12",
         CubeListBuilder.create()
            .texOffs(14, 116)
            .mirror()
            .addBox(1.3F, 4.0F, -5.05F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(40, 105)
            .mirror()
            .addBox(-2.7F, -5.0F, -5.05F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(21, 126)
            .mirror()
            .addBox(-0.7F, 0.0F, -5.05F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(30, 118)
            .mirror()
            .addBox(0.3F, -5.0F, -5.05F, 7.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(-0.05F, -4.0F, -2.25F, 0.0F, -1.5708F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
      this.Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
