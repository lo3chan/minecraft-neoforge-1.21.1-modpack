package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Altar;
import net.joefoxe.hexerei.data.books.BookChapter;
import net.joefoxe.hexerei.data.books.BookEntity;
import net.joefoxe.hexerei.data.books.BookEntries;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.BookPage;
import net.joefoxe.hexerei.data.books.BookPageEntry;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.renderer.CrystalBallRenderer;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.UpdateBookDataToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class BookOfShadowsScreen extends Screen {
   public final BookOfShadowsAltarTile altar;
   protected int left;
   protected int top;
   int img_width = 244;
   int img_height = 170;
   float mouseGrabbedX = 0.0F;
   float mouseGrabbedY = 0.0F;
   ItemStack fromItem = null;
   Player player;
   InteractionHand hand;
   float flippingFast = 0.0F;
   float flippingFastO = 0.0F;

   public BookOfShadowsScreen(BookOfShadowsAltarTile altar) {
      super(Component.translatable("screen.hexerei.book_of_shadows"));
      this.minecraft = Minecraft.getInstance();
      this.altar = altar;
   }

   public BookOfShadowsScreen(Player player, InteractionHand hand) {
      super(Component.translatable("screen.hexerei.book_of_shadows"));
      ItemStack stack = player.getItemInHand(hand);
      this.player = player;
      this.hand = hand;
      this.minecraft = Minecraft.getInstance();
      this.fromItem = stack;
      this.altar = (BookOfShadowsAltarTile)((BlockEntityType)ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get())
         .create(BlockPos.ZERO, ((Altar)ModBlocks.BOOK_OF_SHADOWS_ALTAR.get()).defaultBlockState());
      if (this.altar != null) {
         this.altar.currentBook = (BookData)stack.getOrDefault(ModDataComponents.BOOK, BookData.EMPTY);
         this.altar.fromItem = true;
         if (this.altar.currentBook.isOpened()) {
            this.altar.openedPercent = 0.5F;
            this.altar.floppedPercent = 0.5F;
         }
      } else {
         this.onClose();
      }
   }

   protected void init() {
      this.left = this.width / 2 - this.img_width / 2;
      this.top = this.height / 2 - this.img_height / 2;
   }

   public Vec2 getLeftCursor(int mouseX, int mouseY) {
      float inputXl = (mouseX - (this.left + this.img_width / 2.0F) + 123.0F) / 123.0F * 6.55F - 0.5F;
      float inputYl = (mouseY - (this.top + this.img_height / 2.0F) + 87.0F) / 174.0F * 9.1F - 1.0F;
      return new Vec2(inputXl, inputYl);
   }

   public Vec2 getRightCursor(int mouseX, int mouseY) {
      float inputXr = (mouseX - (this.left + this.img_width / 2.0F)) / 123.0F * 6.55F - 0.5F + 0.25F;
      float inputYr = (mouseY - (this.top + this.img_height / 2.0F) + 87.0F) / 174.0F * 9.1F - 1.0F;
      return new Vec2(inputXr, inputYr);
   }

   private float easeInOutElastic(double x) {
      double c5 = 1.3962634015954636;
      return (float)(
         x == 0.0 ? 0.0 : (x == 1.0 ? 1.0 : (x < 0.5 ? 4.0 * x * x * x : Math.pow(2.0, -20.0 * x + 10.0) * Math.sin((20.0 * x - 11.125) * c5) / 2.0 + 1.0))
      );
   }

   public static float easeOutBack(float x) {
      float c1 = 1.70158F;
      float c3 = c1 + 1.0F;
      return 1.0F + c3 * (float)Math.pow(x - 1.0F, 3.0) + c1 * (float)Math.pow(x - 1.0F, 2.0);
   }

   public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {
      ItemStack stack = this.fromItem != null ? this.fromItem : this.altar.itemHandler.getStackInSlot(0);
      if (stack.getItem() instanceof HexereiBookItem) {
         Vec2 left = this.getLeftCursor(mouseX, mouseY);
         Vec2 right = this.getRightCursor(mouseX, mouseY);
         pGuiGraphics.pose().pushPose();
         pGuiGraphics.pose().translate(this.left + this.img_width / 2.0F, this.top + this.img_height / 2.0F, 1500.0F);
         Lighting.setupForFlatItems();
         float scale = 298.75F;
         float guiScale = (float)Minecraft.getInstance().getWindow().getGuiScale();
         if ((int)guiScale == 3) {
            scale = 306.38F;
         }

         if ((int)guiScale == 4) {
            scale = 298.86F;
         }

         if ((int)guiScale == 5) {
            scale = 294.11F;
         }

         scale = 306.38F;
         this.altar.degreesSpunRender = CrystalBallRenderer.lerpAngle(this.altar.degreesSpunOld, this.altar.degreesSpun, pPartialTick);
         this.altar.buttonScaleRender = Math.max(
            0.0F, BookOfShadowsAltarTile.easeButtons(Mth.lerp(pPartialTick, this.altar.buttonScaleOld, this.altar.buttonScale))
         );
         this.altar.degreesOpenedRender = BookOfShadowsAltarTile.easeOpened(Mth.lerp(pPartialTick, this.altar.openedPercentOld, this.altar.openedPercent))
            * 90.0F;
         this.altar.degreesFloppedRender = BookOfShadowsAltarTile.easeFlop(Mth.lerp(pPartialTick, this.altar.floppedPercentOld, this.altar.floppedPercent))
            * 90.0F;
         this.altar.pageOneRotationRender = Mth.lerp(pPartialTick, this.altar.pageOneRotationLast, this.altar.pageOneRotation);
         this.altar.pageTwoRotationRender = Mth.lerp(pPartialTick, this.altar.pageTwoRotationLast, this.altar.pageTwoRotation);
         if (this.altar.turnPage != 1 && this.altar.turnPage != 2) {
            this.altar.degreesOpenedRender = this.altar.degreesOpenedRender
               + Mth.abs(Mth.sin(Mth.lerp(pPartialTick, this.flippingFastO, this.flippingFast) * 3.1415927F / 2.0F)) * 25.0F;
         } else {
            this.altar.degreesOpenedRender = this.altar.degreesOpenedRender
               + (
                  Mth.sin(this.altar.pageOneRotationRender / 180.0F * 3.1415927F) * 5.0F
                     + Mth.sin(this.altar.pageTwoRotationRender / 180.0F * 3.1415927F) * 5.0F
               );
         }

         pGuiGraphics.pose().scale(scale, -scale, scale);
         pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.altar.degreesFloppedRender));
         pGuiGraphics.pose().translate(0.0F, 0.0F, 0.5F);
         pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(Mth.sin(Math.max(0.0F, this.altar.degreesOpenedRender) / 90.0F * 3.1415927F) * -7.5F));
         if (this.altar.turnPage == 1 || this.altar.turnPage == 2) {
            pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(Mth.sin(this.altar.pageOneRotationRender / 180.0F * 3.1415927F) * -10.0F));
            pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(Mth.sin(this.altar.pageTwoRotationRender / 180.0F * 3.1415927F) * -10.0F));
         }

         pGuiGraphics.pose()
            .mulPose(Axis.XP.rotationDegrees(Mth.sin(Mth.lerp(pPartialTick, this.flippingFastO, this.flippingFast) * 3.1415927F / 2.0F) * -5.0F));
         pGuiGraphics.pose().translate(0.0F, 0.0F, -0.5F);
         pGuiGraphics.pose().translate(-0.5F, -1.4375F, -0.5F);
         pGuiGraphics.pose().translate(0.0041F, 0.0F, 0.0F);
         BufferSource buffer = pGuiGraphics.bufferSource();
         boolean isBookOfShadows = this.altar.currentBook != null && this.altar.currentBook.book().equals(HexereiUtil.getResource("book_of_shadows"));
         float degreesSpunRender = this.altar.degreesSpunRender;
         float degreesOpenedRender = this.altar.degreesOpenedRender;
         float floppedPercent = this.altar.floppedPercent;
         float degreesFloppedRender = this.altar.degreesFloppedRender;
         this.altar.degreesSpunRender = -180.0F;
         this.altar.degreesOpenedRender = Math.clamp(this.altar.degreesOpenedRender, 0.0F, 90.0F);
         this.altar.floppedPercent = 0.0F;
         this.altar.degreesFloppedRender = 0.0F;
         DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString());
         Lighting.setupForFlatItems();
         pGuiGraphics.pose().pushPose();
         pGuiGraphics.pose().translate(0.5F, 1.125F, 0.5F);
         pGuiGraphics.pose()
            .translate(
               (float)Math.sin(this.altar.degreesSpunRender / 57.3F) / 32.0F * (this.altar.degreesOpenedRender / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(this.altar.degreesSpunRender / 57.3F) / 32.0F * (this.altar.degreesOpenedRender / 5.0F - 12.0F)
            );
         pGuiGraphics.pose().translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - this.altar.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
         pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.altar.degreesSpunRender));
         pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90.0F));
         pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-this.altar.degreesFloppedRender));
         pGuiGraphics.pose().translate(0.0F, 0.0F, -(this.altar.degreesFloppedRender / 10.0F) / 32.0F);
         pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(this.altar.degreesOpenedRender - 90.0F));
         pGuiGraphics.pose().translate(0.03125F * (this.altar.degreesOpenedRender / 90.0F), 0.03125F * (1.0F - this.altar.degreesOpenedRender / 90.0F), 0.0F);
         pGuiGraphics.pose().translate(0.001953125F, 0.0F, 0.0F);
         if (isBookOfShadows) {
            this.renderBlock(
               pGuiGraphics.pose(), buffer, 15728880, ((Block)ModBlocks.BOOK_OF_SHADOWS_COVER.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
            );
            this.renderBlock(
               pGuiGraphics.pose(),
               buffer,
               15728880,
               ((Block)ModBlocks.BOOK_OF_SHADOWS_COVER_CORNERS.get()).defaultBlockState(),
               col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
            );
         } else {
            this.renderBlock(pGuiGraphics.pose(), buffer, 15728880, ((Block)ModBlocks.BOOK_COVER.get()).defaultBlockState(), HexereiBookItem.getColor2(stack));
            this.renderBlock(
               pGuiGraphics.pose(),
               buffer,
               15728880,
               ((Block)ModBlocks.BOOK_COVER_CORNERS.get()).defaultBlockState(),
               col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
            );
         }

         pGuiGraphics.pose().popPose();
         pGuiGraphics.pose().pushPose();
         pGuiGraphics.pose().translate(0.5F, 1.125F, 0.5F);
         pGuiGraphics.pose()
            .translate(
               (float)Math.sin(this.altar.degreesSpunRender / 57.3F) / 32.0F * (this.altar.degreesOpenedRender / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(this.altar.degreesSpunRender / 57.3F) / 32.0F * (this.altar.degreesOpenedRender / 5.0F - 12.0F)
            );
         pGuiGraphics.pose().translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - this.altar.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
         pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.altar.degreesSpunRender));
         pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90.0F));
         pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-this.altar.degreesFloppedRender));
         pGuiGraphics.pose().translate(0.0F, 0.0F, -(this.altar.degreesFloppedRender / 10.0F) / 32.0F);
         pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-(this.altar.degreesOpenedRender - 90.0F)));
         pGuiGraphics.pose().translate(-0.03125F * (this.altar.degreesOpenedRender / 90.0F), 0.03125F * (1.0F - this.altar.degreesOpenedRender / 90.0F), 0.0F);
         pGuiGraphics.pose().translate(-0.001953125F, 0.0F, 0.0F);
         if (isBookOfShadows) {
            this.renderBlock(
               pGuiGraphics.pose(), buffer, 15728880, ((Block)ModBlocks.BOOK_OF_SHADOWS_BACK.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
            );
            this.renderBlock(
               pGuiGraphics.pose(),
               buffer,
               15728880,
               ((Block)ModBlocks.BOOK_OF_SHADOWS_BACK_CORNERS.get()).defaultBlockState(),
               col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
            );
         } else {
            this.renderBlock(pGuiGraphics.pose(), buffer, 15728880, ((Block)ModBlocks.BOOK_BACK.get()).defaultBlockState(), HexereiBookItem.getColor2(stack));
            this.renderBlock(
               pGuiGraphics.pose(),
               buffer,
               15728880,
               ((Block)ModBlocks.BOOK_BACK_CORNERS.get()).defaultBlockState(),
               col == null ? HexereiBookItem.getColor1(stack) : HexereiUtil.getColorValue(col)
            );
         }

         pGuiGraphics.pose().popPose();
         pGuiGraphics.pose().pushPose();
         pGuiGraphics.pose().translate(0.5F, 1.125F, 0.5F);
         pGuiGraphics.pose()
            .translate(
               (float)Math.sin(this.altar.degreesSpunRender / 57.3F) / 32.0F * (this.altar.degreesOpenedRender / 5.0F - 12.0F),
               0.0F,
               (float)Math.cos(this.altar.degreesSpunRender / 57.3F) / 32.0F * (this.altar.degreesOpenedRender / 5.0F - 12.0F)
            );
         pGuiGraphics.pose().translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - this.altar.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
         pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.altar.degreesSpunRender));
         pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(-90.0F));
         pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(-this.altar.degreesFloppedRender));
         pGuiGraphics.pose().translate(0.0F, 0.0F, -(this.altar.degreesFloppedRender / 10.0F) / 32.0F);
         if (isBookOfShadows) {
            this.renderBlock(
               pGuiGraphics.pose(), buffer, 15728880, ((Block)ModBlocks.BOOK_OF_SHADOWS_BINDING.get()).defaultBlockState(), HexereiBookItem.getColor2(stack)
            );
         } else {
            this.renderBlock(pGuiGraphics.pose(), buffer, 15728880, ((Block)ModBlocks.BOOK_BINDING.get()).defaultBlockState(), HexereiBookItem.getColor2(stack));
         }

         pGuiGraphics.pose().popPose();
         if (buffer instanceof BufferSource) {
            buffer.endBatch();
         }

         Lighting.setupForFlatItems();
         if (this.altar.openedPercent != 1.0F) {
            this.altar
               .drawing
               .drawPages(
                  this.altar,
                  left.x,
                  left.y,
                  right.x,
                  right.y,
                  pGuiGraphics.pose(),
                  buffer,
                  15728880,
                  OverlayTexture.NO_OVERLAY,
                  pPartialTick,
                  PageDrawing.DrawingType.SCREEN
               );
         }

         buffer.endBatch();
         pGuiGraphics.pose().popPose();
         pGuiGraphics.pose().pushPose();
         pGuiGraphics.pose().translate(0.0F, 0.0F, 2500.0F);
         Lighting.setupForFlatItems();
         if (this.altar.currentBook.isOpened()
            && this.altar.openedPercent < 0.15F
            && this.altar.turnPage == 0
            && (this.altar.drawing.drawTooltipStack || this.altar.drawing.drawTooltipText)) {
            List<Component> tooltip = (List<Component>)(this.altar.drawing.tooltipStack != null && !this.altar.drawing.tooltipStack.isEmpty()
               ? this.altar
                  .drawing
                  .tooltipStack
                  .getTooltipLines(
                     TooltipContext.EMPTY, Hexerei.proxy.getPlayer(), Minecraft.getInstance().options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL
                  )
               : new ArrayList<>());
            if (!tooltip.isEmpty()) {
               tooltip.addAll(this.altar.drawing.tooltipText);
            } else {
               tooltip = this.altar.drawing.tooltipText;
            }

            if (this.altar.drawing.tooltipStack != null && !this.altar.drawing.tooltipStack.isEmpty()) {
               String modId = HexereiUtil.getRegistryName(this.altar.drawing.tooltipStack.getItem()).getNamespace();
               String modName = PageDrawing.getModNameForModId(modId);
               MutableComponent modNameComponent = Component.translatable(modName);
               modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
               if (tooltip.isEmpty() || !((Component)tooltip.getLast()).getString().equals(modName)) {
                  tooltip.add(modNameComponent);
               }
            }

            pGuiGraphics.renderTooltip(
               this.font,
               tooltip,
               this.altar.drawing.tooltipStack != null && !this.altar.drawing.tooltipStack.isEmpty()
                  ? this.altar.drawing.tooltipStack.getTooltipImage()
                  : Optional.empty(),
               mouseX,
               mouseY
            );
         }

         pGuiGraphics.pose().popPose();
         this.altar.degreesSpunRender = degreesSpunRender;
         this.altar.degreesOpenedRender = degreesOpenedRender;
         this.altar.floppedPercent = floppedPercent;
         this.altar.degreesFloppedRender = degreesFloppedRender;
         Lighting.setupFor3DItems();
         super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
      }
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderTransparentBackground(guiGraphics);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      Vec2 left = this.getLeftCursor((int)mouseX, (int)mouseY);
      Vec2 right = this.getRightCursor((int)mouseX, (int)mouseY);
      if (this.altar.openedPercent < 0.15F
         && this.altar.drawing.releaseClick(this.altar, Minecraft.getInstance().player, left.x, left.y, right.x, right.y, PageDrawing.DrawingType.SCREEN)) {
         if (this.fromItem != null) {
            HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(this.hand, this.altar.currentBook));
         }

         return true;
      } else {
         MouseHandler handler = Minecraft.getInstance().mouseHandler;
         if (handler.mouseGrabbed) {
            handler.mouseGrabbed = false;
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, 0.0, 0.0);
         }

         return super.mouseReleased(mouseX, mouseY, button);
      }
   }

   public void onClose() {
      PageDrawing.clearFocusedWritableTextBox();
      if (this.fromItem != null) {
         HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(this.hand, this.altar.currentBook));
      }

      MouseHandler handler = Minecraft.getInstance().mouseHandler;
      if (handler.mouseGrabbed) {
         InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, 0.0, 0.0);
      }

      super.onClose();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      Vec2 left = this.getLeftCursor((int)mouseX, (int)mouseY);
      Vec2 right = this.getRightCursor((int)mouseX, (int)mouseY);
      if (!this.altar.currentBook.isOpened() && this.altar.openedPercent > 0.75F) {
         this.altar.currentBook = this.altar.currentBook.setOpened(true);
         if (this.fromItem != null) {
            HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(this.hand, this.altar.currentBook));
         }
      } else if (this.altar.openedPercent < 0.35F
         && this.altar.drawing.interactClick(this.altar, Minecraft.getInstance().player, left.x, left.y, right.x, right.y, PageDrawing.DrawingType.SCREEN)) {
         if (this.fromItem != null) {
            HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(this.hand, this.altar.currentBook));
         }

         boolean clicked = false;
         BookEntries bookEntries = BookManager.getBookEntries(this.altar.currentBook.getBook());
         if (bookEntries != null) {
            for (BookChapter bookChapter : bookEntries.chapterList) {
               for (BookPageEntry bookPageEntry : bookChapter.pages) {
                  BookPage page = BookManager.getBookPages(this.altar.currentBook.getBook(), ResourceLocation.parse(bookPageEntry.location));
                  if (page != null) {
                     for (BookEntity bookEntity : page.entityList) {
                        if (bookEntity.markedForUpdate && bookEntity.clicked) {
                           clicked = true;
                        }
                     }
                  }
               }
            }
         }

         MouseHandler handler = Minecraft.getInstance().mouseHandler;
         if (!handler.mouseGrabbed && clicked) {
            handler.mouseGrabbed = true;
            this.mouseGrabbedX = (float)handler.xpos;
            this.mouseGrabbedY = (float)handler.ypos;
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.mouseGrabbedX, this.mouseGrabbedY);
         }

         return true;
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public Component getTitle() {
      return super.getTitle();
   }

   private void onDone() {
      this.minecraft.setScreen(null);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      this.flippingFastO = this.flippingFast;
      if (this.altar.turnPage == -1) {
         this.flippingFast = HexereiUtil.moveTo(this.flippingFast, 1.0F, 0.025F + 0.1F * (1.0F - this.flippingFast));
      } else {
         this.flippingFast = HexereiUtil.moveTo(this.flippingFast, 0.0F, 0.05F + 0.1F * (1.0F - this.flippingFast));
      }

      if (this.fromItem != null) {
         this.altar.tickClient();
         this.altar.tickBook(this.fromItem, true);
      }

      if (!this.isValid()) {
         this.onDone();
      }
   }

   private boolean isValid() {
      return this.fromItem != null
         ? this.minecraft != null && this.minecraft.player != null && this.altar != null && this.altar.currentBook != null
         : this.minecraft != null && this.minecraft.player != null && this.altar != null && this.altar.currentBook != null && this.altar.openedPercent < 1.0F;
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
