package com.aetherteam.aether.client.renderer.blockentity;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.dungeon.ChestMimicBlock;
import com.aetherteam.aether.blockentity.ChestMimicBlockEntity;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Calendar;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.fml.ModList;
import noobanidus.mods.lootr.neoforge.config.ConfigManager;

public class ChestMimicRenderer implements BlockEntityRenderer<ChestMimicBlockEntity> {
   private static final Material LOOTR_MATERIAL = new Material(Sheets.CHEST_SHEET, ResourceLocation.fromNamespaceAndPath("lootr", "chest"));
   private static final Material OLD_LOOTR_MATERIAL = new Material(Sheets.CHEST_SHEET, ResourceLocation.fromNamespaceAndPath("lootr", "old_chest"));
   private final ModelPart lid;
   private final ModelPart bottom;
   private final ModelPart lock;
   private boolean xmasTextures = false;

   public ChestMimicRenderer(Context context) {
      Calendar calendar = Calendar.getInstance();
      if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
         this.xmasTextures = true;
      }

      ModelPart root = context.bakeLayer(AetherModelLayers.CHEST_MIMIC);
      this.bottom = root.getChild("bottom");
      this.lid = root.getChild("lid");
      this.lock = root.getChild("lock");
   }

   public void render(ChestMimicBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      BlockState blockState = blockEntity.getLevel() != null
         ? blockEntity.getBlockState()
         : (BlockState)((Block)AetherBlocks.CHEST_MIMIC.get()).defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
      if (blockState.getBlock() instanceof ChestMimicBlock) {
         poseStack.pushPose();
         float f = ((Direction)blockState.getValue(ChestBlock.FACING)).toYRot();
         poseStack.translate(0.5, 0.5, 0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(-f));
         poseStack.translate(-0.5, -0.5, -0.5);
         Material material = this.getMaterial(blockEntity);
         VertexConsumer vertexconsumer = material.buffer(buffer, RenderType::entityCutout);
         this.render(poseStack, vertexconsumer, this.lid, this.lock, this.bottom, packedLight, packedOverlay);
         poseStack.popPose();
      }
   }

   private void render(
      PoseStack poseStack, VertexConsumer consumer, ModelPart chestLid, ModelPart chestLatch, ModelPart chestBottom, int packedLight, int packedOverlay
   ) {
      chestLid.render(poseStack, consumer, packedLight, packedOverlay);
      chestLatch.render(poseStack, consumer, packedLight, packedOverlay);
      chestBottom.render(poseStack, consumer, packedLight, packedOverlay);
   }

   private Material getMaterial(ChestMimicBlockEntity blockEntity) {
      if (!ModList.get().isLoaded("lootr") || ConfigManager.isVanillaTextures()) {
         return Sheets.chooseMaterial(blockEntity, ChestType.SINGLE, this.xmasTextures);
      } else {
         return ConfigManager.isNewTextures() ? LOOTR_MATERIAL : OLD_LOOTR_MATERIAL;
      }
   }
}
