package codx.codxlib.api.ui;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class CodxWidgets {
   private CodxWidgets() {
   }

   public static Button toggle(int x, int y, int width, int height, Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
      return Button.builder(toggleLabel(label, getter.getAsBoolean()), button -> {
         boolean next = !getter.getAsBoolean();
         setter.accept(next);
         button.setMessage(toggleLabel(label, next));
      }).bounds(x, y, width, height).build();
   }

   public static <T> Button cycle(int x, int y, int width, int height, Supplier<T> getter, Consumer<T> setter, Function<T, Component> labeler, T[] values) {
      return Button.builder(labeler.apply(getter.get()), button -> {
         T current = getter.get();
         int index = 0;

         for (int i = 0; i < values.length; i++) {
            if (values[i].equals(current)) {
               index = i;
               break;
            }
         }

         T next = values[(index + 1) % values.length];
         setter.accept(next);
         button.setMessage(labeler.apply(next));
      }).bounds(x, y, width, height).build();
   }

   public static AbstractSliderButton intSlider(int x, int y, int width, int height, Component label, int min, int max, IntSupplier getter, IntConsumer setter) {
      return new CodxWidgets.IntSlider(x, y, width, height, label, min, max, getter, setter);
   }

   public static AbstractSliderButton doubleSlider(
      int x, int y, int width, int height, Component label, double min, double max, int decimals, DoubleSupplier getter, DoubleConsumer setter
   ) {
      return new CodxWidgets.DoubleSlider(x, y, width, height, label, min, max, decimals, getter, setter);
   }

   private static Component toggleLabel(Component label, boolean on) {
      return Component.literal(label.getString() + ": " + (on ? "ON" : "OFF"));
   }

   private static final class DoubleSlider extends AbstractSliderButton {
      private final Component label;
      private final double min;
      private final double max;
      private final int decimals;
      private final DoubleConsumer setter;

      private DoubleSlider(
         int x, int y, int width, int height, Component label, double min, double max, int decimals, DoubleSupplier getter, DoubleConsumer setter
      ) {
         super(x, y, width, height, Component.empty(), max == min ? 0.0 : (getter.getAsDouble() - min) / (max - min));
         this.label = label;
         this.min = min;
         this.max = max;
         this.decimals = decimals;
         this.setter = setter;
         this.updateMessage();
      }

      private double currentValue() {
         return this.min + this.value * (this.max - this.min);
      }

      protected void updateMessage() {
         this.setMessage(Component.literal(this.label.getString() + ": " + String.format("%." + this.decimals + "f", this.currentValue())));
      }

      protected void applyValue() {
         this.setter.accept(this.currentValue());
      }
   }

   private static final class IntSlider extends AbstractSliderButton {
      private final Component label;
      private final int min;
      private final int max;
      private final IntConsumer setter;

      private IntSlider(int x, int y, int width, int height, Component label, int min, int max, IntSupplier getter, IntConsumer setter) {
         super(x, y, width, height, Component.empty(), max == min ? 0.0 : (double)(getter.getAsInt() - min) / (max - min));
         this.label = label;
         this.min = min;
         this.max = max;
         this.setter = setter;
         this.updateMessage();
      }

      private int currentValue() {
         return this.min + (int)Math.round(this.value * (this.max - this.min));
      }

      protected void updateMessage() {
         this.setMessage(Component.literal(this.label.getString() + ": " + this.currentValue()));
      }

      protected void applyValue() {
         this.setter.accept(this.currentValue());
      }
   }
}
