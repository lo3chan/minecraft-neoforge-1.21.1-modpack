package snownee.jade.impl.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.ui.ProgressStyle;
import snownee.jade.api.ui.ScreenDirection;
import snownee.jade.overlay.DisplayHelper;

public class SlimProgressStyle extends ProgressStyle {
   public int color;

   @Override
   public ProgressStyle color(int color, int color2) {
      if (color != color2) {
         throw new UnsupportedOperationException();
      } else {
         this.color = color;
         return this;
      }
   }

   @Override
   public ProgressStyle textColor(int color) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ProgressStyle direction(ScreenDirection direction) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float w, float h, float progress, Component text) {
      if (this.overlay != null) {
         Vec2 size = new Vec2(w * progress, h);
         this.overlay.size(size);
         this.overlay.render(guiGraphics, x, y, size.x, size.y);
      } else {
         DisplayHelper.INSTANCE.drawGradientProgress(guiGraphics, x, y, w, h, progress, this.color);
      }
   }
}
