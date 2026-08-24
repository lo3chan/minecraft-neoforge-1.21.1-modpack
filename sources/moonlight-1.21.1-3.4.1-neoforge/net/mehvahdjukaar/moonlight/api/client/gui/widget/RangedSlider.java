package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class RangedSlider extends AbstractSliderButton {
   private final double min;
   private final double max;
   private final boolean integer;
   private final boolean percent;
   private final Consumer<Double> onValue;

   public RangedSlider(int width, int height, double min, double max, double current, boolean integer, boolean percent, Consumer<Double> onValue) {
      super(0, 0, width, height, Component.empty(), fraction(min, max, current));
      this.min = min;
      this.max = max;
      this.integer = integer;
      this.percent = percent;
      this.onValue = onValue;
      this.updateMessage();
   }

   private static double fraction(double min, double max, double value) {
      return max <= min ? 0.0 : Mth.clamp((value - min) / (max - min), 0.0, 1.0);
   }

   public double actualValue() {
      double v = this.min + this.value * (this.max - this.min);
      return this.integer ? Math.round(v) : v;
   }

   public void setActualValue(double value) {
      this.value = fraction(this.min, this.max, value);
      this.updateMessage();
   }

   protected void updateMessage() {
      double v = this.actualValue();
      String text = this.percent ? Math.round(v * 100.0) + "%" : (this.integer ? String.valueOf((long)v) : String.format("%.2f", v));
      this.setMessage(Component.literal(text));
   }

   protected void applyValue() {
      this.onValue.accept(this.actualValue());
   }
}
