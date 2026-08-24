package com.teamresourceful.resourcefulconfig.client.components.options.types.color;

import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class AlphaSelector extends BaseWidget {
   private final HsbState state;

   public AlphaSelector(int width, int height, HsbState state) {
      super(width, height);
      this.state = state;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      HsbColor color = this.state.get();
      int size = Math.min(this.getWidth(), this.getHeight()) / 2;

      for (int x = 0; x < Math.ceilDiv(this.getWidth(), size); x++) {
         for (int y = 0; y < Math.ceilDiv(this.getHeight(), size); y++) {
            graphics.fill(
               this.getX() + x * size, this.getY() + y * size, this.getX() + (x + 1) * size, this.getY() + (y + 1) * size, (y + x) % 2 == 0 ? -2171170 : -1
            );
         }
      }

      for (int i = 0; i < this.getWidth(); i++) {
         int alpha = Math.round((float)i / this.getWidth() * 255.0F);
         graphics.fill(
            this.getX() + i,
            this.getY(),
            this.getX() + i + 1,
            this.getY() + this.getHeight(),
            Mth.hsvToArgb(color.hue(), color.saturation(), color.brightness(), alpha)
         );
      }

      int posX = Mth.floor(this.state.get().alpha() / 255.0F * this.getWidth());
      graphics.renderOutline(this.getX() + posX - 1, this.getY() - 1, 3, this.getHeight() + 2, -16777216);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return false;
      } else if (!this.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
         float alpha = Mth.clamp((float)(mouseX - this.getX()) / this.getWidth(), 0.0F, 1.0F);
         this.state.set(this.state.get().withAlpha(Mth.ceil(alpha * 255.0F)));
         return true;
      }
   }

   public boolean mouseDragged(double d, double e, int i, double f, double g) {
      return this.mouseClicked(d, e, i);
   }
}
