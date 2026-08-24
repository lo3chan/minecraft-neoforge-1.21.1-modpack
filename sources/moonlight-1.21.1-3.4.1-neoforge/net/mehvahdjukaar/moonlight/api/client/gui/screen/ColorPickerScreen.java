package net.mehvahdjukaar.moonlight.api.client.gui.screen;

import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.ColorFieldWidget;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.ColorSwatchWidget;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;

public class ColorPickerScreen extends Screen {
   private static final int GAP = 4;
   private static final int CONTROL_HEIGHT = 20;
   private static final int TOP_MARGIN = 44;
   private final Screen parent;
   private final Consumer<Integer> onApply;
   private final boolean hasAlpha;
   private float hue;
   private float sat;
   private float val;
   private float alpha;
   private int svX;
   private int svY;
   private int svSize;
   private int hueX;
   private int hueY;
   private int hueW;
   private int hueH;
   private int alphaX;
   private int alphaY;
   private int alphaW;
   private int alphaH;
   private ColorFieldWidget control;
   private boolean suppressControlSync;
   private int dragging = 0;
   private static final int DRAG_NONE = 0;
   private static final int DRAG_SV = 1;
   private static final int DRAG_HUE = 2;
   private static final int DRAG_ALPHA = 3;

   public ColorPickerScreen(int color, Screen parent, Consumer<Integer> onApply) {
      this(color, true, parent, onApply);
   }

   public ColorPickerScreen(int color, boolean hasAlpha, Screen parent, Consumer<Integer> onApply) {
      super(Component.translatable("gui.moonlight.config.color_picker"));
      this.parent = parent;
      this.onApply = onApply;
      this.hasAlpha = hasAlpha;
      float[] hsv = ColorUtils.argbToHsv(color);
      this.hue = hsv[0];
      this.sat = hsv[1];
      this.val = hsv[2];
      this.alpha = hasAlpha ? ARGB32.alpha(color) / 255.0F : 1.0F;
   }

