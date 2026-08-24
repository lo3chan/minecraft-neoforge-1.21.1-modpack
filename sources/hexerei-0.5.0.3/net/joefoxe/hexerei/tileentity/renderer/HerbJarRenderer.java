package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Random;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.block.custom.PickablePlant;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.FlowerOutputItem;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HerbJarRenderer implements BlockEntityRenderer<HerbJarTile> {
   private final Font font;
   private final Minecraft minecraft = Minecraft.getInstance();
   private final ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
   private final ItemModelShaper shaper = this.itemRenderer.getItemModelShaper();

   public HerbJarRenderer() {
      this.font = Minecraft.getInstance().font;
   }

   public AABB getRenderBoundingBox(HerbJarTile blockEntity) {
      return super.getRenderBoundingBox(blockEntity).inflate(5.0);
   }

   public void render(
      HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
         && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof HerbJarTile) {
         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
            && tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasProperty(HorizontalDirectionalBlock.FACING)) {
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               this.renderItemsNorth(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               this.renderItemsWest(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               this.renderItemsSouth(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               this.renderItemsEast(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            }

            matrixStackIn.pushPose();
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               matrixStackIn.translate(1.0, 0.0, 1.0);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(0.0F));
            } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               matrixStackIn.translate(0.0, 0.0, 1.0);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               matrixStackIn.translate(1.0, 0.0, 0.0);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
            }

            this.renderBlock(
               matrixStackIn,
               bufferIn,
               combinedLightIn,
               combinedOverlayIn,
               (BlockState)((BlockState)((HerbJar)ModBlocks.HERB_JAR.get()).defaultBlockState().setValue(HerbJar.GUI_RENDER, true))
                  .setValue(HerbJar.DYED, tileEntityIn.hasDyeColor()),
               null,
               tileEntityIn.getDyeColor()
            );
            matrixStackIn.popPose();
            if (!tileEntityIn.itemHandler.isEmpty()) {
               BlockState state = null;
               Item item = ((ItemStack)tileEntityIn.itemHandler.getContents().get(0)).getItem();
               if (item == ((PickablePlant)ModBlocks.BELLADONNA_PLANT.get()).asItem() || item == ((FlowerOutputItem)ModItems.BELLADONNA_FLOWERS.get()).asItem()
                  )
                {
                  state = ((Block)ModBlocks.HERB_JAR_BELLADONNA.get()).defaultBlockState();
               }

               if (item == ((PickableDoublePlant)ModBlocks.MUGWORT_BUSH.get()).asItem()
                  || item == ((FlowerOutputItem)ModItems.MUGWORT_LEAVES.get()).asItem()
                  || item == ((FlowerOutputItem)ModItems.MUGWORT_FLOWERS.get()).asItem()) {
                  state = ((Block)ModBlocks.HERB_JAR_MUGWORT.get()).defaultBlockState();
               }

               if (item == ((PickablePlant)ModBlocks.MANDRAKE_PLANT.get()).asItem() || item == ((FlowerOutputItem)ModItems.MANDRAKE_FLOWERS.get()).asItem()) {
                  state = ((Block)ModBlocks.HERB_JAR_MANDRAKE_PLANT.get()).defaultBlockState();
               }

               if (item == ModItems.MANDRAKE_ROOT.get()) {
                  state = ((Block)ModBlocks.HERB_JAR_MANDRAKE_ROOT.get()).defaultBlockState();
               }

               if (item == ((PickableDoublePlant)ModBlocks.YELLOW_DOCK_BUSH.get()).asItem()
                  || item == ((FlowerOutputItem)ModItems.YELLOW_DOCK_LEAVES.get()).asItem()
                  || item == ((FlowerOutputItem)ModItems.YELLOW_DOCK_FLOWERS.get()).asItem()) {
                  state = ((Block)ModBlocks.HERB_JAR_YELLOW_DOCK.get()).defaultBlockState();
               }

               long r = tileEntityIn.getBlockPos().asLong();
               Random rand = new Random(r);
               BakedModel itemModel = this.itemRenderer.getModel(new ItemStack(item), null, null, 0);
               boolean is3dModel = itemModel.isGui3d();

               for (int a = 0; a < ((ItemStack)tileEntityIn.itemHandler.getContents().getFirst()).getCount() / 1024.0F * 20.0F; a++) {
                  matrixStackIn.pushPose();
                  if (is3dModel) {
                     matrixStackIn.translate(0.0, 0.09375, 0.0);
                  }

                  if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     if (state != null) {
                        matrixStackIn.translate(0.5, 0.03125 * a, 0.5);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90 * a));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                     } else {
                        matrixStackIn.translate(0.5, 0.03125 * a + 0.0625, 0.5);
                        matrixStackIn.scale(0.4F, 0.4F, 0.4F);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rand.nextInt(90) * a));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(80 + rand.nextInt(20)));
                        if (is3dModel) {
                           matrixStackIn.scale(1.2F, 1.2F, 1.2F);
                        }

                        this.renderItem(
                           new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1),
                           tileEntityIn.getLevel(),
                           matrixStackIn,
                           bufferIn,
                           combinedLightIn
                        );
                     }
                  } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     if (state != null) {
                        matrixStackIn.translate(0.5, 0.03125 * a, 0.5);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90 * a));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(0.0F));
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                     } else {
                        matrixStackIn.translate(0.5, 0.03125 * a + 0.0625, 0.5);
                        matrixStackIn.scale(0.4F, 0.4F, 0.4F);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rand.nextInt(90) * a));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(0.0F));
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(80 + rand.nextInt(20)));
                        if (is3dModel) {
                           matrixStackIn.scale(1.2F, 1.2F, 1.2F);
                        }

                        this.renderItem(
                           new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1),
                           tileEntityIn.getLevel(),
                           matrixStackIn,
                           bufferIn,
                           combinedLightIn
                        );
                     }
                  } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     if (state != null) {
                        matrixStackIn.translate(0.5, 0.03125 * a, 0.5);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90 * a));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                     } else {
                        matrixStackIn.translate(0.5, 0.03125 * a + 0.0625, 0.5);
                        matrixStackIn.scale(0.4F, 0.4F, 0.4F);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rand.nextInt(90) * a));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(80 + rand.nextInt(20)));
                        if (is3dModel) {
                           matrixStackIn.scale(1.2F, 1.2F, 1.2F);
                        }

                        this.renderItem(
                           new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1),
                           tileEntityIn.getLevel(),
                           matrixStackIn,
                           bufferIn,
                           combinedLightIn
                        );
                     }
                  } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     if (state != null) {
                        matrixStackIn.translate(0.5, 0.03125 * a, 0.5);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90 * a));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
                        this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, state);
                     } else {
                        matrixStackIn.translate(0.5, 0.03125 * a + 0.0625, 0.5);
                        matrixStackIn.scale(0.4F, 0.4F, 0.4F);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(rand.nextInt(90) * a));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(80 + rand.nextInt(20)));
                        if (is3dModel) {
                           matrixStackIn.scale(1.2F, 1.2F, 1.2F);
                        }

                        this.renderItem(
                           new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1),
                           tileEntityIn.getLevel(),
                           matrixStackIn,
                           bufferIn,
                           combinedLightIn
                        );
                     }
                  }

                  matrixStackIn.popPose();
               }
            }

            matrixStackIn.pushPose();
            int i = 4607830;
            int j1 = (int)(ARGB32.red(i) * 0.4);
            int k1 = (int)(ARGB32.green(i) * 0.4);
            int l1 = (int)(ARGB32.blue(i) * 0.4);
            int i1 = ARGB32.color(0, j1, k1, l1);
            int j2 = (int)(ARGB32.red(i) * 0.2);
            int k2 = (int)(ARGB32.green(i) * 0.2);
            int l2 = (int)(ARGB32.blue(i) * 0.2);
            int i2 = ARGB32.color(0, j2, k2, l2);
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               matrixStackIn.translate(0.5, 0.5, 0.753125);
            } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               matrixStackIn.translate(0.5, 0.5, 0.24687499999999996);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               matrixStackIn.translate(0.24687499999999996, 0.5, 0.5);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
            } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               matrixStackIn.translate(0.753125, 0.5, 0.5);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            }

            matrixStackIn.scale(0.00694445F, -0.00694445F, 0.00694445F);
            Component component = tileEntityIn.customName;
            if (component == null && !tileEntityIn.getItemStackInSlot(0).isEmpty()) {
               if (tileEntityIn.getItemStackInSlot(0).getHoverName().getString().equals("")) {
                  component = tileEntityIn.getItemStackInSlot(0).getItem().getName(tileEntityIn.getItemStackInSlot(0));
               } else {
                  component = tileEntityIn.getItemStackInSlot(0).getHoverName();
               }
            }

            if (component != null) {
               List<FormattedCharSequence> list = Minecraft.getInstance().font.split(component, 70);
               float f3 = -Minecraft.getInstance().font.width((FormattedCharSequence)list.getFirst()) / 2;
               matrixStackIn.translate(0.0F, 0.0F, 1.0F);
               if (tileEntityIn.hasDyeColor()) {
                  matrixStackIn.translate(0.0F, 5.0F, 0.0F);
               }

               Minecraft.getInstance()
                  .font
                  .drawInBatch(
                     (FormattedCharSequence)list.getFirst(), f3, 0.0F, i1, false, matrixStackIn.last().pose(), bufferIn, DisplayMode.NORMAL, 0, combinedLightIn
                  );

               for (int y = 0; y < 9; y++) {
                  if (y % 2 != 0) {
                     matrixStackIn.pushPose();
                     matrixStackIn.translate((y % 3 - 1) * 0.25F, (y / 3 - 1) * 0.25F, -0.05F);
                     Minecraft.getInstance()
                        .font
                        .drawInBatch(
                           (FormattedCharSequence)list.getFirst(),
                           f3,
                           0.0F,
                           i2,
                           false,
                           matrixStackIn.last().pose(),
                           bufferIn,
                           DisplayMode.NORMAL,
                           0,
                           combinedLightIn
                        );
                     matrixStackIn.popPose();
                  }
               }

               if (list.size() > 1) {
                  matrixStackIn.translate(0.0F, 10.0F, 0.0F);
                  f3 = -Minecraft.getInstance().font.width(list.get(1)) / 2;
                  Minecraft.getInstance()
                     .font
                     .drawInBatch(list.get(1), f3, 0.0F, i1, false, matrixStackIn.last().pose(), bufferIn, DisplayMode.NORMAL, 0, combinedLightIn);

                  for (int yx = 0; yx < 9; yx++) {
                     if (yx % 2 != 0) {
                        matrixStackIn.pushPose();
                        matrixStackIn.translate((yx % 3 - 1) * 0.25F, (yx / 3 - 1) * 0.25F, -0.05F);
                        Minecraft.getInstance()
                           .font
                           .drawInBatch(list.get(1), f3, 0.0F, i2, false, matrixStackIn.last().pose(), bufferIn, DisplayMode.NORMAL, 0, combinedLightIn);
                        matrixStackIn.popPose();
                     }
                  }
               }
            }

            matrixStackIn.popPose();
         }
      }
   }

   private void renderItemsNorth(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.5, 0.265625, 0.75);
      matrixStackIn.scale(0.3F, 0.3F, 0.3F);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      this.renderItem(
         new ItemStack(((ItemStack)tileEntityIn.itemHandler.getContents().get(0)).getItem(), 1),
         tileEntityIn.getLevel(),
         matrixStackIn,
         bufferIn,
         combinedLightIn
      );
      matrixStackIn.popPose();
   }

   private void renderItemsSouth(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.5, 0.265625, 0.25);
      matrixStackIn.scale(0.3F, 0.3F, 0.3F);
      this.renderItem(
         new ItemStack(((ItemStack)tileEntityIn.itemHandler.getContents().get(0)).getItem(), 1),
         tileEntityIn.getLevel(),
         matrixStackIn,
         bufferIn,
         combinedLightIn
      );
      matrixStackIn.popPose();
   }

   private void renderItemsWest(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(1.0F, 0.0F, 0.0F);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.translate(0.5, 0.265625, 0.25);
      matrixStackIn.scale(0.3F, 0.3F, 0.3F);
      this.renderItem(
         new ItemStack(((ItemStack)tileEntityIn.itemHandler.getContents().getFirst()).getItem(), 1),
         tileEntityIn.getLevel(),
         matrixStackIn,
         bufferIn,
         combinedLightIn
      );
      matrixStackIn.popPose();
   }

   private void renderItemsEast(HerbJarTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(1.0F, 0.0F, 0.0F);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.translate(0.5, 0.265625, 0.75);
      matrixStackIn.scale(0.3F, 0.3F, 0.3F);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      this.renderItem(
         new ItemStack(((ItemStack)tileEntityIn.itemHandler.getContents().getFirst()).getItem(), 1),
         tileEntityIn.getLevel(),
         matrixStackIn,
         bufferIn,
         combinedLightIn
      );
      matrixStackIn.popPose();
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

   private void renderBlock(
      PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BlockState state, RenderType renderType, int color
   ) {
      this.renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType, color);
   }

   public void renderSingleBlock(
      BlockState p_110913_,
      PoseStack p_110914_,
      MultiBufferSource p_110915_,
      int p_110916_,
      int p_110917_,
      ModelData modelData,
      RenderType renderType,
      int color
   ) {
      RenderShape rendershape = p_110913_.getRenderShape();
      if (rendershape != RenderShape.INVISIBLE) {
         switch (rendershape) {
            case MODEL:
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
               float f = (color >> 16 & 0xFF) / 255.0F;
               float f1 = (color >> 8 & 0xFF) / 255.0F;
               float f2 = (color & 0xFF) / 255.0F;

               for (RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42L), modelData)) {
                  dispatcher.getModelRenderer()
                     .renderModel(
                        p_110914_.last(),
                        p_110915_.getBuffer(renderType != null ? renderType : RenderTypeHelper.getEntityRenderType(rt, false)),
                        p_110913_,
                        bakedmodel,
                        f,
                        f1,
                        f2,
                        p_110916_,
                        p_110917_,
                        modelData,
                        rt
                     );
               }
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
