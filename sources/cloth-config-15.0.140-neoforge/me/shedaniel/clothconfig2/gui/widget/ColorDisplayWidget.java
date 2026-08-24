package me.shedaniel.clothconfig2.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ColorDisplayWidget extends AbstractWidget {
   protected EditBox textFieldWidget;
   protected int color;
   protected int size;

   public ColorDisplayWidget(EditBox textFieldWidget, int x, int y, int size, int color) {
      super(x, y, size, size, Component.empty());
      this.textFieldWidget = textFieldWidget;
      this.color = color;
      this.size = size;
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      graphics.fillGradient(
         this.getX(),
         this.getY(),
         this.getX() + this.size,
         this.getY() + this.size,
         this.textFieldWidget.isFocused() ? -1 : -6250336,
         this.textFieldWidget.isFocused() ? -1 : -6250336
      );
      graphics.fillGradient(this.getX() + 1, this.getY() + 1, this.getX() + this.size - 1, this.getY() + this.size - 1, -1, -1);
      graphics.fillGradient(this.getX() + 1, this.getY() + 1, this.getX() + this.size - 1, this.getY() + this.size - 1, this.color, this.color);
   }

   public void onClick(double mouseX, double mouseY) {
   }

   public void onRelease(double mouseX, double mouseY) {
   }

   public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   public void setColor(int color) {
      this.color = color;
   }
}
