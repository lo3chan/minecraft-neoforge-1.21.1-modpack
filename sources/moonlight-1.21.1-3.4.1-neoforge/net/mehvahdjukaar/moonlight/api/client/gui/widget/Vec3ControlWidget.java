package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.List;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class Vec3ControlWidget extends CompositeWidget {
   private static final int INNER_GAP = 3;
   private final EditBox xBox;
   private final EditBox yBox;
   private final EditBox zBox;
   private final List<EditBox> boxes;
   private final double boundLo;
   private final double boundHi;
   private final boolean integer;
   private final Vec3ControlWidget.Sink onChange;

   public Vec3ControlWidget(
      int width, int height, double x, double y, double z, double boundLo, double boundHi, boolean integer, Vec3ControlWidget.Sink onChange
   ) {
      super(0, 0, width, height, Component.empty());
      this.boundLo = boundLo;
      this.boundHi = boundHi;
      this.integer = integer;
      this.onChange = onChange;
      Font font = Minecraft.getInstance().font;
      int third = Math.max(1, (width - 6) / 3);
      this.xBox = this.makeBox(font, third, height, x);
      this.yBox = this.makeBox(font, third, height, y);
      this.zBox = this.makeBox(font, third, height, z);
      this.boxes = List.of(this.xBox, this.yBox, this.zBox);
   }

   private EditBox makeBox(Font font, int width, int height, double value) {
      EditBox box = new EditBox(font, 0, 0, width, height, Component.empty());
      box.setMaxLength(32767);
      box.setValue(this.format(value));
      box.setResponder(s -> this.onEdited());
      return box;
   }

   public void setValues(double x, double y, double z) {
      this.xBox.setValue(this.format(x));
      this.yBox.setValue(this.format(y));
      this.zBox.setValue(this.format(z));
   }

   private void onEdited() {
      Double px = this.parse(this.xBox);
      Double py = this.parse(this.yBox);
      Double pz = this.parse(this.zBox);
      this.xBox.setTextColor(px != null ? ConfigGuiColors.TEXT : ConfigGuiColors.ERROR);
      this.yBox.setTextColor(py != null ? ConfigGuiColors.TEXT : ConfigGuiColors.ERROR);
      this.zBox.setTextColor(pz != null ? ConfigGuiColors.TEXT : ConfigGuiColors.ERROR);
      if (px != null && py != null && pz != null) {
         this.onChange.accept(px, py, pz);
      }
   }

   private Double parse(EditBox box) {
      try {
         String t = box.getValue().trim();
         double v = this.integer ? Integer.parseInt(t) : Double.parseDouble(t);
         return !(v < this.boundLo) && !(v > this.boundHi) ? v : null;
      } catch (Exception var5) {
         return null;
      }
   }

   private String format(double v) {
      return this.integer ? String.valueOf((long)v) : TextHelper.formatNumber(v);
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int third = (this.getWidth() - 6) / 3;
      int y = this.getY();
      this.xBox.setPosition(this.getX(), y);
      this.xBox.setWidth(third);
      this.yBox.setPosition(this.getX() + third + 3, y);
      this.yBox.setWidth(third);
      this.zBox.setPosition(this.getX() + 2 * (third + 3), y);
      this.zBox.setWidth(third);
      this.xBox.render(graphics, mouseX, mouseY, partialTick);
      this.yBox.render(graphics, mouseX, mouseY, partialTick);
      this.zBox.render(graphics, mouseX, mouseY, partialTick);
   }

   public List<? extends GuiEventListener> children() {
      return this.boxes;
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   @FunctionalInterface
   public interface Sink {
      void accept(double var1, double var3, double var5);
   }
}
