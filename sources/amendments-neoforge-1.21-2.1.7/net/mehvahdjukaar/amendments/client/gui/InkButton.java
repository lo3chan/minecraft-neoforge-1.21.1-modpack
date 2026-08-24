package net.mehvahdjukaar.amendments.client.gui;

import java.util.Arrays;
import java.util.Locale;
import net.mehvahdjukaar.amendments.Amendments;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class InkButton extends AbstractWidget {
   protected static final ResourceLocation[] textures = Arrays.stream(InkButton.Ink.values())
      .map(t -> Amendments.res("textures/gui/ink_well/" + t.name().toLowerCase(Locale.ROOT) + ".png"))
      .toArray(ResourceLocation[]::new);
   private final Runnable clickCallback;
   private int type = 0;

   public InkButton(LecternBookEditScreen screen) {
      super(screen.width / 2 - 130, 90, 52, 50, Component.empty());
      LecternBookEditScreen.hasShiftDown();
      this.refreshTooltip();
      this.clickCallback = screen::onInkClicked;
   }

   private void refreshTooltip() {
      this.setTooltip(Tooltip.create(Component.translatable("gui.amendments.ink." + this.getType().name().toLowerCase(Locale.ROOT))));
   }

   public InkButton.Ink getType() {
      return InkButton.Ink.values()[this.type];
   }

   public void onClick(double mouseX, double mouseY) {
      int length = InkButton.Ink.values().length;
      if (Screen.hasShiftDown()) {
         this.type = (length + this.type - 1) % length;
      } else {
         this.type = (this.type + 1) % length;
      }

      this.refreshTooltip();
      this.clickCallback.run();
   }

   public void onClick(double mouseX, double mouseY, int button) {
      this.onClick(mouseX, mouseY);
   }

   protected boolean isValidClickButton(int button) {
      return super.isValidClickButton(button) || button == 1;
   }

   public ChatFormatting getChatFormatting() {
      return switch (this.getType()) {
         case BLACK -> ChatFormatting.BLACK;
         case DARK_RED -> ChatFormatting.DARK_RED;
         case RED -> ChatFormatting.RED;
         case LIGHT_PURPLE -> ChatFormatting.LIGHT_PURPLE;
         case DARK_PURPLE -> ChatFormatting.DARK_PURPLE;
         case DARK_BLUE -> ChatFormatting.DARK_BLUE;
         case BLUE -> ChatFormatting.BLUE;
         case DARK_AQUA -> ChatFormatting.DARK_AQUA;
         case AQUA -> ChatFormatting.AQUA;
         case DARK_GREEN -> ChatFormatting.DARK_GREEN;
         case GREEN -> ChatFormatting.GREEN;
         case YELLOW -> ChatFormatting.YELLOW;
         case GOLD -> ChatFormatting.GOLD;
         case WHITE -> ChatFormatting.WHITE;
         case GRAY -> ChatFormatting.GRAY;
         case DARK_GRAY -> ChatFormatting.DARK_GRAY;
      };
   }

   protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
      guiGraphics.blit(textures[this.type], this.getX(), this.getY(), 0.0F, 0.0F, this.width, this.height, 64, 64);
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   public static enum Ink {
      BLACK,
      DARK_RED,
      RED,
      LIGHT_PURPLE,
      DARK_PURPLE,
      DARK_BLUE,
      BLUE,
      DARK_AQUA,
      AQUA,
      DARK_GREEN,
      GREEN,
      YELLOW,
      GOLD,
      WHITE,
      GRAY,
      DARK_GRAY;
   }
}
