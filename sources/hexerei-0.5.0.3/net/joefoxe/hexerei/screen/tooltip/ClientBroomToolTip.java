package net.joefoxe.hexerei.screen.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.util.HexereiTags;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ClientBroomToolTip implements HexereiBookTooltip {
   public static final ResourceLocation TEXTURE_LOCATION = HexereiUtil.getResource("textures/gui/broom_tooltip_inventory.png");
   private final ItemStackHandler handler;
   private final ItemStack self;
   public Font font;
   public MutableComponent shift_down;
   public MutableComponent shift_up;

   public ClientBroomToolTip(BroomItem.BroomItemToolTip tooltip) {
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
      return (!Screen.hasShiftDown() ? 9 / 2 : (!this.isEmpty() ? this.gridSizeY() * 20 + 2 + 8 : 0)) + this.getHeightOffset();
   }

   public int getHeightOffset() {
      return (int)((9 + 1) * 3.6);
   }

   public boolean isEmpty() {
      boolean empty = true;

      for (int i = 2; i < this.handler.getSlots(); i++) {
         if (!this.handler.getStackInSlot(i).isEmpty()) {
            empty = false;
            break;
         }
      }

      return empty;
   }

   public int getWidth(Font font) {
      if (Screen.hasShiftDown()) {
         return this.isEmpty() ? 82 : this.gridSizeX() * 18 + 2 + 10;
      } else {
         return 82;
      }
   }

   public void renderImage(Font p_194042_, int p_194043_, int p_194044_, GuiGraphics pGuiGraphics) {
      int k = 0;

      for (int x = 0; x < this.gridSizeX() && k != 3; x++) {
         int j1 = p_194043_ + x * 26 + 1 + 3;
         int k1 = p_194044_ + 1 + 5;
         this.renderSlotTop(j1, k1 + this.getHeightOffset() - 28, k, true, p_194042_, pGuiGraphics);
         k++;
      }

      if (Screen.hasShiftDown() && !this.isEmpty()) {
         int i = this.gridSizeX();
         int j = this.gridSizeY();

         for (int y = 0; y < this.gridSizeY(); y++) {
            for (int x = 0; x < this.gridSizeX() && k != 30; x++) {
               int j1 = p_194043_ + x * 18 + 1 + 5;
               int k1 = p_194044_ + y * 18 + 1 + 5;
               this.renderSlot(j1, k1 + this.getHeightOffset(), k, true, p_194042_, pGuiGraphics);
               k++;
            }
         }

         if (!this.handler.getStackInSlot(1).isEmpty()) {
            this.drawBorder(p_194043_, p_194044_ + this.getHeightOffset(), i, j, pGuiGraphics, 0);
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
      VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION));
      int k = 0;

      for (int x = 0; x < this.gridSizeX() && k != 3; x++) {
         int j1 = p_194043_ + x * 26 + 1 + 3;
         int k1 = p_194044_ + 1 + 5;
         this.renderSlotTop(bufferSource, buffer, j1, k1 + this.getHeightOffset() - 28, k, p_194042_, matrixStack, p_194046_, z, overlay, light);
         k++;
      }

      k = 0;

      for (int x = 0; x < this.gridSizeX() && k != 3; x++) {
         int j1 = p_194043_ + x * 26 + 1 + 3;
         int k1 = p_194044_ + 1 + 5;
         this.renderSlotItemDecorations(bufferSource, buffer, j1, k1 + this.getHeightOffset() - 28, k, p_194042_, matrixStack, p_194046_, z, overlay, light);
         k++;
      }

      k = 0;

      for (int x = 0; x < this.gridSizeX() && k != 3; x++) {
         int j1 = p_194043_ + x * 26 + 1 + 3;
         int k1 = p_194044_ + 1 + 5;
         this.renderSlotItemCount(bufferSource, buffer, j1, k1 + this.getHeightOffset() - 28, k, p_194042_, matrixStack, p_194046_, z, overlay, light);
         k++;
      }

      if (Screen.hasShiftDown() && !this.isEmpty()) {
         int i = this.gridSizeX();
         int j = this.gridSizeY();

         for (int y = 0; y < this.gridSizeY(); y++) {
            for (int x = 0; x < this.gridSizeX() && k != 30; x++) {
               int j1 = p_194043_ + x * 18 + 1 + 5;
               int k1 = p_194044_ + y * 18 + 1 + 5;
               this.renderSlot(bufferSource, buffer, j1, k1 + this.getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
               k++;
            }
         }

         if (!this.handler.getStackInSlot(1).isEmpty()) {
            this.drawBorder(buffer, p_194043_, p_194044_ + this.getHeightOffset(), i, j, matrixStack, z, overlay, light);
         }

         k = 3;

         for (int y = 0; y < this.gridSizeY(); y++) {
            for (int x = 0; x < this.gridSizeX() && k != 30; x++) {
               int j1 = p_194043_ + x * 18 + 1 + 5;
               int k1 = p_194044_ + y * 18 + 1 + 5;
               this.renderSlotItemDecorations(bufferSource, buffer, j1, k1 + this.getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
               k++;
            }
         }

         k = 3;

         for (int y = 0; y < this.gridSizeY(); y++) {
            for (int x = 0; x < this.gridSizeX() && k != 30; x++) {
               int j1 = p_194043_ + x * 18 + 1 + 5;
               int k1 = p_194044_ + y * 18 + 1 + 5;
               this.renderSlotItemCount(bufferSource, buffer, j1, k1 + this.getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
               k++;
            }
         }

         k = 3;

         for (int y = 0; y < this.gridSizeY(); y++) {
            for (int x = 0; x < this.gridSizeX() && k != 30; x++) {
               int j1 = p_194043_ + x * 18 + 1 + 5;
               int k1 = p_194044_ + y * 18 + 1 + 5;
               this.renderSlotItem(bufferSource, buffer, j1, k1 + this.getHeightOffset(), k, p_194042_, matrixStack, p_194046_, z, overlay, light);
               k++;
            }
         }
      }

      k = 0;

      for (int x = 0; x < this.gridSizeX() && k != 3; x++) {
         int j1 = p_194043_ + x * 26 + 1 + 3;
         int k1 = p_194044_ + 1 + 5;
         this.renderSlotItem(bufferSource, buffer, j1, k1 + this.getHeightOffset() - 28, k, p_194042_, matrixStack, p_194046_, z, overlay, light);
         k++;
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

   private void drawFont(GuiGraphics guiGraphics, MutableComponent component, float x, float y, int color) {
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(x, y, 1.0F);
      guiGraphics.drawString(Minecraft.getInstance().font, component, 0, 0, color, true);
      guiGraphics.pose().popPose();
   }

   private void renderSlot(int p_194027_, int p_194028_, int slot, boolean isGui, Font p_194031_, GuiGraphics guiGraphics) {
      ItemStack itemstack = this.handler.getStackInSlot(slot);
      if (itemstack.isEmpty()) {
         this.blit(guiGraphics, p_194027_, p_194028_, 1, ClientBroomToolTip.Texture.BLOCKED_SLOT);
      } else {
         this.blit(guiGraphics, p_194027_, p_194028_, 1, ClientBroomToolTip.Texture.SLOT);
      }

      if (isGui) {
         guiGraphics.renderItem(itemstack, p_194027_ + 1, p_194028_ + 1, slot);
         guiGraphics.renderItemDecorations(p_194031_, itemstack, p_194027_ + 1, p_194028_ + 1);
      }
   }

   private void renderSlotTop(int p_194027_, int p_194028_, int slot, boolean isGui, Font p_194031_, GuiGraphics guiGraphics) {
      ItemStack itemstack = this.handler.getStackInSlot(slot);
      if (slot == 0) {
         this.blit(guiGraphics, p_194027_ - 4, p_194028_ - 4, 1, ClientBroomToolTip.Texture.MISC_SLOT);
      }

      if (slot == 1) {
         this.blit(guiGraphics, p_194027_ - 4, p_194028_ - 4, 1, ClientBroomToolTip.Texture.SATCHEL_SLOT);
      }

      if (slot == 2) {
         this.blit(guiGraphics, p_194027_ - 4, p_194028_ - 4, 1, ClientBroomToolTip.Texture.BRUSH_SLOT);
         CustomData customData = (CustomData)this.self.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
         if (!customData.contains("floatMode") && isGui) {
            guiGraphics.renderItem(new ItemStack((ItemLike)ModItems.BROOM_BRUSH.get()), p_194027_ + 1, p_194028_ + 1, slot);
            guiGraphics.renderItemDecorations(p_194031_, new ItemStack((ItemLike)ModItems.BROOM_BRUSH.get()), p_194027_ + 1, p_194028_ + 1);
         }
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
         this.blit(matrixStack, buffer, xIn, yIn, 0, ClientBroomToolTip.Texture.BLOCKED_SLOT, overlay, light);
      } else {
         this.blit(matrixStack, buffer, xIn, yIn, 0, ClientBroomToolTip.Texture.SLOT, overlay, light);
      }

      matrixStack.popPose();
      RenderSystem.enableDepthTest();
   }

   private void renderSlotTop(
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
      if (slot == 0) {
         this.blit(matrixStack, buffer, xIn - 4, yIn - 4, 0, ClientBroomToolTip.Texture.MISC_SLOT, overlay, light);
      }

      if (slot == 1) {
         this.blit(matrixStack, buffer, xIn - 4, yIn - 4, 0, ClientBroomToolTip.Texture.SATCHEL_SLOT, overlay, light);
      }

      if (slot == 2) {
         this.blit(matrixStack, buffer, xIn - 4, yIn - 4, 0, ClientBroomToolTip.Texture.BRUSH_SLOT, overlay, light);
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
      CustomData customData = (CustomData)this.self.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      if (!customData.contains("floatMode") && slot == 2) {
         PageDrawing.renderGuiItem(bufferSource, p_194031_, new ItemStack((ItemLike)ModItems.BROOM_BRUSH.get()), matrixStack, xIn, yIn, overlay, light);
      }

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

   private void drawBorder(int p_194020_, int p_194021_, int p_194022_, int p_194023_, GuiGraphics guiGraphics, int z) {
      this.blit(guiGraphics, p_194020_ + 5, p_194021_ + 5, z - 1, ClientBroomToolTip.Texture.BORDER_CORNER_TOP);
      this.blit(guiGraphics, p_194020_ + p_194022_ * 18 + 5, p_194021_ + 5, z - 1, ClientBroomToolTip.Texture.BORDER_CORNER_TOP);
      this.blit(guiGraphics, p_194020_, p_194021_ + 3, z, ClientBroomToolTip.Texture.THICK_BORDER_VERTICAL);
      this.blit(guiGraphics, p_194020_ + p_194022_ * 18 + 6, p_194021_ + 3, z, ClientBroomToolTip.Texture.THICK_BORDER_VERTICAL);

      for (int j = 0; j < p_194023_; j++) {
         this.blit(guiGraphics, p_194020_ + 5, p_194021_ + 5 + j * 18 - 1, z, ClientBroomToolTip.Texture.BORDER_VERTICAL);
         this.blit(guiGraphics, p_194020_ + p_194022_ * 18 + 5, p_194021_ + 5 + j * 18, z, ClientBroomToolTip.Texture.BORDER_VERTICAL);
         this.blit(guiGraphics, p_194020_, p_194021_ + 6 + j * 18, z, ClientBroomToolTip.Texture.THICK_BORDER_VERTICAL);
         this.blit(guiGraphics, p_194020_ + p_194022_ * 18 + 6, p_194021_ + 6 + j * 18, z, ClientBroomToolTip.Texture.THICK_BORDER_VERTICAL);
      }

      this.blit(guiGraphics, p_194020_ + 1 + 4, p_194021_, z, ClientBroomToolTip.Texture.THICK_BORDER_HORIZONTAL);
      this.blit(guiGraphics, p_194020_ + 1 + 4, p_194021_ + 6 + p_194023_ * 18, z, ClientBroomToolTip.Texture.THICK_BORDER_HORIZONTAL);

      for (int i = 0; i < p_194022_; i++) {
         this.blit(guiGraphics, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5, z, ClientBroomToolTip.Texture.BORDER_HORIZONTAL_TOP);
         this.blit(guiGraphics, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5 + p_194023_ * 18, z, ClientBroomToolTip.Texture.BORDER_HORIZONTAL_BOTTOM);
         this.blit(guiGraphics, p_194020_ + 1 + i * 18 + 5, p_194021_, z, ClientBroomToolTip.Texture.THICK_BORDER_HORIZONTAL);
         this.blit(guiGraphics, p_194020_ + 1 + i * 18 + 5, p_194021_ + 6 + p_194023_ * 18, z, ClientBroomToolTip.Texture.THICK_BORDER_HORIZONTAL);
      }

      this.blit(guiGraphics, p_194020_, p_194021_, z, ClientBroomToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT);
      this.blit(guiGraphics, p_194020_ + 162 + 6, p_194021_ + 18 * this.gridSizeY() + 6, z, ClientBroomToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT);
      this.blit(guiGraphics, p_194020_, p_194021_ + 18 * this.gridSizeY() + 6, z, ClientBroomToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT);
      this.blit(guiGraphics, p_194020_ + 18 * this.gridSizeX() + 6, p_194021_, z, ClientBroomToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT);
   }

   private void drawBorder(
      VertexConsumer buffer, int p_194020_, int p_194021_, int p_194022_, int p_194023_, PoseStack p_194024_, int z, int overlay, int light
   ) {
      this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + 5, z - 1, ClientBroomToolTip.Texture.BORDER_CORNER_TOP, overlay, light);
      this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 5, p_194021_ + 5, z - 1, ClientBroomToolTip.Texture.BORDER_CORNER_TOP, overlay, light);
      this.blit(p_194024_, buffer, p_194020_, p_194021_ + 3, z, ClientBroomToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light);
      this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 6, p_194021_ + 3, z, ClientBroomToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light);

      for (int j = 0; j < p_194023_; j++) {
         this.blit(p_194024_, buffer, p_194020_ + 5, p_194021_ + 5 + j * 18 - 1, z, ClientBroomToolTip.Texture.BORDER_VERTICAL, overlay, light);
         this.blit(p_194024_, buffer, p_194020_ + p_194022_ * 18 + 5, p_194021_ + 5 + j * 18, z, ClientBroomToolTip.Texture.BORDER_VERTICAL, overlay, light);
         this.blit(p_194024_, buffer, p_194020_, p_194021_ + 6 + j * 18, z, ClientBroomToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light);
         this.blit(
            p_194024_, buffer, p_194020_ + p_194022_ * 18 + 6, p_194021_ + 6 + j * 18, z, ClientBroomToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light
         );
      }

      this.blit(p_194024_, buffer, p_194020_ + 1 + 4, p_194021_, z, ClientBroomToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light);
      this.blit(p_194024_, buffer, p_194020_ + 1 + 4, p_194021_ + 6 + p_194023_ * 18, z, ClientBroomToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light);

      for (int i = 0; i < p_194022_; i++) {
         this.blit(p_194024_, buffer, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5, z, ClientBroomToolTip.Texture.BORDER_HORIZONTAL_TOP, overlay, light);
         this.blit(
            p_194024_,
            buffer,
            p_194020_ + 1 + i * 18 + 5,
            p_194021_ + 5 + p_194023_ * 18,
            z,
            ClientBroomToolTip.Texture.BORDER_HORIZONTAL_BOTTOM,
            overlay,
            light
         );
         this.blit(p_194024_, buffer, p_194020_ + 1 + i * 18 + 5, p_194021_, z, ClientBroomToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light);
         this.blit(
            p_194024_,
            buffer,
            p_194020_ + 1 + i * 18 + 5,
            p_194021_ + 6 + p_194023_ * 18,
            z,
            ClientBroomToolTip.Texture.THICK_BORDER_HORIZONTAL,
            overlay,
            light
         );
      }

      this.blit(p_194024_, buffer, p_194020_, p_194021_, z - 1, ClientBroomToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT, overlay, light);
      this.blit(
         p_194024_,
         buffer,
         p_194020_ + 162 + 6,
         p_194021_ + 18 * this.gridSizeY() + 6,
         z - 1,
         ClientBroomToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT,
         overlay,
         light
      );
      this.blit(
         p_194024_, buffer, p_194020_, p_194021_ + 18 * this.gridSizeY() + 6, z - 1, ClientBroomToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT, overlay, light
      );
      this.blit(
         p_194024_, buffer, p_194020_ + 18 * this.gridSizeX() + 6, p_194021_, z - 1, ClientBroomToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT, overlay, light
      );
   }

   private void blit(GuiGraphics guiGraphics, int p_194037_, int p_194038_, int p_194039_, ClientBroomToolTip.Texture p_194040_) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.blit(TEXTURE_LOCATION, p_194037_, p_194038_, p_194039_, p_194040_.x, p_194040_.y, p_194040_.w, p_194040_.h, 128, 128);
   }

   private void blit(PoseStack poseStack, VertexConsumer buffer, int xIn, int yIn, int zIn, ClientBroomToolTip.Texture texture, int overlay, int light) {
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
      ItemStack satchel = this.handler.getStackInSlot(1);
      if (satchel.is(HexereiTags.Items.SMALL_SATCHELS)) {
         return 1;
      } else if (satchel.is(HexereiTags.Items.MEDIUM_SATCHELS)) {
         return 2;
      } else {
         return satchel.is(HexereiTags.Items.LARGE_SATCHELS) ? 3 : 0;
      }
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
      THICK_BORDER_HORIZONTAL(0, 70, 18, 5),
      MISC_SLOT(30, 0, 26, 26),
      SATCHEL_SLOT(57, 0, 26, 26),
      BRUSH_SLOT(84, 0, 26, 26);

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
