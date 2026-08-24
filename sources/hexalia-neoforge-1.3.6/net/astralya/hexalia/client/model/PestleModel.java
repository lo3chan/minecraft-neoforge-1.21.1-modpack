package net.astralya.hexalia.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public final class PestleModel {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("hexalia", "pestle"), "main");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("hexalia", "textures/block/pestle.png");
   private static final String PESTLE = "pestle";
   private static final float DEGREES_TO_RADIANS = 0.017453292F;
   private final ModelPart pestle;

   public PestleModel(ModelPart root) {
      this.pestle = root.getChild("pestle");
   }

   public static LayerDefinition createLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild(
         "pestle",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F),
         PartPose.offsetAndRotation(6.0F, 4.69344F, 9.5412F, 0.3926991F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(mesh, 16, 16);
   }

   public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
      this.pestle.render(poseStack, consumer, packedLight, packedOverlay);
   }
}
