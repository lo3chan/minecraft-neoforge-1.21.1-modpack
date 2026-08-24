package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Altar;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HexereiBookItemRenderer extends CustomItemRenderer {
   float degreesOpened;
   float degreesOpened2;
   float yPos;
   float xPos;
   float zPos;

   @OnlyIn(Dist.CLIENT)
   @Override
   public void renderByItem(
      ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      this.renderTileStuff(stack, transformType, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
   }

   public static BookOfShadowsAltarTile loadBlockEntityFromItem(ItemStack item) {
      if (item.getItem() instanceof HexereiBookItem) {
         BookOfShadowsAltarTile te = new BookOfShadowsAltarTile(BlockPos.ZERO, ((Altar)ModBlocks.BOOK_OF_SHADOWS_ALTAR.get()).defaultBlockState());
         te.itemHandler.setStackInSlot(0, item);
         return te;
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
      Minecraft.getInstance()
         .getBlockRenderer()
         .renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
   }

   @OnlyIn(Dist.CLIENT)
   public void renderTileStuff(
      ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      BookOfShadowsAltarTile altarTile = loadBlockEntityFromItem(stack);
      if (altarTile != null) {
         altarTile.tickCount = ClientEvents.getClientTicks();
         if (altarTile.itemHandler.getStackInSlot(0).getItem() instanceof HexereiBookItem) {
            BookData bookData = (BookData)stack.getOrDefault(ModDataComponents.BOOK, BookData.EMPTY);
            boolean isBookOfShadows = bookData.book().equals(HexereiUtil.getResource("book_of_shadows"));
            this.yPos = 0.0F;
            this.xPos = 0.0F;
            this.zPos = 0.0F;
            this.degreesOpened2 = 0.0F;
            this.degreesOpened = 45.0F;
            matrixStackIn.pushPose();
            altarTile.degreesOpened = 90.0F;
            altarTile.degreesFlopped = 90.0F;
            if (bookData.isOpened()) {
               altarTile.degreesOpened = 18.0F;
               altarTile.degreesFlopped = 0.0F;
               this.degreesOpened = -10.0F;
               altarTile.degreesSpun = 270.0F;
            }

            altarTile.degreesSpunRender = altarTile.degreesSpun;
            altarTile.degreesFloppedRender = altarTile.degreesFlopped;
            altarTile.degreesOpenedRender = altarTile.degreesOpened;
            altarTile.pageOneRotationRender = altarTile.pageOneRotation;
            altarTile.pageTwoRotationRender = altarTile.pageTwoRotation;
            if (altarTile.degreesOpened != 90.0F) {
               altarTile.drawing
                  .drawPages(
                     altarTile,
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     matrixStackIn,
                     bufferIn,
                     combinedLightIn,
                     combinedOverlayIn,
                     PageDrawing.DrawingType.GUI,
                     transformType,
                     ClientEvents.getPartial()
                  );
            }

            this.yPos = 0.0F;
            this.xPos = 0.0F;
            this.zPos = 0.0F;
            this.degreesOpened2 = 0.0F;
            this.degreesOpened = 45.0F;
            if (bookData.isOpened()) {
               altarTile.degreesOpened = 18.0F;
               altarTile.degreesFlopped = 0.0F;
               this.degreesOpened = -10.0F;
               altarTile.degreesSpun = 270.0F;
            } else {
               altarTile.degreesOpened = 90.0F;
               altarTile.degreesFlopped = 90.0F;
               if (transformType == ItemDisplayContext.GUI) {
                  this.yPos = 0.375F;
                  this.xPos = 0.125F;
                  this.zPos = -0.375F;
                  matrixStackIn.scale(1.35F, 1.35F, 1.35F);
               }

               if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                  this.degreesOpened2 = 90.0F;
                  this.xPos = 0.25F;
                  this.zPos = -0.375F;
               }

               if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                  this.degreesOpened2 = 90.0F;
                  this.xPos = 0.25F;
                  this.zPos = -0.03125F;
               }
            }

            altarTile.degreesSpunRender = altarTile.degreesSpun;
            altarTile.degreesFloppedRender = altarTile.degreesFlopped;
            altarTile.degreesOpenedRender = altarTile.degreesOpened;
            altarTile.pageOneRotationRender = altarTile.pageOneRotation;
            altarTile.pageTwoRotationRender = altarTile.pageTwoRotation;
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F + this.xPos, 1.125F + this.yPos, 0.5F + this.zPos);
            matrixStackIn.translate(
               (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
            );
            matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2.0F + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(this.degreesOpened2));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(altarTile.degreesOpenedRender - 90.0F));
            matrixStackIn.translate(0.03125F * (altarTile.degreesOpenedRender / 90.0F), 0.03125F * (1.0F - altarTile.degreesOpenedRender / 90.0F), 0.0F);
            DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
            if (isBookOfShadows) {
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_OF_SHADOWS_COVER.get()).defaultBlockState(),
                  HexereiBookItem.getColor2(stack)
               );
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_OF_SHADOWS_COVER_CORNERS.get()).defaultBlockState(),
                  col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
               );
            } else {
               this.renderBlock(
                  matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_COVER.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_COVER_CORNERS.get()).defaultBlockState(),
                  col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
               );
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F + this.xPos, 1.125F + this.yPos, 0.5F + this.zPos);
            matrixStackIn.translate(
               (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
            );
            matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2.0F + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(this.degreesOpened2));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(altarTile.degreesOpenedRender - 90.0F)));
            matrixStackIn.translate(-0.03125F * (altarTile.degreesOpenedRender / 90.0F), 0.03125F * (1.0F - altarTile.degreesOpenedRender / 90.0F), 0.0F);
            if (isBookOfShadows) {
               this.renderBlock(
                  matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_OF_SHADOWS_BACK.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_OF_SHADOWS_BACK_CORNERS.get()).defaultBlockState(),
                  col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
               );
            } else {
               this.renderBlock(
                  matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_BACK.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_BACK_CORNERS.get()).defaultBlockState(),
                  col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
               );
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F + this.xPos, 1.125F + this.yPos, 0.5F + this.zPos);
            matrixStackIn.translate(
               (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F)
            );
            matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2.0F + this.degreesOpened)));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(this.degreesOpened2));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
            if (isBookOfShadows) {
               this.renderBlock(
                  matrixStackIn,
                  bufferIn,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_OF_SHADOWS_BINDING.get()).defaultBlockState(),
                  HexereiBookItem.getColor2(stack)
               );
            } else {
               this.renderBlock(
                  matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_BINDING.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
            }

            matrixStackIn.popPose();
            if (altarTile.degreesFloppedRender != 90.0F) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(0.5F + this.xPos, 1.125F + this.yPos, 0.5F + this.zPos);
               matrixStackIn.translate(
                  (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F),
                  0.0F,
                  (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F)
               );
               matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2.0F + this.degreesOpened)));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(this.degreesOpened2));
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
               matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
               matrixStackIn.translate(0.0F, 0.03125F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(80.0F - altarTile.degreesOpened / 1.12F));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpened / 1.12F) / 90.0F * -altarTile.pageOneRotationRender));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpened / 1.12F) / 90.0F * (altarTile.pageTwoRotationRender / 16.0F)));
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_OF_SHADOWS_PAGE.get()).defaultBlockState());
               matrixStackIn.popPose();
               matrixStackIn.pushPose();
               matrixStackIn.translate(0.5F + this.xPos, 1.125F + this.yPos, 0.5F + this.zPos);
               matrixStackIn.translate(
                  (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F),
                  0.0F,
                  (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F)
               );
               matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2.0F + this.degreesOpened)));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(this.degreesOpened2));
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
               matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
               matrixStackIn.translate(0.0F, 0.03125F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(80.0F - altarTile.degreesOpened / 1.12F));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpened / 1.12F) / 90.0F * -altarTile.pageOneRotation));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpened / 1.12F) / 90.0F * (altarTile.pageTwoRotation / 16.0F)));
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_OF_SHADOWS_PAGE.get()).defaultBlockState());
               matrixStackIn.popPose();
            }

            if (altarTile.turnPage == 1 || altarTile.turnPage == -1) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(0.5F + this.xPos, 1.125F + this.yPos, 0.5F + this.zPos);
               matrixStackIn.translate(
                  (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F),
                  0.0F,
                  (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F)
               );
               matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2.0F + this.degreesOpened)));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(this.degreesOpened2));
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
               matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
               matrixStackIn.translate(0.0F, 0.03125F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(80.0F - altarTile.degreesOpened / 1.12F));
               matrixStackIn.mulPose(
                  Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpened / 1.12F) / 90.0F * (-altarTile.pageOneRotationRender / 16.0F + 11.25F))
               );
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_OF_SHADOWS_PAGE.get()).defaultBlockState());
               matrixStackIn.popPose();
            }

            if (altarTile.degreesFloppedRender != 90.0F) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(0.5F + this.xPos, 1.125F + this.yPos, 0.5F + this.zPos);
               matrixStackIn.translate(
                  (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F),
                  0.0F,
                  (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F)
               );
               matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2.0F + this.degreesOpened)));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(this.degreesOpened2));
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
               matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
               matrixStackIn.translate(0.0F, 0.03125F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpened / 1.12F)));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpened / 1.12F) / 90.0F * -altarTile.pageTwoRotationRender));
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpened / 1.12F) / 90.0F * (altarTile.pageOneRotationRender / 16.0F)));
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_OF_SHADOWS_PAGE.get()).defaultBlockState());
               matrixStackIn.popPose();
            }

            if (altarTile.turnPage == 2 || altarTile.turnPage == -1) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(0.5F + this.xPos, 1.125F + this.yPos, 0.5F + this.zPos);
               matrixStackIn.translate(
                  (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F),
                  0.0F,
                  (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F)
               );
               matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpened / 2.0F + this.degreesOpened)));
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(this.degreesOpened2));
               matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
               matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
               matrixStackIn.translate(0.0F, 0.03125F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpened / 1.12F)));
               matrixStackIn.mulPose(
                  Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpened / 1.12F) / 90.0F * (-altarTile.pageTwoRotationRender / 16.0F + 11.25F))
               );
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BOOK_OF_SHADOWS_PAGE.get()).defaultBlockState());
               matrixStackIn.popPose();
            }

            matrixStackIn.popPose();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
      this.renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
   }

   @OnlyIn(Dist.CLIENT)
   public void renderSingleBlock(
      BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color
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
               dispatcher.getModelRenderer()
                  .renderModel(
                     poseStack.last(),
                     p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)),
                     p_110913_,
                     bakedmodel,
                     f,
                     f1,
                     f2,
                     p_110916_,
                     p_110917_,
                     modelData,
                     null
                  );
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               poseStack.translate(0.2, -0.1, -0.1);
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
