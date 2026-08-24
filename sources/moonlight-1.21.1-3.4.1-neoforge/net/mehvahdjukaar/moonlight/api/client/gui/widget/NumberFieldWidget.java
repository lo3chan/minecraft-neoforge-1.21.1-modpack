package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.List;
import java.util.function.DoubleConsumer;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NumberFieldWidget extends CompositeWidget {
   private static final ResourceLocation FIELD = ResourceLocation.withDefaultNamespace("widget/text_field");
   private static final ResourceLocation FIELD_FOCUSED = ResourceLocation.withDefaultNamespace("widget/text_field_highlighted");
   private static final String MINUS = "-";
   private static final String PLUS = "+";
   private static final int STEP_W = 12;
   private static final int TEXT_PAD = 3;
   private static final int GLYPH_H = 8;
   private static final int BORDER = -6250336;
   private static final int BORDER_FOCUSED = -1;
   private static final int ARROW_AT_BOUND = -10855846;
   private static final int SHIFT_MULTIPLIER = 10;
   private final EditBox box;
   private final List<EditBox> children;
   private final double min;
   private final double max;
   private final double step;
   private final boolean integer;

   public NumberFieldWidget(int width, int height, double initial, double min, double max, boolean integer, DoubleConsumer onChange) {
      super(0, 0, width, height, Component.empty());
      this.min = min;
      this.max = max;
      this.integer = integer;
      this.step = integer ? 1.0 : 0.1;
      Font font = Minecraft.getInstance().font;
      this.box = new EditBox(font, 0, 0, innerWidth(width), height - (height - 8) / 2, Component.empty());
      this.box.setBordered(false);
      this.box.setMaxLength(32767);
      this.box.setValue(this.format(initial));
      this.box.setResponder(s -> {
         Double parsed = this.parse(s);
         this.box.setTextColor(parsed != null ? ConfigGuiColors.TEXT : ConfigGuiColors.ERROR);
         if (parsed != null) {
            onChange.accept(parsed);
         }
      });
      this.children = List.of(this.box);
   }

   private static int innerWidth(int width) {
      return width - 32;
   }

   private static int glyphWidth(Font font, String glyph) {
      return font.width(glyph) - 1;
   }

   private static int textY(int y, int height) {
      return y + (height - 8) / 2;
   }

   public void setValue(double v) {
      this.box.setValue(this.format(v));
   }

   private Double parse(String s) {
      try {
         double v = this.integer ? Long.parseLong(s.trim()) : Double.parseDouble(s.trim());
         return v >= this.min && v <= this.max ? v : null;
      } catch (Exception var4) {
         return null;
      }
   }

   private String format(double v) {
      return this.integer ? String.valueOf(Math.round(v)) : TextHelper.formatNumber(Math.round(v * 10000.0) / 10000.0);
   }

   private double currentOrNearest() {
      Double parsed = this.parse(this.box.getValue());
      return parsed != null ? parsed : Math.clamp(0.0, this.min, this.max);
   }

   private boolean canStep(int dir) {
      if (!this.active) {
         return false;
      } else {
         Double parsed = this.parse(this.box.getValue());
         if (parsed == null) {
            return true;
         } else {
            return dir > 0 ? parsed < this.max : parsed > this.min;
         }
      }
   }

   private void step(int dir) {
      double from = this.currentOrNearest();
      double next = Math.clamp(from + dir * this.step * (Screen.hasShiftDown() ? 10 : 1), this.min, this.max);
      if (next != from || this.parse(this.box.getValue()) == null) {
         this.box.setValue(this.format(next));
         GuiHelper.playClickSound();
      }
   }

   private boolean overStep(double mouseX, double mouseY, int dir) {
      if (!(mouseY < this.getY()) && !(mouseY >= this.getY() + this.getHeight())) {
         int left = dir > 0 ? this.getX() + this.getWidth() - 12 : this.getX();
         return mouseX >= left && mouseX < left + 12;
      } else {
         return false;
      }
   }

   private int arrowColor(double mouseX, double mouseY, int dir) {
      if (!this.canStep(dir)) {
         return -10855846;
      } else {
         return this.overStep(mouseX, mouseY, dir) ? -1 : -6250336;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.active) {
         int dir = this.overStep(mouseX, mouseY, -1) ? -1 : (this.overStep(mouseX, mouseY, 1) ? 1 : 0);
         if (dir != 0) {
            if (this.canStep(dir)) {
               this.step(dir);
            }

            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int x = this.getX();
      int y = this.getY();
      int w = this.getWidth();
      int h = this.getHeight();
      boolean focused = this.box.isFocused();
      graphics.blitSprite(focused ? FIELD_FOCUSED : FIELD, x, y, w, h);
      int leftDivider = x + 12;
      int rightDivider = x + w - 12 - 1;
      int line = focused ? -1 : -6250336;
      graphics.fill(leftDivider, y + 1, leftDivider + 1, y + h - 1, line);
      graphics.fill(rightDivider, y + 1, rightDivider + 1, y + h - 1, line);
      Font font = Minecraft.getInstance().font;
      int textY = textY(y, h);
      int minusW = glyphWidth(font, "-");
      int plusW = glyphWidth(font, "+");
      graphics.drawString(font, "-", x + 1 + (11 - minusW) / 2, textY, this.arrowColor(mouseX, mouseY, -1), false);
      graphics.drawString(font, "+", x + w - 1 - (11 - plusW) / 2 - plusW, textY, this.arrowColor(mouseX, mouseY, 1), false);
      int fieldStart = leftDivider + 1 + 3;
      int fieldWidth = rightDivider - 3 - fieldStart;
      int slack = Math.max(0, fieldWidth - font.width(this.box.getValue()));
      this.box.setPosition(fieldStart + slack / 2, textY);
      this.box.setWidth(fieldWidth - slack / 2);
      this.box.render(graphics, mouseX, mouseY, partialTick);
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }
}
