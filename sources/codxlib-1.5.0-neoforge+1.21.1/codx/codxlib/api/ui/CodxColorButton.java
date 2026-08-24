package codx.codxlib.api.ui;

import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public final class CodxColorButton extends Button {
   private final IntSupplier colorSupplier;

   public CodxColorButton(int x, int y, int width, int height, IntSupplier colorSupplier, OnPress onPress) {
      super(x, y, width, height, hexLabel(colorSupplier.getAsInt()), onPress, DEFAULT_NARRATION);
      this.colorSupplier = colorSupplier;
   }

   private static Component hexLabel(int color) {
      return Component.literal(String.format("#%06X", color & 16777215));
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderWidget(graphics, mouseX, mouseY, partialTick);
      this.drawSwatch(graphics);
   }

   private void drawSwatch(GuiGraphics graphics) {
      this.setMessage(hexLabel(this.colorSupplier.getAsInt()));
      int swatchColor = 0xFF000000 | this.colorSupplier.getAsInt() & 16777215;
      int inset = this.isHoveredOrFocused() ? 2 : 3;
      int contentLeft = this.getX() + inset;
      int contentTop = this.getY() + inset;
      int contentRight = this.getX() + this.getWidth() - inset;
      int contentBottom = this.getY() + this.getHeight() - inset;
      int contentWidth = this.getWidth() - inset * 2;
      int contentHeight = this.getHeight() - inset * 2;
      int swatchWidth = Math.min(18, contentWidth - 8);
      int textLeft = contentLeft + swatchWidth + 3;
      graphics.fill(contentLeft, contentTop, contentRight, contentBottom, this.isHoveredOrFocused() ? -13948117 : -14803426);
      graphics.fill(contentLeft, contentTop, contentLeft + swatchWidth, contentTop + contentHeight, swatchColor);
      graphics.renderOutline(contentLeft, contentTop, contentWidth, contentHeight, this.isHoveredOrFocused() ? -1 : -10855846);
      graphics.renderOutline(contentLeft, contentTop, swatchWidth, contentHeight, -15724528);
      int textCenterX = textLeft + (contentRight - textLeft) / 2;
      graphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), textCenterX, this.getY() + 6, -1);
   }
}
