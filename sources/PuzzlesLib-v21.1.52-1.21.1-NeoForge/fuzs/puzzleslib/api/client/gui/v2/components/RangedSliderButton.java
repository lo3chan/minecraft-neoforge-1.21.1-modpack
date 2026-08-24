package fuzs.puzzleslib.api.client.gui.v2.components;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class RangedSliderButton extends AbstractSliderButton {
   protected final double minValue;
   protected final double maxValue;

   public RangedSliderButton(int x, int y, int width, int height, double value, double minValue, double maxValue) {
      super(x, y, width, height, CommonComponents.EMPTY, 0.0);
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.setScaledValue(value);
   }

   public double getScaledValue() {
      return this.getValue() * (this.maxValue - this.minValue) + this.minValue;
   }

   public void setScaledValue(double value) {
      this.setValue((value - this.minValue) / (this.maxValue - this.minValue));
   }

   public double getValue() {
      return this.value;
   }

   private void setValue(double value) {
      double oldValue = this.value;
      this.value = Mth.clamp(value, 0.0, 1.0);
      if (oldValue != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void updateMessage() {
      this.setMessage(this.getMessageFromValue(this.getScaledValue()));
   }

   protected void applyValue() {
      this.applyValue(this.getScaledValue());
   }

   protected abstract Component getMessageFromValue(double var1);

   protected abstract void applyValue(double var1);
}
