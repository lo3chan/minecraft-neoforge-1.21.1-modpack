package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Random;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.block.custom.PickablePlant;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.renderer.HerbJarRenderer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HerbJarItemRenderer extends CustomItemRenderer {
   private HerbJarRenderer renderer;
   public static ModelResourceLocation JAR = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hexerei", "block/herb_jar_model"));

   @OnlyIn(Dist.CLIENT)
   @Override
   public void renderByItem(
      ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      this.renderTileStuff(stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
   }

   @OnlyIn(Dist.CLIENT)
   public static HerbJarTile loadBlockEntityFromItem(CompoundTag tag, ItemStack item) {
      if (item.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         if (block instanceof HerbJar herbJar) {
            HerbJarTile te = (HerbJarTile)herbJar.newBlockEntity(
               BlockPos.ZERO,
               (BlockState)((BlockState)block.defaultBlockState().setValue(HerbJar.GUI_RENDER, true))
                  .setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH)
            );
            te.itemHandler.deserializeNBT(Hexerei.DynamicRegistries.get(), tag.getCompound("Inventory"));
            if (item.has(DataComponents.DYED_COLOR)) {
               te.setComponents(DataComponentMap.builder().set(DataComponents.DYED_COLOR, (DyedItemColor)item.get(DataComponents.DYED_COLOR)).build());
            }

            if (item.has(DataComponents.CUSTOM_NAME)) {
               te.customName = item.getHoverName();
            }

            return te;
         }
      }

      return null;
   }

   private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
   }

   public void renderTileStuff(
      ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      HerbJarTile tileEntityIn = loadBlockEntityFromItem(tag, stack);
      String name = tileEntityIn.getDisplayName().getString();
      DyeColor col = HexereiUtil.getDyeColorNamed(name);
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.2, -0.1, -0.1);
      matrixStackIn.translate(0.5, 0.265625, 0.25);
      matrixStackIn.scale(0.3F, 0.3F, 0.3F);
      this.renderItem(new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      if (!tileEntityIn.itemHandler.isEmpty()) {
         BlockState state = null;
         Item item = ((ItemStack)tileEntityIn.itemHandler.getContents().get(0)).getItem();
         if (item == ((PickablePlant)ModBlocks.BELLADONNA_PLANT.get()).asItem() || item == ((FlowerOutputItem)ModItems.BELLADONNA_FLOWERS.get()).asItem()) {
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

         Random rand = new Random(0L);
         Minecraft minecraft = Minecraft.getInstance();
         ItemRenderer itemRenderer = minecraft.getItemRenderer();
         ItemModelShaper shaper = itemRenderer.getItemModelShaper();
         boolean is3dModel = shaper.getModelManager().getModel(new ModelResourceLocation(HexereiUtil.getRegistryName(item), "inventory")).isGui3d();

         for (int a = 0; a < ((ItemStack)tileEntityIn.itemHandler.getContents().get(0)).getCount() / 1024.0F * 10.0F; a++) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.2, -0.1, -0.1);
            if (is3dModel) {
               matrixStackIn.translate(0.0, 0.09375, 0.0);
            }

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
                  new ItemStack(tileEntityIn.itemHandler.getStackInSlot(0).getItem(), 1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn
               );
            }

            matrixStackIn.popPose();
         }
      }

      int i = 4607830;
      int j1 = (int)(ARGB32.red(i) * 0.4);
      int k1 = (int)(ARGB32.green(i) * 0.4);
      int l1 = (int)(ARGB32.blue(i) * 0.4);
      int i1 = ARGB32.color(0, j1, k1, l1);
      int j2 = (int)(ARGB32.red(i) * 0.2);
      int k2 = (int)(ARGB32.green(i) * 0.2);
      int l2 = (int)(ARGB32.blue(i) * 0.2);
      int i2 = ARGB32.color(0, j2, k2, l2);
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.2, -0.1, -0.1);
      matrixStackIn.translate(0.5, 0.5, 0.24687499999999996);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.scale(0.0069444445F, -0.0069444445F, 0.0069444445F);
      Component component = null;
      if (stack.has(DataComponents.CUSTOM_NAME)) {
         component = stack.getHoverName();
      }

      if (component == null && tileEntityIn.getItemStackInSlot(0) != ItemStack.EMPTY) {
         if (tileEntityIn.getItemStackInSlot(0).getHoverName().getString().isEmpty()) {
            component = tileEntityIn.getItemStackInSlot(0).getItem().getName(tileEntityIn.getItemStackInSlot(0));
         } else {
            component = tileEntityIn.getItemStackInSlot(0).getHoverName();
         }
      }

      if (component != null) {
         List<FormattedCharSequence> list = Minecraft.getInstance().font.split(component, 70);
         float f3 = -Minecraft.getInstance().font.width((FormattedCharSequence)list.getFirst()) / 2;
         matrixStackIn.translate(0.0F, 0.0F, 1.0F);
         if (tileEntityIn.components().has(DataComponents.DYED_COLOR)) {
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
                     (FormattedCharSequence)list.getFirst(), f3, 0.0F, i2, false, matrixStackIn.last().pose(), bufferIn, DisplayMode.NORMAL, 0, combinedLightIn
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

      if (bufferIn instanceof BufferSource bufferSource) {
         bufferSource.endBatch();
      }

      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(0.0F));
      matrixStackIn.translate(0.7, 0.4, 0.4);
      matrixStackIn.scale(1.33F, 1.33F, 1.33F);
      BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(JAR);
      if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
         Minecraft.getInstance()
            .getItemRenderer()
            .render(stack, ItemDisplayContext.FIXED, false, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, baseModel);
      }

      matrixStackIn.popPose();
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
