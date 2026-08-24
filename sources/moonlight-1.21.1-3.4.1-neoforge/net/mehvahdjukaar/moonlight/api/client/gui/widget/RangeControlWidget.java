package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.api.util.math.Range;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class RangeControlWidget extends CompositeWidget {
   private static final int INNER_GAP = 8;
   private static final String SEPARATOR = "<";
   private final EditBox minBox;
   private final EditBox maxBox;
   private final double boundLo;
   private final double boundHi;
   private final Consumer<Range> onChange;
   private final List<EditBox> boxes;

   public RangeControlWidget(int width, int height, Range initial, double boundLo, double boundHi, Consumer<Range> onChange) {
      super(0, 0, width, height, Component.empty());
      this.boundLo = boundLo;
      this.boundHi = boundHi;
      this.onChange = onChange;
      Font font = Minecraft.getInstance().font;
      int half = (width - 8) / 2;
      this.minBox = new EditBox(font, 0, 0, half, height, Component.empty());
      this.maxBox = new EditBox(font, 0, 0, half, height, Component.empty());
      this.minBox.setMaxLength(32767);
      this.maxBox.setMaxLength(32767);
      this.minBox.setValue(TextHelper.formatNumber(initial.min()));
      this.maxBox.setValue(TextHelper.formatNumber(initial.max()));
      this.minBox.setResponder(s -> this.onEdited());
      this.maxBox.setResponder(s -> this.onEdited());
      this.boxes = List.of(this.minBox, this.maxBox);
   }

   public void setRange(Range range) {
      this.minBox.setValue(TextHelper.formatNumber(range.min()));
      this.maxBox.setValue(TextHelper.formatNumber(range.max()));
   }

   private void onEdited() {
      Double parsedMin = this.parse(this.minBox);
      Double parsedMax = this.parse(this.maxBox);
      this.minBox.setTextColor(parsedMin != null ? ConfigGuiColors.TEXT : ConfigGuiColors.ERROR);
      this.maxBox.setTextColor(parsedMax != null ? ConfigGuiColors.TEXT : ConfigGuiColors.ERROR);
      if (parsedMin != null && parsedMax != null) {
         this.onChange.accept(new Range(parsedMin, parsedMax));
      }
   }

   private Double parse(EditBox box) {
      try {
         double v = Double.parseDouble(box.getValue().trim());
         return !(v < this.boundLo) && !(v > this.boundHi) ? v : null;
      } catch (Exception var4) {
         return null;
      }
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int half = (this.getWidth() - 8) / 2;
      this.minBox.setPosition(this.getX(), this.getY());
      this.minBox.setWidth(half);
      this.maxBox.setPosition(this.getX() + half + 8, this.getY());
      this.maxBox.setWidth(half);
      this.minBox.render(graphics, mouseX, mouseY, partialTick);
      this.maxBox.render(graphics, mouseX, mouseY, partialTick);
      Font font = Minecraft.getInstance().font;
      graphics.drawString(
         font, "<", this.getX() + half + (8 - font.width("<")) / 2 + 1, this.getY() + (this.getHeight() - 9) / 2 + 1, ConfigGuiColors.LABEL, false
      );
   }

   public List<? extends GuiEventListener> children() {
      return this.boxes;
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }
}
