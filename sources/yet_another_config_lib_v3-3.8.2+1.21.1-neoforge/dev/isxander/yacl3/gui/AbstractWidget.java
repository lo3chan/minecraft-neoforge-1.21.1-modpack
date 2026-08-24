package dev.isxander.yacl3.gui;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.render.ColorGradientRenderState;
import dev.isxander.yacl3.gui.utils.YACLRenderHelper;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public abstract class AbstractWidget implements GuiEventListener, Renderable, NarratableEntry {
   protected final Minecraft client = Minecraft.getInstance();
   protected final Font textRenderer = this.client.font;
   protected final int inactiveColor = -6250336;
   private Dimension<Integer> dim;

   public AbstractWidget(Dimension<Integer> dim) {
      this.dim = dim;
   }

   public boolean canReset() {
      return false;
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.dim == null ? false : this.dim.isPointInside((int)mouseX, (int)mouseY);
   }

   public void setDimension(Dimension<Integer> dim) {
      this.dim = dim;
   }

   public Dimension<Integer> getDimension() {
      return this.dim;
   }

   public NarrationPriority narrationPriority() {
      return NarrationPriority.NONE;
   }

   public void unfocus() {
   }

   public boolean matchesSearch(String query) {
      return true;
   }

   public void updateNarration(NarrationElementOutput builder) {
   }

   protected void drawButtonRect(GuiGraphics graphics, int x1, int y1, int x2, int y2, boolean hovered, boolean enabled) {
      if (x1 > x2) {
         int xx1 = x1;
         x1 = x2;
         x2 = xx1;
      }

      if (y1 > y2) {
         int yy1 = y1;
         y1 = y2;
         y2 = yy1;
      }

      int width = x2 - x1;
      int height = y2 - y1;
      YACLRenderHelper.renderButtonTexture(graphics, x1, y1, width, height, enabled, hovered);
   }

   protected void drawOutline(GuiGraphics graphics, int x1, int y1, int x2, int y2, int width, int color) {
      graphics.fill(x1, y1, x2, y1 + width, color);
      graphics.fill(x2, y1, x2 - width, y2, color);
      graphics.fill(x1, y2, x2, y2 - width, color);
      graphics.fill(x1, y1, x1 + width, y2, color);
   }

   protected void drawRainbowGradient(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
      int[] colors = new int[]{-65536, -256, -16711936, -16711681, -16776961, -65281, -65536};
      int width = x2 - x1;
      int maxColors = colors.length - 1;

      for (int color = 0; color < maxColors; color++) {
         ColorGradientRenderState.createHorizontal(
               graphics,
               x1 + width / maxColors * color,
               y1,
               color == maxColors - 1 ? x2 : x1 + width / maxColors * (color + 1),
               y2,
               colors[color],
               colors[color + 1]
            )
            .submit(graphics);
      }
   }

   protected int multiplyColor(int hex, float amount) {
      Color color = new Color(hex, true);
      return new Color(
            Math.max((int)(color.getRed() * amount), 0),
            Math.max((int)(color.getGreen() * amount), 0),
            Math.max((int)(color.getBlue() * amount), 0),
            color.getAlpha()
         )
         .getRGB();
   }

   public void playDownSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.onMouseClicked(mouseX, mouseY, button);
   }

   public boolean onMouseClicked(double mouseX, double mouseY, int button) {
      return false;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return this.onMouseReleased(mouseX, mouseY, button);
   }

   public boolean onMouseReleased(double mouseX, double mouseY, int button) {
      return false;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
      return this.onMouseDragged(mouseX, mouseY, button, dx, dy);
   }

   public boolean onMouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
      return false;
   }

   public boolean keyPressed(int keycode, int scancode, int modifiers) {
      return this.onKeyPressed(keycode, scancode, modifiers);
   }

   public boolean onKeyPressed(int key, int scancode, int modifiers) {
      return false;
   }

   public boolean keyReleased(int keycode, int scancode, int modifiers) {
      return this.onKeyReleased(keycode, scancode, modifiers);
   }

   public boolean onKeyReleased(int key, int scancode, int modifiers) {
      return false;
   }

   public boolean charTyped(char codePoint, int modifiers) {
      return this.onCharTyped(codePoint, Character.toString(codePoint), modifiers);
   }

   public boolean onCharTyped(char ch, String cpStr, int modifiers) {
      return false;
   }
}
