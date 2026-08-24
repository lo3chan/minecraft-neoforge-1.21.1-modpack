package net.joefoxe.hexerei.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class BroomKeychainChainModel extends ListModel<BroomEntity> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(HexereiUtil.getResource("broom_keychain_chain"), "main");
   private final ModelPart KeychainChain;

   public BroomKeychainChainModel(ModelPart root) {
      this.KeychainChain = root.getChild("Keychain");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Keychain = partdefinition.addOrReplaceChild("Keychain", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition chain_r1 = Keychain.addOrReplaceChild(
         "chain_r1",
         CubeListBuilder.create().texOffs(0, 1).addBox(0.0F, 0.25F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.55F, 0.0F, 0.0F, 0.7854F, 0.0F)
      );
      PartDefinition chain_r2 = Keychain.addOrReplaceChild(
         "chain_r2",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(0.0F, 1.0F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 5)
            .addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.55F, 0.0F, 0.0F, -0.7854F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 16, 16);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.KeychainChain.render(poseStack, buffer, packedLight, packedOverlay, color);
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.KeychainChain);
   }

   public void setupAnim(BroomEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
   }
}
