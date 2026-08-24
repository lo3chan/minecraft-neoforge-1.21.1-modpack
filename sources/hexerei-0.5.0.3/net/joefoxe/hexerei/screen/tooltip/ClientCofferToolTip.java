package net.joefoxe.hexerei.screen.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ClientCofferToolTip implements HexereiBookTooltip {
   public static final ResourceLocation TEXTURE_LOCATION = HexereiUtil.getResource("textures/gui/coffer_tooltip_inventory.png");
   private final ItemStackHandler handler;
   private final ItemStack self;
   public Font font;
   public MutableComponent shift_down;
   public MutableComponent shift_up;

   public ClientCofferToolTip(CofferItem.CofferItemToolTip tooltip) {
      this.font = Minecraft.getInstance().font;
      this.shift_down = Component.translatable(
            "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
         )
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
      this.shift_up = Component.translatable(
            "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
         )
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
      this.handler = tooltip.handler();
      this.self = tooltip.self();
   }

   public int getHeight() {
      return (!Screen.hasShiftDown() ? 9 + 1 : (!this.isEmpty() ? this.gridSizeY() * 20 + 2 + 4 : 0)) + this.getHeightOffset();
   }

   public int getHeightOffset() {
      return (9 + 1) * (!Screen.hasShiftDown() ? 0 : 1);
   }

   public boolean isEmpty() {
      boolean empty = true;

      for (int i = 0; i < this.handler.getSlots(); i++) {
         if (!this.handler.getStackInSlot(i).isEmpty()) {
            empty = false;
            break;
         }
      }

      return empty;
   }

   public int getWidth(Font font) {
      if (Screen.hasShiftDown()) {
         return this.isEmpty() ? 0 : this.gridSizeX() * 18 + 2 + 10;
      } else {
         return 0;
      }
   }

   public void renderImage(Font p_194042_, int p_194043_, int p_194044_, GuiGraphics guiGraphics) {
      if (Screen.hasShiftDown() && !this.isEmpty()) {
         int i = this.gridSizeX();
         int j = this.gridSizeY();
         int k = 0;
         this.drawBorder(p_194043_, p_194044_ + this.getHeightOffset(), i, j, guiGraphics, 0);

         for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9 && k != 36; x++) {
               if (y <= 0 || y >= 4 || x <= 2 || x >= 6) {
                  int j1 = p_194043_ + x * 18 + 1 + 5;
                  int k1 = p_194044_ + y * 18 + 1 + 5;
                  this.renderSlot(j1, k1 + this.getHeightOffset(), k, true, p_194042_, guiGraphics);
                  k++;
               }
            }
         }
      }
   }

   @Override
   public void renderImage(
      Font p_194042_,
      MultiBufferSource bufferSource,
      int p_194043_,
      int p_194044_,
      PoseStack matrixStack,
      ItemRenderer p_194046_,
      int z,
      int overlay,
      int light
   ) {
      if (Screen.hasShiftDown() && !this.isEmpty()) {
         VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION));
         int i = this.gridSizeX();
         int j = this.gridSizeY();
         int k = 0;

         for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9 && k != 36; x++) {
               if (y <= 0 || y >= 4 || x <= 2 || x >= 6) {
                  int j1 = p_194043_ + x * 18 + 1 + 5;
                  int k1 = p_194044_ + y * 18 + 1 + 5;
                  this.renderSlot(bufferSource, buffer, j1, k1 + this.getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
                  k++;
               }
            }
         }

         this.drawBorder(buffer, p_194043_, p_194044_ + this.getHeightOffset(), i, j, matrixStack, z, overlay, light);
         k = 0;

         for (int y = 0; y < 5; y++) {
            for (int xx = 0; xx < 9 && k != 36; xx++) {
               if (y <= 0 || y >= 4 || xx <= 2 || xx >= 6) {
                  int j1 = p_194043_ + xx * 18 + 1 + 5;
                  int k1 = p_194044_ + y * 18 + 1 + 5;
                  this.renderSlotItemDecorations(bufferSource, buffer, j1, k1 + this.getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
                  k++;
               }
            }
         }

         k = 0;

         for (int y = 0; y < 5; y++) {
            for (int xxx = 0; xxx < 9 && k != 36; xxx++) {
               if (y <= 0 || y >= 4 || xxx <= 2 || xxx >= 6) {
                  int j1 = p_194043_ + xxx * 18 + 1 + 5;
                  int k1 = p_194044_ + y * 18 + 1 + 5;
                  this.renderSlotItemCount(bufferSource, buffer, j1, k1 + this.getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
                  k++;
               }
            }
         }

         k = 0;

         for (int y = 0; y < 5; y++) {
            for (int xxxx = 0; xxxx < 9 && k != 36; xxxx++) {
               if (y <= 0 || y >= 4 || xxxx <= 2 || xxxx >= 6) {
                  int j1 = p_194043_ + xxxx * 18 + 1 + 5;
                  int k1 = p_194044_ + y * 18 + 1 + 5;
                  this.renderSlotItem(bufferSource, buffer, j1, k1 + this.getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
                  k++;
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void renderText(Font p_169953_, int mouseX, int mouseY, Matrix4f lastpose, BufferSource buffer) {
      if (Screen.hasShiftDown()) {
         Minecraft.getInstance()
            .font
            .drawInBatch(this.shift_down.getVisualOrderText(), mouseX, mouseY, 16777215, true, lastpose, buffer, DisplayMode.NORMAL, 0, 15728880);
      } else {
         Minecraft.getInstance()
            .font
            .drawInBatch(this.shift_up.getVisualOrderText(), mouseX, mouseY, 16777215, true, lastpose, buffer, DisplayMode.NORMAL, 0, 15728880);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void renderText(Font p_169953_, int mouseX, int mouseY, Matrix4f lastpose, BufferSource buffer, int overlay, int light) {
      if (Screen.hasShiftDown()) {
         Minecraft.getInstance()
            .font
            .drawInBatch(this.shift_down.getVisualOrderText(), mouseX, mouseY, 16777215, false, lastpose, buffer, DisplayMode.NORMAL, 0, 15728880);
      } else {
         Minecraft.getInstance()
            .font
            .drawInBatch(this.shift_up.getVisualOrderText(), mouseX, mouseY, 16777215, false, lastpose, buffer, DisplayMode.NORMAL, 0, 15728880);
      }
   }

   private static int adjustColor(int p_92720_) {
      return (p_92720_ & -67108864) == 0 ? p_92720_ | 0xFF000000 : p_92720_;
   }

   private void renderSlot(int p_194027_, int p_194028_, int slot, boolean isGui, Font p_194031_, GuiGraphics guiGraphics) {
      ItemStack itemstack = this.handler.getStackInSlot(slot);
      if (itemstack.isEmpty()) {
         this.blit(guiGraphics, p_194027_, p_194028_, 1, ClientCofferToolTip.Texture.BLOCKED_SLOT);
      } else {
         this.blit(guiGraphics, p_194027_, p_194028_, 1, ClientCofferToolTip.Texture.SLOT);
      }

      if (isGui) {
         guiGraphics.renderItem(itemstack, p_194027_ + 1, p_194028_ + 1, slot);
         guiGraphics.renderItemDecorations(p_194031_, itemstack, p_194027_ + 1, p_194028_ + 1);
      }
   }

   private void renderSlot(
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int slot,
      Font p_194031_,
      PoseStack matrixStack,
      ItemRenderer p_194033_,
      int z,
      int overlay,
      int light
   ) {
      ItemStack itemstack = this.handler.getStackInSlot(slot);
      matrixStack.pushPose();
      matrixStack.scale(1.0F, 1.0F, 1.0E-4F);
      if (itemstack.isEmpty()) {
         this.blit(matrixStack, buffer, xIn, yIn, 0, ClientCofferToolTip.Texture.BLOCKED_SLOT, overlay, light);
      } else {
         this.blit(matrixStack, buffer, xIn, yIn, 0, ClientCofferToolTip.Texture.SLOT, overlay, light);
      }

      matrixStack.popPose();
      RenderSystem.enableDepthTest();
   }

   private void renderSlotItem(
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int slot,
      Font p_194031_,
      PoseStack matrixStack,
      ItemRenderer p_194033_,
      int z,
      int overlay,
      int light
   ) {
      ItemStack itemstack = this.handler.getStackInSlot(slot);
      PageDrawing.renderGuiItem(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);
      RenderSystem.enableDepthTest();
   }

   private void renderSlotItemCount(
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int slot,
      Font p_194031_,
      PoseStack matrixStack,
      ItemRenderer p_194033_,
      int z,
      int overlay,
      int light
   ) {
      ItemStack itemstack = this.handler.getStackInSlot(slot);
      PageDrawing.renderGuiItemCount(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);
      RenderSystem.enableDepthTest();
   }

   private void renderSlotItemDecorations(
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int slot,
      Font p_194031_,
      PoseStack matrixStack,
      ItemRenderer p_194033_,
      int z,
      int overlay,
      int light
   ) {
      ItemStack itemstack = this.handler.getStackInSlot(slot);
      PageDrawing.renderGuiItemDecorations(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);
      RenderSystem.enableDepthTest();
   }

   private void drawBorder(int xIn, int yIn, int p_194022_, int p_194023_, GuiGraphics guiGraphics, int z) {
      this.blit(guiGraphics, xIn + 5, yIn + 5, z, ClientCofferToolTip.Texture.BORDER_CORNER_TOP);
      this.blit(guiGraphics, xIn + p_194022_ * 18 + 5, yIn + 5, z, ClientCofferToolTip.Texture.BORDER_CORNER_TOP);
      this.blit(guiGraphics, xIn, yIn + 3, z, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL);
      this.blit(guiGraphics, xIn + p_194022_ * 18 + 6, yIn + 3, z, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL);

      for (int j = 0; j < p_194023_; j++) {
         this.blit(guiGraphics, xIn + 5, yIn + 5 + j * 18, z, ClientCofferToolTip.Texture.BORDER_VERTICAL);
         this.blit(guiGraphics, xIn + p_194022_ * 18 + 5, yIn + 5 + j * 18, z, ClientCofferToolTip.Texture.BORDER_VERTICAL);
         this.blit(guiGraphics, xIn, yIn + 6 + j * 18, z, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL);
         this.blit(guiGraphics, xIn + p_194022_ * 18 + 6, yIn + 6 + j * 18, z, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL);
         if (j > 0 && j < 4) {
            this.blit(guiGraphics, xIn + p_194022_ * 18 - 54 + 5, yIn + 5 + j * 18, z, ClientCofferToolTip.Texture.BORDER_VERTICAL);
            this.blit(guiGraphics, xIn + p_194022_ * 18 - 54 - 5 + 5, yIn + 5 + j * 17 + 3, z, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL);
            this.blit(guiGraphics, xIn + p_194022_ * 18 - 108 + 1 + 5, yIn + 5 + j * 17 + 3, z, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL);
         }
      }

      this.blit(guiGraphics, xIn + 1 + 4, yIn, z, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL);
      this.blit(guiGraphics, xIn + 1 + 4, yIn + 6 + p_194023_ * 18, z, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL);

      for (int i = 0; i < p_194022_; i++) {
         this.blit(guiGraphics, xIn + 1 + i * 18 + 5, yIn + 5, z, ClientCofferToolTip.Texture.BORDER_HORIZONTAL_TOP);
         this.blit(guiGraphics, xIn + 1 + i * 18 + 5, yIn + 5 + p_194023_ * 18, z, ClientCofferToolTip.Texture.BORDER_HORIZONTAL_BOTTOM);
         this.blit(guiGraphics, xIn + 1 + i * 18 + 5, yIn, z, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL);
         this.blit(guiGraphics, xIn + 1 + i * 18 + 5, yIn + 6 + p_194023_ * 18, z, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL);
         if (i > 2 && i < 6) {
            this.blit(guiGraphics, xIn + 1 + i * 18 + 5, yIn + 5 + 72, z, ClientCofferToolTip.Texture.BORDER_HORIZONTAL_TOP);
            this.blit(guiGraphics, xIn + 1 + i * 17 + 9, yIn + 5 + 72 - 5, z, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL);
            this.blit(guiGraphics, xIn + 1 + i * 17 + 9, yIn + 5 + 18 + 1, z, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL);
         }
      }

      this.blit(guiGraphics, xIn + 54 + 1 + 5, yIn + 5 + p_194023_ * 18 - 72 + 1, z, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT);
      this.blit(guiGraphics, xIn + 54 + 1 + 5, yIn + 5 + p_194023_ * 18 - 18 - 5, z, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT);
      this.blit(guiGraphics, xIn + 108 - 5 + 5, yIn + 5 + p_194023_ * 18 - 72 + 1, z, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT);
      this.blit(guiGraphics, xIn + 108 - 5 + 5, yIn + 5 + p_194023_ * 18 - 18 - 5, z, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT);
      this.blit(guiGraphics, xIn, yIn, z, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT);
      this.blit(guiGraphics, xIn + 162 + 6, yIn + 90 + 6, z, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT);
      this.blit(guiGraphics, xIn, yIn + 90 + 6, z, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT);
      this.blit(guiGraphics, xIn + 162 + 6, yIn, z, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT);
   }

   private void drawBorder(VertexConsumer buffer, int xIn, int yIn, int p_194022_, int p_194023_, PoseStack guiGraphics, int z, int overlay, int light) {
      this.blit(guiGraphics, buffer, xIn + 5, yIn + 5, z - 2, ClientCofferToolTip.Texture.BORDER_CORNER_TOP, overlay, light);
      this.blit(guiGraphics, buffer, xIn + p_194022_ * 18 + 5, yIn + 5, z - 2, ClientCofferToolTip.Texture.BORDER_CORNER_TOP, overlay, light);
      this.blit(guiGraphics, buffer, xIn, yIn + 3, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light);
      this.blit(guiGraphics, buffer, xIn + p_194022_ * 18 + 6, yIn + 3, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light);

      for (int j = 0; j < p_194023_; j++) {
         this.blit(guiGraphics, buffer, xIn + 5, yIn + 5 + j * 18, z, ClientCofferToolTip.Texture.BORDER_VERTICAL, overlay, light);
         this.blit(guiGraphics, buffer, xIn + p_194022_ * 18 + 5, yIn + 5 + j * 18, z, ClientCofferToolTip.Texture.BORDER_VERTICAL, overlay, light);
         this.blit(guiGraphics, buffer, xIn, yIn + 6 + j * 18, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light);
         this.blit(guiGraphics, buffer, xIn + p_194022_ * 18 + 6, yIn + 6 + j * 18, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light);
         if (j > 0 && j < 4) {
            this.blit(guiGraphics, buffer, xIn + p_194022_ * 18 - 54 + 5, yIn + 5 + j * 18, z, ClientCofferToolTip.Texture.BORDER_VERTICAL, overlay, light);
            this.blit(
               guiGraphics,
               buffer,
               xIn + p_194022_ * 18 - 54 - 5 + 5,
               yIn + 5 + j * 17 + 3,
               z - 1,
               ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL,
               overlay,
               light
            );
            this.blit(
               guiGraphics,
               buffer,
               xIn + p_194022_ * 18 - 108 + 1 + 5,
               yIn + 5 + j * 17 + 3,
               z - 1,
               ClientCofferToolTip.Texture.THICK_BORDER_VERTICAL,
               overlay,
               light
            );
         }
      }

      this.blit(guiGraphics, buffer, xIn + 1 + 4, yIn, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light);
      this.blit(guiGraphics, buffer, xIn + 1 + 4, yIn + 6 + p_194023_ * 18, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light);

      for (int i = 0; i < p_194022_; i++) {
         this.blit(guiGraphics, buffer, xIn + 1 + i * 18 + 5, yIn + 5, z - 1, ClientCofferToolTip.Texture.BORDER_HORIZONTAL_TOP, overlay, light);
         this.blit(
            guiGraphics, buffer, xIn + 1 + i * 18 + 5, yIn + 5 + p_194023_ * 18, z - 1, ClientCofferToolTip.Texture.BORDER_HORIZONTAL_BOTTOM, overlay, light
         );
         this.blit(guiGraphics, buffer, xIn + 1 + i * 18 + 5, yIn, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light);
         this.blit(
            guiGraphics, buffer, xIn + 1 + i * 18 + 5, yIn + 6 + p_194023_ * 18, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light
         );
         if (i > 2 && i < 6) {
            this.blit(guiGraphics, buffer, xIn + 1 + i * 18 + 5, yIn + 5 + 72, z, ClientCofferToolTip.Texture.BORDER_HORIZONTAL_TOP, overlay, light);
            this.blit(guiGraphics, buffer, xIn + 1 + i * 17 + 9, yIn + 5 + 72 - 5, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light);
            this.blit(guiGraphics, buffer, xIn + 1 + i * 17 + 9, yIn + 5 + 18 + 1, z - 1, ClientCofferToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light);
         }
      }

      this.blit(
         guiGraphics,
         buffer,
         xIn + 54 + 1 + 5,
         yIn + 5 + p_194023_ * 18 - 72 + 1,
         z - 2,
         ClientCofferToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT,
         overlay,
         light
      );
      this.blit(
         guiGraphics,
         buffer,
         xIn + 54 + 1 + 5,
         yIn + 5 + p_194023_ * 18 - 18 - 5,
         z - 2,
         ClientCofferToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT,
         overlay,
         light
      );
      this.blit(
         guiGraphics,
         buffer,
         xIn + 108 - 5 + 5,
         yIn + 5 + p_194023_ * 18 - 72 + 1,
         z - 2,
         ClientCofferToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT,
         overlay,
         light
      );
      this.blit(
         guiGraphics,
         buffer,
         xIn + 108 - 5 + 5,
         yIn + 5 + p_194023_ * 18 - 18 - 5,
         z - 2,
         ClientCofferToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT,
         overlay,
         light
      );
      this.blit(guiGraphics, buffer, xIn, yIn, z - 2, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT, overlay, light);
      this.blit(guiGraphics, buffer, xIn + 162 + 6, yIn + 90 + 6, z - 2, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT, overlay, light);
      this.blit(guiGraphics, buffer, xIn, yIn + 90 + 6, z - 2, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT, overlay, light);
      this.blit(guiGraphics, buffer, xIn + 162 + 6, yIn, z - 2, ClientCofferToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT, overlay, light);
   }

   private void blit(GuiGraphics guiGraphics, int p_194037_, int p_194038_, int p_194039_, ClientCofferToolTip.Texture p_194040_) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.blit(TEXTURE_LOCATION, p_194037_, p_194038_, p_194039_, p_194040_.x, p_194040_.y, p_194040_.w, p_194040_.h, 128, 128);
   }

   private void blit(PoseStack poseStack, VertexConsumer buffer, int xIn, int yIn, int zIn, ClientCofferToolTip.Texture texture, int overlay, int light) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
      blit(poseStack, buffer, xIn, yIn, zIn, texture.x, texture.y, texture.w, texture.h, 128, 128, overlay, light);
   }

   public static void blit(
      PoseStack poseStack,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int zIn,
      float p_93148_,
      float p_93149_,
      int p_93150_,
      int p_93151_,
      int p_93152_,
      int p_93153_,
      int overlay,
      int light
   ) {
      innerBlit(poseStack, buffer, xIn, xIn + p_93150_, yIn, yIn + p_93151_, zIn, p_93150_, p_93151_, p_93148_, p_93149_, p_93152_, p_93153_, overlay, light);
   }

   private static void innerBlit(
      PoseStack p_93188_,
      VertexConsumer buffer,
      int p_93189_,
      int p_93190_,
      int p_93191_,
      int p_93192_,
      int p_93193_,
      int p_93194_,
      int p_93195_,
      float p_93196_,
      float p_93197_,
      int p_93198_,
      int p_93199_,
      int overlay,
      int light
   ) {
      innerBlit(
         p_93188_,
         buffer,
         p_93189_,
         p_93190_,
         p_93191_,
         p_93192_,
         p_93193_,
         (p_93196_ + 0.0F) / p_93198_,
         (p_93196_ + p_93194_) / p_93198_,
         (p_93197_ + 0.0F) / p_93199_,
         (p_93197_ + p_93195_) / p_93199_,
         overlay,
         light
      );
   }

   private static void innerBlit(
      PoseStack poseStack,
      VertexConsumer buffer,
      int p_93114_,
      int p_93115_,
      int p_93116_,
      int p_93117_,
      int p_93118_,
      float p_93119_,
      float p_93120_,
      float p_93121_,
      float p_93122_,
      int overlay,
      int light
   ) {
      poseStack.pushPose();
      poseStack.translate(0.0F, 0.0F, p_93118_);
      buffer.addVertex(poseStack.last().pose(), p_93114_, p_93117_, p_93118_)
         .setColor(255, 255, 255, 255)
         .setUv(p_93119_, p_93122_)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      buffer.addVertex(poseStack.last().pose(), p_93115_, p_93117_, p_93118_)
         .setColor(255, 255, 255, 255)
         .setUv(p_93120_, p_93122_)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      buffer.addVertex(poseStack.last().pose(), p_93115_, p_93116_, p_93118_)
         .setColor(255, 255, 255, 255)
         .setUv(p_93120_, p_93121_)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      buffer.addVertex(poseStack.last().pose(), p_93114_, p_93116_, p_93118_)
         .setColor(255, 255, 255, 255)
         .setUv(p_93119_, p_93121_)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      poseStack.popPose();
   }

   private int gridSizeX() {
      return 9;
   }

   private int gridSizeY() {
      return 5;
   }

   @OnlyIn(Dist.CLIENT)
   static enum Texture {
      SLOT(0, 0, 18, 18),
      BLOCKED_SLOT(0, 40, 18, 18),
      BORDER_VERTICAL(0, 20, 1, 20),
      BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
      BORDER_HORIZONTAL_BOTTOM(0, 58, 18, 1),
      BORDER_CORNER_TOP(0, 20, 1, 1),
      BORDER_CORNER_BOTTOM(0, 60, 1, 1),
      THICK_BORDER_CORNER_TOP_LEFT(0, 60, 5, 5),
      THICK_BORDER_CORNER_TOP_RIGHT(5, 60, 5, 5),
      THICK_BORDER_CORNER_BOTTOM_LEFT(0, 65, 5, 5),
      THICK_BORDER_CORNER_BOTTOM_RIGHT(5, 65, 5, 5),
      THICK_BORDER_VERTICAL(0, 75, 5, 18),
      THICK_BORDER_HORIZONTAL(0, 70, 18, 5);

      public final int x;
      public final int y;
      public final int w;
      public final int h;

      private Texture(int p_169928_, int p_169929_, int p_169930_, int p_169931_) {
         this.x = p_169928_;
         this.y = p_169929_;
         this.w = p_169930_;
         this.h = p_169931_;
      }
   }
}
