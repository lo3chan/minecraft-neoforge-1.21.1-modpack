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

public class QuillButton extends AbstractWidget {
   protected static final ResourceLocation[] textures = Arrays.stream(QuillButton.QuillType.values())
      .map(t -> Amendments.res("textures/gui/quill/" + t.name().toLowerCase(Locale.ROOT) + ".png"))
      .toArray(ResourceLocation[]::new);
   private int type = 0;

   public QuillButton(Screen screen) {
      super(screen.width / 2 + 70, 20, 48, 144, Component.empty());
      this.refreshTooltip();
   }

   private void refreshTooltip() {
      this.setTooltip(Tooltip.create(Component.translatable("gui.amendments.quill." + this.getType().name().toLowerCase(Locale.ROOT))));
   }

   public QuillButton.QuillType getType() {
      return QuillButton.QuillType.values()[this.type];
   }

   public void onClick(double mouseX, double mouseY) {
      int length = QuillButton.QuillType.values().length;
      int inc = Screen.hasShiftDown() ? 1 : -1;
      this.type = (this.type + inc + length) % length;
      this.refreshTooltip();
   }

   public void onClick(double mouseX, double mouseY, int button) {
      int length = QuillButton.QuillType.values().length;
      int inc = button == 0 ? 1 : -1;
      this.type = (this.type + inc + length) % length;
      this.refreshTooltip();
   }

   protected boolean isValidClickButton(int button) {
      return super.isValidClickButton(button) || button == 1;
   }

   protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
      guiGraphics.blit(textures[this.type], this.getX(), this.getY(), 0.0F, 0.0F, this.width, this.height, this.width, this.height);
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   public ChatFormatting getChatFormatting() {
      return switch (this.getType()) {
         case ITALIC -> ChatFormatting.ITALIC;
         case BOLD -> ChatFormatting.BOLD;
         case UNDERLINE -> ChatFormatting.UNDERLINE;
         case STRIKETHROUGH -> ChatFormatting.STRIKETHROUGH;
         case OBFUSCATED -> ChatFormatting.OBFUSCATED;
         default -> ChatFormatting.RESET;
      };
   }

   public static enum QuillType {
      DEFAULT,
      ITALIC,
      BOLD,
      UNDERLINE,
      STRIKETHROUGH,
      OBFUSCATED;
   }
}
