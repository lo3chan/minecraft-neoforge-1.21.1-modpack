package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ColorSwatchWidget extends AbstractWidget {
   private int color;
   @Nullable
   private final Consumer<Integer> onPress;

   public ColorSwatchWidget(int width, int height, int color, @Nullable Consumer<Integer> onPress) {
      super(0, 0, width, height, Component.empty());
      this.color = color;
      this.onPress = onPress;
      this.active = onPress != null;
      if (onPress != null) {
         this.setTooltip(Tooltip.create(Component.translatable("gui.moonlight.config.color_pick")));
      }
   }

   public void setColor(int color) {
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      renderChecker(graphics, this.getX() + 1, this.getY() + 1, this.getWidth() - 2, this.getHeight() - 2);
      graphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.getWidth() - 1, this.getY() + this.getHeight() - 1, this.color);
      int border = this.onPress != null && this.isHovered() ? -1 : -16777216;
      graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), border);
   }

   public void onClick(double mouseX, double mouseY) {
      if (this.onPress != null) {
         this.onPress.accept(this.color);
      }
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   public static void renderChecker(GuiGraphics graphics, int x, int y, int w, int h) {
      int cell = 4;

      for (int yy = 0; yy < h; yy += cell) {
         for (int xx = 0; xx < w; xx += cell) {
            boolean light = (xx / cell + yy / cell & 1) == 0;
            graphics.fill(x + xx, y + yy, Math.min(x + xx + cell, x + w), Math.min(y + yy + cell, y + h), light ? -4473925 : -9539986);
         }
      }
   }
}
