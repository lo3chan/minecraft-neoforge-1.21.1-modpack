package com.mrcrayfish.configured.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class CheckBoxButton extends AbstractButton {
   public static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath("configured", "textures/gui/icons.png");
   private final CheckBoxButton.OnPress onPress;
   private boolean selected;

   public CheckBoxButton(int x, int y, CheckBoxButton.OnPress onPress) {
      super(x, y, 14, 14, CommonComponents.EMPTY);
      this.onPress = onPress;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void onPress() {
      this.selected = !this.selected;
      this.onPress.onPress(this);
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blit(ICONS, this.getX(), this.getY(), this.isHoveredOrFocused() ? 50.0F : 36.0F, this.isSelected() ? 49.0F : 35.0F, 14, 14, 64, 64);
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   public interface OnPress {
      void onPress(CheckBoxButton var1);
   }
}
