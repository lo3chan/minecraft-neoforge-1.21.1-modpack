package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.function.Supplier;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class BookOfShadowsAltarRenderer implements BlockEntityRenderer<BookOfShadowsAltarTile> {
   private final Minecraft minecraft = Minecraft.getInstance();
   private final ItemRenderer itemRenderer = this.minecraft.getItemRenderer();

   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.getX() - pos.getX();
      double deltaY = entity.getY() - pos.getY();
      double deltaZ = entity.getZ() - pos.getZ();
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public AABB getRenderBoundingBox(BookOfShadowsAltarTile blockEntity) {
      return super.getRenderBoundingBox(blockEntity).inflate(5.0, 5.0, 5.0);
   }

   public void render(
      BookOfShadowsAltarTile altarTile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (altarTile.getLevel().getBlockState(altarTile.getBlockPos()).hasBlockEntity()
         && altarTile.getLevel().getBlockEntity(altarTile.getBlockPos()) instanceof BookOfShadowsAltarTile) {
         ItemStack stack = altarTile.itemHandler.getStackInSlot(0);
         if (stack.getItem() instanceof HexereiBookItem) {
            boolean isBookOfShadows = altarTile.currentBook != null && altarTile.currentBook.book().equals(HexereiUtil.getResource("book_of_shadows"));
            BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            altarTile.degreesSpunRender = CrystalBallRenderer.lerpAngle(altarTile.degreesSpunOld, altarTile.degreesSpun, partialTicks);
            altarTile.buttonScaleRender = Math.max(
               0.0F, BookOfShadowsAltarTile.easeButtons(Mth.lerp(partialTicks, altarTile.buttonScaleOld, altarTile.buttonScale))
            );
            altarTile.degreesOpenedRender = BookOfShadowsAltarTile.easeOpened(Mth.lerp(partialTicks, altarTile.openedPercentOld, altarTile.openedPercent))
               * 90.0F;
            altarTile.degreesFloppedRender = BookOfShadowsAltarTile.easeFlop(Mth.lerp(partialTicks, altarTile.floppedPercentOld, altarTile.floppedPercent))
               * 90.0F;
            altarTile.pageOneRotationRender = Mth.lerp(partialTicks, altarTile.pageOneRotationLast, altarTile.pageOneRotation);
            altarTile.pageTwoRotationRender = Mth.lerp(partialTicks, altarTile.pageTwoRotationLast, altarTile.pageTwoRotation);
            DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1.125F, 0.5F);
            matrixStackIn.translate(
               (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
            );
            matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0.0F, -0.00390625F * (1.0F - altarTile.degreesOpenedRender / 90.0F), -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(altarTile.degreesOpenedRender - 90.0F));
            matrixStackIn.translate(0.03125F * (altarTile.degreesOpenedRender / 90.0F), 0.03125F * (1.0F - altarTile.degreesOpenedRender / 90.0F), 0.0F);
            if (isBookOfShadows) {
               this.renderBlock(
                  matrixStackIn, buffer, combinedLightIn, ((Block)ModBlocks.BOOK_OF_SHADOWS_COVER.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
               this.renderBlock(
                  matrixStackIn,
                  buffer,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_OF_SHADOWS_COVER_CORNERS.get()).defaultBlockState(),
                  col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
               );
            } else {
               this.renderBlock(
                  matrixStackIn, buffer, combinedLightIn, ((Block)ModBlocks.BOOK_COVER.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
               this.renderBlock(
                  matrixStackIn,
                  buffer,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_COVER_CORNERS.get()).defaultBlockState(),
                  col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
               );
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1.125F, 0.5F);
            matrixStackIn.translate(
               (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
            );
            matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0.0F, -0.00390625F * (1.0F - altarTile.degreesOpenedRender / 90.0F), -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-(altarTile.degreesOpenedRender - 90.0F)));
            matrixStackIn.translate(-0.03125F * (altarTile.degreesOpenedRender / 90.0F), 0.03125F * (1.0F - altarTile.degreesOpenedRender / 90.0F), 0.0F);
            if (isBookOfShadows) {
               this.renderBlock(
                  matrixStackIn, buffer, combinedLightIn, ((Block)ModBlocks.BOOK_OF_SHADOWS_BACK.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
               this.renderBlock(
                  matrixStackIn,
                  buffer,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_OF_SHADOWS_BACK_CORNERS.get()).defaultBlockState(),
                  col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
               );
            } else {
               this.renderBlock(
                  matrixStackIn, buffer, combinedLightIn, ((Block)ModBlocks.BOOK_BACK.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
               this.renderBlock(
                  matrixStackIn,
                  buffer,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_BACK_CORNERS.get()).defaultBlockState(),
                  col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
               );
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1.125F, 0.5F);
            matrixStackIn.translate(
               (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
            );
            matrixStackIn.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
            matrixStackIn.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
            if (isBookOfShadows) {
               this.renderBlock(
                  matrixStackIn,
                  buffer,
                  combinedLightIn,
                  ((Block)ModBlocks.BOOK_OF_SHADOWS_BINDING.get()).defaultBlockState(),
                  HexereiBookItem.getColor2(stack)
               );
            } else {
               this.renderBlock(
                  matrixStackIn, buffer, combinedLightIn, ((Block)ModBlocks.BOOK_BINDING.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
               );
            }

            matrixStackIn.popPose();
            buffer.endBatch();
            Vec2 ip = PageDrawing.getIntersectPoint(
               Minecraft.getInstance().player.getLookAngle(), Minecraft.getInstance().player.getEyePosition(), altarTile, PageDrawing.PageOn.LEFT_PAGE
            );
            Vec2 ip2 = PageDrawing.getIntersectPoint(
               Minecraft.getInstance().player.getLookAngle(), Minecraft.getInstance().player.getEyePosition(), altarTile, PageDrawing.PageOn.RIGHT_PAGE
            );
            if (ip == null) {
               ip = new Vec2(50.0F, 50.0F);
            }

            if (ip2 == null) {
               ip2 = new Vec2(50.0F, 50.0F);
            }

            if (altarTile.openedPercent != 1.0F) {
               altarTile.drawing
                  .drawPages(
                     altarTile, ip.x, ip.y, ip2.x, ip2.y, matrixStackIn, buffer, combinedLightIn, combinedOverlayIn, partialTicks, PageDrawing.DrawingType.BOOK
                  );
            }

            buffer.endBatch();
            if (altarTile.openedPercent != 1.0F) {
               try {
                  buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                  altarTile.drawing.drawTooltips(altarTile, matrixStackIn, buffer, combinedLightIn, combinedOverlayIn, partialTicks);
               } catch (CommandSyntaxException var16) {
                  var16.printStackTrace();
               }
            }
         } else if (!stack.isEmpty()) {
            FlowerPotBlock block = (FlowerPotBlock)Blocks.FLOWER_POT;
            Map<ResourceLocation, Supplier<? extends Block>> map = block.getFullPotsView();
            ResourceLocation loc = BuiltInRegistries.ITEM.getKey(stack.getItem());
            BlockState blockState = map.getOrDefault(loc, () -> Blocks.AIR).get().defaultBlockState();
            if (!blockState.isAir()) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(0.1875F, 1.015625F, 0.1875F);
               matrixStackIn.translate(
                  Math.cos(blockState.getBlock().toString().length() * 14.0F) / 4.0, 0.0, Math.sin(blockState.getBlock().toString().length() * 14.0F) / 4.0
               );
               matrixStackIn.scale(0.65F, 0.65F, 0.65F);
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, blockState);
               matrixStackIn.popPose();
            } else {
               BakedModel itemModel = this.itemRenderer.getModel(stack, altarTile.getLevel(), null, 0);
               boolean is3dModel = itemModel.isGui3d();
               if (stack.getItem() instanceof BlockItem blockItem && is3dModel) {
                  matrixStackIn.translate(0.5F, 1.171875F, 0.5F);
                  matrixStackIn.scale(0.65F, 0.65F, 0.65F);
                  this.renderItem(stack, altarTile.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
               } else {
                  matrixStackIn.pushPose();
                  matrixStackIn.translate(0.5F, 1.015625F, 0.5F);
                  matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
                  matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                  this.renderItem(stack, altarTile.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                  matrixStackIn.popPose();
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

   public void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
      this.renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
   }

   public void renderSingleBlock(
      BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color
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
                     p_110914_.last(),
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
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
