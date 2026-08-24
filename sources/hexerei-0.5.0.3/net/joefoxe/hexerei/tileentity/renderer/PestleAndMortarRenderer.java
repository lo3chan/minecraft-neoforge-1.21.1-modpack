package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.tileentity.PestleAndMortarTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class PestleAndMortarRenderer implements BlockEntityRenderer<PestleAndMortarTile> {
   public void render(
      PestleAndMortarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
         && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof PestleAndMortarTile) {
         float craftPercent = (float)(tileEntityIn.grindingTimeMax - tileEntityIn.grindingTime) / tileEntityIn.grindingTimeMax;
         float craftPercent2 = (tileEntityIn.grindingTimeMax - tileEntityIn.grindingTime) / 100.0F;
         float height = 0.375F;

         for (int i = 0; i < 5; i++) {
            ItemStack item = tileEntityIn.getItemStackInSlot(i);
            if (!item.isEmpty()) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(0.5, height + 0.00390625F, 0.5);
               float currentTime = (float)tileEntityIn.getLevel().getGameTime() + partialTicks;
               double itemRotationOffset = 2.512 * i + tileEntityIn.grindingTime / 6.0F - Math.pow(Mth.sin(craftPercent2 * 3.14F * 5.0F - 3.14F), 2.0);
               matrixStackIn.translate(
                  0.0 + Math.sin(itemRotationOffset) / (6.5F + craftPercent2 * craftPercent2 * 10.0F),
                  -0.13333334F * craftPercent,
                  0.0 + Math.cos(itemRotationOffset) / (6.5F + craftPercent2 * craftPercent2 * 10.0F)
               );
               matrixStackIn.mulPose(Axis.YP.rotationDegrees((float)itemRotationOffset * 58.0F - 8.0F));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(55.0F - 40.0F * craftPercent));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-2.5F));
               matrixStackIn.scale(0.4F, 0.4F, 0.4F);
               this.renderItem(item, tileEntityIn.getLevel(), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
               matrixStackIn.popPose();
            }
         }

         ItemStack item2 = tileEntityIn.getItemStackInSlot(5);
         if (!item2.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5, height + 0.00390625F - 0.125F, 0.5);
            float currentTime = (float)tileEntityIn.getLevel().getGameTime() + partialTicks;
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(45.0F - craftPercent * craftPercent * 720.0F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(75.0F));
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-2.5F));
            matrixStackIn.scale(0.4F, 0.4F, 0.4F);
            if (item2.getCount() >= 8) {
               matrixStackIn.translate(0.140625F, -0.125F, -0.078125F);
               this.renderItem(item2, tileEntityIn.getLevel(), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
               matrixStackIn.translate(-0.203125F, 0.0625F, 0.15625F);
               matrixStackIn.translate(-0.140625F, 0.0F, -0.078125F);
               this.renderItem(item2, tileEntityIn.getLevel(), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
               matrixStackIn.translate(0.203125F, 0.125F, 0.078125F);
            } else if (item2.getCount() >= 2) {
               matrixStackIn.translate(0.140625F, 0.0F, -0.078125F);
               this.renderItem(item2, tileEntityIn.getLevel(), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
               matrixStackIn.translate(-0.203125F, 0.125F, 0.078125F);
            }

            this.renderItem(item2, tileEntityIn.getLevel(), partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            matrixStackIn.popPose();
         }

         matrixStackIn.pushPose();
         matrixStackIn.translate(0.5, 0.28125, 0.5);
         int rotationOffset = 0;
         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            rotationOffset = 0;
         }

         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            rotationOffset = 90;
         }

         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            rotationOffset = 180;
         }

         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            rotationOffset = 270;
         }

         double itemRotationOffset = 2.512 + tileEntityIn.grindingTime / 6.0F - Math.pow(Mth.sin(craftPercent2 * 3.14F * 5.0F - 3.14F), 2.0);
         double pestleYOffset = Math.pow(Mth.sin(craftPercent2 * 3.14F * 5.0F - 1.2F), 10.0) / 4.0;
         double pestleTwistOffset = Math.pow(Mth.sin(craftPercent2 * 3.14F * 5.0F - 3.14F), 10.0) / 4.0;
         double pestleTwistOffset2 = Math.pow(Mth.sin(craftPercent2 * 3.14F * 5.0F - 3.14F), 10.0) / 4.0
            - Math.pow(Mth.cos(craftPercent2 * 3.14F * 5.0F - 3.14F), 10.0) / 4.0;
         if (!tileEntityIn.crafting) {
            pestleYOffset = 0.0;
            pestleTwistOffset = 0.0;
         }

         matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotationOffset + (float)itemRotationOffset / 6.28F * 360.0F + 65.0F));
         matrixStackIn.translate(0.05, pestleYOffset, 0.0);
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(-((float)itemRotationOffset) / 6.28F * 360.0F - 90.0F + (float)pestleTwistOffset * 150.0F - 30.0F));
         if (!tileEntityIn.crafting) {
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-20.0F));
         } else {
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-40.0F * (float)pestleTwistOffset2));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(40.0F * (float)pestleTwistOffset));
         }

         this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.PESTLE_AND_MORTAR_PESTLE.get()).defaultBlockState());
         matrixStackIn.popPose();
      }
   }

   private void renderItem(ItemStack stack, Level level, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
   }

   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
      Minecraft.getInstance()
         .getBlockRenderer()
         .renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
   }
}
