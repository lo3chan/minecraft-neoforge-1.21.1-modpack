package com.teamresourceful.resourcefulconfig.client.components.options.types.color;

import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class HueSelector extends BaseWidget {
   private final HsbState state;

   public HueSelector(int width, int height, HsbState state) {
      super(width, height);
      this.state = state;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      for (int i = 0; i < this.getWidth(); i++) {
         graphics.fill(
            this.getX() + i, this.getY(), this.getX() + i + 1, this.getY() + this.getHeight(), Mth.hsvToArgb((float)i / this.getWidth(), 1.0F, 1.0F, 255)
         );
      }

      int posX = Mth.floor(this.state.get().hue() * this.getWidth());
      graphics.renderOutline(this.getX() + posX - 1, this.getY() - 1, 3, this.getHeight() + 2, -16777216);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return false;
      } else if (!this.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
         float hue = Mth.clamp((float)(mouseX - this.getX()) / this.getWidth(), 0.0F, 1.0F);
         this.state.set(this.state.get().withHue(hue));
         return true;
      }
   }

   public boolean mouseDragged(double d, double e, int i, double f, double g) {
      return this.mouseClicked(d, e, i);
   }
}