   protected void init() {
      int cx = this.width / 2;
      this.svSize = 120;
      this.hueW = 14;
      this.alphaH = this.hasAlpha ? 12 : 0;
      int blockW = this.svSize + 4 + this.hueW;
      int blockX = cx - blockW / 2;
      int blockH = this.svSize + 10 + this.alphaH + 12 + 20;
      int buttonsY = this.height - 30;
      int top = Mth.clamp((this.height - blockH) / 2, 52, buttonsY - blockH - 8);
      this.svX = blockX;
      this.svY = top;
      this.hueX = this.svX + this.svSize + 4;
      this.hueY = this.svY;
      this.hueH = this.svSize;
      this.alphaX = this.svX;
      this.alphaY = this.svY + this.svSize + 10;
      this.alphaW = blockW;
      this.control = new ColorFieldWidget(blockW, 20, this.currentColor(), this.hasAlpha, this::onControlColorChanged, null);
      this.control.setPosition(blockX, this.alphaY + this.alphaH + 12);
      this.addRenderableWidget(this.control);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
         this.onApply.accept(this.currentColor());
         this.onClose();
      }).bounds(cx - 100, this.height - 30, 96, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(cx + 4, this.height - 30, 96, 20).build());
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   private int currentColor() {
      int argb = ColorUtils.hsvToArgb(this.hue, this.sat, this.val, Math.round(this.alpha * 255.0F));
      return this.hasAlpha ? argb : argb & 16777215;
   }

   private void syncControl() {
      this.suppressControlSync = true;
      this.control.setColor(this.currentColor());
      this.suppressControlSync = false;
   }

   private void onControlColorChanged(int c) {
      if (!this.suppressControlSync) {
         float[] hsv = ColorUtils.argbToHsv(c);
         this.hue = hsv[0];
         this.sat = hsv[1];
         this.val = hsv[2];
         if (this.hasAlpha) {
            this.alpha = ARGB32.alpha(c) / 255.0F;
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (inside(mouseX, mouseY, this.svX, this.svY, this.svSize, this.svSize)) {
         this.dragging = 1;
         this.updateDrag(mouseX, mouseY);
         return true;
      } else if (inside(mouseX, mouseY, this.hueX, this.hueY, this.hueW, this.hueH)) {
         this.dragging = 2;
         this.updateDrag(mouseX, mouseY);
         return true;
      } else if (inside(mouseX, mouseY, this.alphaX, this.alphaY, this.alphaW, this.alphaH)) {
         this.dragging = 3;
         this.updateDrag(mouseX, mouseY);
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.dragging != 0) {
         this.updateDrag(mouseX, mouseY);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.dragging = 0;
      return super.mouseReleased(mouseX, mouseY, button);
   }

   private void updateDrag(double mouseX, double mouseY) {
      switch (this.dragging) {
         case 1:
            this.sat = Mth.clamp((float)(mouseX - this.svX) / this.svSize, 0.0F, 1.0F);
            this.val = Mth.clamp(1.0F - (float)(mouseY - this.svY) / this.svSize, 0.0F, 1.0F);
            break;
         case 2:
            this.hue = Mth.clamp((float)(mouseY - this.hueY) / this.hueH, 0.0F, 1.0F);
            break;
         case 3:
            this.alpha = Mth.clamp((float)(mouseX - this.alphaX) / this.alphaW, 0.0F, 1.0F);
      }

      this.syncControl();
   }

   private static boolean inside(double mx, double my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, ConfigGuiColors.TITLE);
      this.renderSvSquare(graphics);
      this.renderHueBar(graphics);
      if (this.hasAlpha) {
         this.renderAlphaBar(graphics);
      }
   }

   private void renderSvSquare(GuiGraphics graphics) {
      for (int i = 0; i < this.svSize; i++) {
         float s = (float)i / this.svSize;
         int top = ColorUtils.hsvToArgb(this.hue, s, 1.0F, 255);
         graphics.fillGradient(this.svX + i, this.svY, this.svX + i + 1, this.svY + this.svSize, top, -16777216);
      }

      graphics.renderOutline(this.svX - 1, this.svY - 1, this.svSize + 2, this.svSize + 2, -16777216);
      int cxp = this.svX + Math.round(this.sat * this.svSize);
      int cyp = this.svY + Math.round((1.0F - this.val) * this.svSize);
      ring(graphics, cxp, cyp);
   }

   private void renderHueBar(GuiGraphics graphics) {
      for (int i = 0; i < this.hueH; i++) {
         graphics.fill(this.hueX, this.hueY + i, this.hueX + this.hueW, this.hueY + i + 1, ColorUtils.hsvToArgb((float)i / this.hueH, 1.0F, 1.0F, 255));
      }

      graphics.renderOutline(this.hueX - 1, this.hueY - 1, this.hueW + 2, this.hueH + 2, -16777216);
      int y = this.hueY + Math.round(this.hue * this.hueH);
      graphics.fill(this.hueX - 2, y - 1, this.hueX + this.hueW + 2, y + 1, -1);
   }

   private void renderAlphaBar(GuiGraphics graphics) {
      ColorSwatchWidget.renderChecker(graphics, this.alphaX, this.alphaY, this.alphaW, this.alphaH);
      int rgb = this.currentColor() & 16777215;

      for (int i = 0; i < this.alphaW; i++) {
         int a = Math.round((float)i / this.alphaW * 255.0F);
         graphics.fill(this.alphaX + i, this.alphaY, this.alphaX + i + 1, this.alphaY + this.alphaH, a << 24 | rgb);
      }

      graphics.renderOutline(this.alphaX - 1, this.alphaY - 1, this.alphaW + 2, this.alphaH + 2, -16777216);
      int x = this.alphaX + Math.round(this.alpha * this.alphaW);
      graphics.fill(x - 1, this.alphaY - 2, x + 1, this.alphaY + this.alphaH + 2, -1);
   }

   private static void ring(GuiGraphics graphics, int cx, int cy) {
      graphics.renderOutline(cx - 3, cy - 3, 6, 6, -1);
      graphics.renderOutline(cx - 4, cy - 4, 8, 8, -16777216);
   }
}
