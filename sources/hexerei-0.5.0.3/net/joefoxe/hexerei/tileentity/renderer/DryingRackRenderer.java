package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.WallDryingRack;
import net.joefoxe.hexerei.tileentity.DryingRackTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class DryingRackRenderer implements BlockEntityRenderer<DryingRackTile> {
   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.getX() - pos.getX();
      double deltaY = entity.getY() - pos.getY();
      double deltaZ = entity.getZ() - pos.getZ();
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public void render(
      DryingRackTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.hasLevel()) {
         BlockState state = tileEntityIn.getBlockState();
         if (state.hasBlockEntity()) {
            float rotation = 0.0F;
            if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               rotation = 180.0F;
            } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               rotation = 0.0F;
            } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               rotation = 90.0F;
            } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               rotation = 270.0F;
            }

            if (!((ItemStack)tileEntityIn.getItems().get(0)).isEmpty()) {
               if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() != Items.BROWN_MUSHROOM
                  && ((ItemStack)tileEntityIn.getItems().get(0)).getItem() != Items.RED_MUSHROOM) {
                  matrixStackIn.pushPose();
                  matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                  matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                  matrixStackIn.translate(0.25F, 0.22F, 0.525F);
                  if (state.getBlock() instanceof WallDryingRack) {
                     matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                  }

                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                  matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                  matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                  this.renderItem((ItemStack)tileEntityIn.getItems().get(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                  matrixStackIn.popPose();
                  if (((ItemStack)tileEntityIn.getItems().get(0)).getCount() >= 2) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.25F, 0.22F, 0.525F);
                     matrixStackIn.translate(0.075F, 0.05F, -0.025F);
                     if (state.getBlock() instanceof WallDryingRack) {
                        matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                     }

                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                     matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                     this.renderItem((ItemStack)tileEntityIn.getItems().get(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                     matrixStackIn.popPose();
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(0)).getCount() >= 3) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.25F, 0.22F, 0.525F);
                     matrixStackIn.translate(-0.075F, 0.025F, -0.025F);
                     if (state.getBlock() instanceof WallDryingRack) {
                        matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                     }

                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                     matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                     this.renderItem((ItemStack)tileEntityIn.getItems().get(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                     matrixStackIn.popPose();
                  }
               } else {
                  matrixStackIn.pushPose();
                  matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                  matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                  matrixStackIn.translate(0.25F, 0.22F, 0.525F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                  matrixStackIn.translate(0.0F, 0.09F, 0.0F);
                  if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() == Items.BROWN_MUSHROOM) {
                     this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_1.get()).defaultBlockState());
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() == Items.RED_MUSHROOM) {
                     this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_1.get()).defaultBlockState());
                  }

                  matrixStackIn.popPose();
                  if (((ItemStack)tileEntityIn.getItems().get(0)).getCount() >= 2) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.25F, 0.22F, 0.525F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.translate(0.0F, -0.03F, 0.0F);
                     if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() == Items.BROWN_MUSHROOM) {
                        this.renderBlock(
                           matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_2.get()).defaultBlockState()
                        );
                     }

                     if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() == Items.RED_MUSHROOM) {
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_2.get()).defaultBlockState());
                     }

                     matrixStackIn.popPose();
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(0)).getCount() >= 3) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.25F, 0.22F, 0.525F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.translate(0.0F, -0.15F, 0.0F);
                     if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() == Items.BROWN_MUSHROOM) {
                        this.renderBlock(
                           matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_1.get()).defaultBlockState()
                        );
                     }

                     if (((ItemStack)tileEntityIn.getItems().get(0)).getItem() == Items.RED_MUSHROOM) {
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_1.get()).defaultBlockState());
                     }

                     matrixStackIn.popPose();
                  }
               }
            }

            if (!((ItemStack)tileEntityIn.getItems().get(1)).isEmpty()) {
               if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() != Items.BROWN_MUSHROOM
                  && ((ItemStack)tileEntityIn.getItems().get(1)).getItem() != Items.RED_MUSHROOM) {
                  matrixStackIn.pushPose();
                  matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                  matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                  matrixStackIn.translate(0.5F, 0.22F, 0.525F);
                  if (state.getBlock() instanceof WallDryingRack) {
                     matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                  }

                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                  matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                  matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                  this.renderItem((ItemStack)tileEntityIn.getItems().get(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                  matrixStackIn.popPose();
                  if (((ItemStack)tileEntityIn.getItems().get(1)).getCount() >= 2) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.5F, 0.22F, 0.525F);
                     matrixStackIn.translate(0.075F, 0.05F, -0.025F);
                     if (state.getBlock() instanceof WallDryingRack) {
                        matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                     }

                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                     matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                     this.renderItem((ItemStack)tileEntityIn.getItems().get(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                     matrixStackIn.popPose();
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(1)).getCount() >= 3) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.5F, 0.22F, 0.525F);
                     matrixStackIn.translate(-0.075F, 0.025F, -0.025F);
                     if (state.getBlock() instanceof WallDryingRack) {
                        matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                     }

                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                     matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                     this.renderItem((ItemStack)tileEntityIn.getItems().get(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                     matrixStackIn.popPose();
                  }
               } else {
                  matrixStackIn.pushPose();
                  matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                  matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                  matrixStackIn.translate(0.5F, 0.22F, 0.525F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                  matrixStackIn.translate(0.0F, 0.09F, 0.0F);
                  if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() == Items.BROWN_MUSHROOM) {
                     this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_1.get()).defaultBlockState());
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() == Items.RED_MUSHROOM) {
                     this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_1.get()).defaultBlockState());
                  }

                  matrixStackIn.popPose();
                  if (((ItemStack)tileEntityIn.getItems().get(1)).getCount() >= 2) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.5F, 0.22F, 0.525F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.translate(0.0F, -0.03F, 0.0F);
                     if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() == Items.BROWN_MUSHROOM) {
                        this.renderBlock(
                           matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_2.get()).defaultBlockState()
                        );
                     }

                     if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() == Items.RED_MUSHROOM) {
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_2.get()).defaultBlockState());
                     }

                     matrixStackIn.popPose();
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(1)).getCount() >= 3) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.5F, 0.22F, 0.525F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.translate(0.0F, -0.15F, 0.0F);
                     if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() == Items.BROWN_MUSHROOM) {
                        this.renderBlock(
                           matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_1.get()).defaultBlockState()
                        );
                     }

                     if (((ItemStack)tileEntityIn.getItems().get(1)).getItem() == Items.RED_MUSHROOM) {
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_1.get()).defaultBlockState());
                     }

                     matrixStackIn.popPose();
                  }
               }
            }

            if (!((ItemStack)tileEntityIn.getItems().get(2)).isEmpty()) {
               if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() != Items.BROWN_MUSHROOM
                  && ((ItemStack)tileEntityIn.getItems().get(2)).getItem() != Items.RED_MUSHROOM) {
                  matrixStackIn.pushPose();
                  matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                  matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                  matrixStackIn.translate(0.75F, 0.22F, 0.525F);
                  if (state.getBlock() instanceof WallDryingRack) {
                     matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                  }

                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                  matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                  matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                  this.renderItem((ItemStack)tileEntityIn.getItems().get(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                  matrixStackIn.popPose();
                  if (((ItemStack)tileEntityIn.getItems().get(2)).getCount() >= 2) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.75F, 0.22F, 0.525F);
                     matrixStackIn.translate(0.075F, 0.05F, -0.025F);
                     if (state.getBlock() instanceof WallDryingRack) {
                        matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                     }

                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                     matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                     this.renderItem((ItemStack)tileEntityIn.getItems().get(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                     matrixStackIn.popPose();
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(2)).getCount() >= 3) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.75F, 0.22F, 0.525F);
                     matrixStackIn.translate(-0.075F, 0.025F, -0.025F);
                     if (state.getBlock() instanceof WallDryingRack) {
                        matrixStackIn.translate(0.0F, 0.1F, 0.275F);
                     }

                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
                     matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                     this.renderItem((ItemStack)tileEntityIn.getItems().get(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                     matrixStackIn.popPose();
                  }
               } else {
                  matrixStackIn.pushPose();
                  matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                  matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                  matrixStackIn.translate(0.75F, 0.22F, 0.525F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                  matrixStackIn.translate(0.0F, 0.09F, 0.0F);
                  if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() == Items.BROWN_MUSHROOM) {
                     this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_1.get()).defaultBlockState());
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() == Items.RED_MUSHROOM) {
                     this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_1.get()).defaultBlockState());
                  }

                  matrixStackIn.popPose();
                  if (((ItemStack)tileEntityIn.getItems().get(2)).getCount() >= 2) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.75F, 0.22F, 0.525F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.translate(0.0F, -0.03F, 0.0F);
                     if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() == Items.BROWN_MUSHROOM) {
                        this.renderBlock(
                           matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_2.get()).defaultBlockState()
                        );
                     }

                     if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() == Items.RED_MUSHROOM) {
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_2.get()).defaultBlockState());
                     }

                     matrixStackIn.popPose();
                  }

                  if (((ItemStack)tileEntityIn.getItems().get(2)).getCount() >= 3) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate(0.5F, 0.0F, 0.5F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(rotation));
                     matrixStackIn.translate(-0.5F, 0.0F, -0.5F);
                     matrixStackIn.translate(0.75F, 0.22F, 0.525F);
                     matrixStackIn.mulPose(Axis.YP.rotationDegrees(15.0F));
                     matrixStackIn.translate(0.0F, -0.15F, 0.0F);
                     if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() == Items.BROWN_MUSHROOM) {
                        this.renderBlock(
                           matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_BROWN_MUSHROOM_1.get()).defaultBlockState()
                        );
                     }

                     if (((ItemStack)tileEntityIn.getItems().get(2)).getItem() == Items.RED_MUSHROOM) {
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.HERB_DRYING_RACK_RED_MUSHROOM_1.get()).defaultBlockState());
                     }

                     matrixStackIn.popPose();
                  }
               }
            }
         }
      }
   }

   private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
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
