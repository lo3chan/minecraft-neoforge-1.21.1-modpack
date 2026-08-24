package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.SageBurningPlate;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.SageBurningPlateTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class SageBurningPlateRenderer implements BlockEntityRenderer<SageBurningPlateTile> {
   public void render(
      SageBurningPlateTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
         && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof SageBurningPlateTile) {
         if (((ItemStack)tileEntityIn.getItems().get(0)).is((Item)ModItems.DRIED_SAGE_BUNDLE.get())) {
            matrixStackIn.pushPose();
            int rotationOffset = 0;
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               rotationOffset = 0;
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               rotationOffset = 90;
               matrixStackIn.translate(0.0, 0.0, 1.0);
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               rotationOffset = 180;
               matrixStackIn.translate(1.0, 0.0, 1.0);
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               rotationOffset = 270;
               matrixStackIn.translate(1.0, 0.0, 0.0);
            }

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotationOffset));
            float damageOutOf5 = (float)(
                  ((ItemStack)tileEntityIn.getItems().get(0)).getMaxDamage() - ((ItemStack)tileEntityIn.getItems().get(0)).getDamageValue()
               )
               / ((ItemStack)tileEntityIn.getItems().get(0)).getMaxDamage()
               * 5.0F;
            if (damageOutOf5 <= 5.0F && damageOutOf5 > 4.0F) {
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(SageBurningPlate.LIT)
                     ? ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_5_LIT.get()).defaultBlockState()
                     : ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_5.get()).defaultBlockState()
               );
            }

            if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(SageBurningPlate.LIT)
                     ? ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_4_LIT.get()).defaultBlockState()
                     : ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_4.get()).defaultBlockState()
               );
            }

            if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(SageBurningPlate.LIT)
                     ? ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_3_LIT.get()).defaultBlockState()
                     : ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_3.get()).defaultBlockState()
               );
            }

            if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(SageBurningPlate.LIT)
                     ? ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_2_LIT.get()).defaultBlockState()
                     : ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_2.get()).defaultBlockState()
               );
            }

            if (damageOutOf5 <= 1.0F && damageOutOf5 > 0.0F) {
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(SageBurningPlate.LIT)
                     ? ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_1_LIT.get()).defaultBlockState()
                     : ((Block)ModBlocks.DRIED_SAGE_BUNDLE_PLATE_1.get()).defaultBlockState()
               );
            }

            matrixStackIn.popPose();
         }
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
