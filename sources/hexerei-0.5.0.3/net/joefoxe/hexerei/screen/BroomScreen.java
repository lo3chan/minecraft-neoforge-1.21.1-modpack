package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.container.BroomContainer;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BroomScreen extends AbstractContainerScreen<BroomContainer> {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/broom_gui.png");
   private final ResourceLocation INVENTORY = HexereiUtil.getResource("textures/gui/inventory.png");
   public final BroomEntity broomEntity;
   public float dropdownOffset = 0.0F;
   public float dropdownOffsetO = 0.0F;
   public int offset = 0;
   public boolean dropdownClicked = false;

   public BroomScreen(BroomContainer broomContainer, Inventory inv, Component titleIn) {
      super(broomContainer, inv, titleIn);
      this.broomEntity = broomContainer.broomEntity;
      this.titleLabelY = -33;
      this.titleLabelX = 4;
      this.inventoryLabelY = 60;
   }

   protected void containerTick() {
      super.containerTick();
      this.dropdownOffsetO = this.dropdownOffset;
      if (this.dropdownClicked) {
         this.dropdownOffset = HexereiUtil.moveTo(this.dropdownOffset, 58.0F, 4.0F);
      } else {
         this.dropdownOffset = HexereiUtil.moveTo(this.dropdownOffset, 15.0F, 4.0F);
      }
   }

   public double easeInOutCubic(float x) {
      double c1 = 1.70158;
      double c2 = c1 * 1.525;
      return x < 0.5
         ? Math.pow(2.0F * x, 2.0) * ((c2 + 1.0) * 2.0 * x - c2) / 2.0
         : (Math.pow(2.0F * x - 2.0F, 2.0) * ((c2 + 1.0) * (x * 2.0F - 2.0F) + c2) + 2.0) / 2.0;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
      this.renderButtonTooltip(guiGraphics, mouseX, mouseY);
   }

   public Component getTitle() {
      return super.getTitle();
   }

   public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
   }

   public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      int offset = 0;
      if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS)) {
         offset = 21;
      }

      if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
         offset = 42;
      }

      if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS)) {
         offset = 63;
      }

      List<Component> components = new ArrayList<>();
      if (this.isHovering(mouseX, mouseY, 188.25, 89 + offset - 34, 18.0, 18.0)) {
         components.add(Component.translatable("tooltip.hexerei.broom_settings"));
         guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
      } else if (this.dropdownOffset > 29.0F) {
         if (this.isHovering(mouseX, mouseY, 188.25, 88 + offset + (int)this.dropdownOffset - 34, 18.0, 18.0)) {
            components.add(Component.translatable("tooltip.hexerei.broom_float_mode_off"));
            if (Screen.hasShiftDown()) {
               components.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               components.add(Component.translatable("tooltip.hexerei.broom_float_mode_off_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               components.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }

            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
         }

         if (this.isHovering(mouseX, mouseY, 188.25, 60 + offset + (int)this.dropdownOffset - 34, 18.0, 18.0)) {
            components.add(Component.translatable("tooltip.hexerei.broom_float_mode_on"));
            if (Screen.hasShiftDown()) {
               components.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               components.add(Component.translatable("tooltip.hexerei.broom_float_mode_on_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               components.add(Component.translatable("tooltip.hexerei.broom_float_mode_on_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               components.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }

            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
         }
      }
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.GUI);
      int ddOffset = (int)(this.easeInOutCubic(Mth.lerp(partialTicks, this.dropdownOffsetO, this.dropdownOffset) / 58.0F) * 58.0);
      int i = this.leftPos;
      int j = this.topPos;
      this.offset = 0;
      if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS)) {
         this.offset = 21;
      }

      if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
         this.offset = 42;
      }

      if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS)) {
         this.offset = 63;
      }

      this.inventoryLabelY = 94 + this.offset - 34;
      guiGraphics.blit(this.GUI, i + 184, j + 55 + this.offset + ddOffset - 34 - 5, 230, 164, 26, 63);
      if (((BroomContainer)this.menu).getFloatMode()) {
         guiGraphics.blit(this.GUI, i + 188, j + 60 + this.offset + ddOffset - 34, 238, 106, 18, 18);
      } else {
         guiGraphics.blit(this.GUI, i + 188, j + 88 + this.offset + ddOffset - 34, 238, 70, 18, 18);
      }

      guiGraphics.blit(this.GUI, i, j - 3 - 34, 0, 0, 214, 82);
      guiGraphics.blit(this.GUI, i, j + 79 + this.offset - 34, 0, 82, 214, 34);
      if (!this.broomEntity.getModule(BroomEntity.BroomSlot.MISC).isEmpty()) {
         guiGraphics.blit(this.GUI, i + 37, j + 47 - 34, 235, 31, 16, 16);
      }

      if (!this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).isEmpty()) {
         guiGraphics.blit(this.GUI, i + 99, j + 47 - 34, 235, 31, 16, 16);
      }

      if (!this.broomEntity.getModule(BroomEntity.BroomSlot.BRUSH).isEmpty()) {
         guiGraphics.blit(this.GUI, i + 160, j + 47 - 34, 235, 31, 16, 16);
      }

      if (!this.broomEntity.isEnder()) {
         ItemStack satchel = this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL);
         if (satchel.is(HexereiTags.Items.SMALL_SATCHELS)) {
            guiGraphics.blit(this.GUI, i, j + 79 - 34, 0, 116, 214, 21);
         }

         if (satchel.is(HexereiTags.Items.MEDIUM_SATCHELS)) {
            guiGraphics.blit(this.GUI, i, j + 79 - 34, 0, 116, 214, 21);
            guiGraphics.blit(this.GUI, i, j + 79 + 21 - 34, 0, 116, 214, 21);
         }

         if (satchel.is(HexereiTags.Items.LARGE_SATCHELS)) {
            guiGraphics.blit(this.GUI, i, j + 79 - 34, 0, 116, 214, 21);
            guiGraphics.blit(this.GUI, i, j + 79 + 21 - 34, 0, 116, 214, 21);
            guiGraphics.blit(this.GUI, i, j + 79 + 42 - 34, 0, 116, 214, 21);
         }
      } else {
         guiGraphics.blit(this.GUI, i, j + 79 - 34, 0, 200, 214, 21);
         guiGraphics.blit(this.GUI, i, j + 79 + 21 - 34, 0, 200, 214, 21);
         guiGraphics.blit(this.GUI, i, j + 79 + 42 - 34, 0, 200, 214, 21);
         guiGraphics.blit(this.GUI, i, j + 79 + 42 - 34 + 21, 0, 221, 214, 5);
         guiGraphics.blit(this.GUI, i, j + 75 - 34, 0, 137, 214, 72);
      }

      guiGraphics.blit(this.GUI, i + 94, j - 30 - 34, 230, 0, 26, 26);
      if (this.dropdownClicked) {
         guiGraphics.blit(this.GUI, i + 188, j + 89 + this.offset - 34, 238, 124, 18, 18);
      }

      RenderSystem.setShaderTexture(0, this.INVENTORY);
      guiGraphics.blit(this.INVENTORY, i + 3, j + 88 + this.offset - 34, 0, 0, 176, 100);
      RenderSystem.setShaderTexture(0, this.GUI);
      int extraBrush = this.broomEntity.getExtraBrush();
      if (this.broomEntity.isReplacer() && extraBrush != -1) {
         guiGraphics.blit(this.GUI, i + 12 + 21 * ((extraBrush - 3) % 9), j + 21 * ((extraBrush - 3) / 9) + 79 - 34, 234, 142, 22, 22);
      }

      Minecraft minecraft = Minecraft.getInstance();
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      RenderSystem.disableDepthTest();
      guiGraphics.renderItem(this.broomEntity.getPickResult(), this.leftPos + 99, this.topPos - 25 - 34);
      MutableComponent misc = Component.translatable("tooltip.hexerei.broom_misc");
      MutableComponent satchelx = Component.translatable("tooltip.hexerei.broom_satchel");
      MutableComponent brush = Component.translatable("tooltip.hexerei.broom_brush");
      this.drawFont(guiGraphics, misc, this.leftPos + 34, this.topPos + 29 - 34, -10461088, false);
      this.drawFont(guiGraphics, satchelx, this.leftPos + 89, this.topPos + 29 - 34, -10461088, false);
      this.drawFont(guiGraphics, brush, this.leftPos + 154, this.topPos + 29 - 34, -10461088, false);
   }

   private void drawFont(GuiGraphics guiGraphics, MutableComponent component, float x, float y, int color, boolean shadow) {
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(x, y, 1.0F);
      guiGraphics.drawString(this.minecraft.font, component, 0, 0, color, shadow);
      guiGraphics.pose().popPose();
   }

   public boolean mouseClicked(double x, double y, int button) {
      boolean mouseClicked = super.mouseClicked(x, y, button);
      if (this.dropdownOffset > 29.0F) {
         if (x > this.leftPos + 188.25F
            && x < this.leftPos + 188.25F + 18.0F
            && y > this.topPos + 88 + this.offset + (int)this.dropdownOffset - 34
            && y < this.topPos + 88 + this.offset + (int)this.dropdownOffset + 18 - 34) {
            ((BroomContainer)this.menu).setFloatMode(false);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

         if (x > this.leftPos + 188.25F
            && x < this.leftPos + 188.25F + 18.0F
            && y > this.topPos + 60 + this.offset + (int)this.dropdownOffset - 34
            && y < this.topPos + 60 + this.offset + (int)this.dropdownOffset + 18 - 34) {
            ((BroomContainer)this.menu).setFloatMode(true);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      }

      if (x > this.leftPos + 188.25F
         && x < this.leftPos + 188.25F + 18.0F
         && y > this.topPos + 89 + this.offset - 34
         && y < this.topPos + 89 + 18 + this.offset - 34) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.dropdownClicked = !this.dropdownClicked;
      }

      return mouseClicked;
   }
}
