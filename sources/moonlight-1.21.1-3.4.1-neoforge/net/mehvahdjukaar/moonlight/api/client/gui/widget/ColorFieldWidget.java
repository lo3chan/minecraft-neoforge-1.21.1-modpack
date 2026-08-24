package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ColorFieldWidget extends CompositeWidget {
   private static final int GAP = 4;
   private final EditBox hexBox;
   private final ColorSwatchWidget swatch;
   private final boolean hasAlpha;
   private int color;
   private final List<AbstractWidget> children;

   public ColorFieldWidget(int width, int height, int initial, Consumer<Integer> onChange, @Nullable Consumer<Integer> onSwatchClick) {
      this(width, height, initial, true, onChange, onSwatchClick);
   }

   public ColorFieldWidget(int width, int height, int initial, boolean hasAlpha, Consumer<Integer> onChange, @Nullable Consumer<Integer> onSwatchClick) {
      super(0, 0, width, height, Component.empty());
      this.hasAlpha = hasAlpha;
      this.color = this.sanitize(initial);
      Font font = Minecraft.getInstance().font;
      this.hexBox = new EditBox(font, 0, 0, width - height - 4, height, Component.empty());
      this.hexBox.setMaxLength(hasAlpha ? 9 : 7);
      this.hexBox.setValue(ColorUtils.toHexString(this.color, hasAlpha));
      this.swatch = new ColorSwatchWidget(height, height, this.opaqueIfNeeded(this.color), onSwatchClick);
      this.hexBox.setResponder(str -> {
         try {
            int c = this.sanitize(ColorUtils.parseHex(str));
            this.color = c;
            this.swatch.setColor(this.opaqueIfNeeded(c));
            this.hexBox.setTextColor(ConfigGuiColors.TEXT);
            onChange.accept(c);
         } catch (Exception var4x) {
            this.hexBox.setTextColor(ConfigGuiColors.ERROR);
         }
      });
      this.children = List.of(this.hexBox, this.swatch);
   }

   private int sanitize(int c) {
      return this.hasAlpha ? c : c & 16777215;
   }

   private int opaqueIfNeeded(int c) {
      return this.hasAlpha ? c : c | 0xFF000000;
   }

   public void setColor(int c) {
      this.color = this.sanitize(c);
      this.hexBox.setValue(ColorUtils.toHexString(this.color, this.hasAlpha));
      this.swatch.setColor(this.opaqueIfNeeded(this.color));
   }

   public int getColor() {
      return this.color;
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.hexBox.setPosition(this.getX(), this.getY());
      this.hexBox.render(graphics, mouseX, mouseY, partialTick);
      this.swatch.setPosition(this.getX() + this.getWidth() - this.swatch.getWidth(), this.getY());
      this.swatch.render(graphics, mouseX, mouseY, partialTick);
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }
}
