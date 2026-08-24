package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Random;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.CourierLetter;
import net.joefoxe.hexerei.block.custom.CourierPackage;
import net.joefoxe.hexerei.block.custom.OwlCourierDepotWall;
import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class OwlCourierDepotRenderer implements BlockEntityRenderer<OwlCourierDepotTile> {
   private final Font font;
   private final Minecraft minecraft = Minecraft.getInstance();
   private final ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
   private final ItemModelShaper shaper = this.itemRenderer.getItemModelShaper();

   public OwlCourierDepotRenderer() {
      this.font = Minecraft.getInstance().font;
   }

   public void render(
      OwlCourierDepotTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      BlockState state = tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos());
      if (state.hasBlockEntity() && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof OwlCourierDepotTile owlCourierDepotTile) {
         int col = 4607830;
         int colr = (int)(ARGB32.red(col) * 0.4);
         int colg = (int)(ARGB32.green(col) * 0.4);
         int colb = (int)(ARGB32.blue(col) * 0.4);
         int i1 = ARGB32.color(0, colr, colg, colb);
         if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            matrixStackIn.translate(0.5, 0.359375, 0.859375);
         } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.translate(0.5, 0.359375, 0.140625);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
         } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            matrixStackIn.translate(0.140625, 0.359375, 0.5);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
         } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.translate(0.859375, 0.359375, 0.5);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
         }

         if (!(state.getBlock() instanceof OwlCourierDepotWall)) {
            matrixStackIn.translate(0.0, -0.3125, 0.0);
         }

         if (ClientOwlCourierDepotData.getDepots().containsKey(GlobalPos.of(tileEntityIn.getLevel().dimension(), tileEntityIn.getBlockPos()))) {
            OwlCourierDepotData depotData = ClientOwlCourierDepotData.getDepots()
               .get(GlobalPos.of(tileEntityIn.getLevel().dimension(), tileEntityIn.getBlockPos()));
            int packages = 0;
            int letters = 0;

            for (ItemStack stack : depotData.items) {
               if (stack.getItem() == ModItems.COURIER_PACKAGE.get()) {
                  packages++;
               }

               if (stack.getItem() == ModItems.COURIER_LETTER.get()) {
                  letters++;
               }
            }

            Random random = new Random(4200L);
            float packageOffset = 0.5F;

            for (int i = 0; i < packages; i++) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(-0.39375, packageOffset / 16.0, -0.7625);
               matrixStackIn.scale(0.8F, 0.8F, 0.8F);
               matrixStackIn.translate(0.5, 0.0, 0.5);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(random.nextInt(360) * i));
               matrixStackIn.translate(-0.5, 0.0, -0.5);
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((CourierPackage)ModBlocks.COURIER_PACKAGE.get()).defaultBlockState());
               matrixStackIn.popPose();
               packageOffset += 5.7F;
            }

            packageOffset += 0.1F;

            for (int i = 0; i < letters; i++) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(
                  (-6.3 - random.nextFloat(0.8F * (letters - i)) + 0.4F * (letters - i)) / 16.0,
                  packageOffset / 16.0,
                  (-12.2 - random.nextFloat(0.8F * (letters - i)) + 0.4F * (letters - i)) / 16.0
               );
               matrixStackIn.scale(0.8F, 0.8F, 0.8F);
               matrixStackIn.translate(0.5, 0.0, 0.5);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(random.nextInt(360) * i));
               matrixStackIn.translate(-0.5, 0.0, -0.5);
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((CourierLetter)ModBlocks.COURIER_LETTER.get()).defaultBlockState());
               matrixStackIn.popPose();
               packageOffset += 0.2F;
            }
         }

         if (state.getBlock() instanceof OwlCourierDepotWall) {
            matrixStackIn.translate(0.0, -0.0625, 0.0);
         }

         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-22.0F));
         matrixStackIn.translate(0.0, 0.0625, 0.0);
         matrixStackIn.scale(0.00694445F, -0.00694445F, 0.00694445F);
         Component component = Component.literal("").withStyle(Style.EMPTY.withColor(11184810));
         if (owlCourierDepotTile.hasCustomName() && owlCourierDepotTile.getCustomName().getString().length() > 0) {
            component = owlCourierDepotTile.getCustomName();
         }

         List<FormattedCharSequence> list = Minecraft.getInstance().font.split(component, 512);
         if (list.size() > 0) {
            int width = this.minecraft.font.width(list.get(0));
            float lineHeight = 9.0F / 2.0F;
            if (width > 70) {
               float percent = width / 70.0F;
               matrixStackIn.pushPose();
               matrixStackIn.scale(1.0F / percent, 1.0F / percent, 1.0F / percent);
               Minecraft.getInstance()
                  .font
                  .drawInBatch8xOutline(list.get(0), -width / 2.0F, lineHeight * percent, i1, 2236962, matrixStackIn.last().pose(), bufferIn, combinedLightIn);
               matrixStackIn.popPose();
            } else {
               Minecraft.getInstance()
                  .font
                  .drawInBatch8xOutline(list.get(0), -width / 2.0F, lineHeight, i1, 2236962, matrixStackIn.last().pose(), bufferIn, combinedLightIn);
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
