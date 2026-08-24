package mezz.jei.common.gui.elements;

import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import net.minecraft.client.gui.GuiGraphics;

public record DrawableBlank(int width, int height) implements IDrawableStatic, IDrawableAnimated, IScalableDrawable {
   public static final DrawableBlank EMPTY = new DrawableBlank(0, 0);

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   @Override
   public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
   }

   @Override
   public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
   }

   @Override
   public void draw(GuiGraphics guiGraphics, int x, int y, int width, int height) {
   }
}
