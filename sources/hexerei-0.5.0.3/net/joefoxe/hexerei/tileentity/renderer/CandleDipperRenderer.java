package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.tileentity.CandleDipperTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CandleDipperRenderer implements BlockEntityRenderer<CandleDipperTile> {
   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.getX() - pos.getX();
      double deltaY = entity.getY() - pos.getY();
      double deltaZ = entity.getZ() - pos.getZ();
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public AABB getRenderBoundingBox(CandleDipperTile blockEntity) {
      return super.getRenderBoundingBox(blockEntity).inflate(5.0);
   }

   public void render(
      CandleDipperTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
         && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CandleDipperTile) {
         float rotation = 0.0F;
         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            rotation = 180.0F;
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            rotation = 0.0F;
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            rotation = 90.0F;
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            rotation = 270.0F;
         }

         matrixStackIn.pushPose();
         matrixStackIn.translate(
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.x(), tileEntityIn.dipperSlots.get(0).pos.x()),
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.y(), tileEntityIn.dipperSlots.get(0).pos.y()),
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.z(), tileEntityIn.dipperSlots.get(0).pos.z())
         );
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
         if (!(Block.byItem(((ItemStack)tileEntityIn.getItems().get(0)).getItem()) instanceof Candle)
            || CandleItem.getBaseLayer((ItemStack)tileEntityIn.getItems().get(0)) == null) {
            this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_WICK_BASE.get()).defaultBlockState());
         }

         matrixStackIn.popPose();
         if (!((ItemStack)tileEntityIn.getItems().get(0)).isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.x(), tileEntityIn.dipperSlots.get(0).pos.x()),
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.y(), tileEntityIn.dipperSlots.get(0).pos.y()),
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.z(), tileEntityIn.dipperSlots.get(0).pos.z())
            );
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() == Items.STRING) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_WICK.get()).defaultBlockState());
            } else {
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               matrixStackIn.translate(0.0F, 0.203225F, 0.0F);
               if (!(Block.byItem(((ItemStack)tileEntityIn.getItems().get(0)).getItem()) instanceof Candle)) {
                  matrixStackIn.scale(0.4F, 0.4F, 0.4F);
                  matrixStackIn.translate(0.0F, -0.09375F, 0.0F);
               } else {
                  matrixStackIn.scale(0.9F, 0.9F, 0.9F);
                  matrixStackIn.translate(0.0F, 0.03125F, 0.0F);
               }

               this.renderItem((ItemStack)tileEntityIn.getItems().get(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }

            matrixStackIn.popPose();
         }

         if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() == Items.STRING) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.x(), tileEntityIn.dipperSlots.get(0).pos.x()),
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.y(), tileEntityIn.dipperSlots.get(0).pos.y()),
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(0).posLast.z(), tileEntityIn.dipperSlots.get(0).pos.z())
            );
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if (tileEntityIn.dipperSlots.get(0).timesDipped == 1) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_1.get()).defaultBlockState());
            }

            if (tileEntityIn.dipperSlots.get(0).timesDipped == 2) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_2.get()).defaultBlockState());
            }

            if (tileEntityIn.dipperSlots.get(0).timesDipped == 3) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_3.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
         }

         matrixStackIn.pushPose();
         matrixStackIn.translate(
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.x(), tileEntityIn.dipperSlots.get(1).pos.x()),
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.y(), tileEntityIn.dipperSlots.get(1).pos.y()),
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.z(), tileEntityIn.dipperSlots.get(1).pos.z())
         );
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
         if (!(Block.byItem(((ItemStack)tileEntityIn.getItems().get(1)).getItem()) instanceof Candle)
            || CandleItem.getBaseLayer((ItemStack)tileEntityIn.getItems().get(1)) == null) {
            this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_WICK_BASE.get()).defaultBlockState());
         }

         matrixStackIn.popPose();
         if (!((ItemStack)tileEntityIn.getItems().get(1)).isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.x(), tileEntityIn.dipperSlots.get(1).pos.x()),
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.y(), tileEntityIn.dipperSlots.get(1).pos.y()),
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(1).posLast.z(), tileEntityIn.dipperSlots.get(1).pos.z())
            );
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() == Items.STRING) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_WICK.get()).defaultBlockState());
            } else {
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               matrixStackIn.translate(0.0F, 0.203225F, 0.0F);
               if (!(Block.byItem(((ItemStack)tileEntityIn.getItems().get(1)).getItem()) instanceof Candle)) {
                  matrixStackIn.scale(0.4F, 0.4F, 0.4F);
                  matrixStackIn.translate(0.0F, -0.09375F, 0.0F);
               } else {
                  matrixStackIn.scale(0.9F, 0.9F, 0.9F);
                  matrixStackIn.translate(0.0F, 0.03125F, 0.0F);
               }

               this.renderItem((ItemStack)tileEntityIn.getItems().get(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }

            matrixStackIn.popPose();
         }

         if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() == Items.STRING) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.dipperSlots.get(1).pos.x(), tileEntityIn.dipperSlots.get(1).pos.y(), tileEntityIn.dipperSlots.get(1).pos.z());
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if (tileEntityIn.dipperSlots.get(1).timesDipped == 1) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_1.get()).defaultBlockState());
            }

            if (tileEntityIn.dipperSlots.get(1).timesDipped == 2) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_2.get()).defaultBlockState());
            }

            if (tileEntityIn.dipperSlots.get(1).timesDipped == 3) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_3.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
         }

         matrixStackIn.pushPose();
         matrixStackIn.translate(
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.x(), tileEntityIn.dipperSlots.get(2).pos.x()),
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.y(), tileEntityIn.dipperSlots.get(2).pos.y()),
            Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.z(), tileEntityIn.dipperSlots.get(2).pos.z())
         );
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
         if (!(Block.byItem(((ItemStack)tileEntityIn.getItems().get(2)).getItem()) instanceof Candle)
            || CandleItem.getBaseLayer((ItemStack)tileEntityIn.getItems().get(2)) == null) {
            this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_WICK_BASE.get()).defaultBlockState());
         }

         matrixStackIn.popPose();
         if (!((ItemStack)tileEntityIn.getItems().get(2)).isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.x(), tileEntityIn.dipperSlots.get(2).pos.x()),
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.y(), tileEntityIn.dipperSlots.get(2).pos.y()),
               Mth.lerp(partialTicks, tileEntityIn.dipperSlots.get(2).posLast.z(), tileEntityIn.dipperSlots.get(2).pos.z())
            );
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() == Items.STRING) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_WICK.get()).defaultBlockState());
            } else {
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               matrixStackIn.translate(0.0F, 0.203225F, 0.0F);
               if (!(Block.byItem(((ItemStack)tileEntityIn.getItems().get(2)).getItem()) instanceof Candle)) {
                  matrixStackIn.scale(0.4F, 0.4F, 0.4F);
                  matrixStackIn.translate(0.0F, -0.09375F, 0.0F);
               } else {
                  matrixStackIn.scale(0.9F, 0.9F, 0.9F);
                  matrixStackIn.translate(0.0F, 0.03125F, 0.0F);
               }

               this.renderItem((ItemStack)tileEntityIn.getItems().get(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }

            matrixStackIn.popPose();
         }

         if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() == Items.STRING) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(tileEntityIn.dipperSlots.get(2).pos.x(), tileEntityIn.dipperSlots.get(2).pos.y(), tileEntityIn.dipperSlots.get(2).pos.z());
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
            if (tileEntityIn.dipperSlots.get(2).timesDipped == 1) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_1.get()).defaultBlockState());
            }

            if (tileEntityIn.dipperSlots.get(2).timesDipped == 2) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_2.get()).defaultBlockState());
            }

            if (tileEntityIn.dipperSlots.get(2).timesDipped == 3) {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.CANDLE_DIPPER_CANDLE_3.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
         }
      }
   }

   private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int overlayLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, overlayLightIn, matrixStackIn, bufferIn, level, 1);
   }

   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
      Minecraft.getInstance()
         .getBlockRenderer()
         .renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
   }
}
