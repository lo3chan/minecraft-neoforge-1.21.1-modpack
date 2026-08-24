package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TConfigEntryInt extends TConfigEntryValue<Integer> {
   private final TConfigEntryInt.IntSliderWidget widget;
   private boolean modifiesOffMaxToMin = true;

   public TConfigEntryInt(
      @Translatable String text,
      @Translatable String tooltip,
      Supplier<Integer> getter,
      Consumer<Integer> setter,
      int defaultValue,
      int min,
      int max,
      boolean isMinOff,
      boolean isMaxOff
   ) {
      super(text, tooltip, getter, setter, defaultValue);
      this.widget = new TConfigEntryInt.IntSliderWidget(this.getText(), getter.get(), this.getTooltip(), min, max, isMinOff, isMaxOff);
   }

   public TConfigEntryInt(
      @Translatable String text, @Translatable String tooltip, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max
   ) {
      super(text, tooltip, getter, setter, defaultValue);
      this.widget = new TConfigEntryInt.IntSliderWidget(this.getText(), getter.get(), this.getTooltip(), min, max, false, false);
   }

   public TConfigEntryInt(
      @Translatable String text, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max, boolean isMinOff, boolean isMaxOff
   ) {
      this(text, null, getter, setter, defaultValue, min, max, isMinOff, isMaxOff);
   }

   public TConfigEntryInt(@Translatable String text, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max) {
      this(text, null, getter, setter, defaultValue, min, max, false, false);
   }

   protected Integer getValueFromWidget() {
      return this.widget.getValueRoundedToIntBetweenMinMax();
   }

   @Override
   public AbstractWidget getWidget(int x, int y, int width, int height) {
      this.widget.setRectangle(width, height, x, y);
      return this.widget;
   }

   @Override
   void setWidgetToDefaultValue() {
      this.widget.setValue(this.defaultValue);
   }

   @Override
   void resetWidgetToInitialValue() {
      this.widget.setValue(this.getter.get());
   }

   public TConfigEntryInt dontModifyOffMaxValues() {
      this.modifiesOffMaxToMin = false;
      return this;
   }

   public class IntSliderWidget extends AbstractSliderButton {
      private final int max;
      private final int min;
      private final String title;
      private final boolean isMinOff;
      private final boolean isMaxOff;
      private final int difference;

      public IntSliderWidget(final Component text, final int initialValue, final Tooltip tooltip, int min, int max, boolean isMinOff, boolean isMaxOff) {
         super(0, 0, 20, 20, text, 0.0);
         this.min = min;
         this.max = max;
         this.isMinOff = isMinOff;
         this.isMaxOff = isMaxOff;
         this.difference = max - min;
         this.title = text.getString() + ": ";
         this.setValue(initialValue);
         this.setTooltip(tooltip);
      }

      private boolean isOff() {
         return this.isMinOff && this.value == 0.0 ? true : this.isMaxOff && this.value == 1.0;
      }

      private void setValue(int intIndex) {
         this.value = (double)(Mth.clamp(intIndex, this.min, this.max) - this.min) / this.difference;
         this.updateMessage();
      }

      protected void updateMessage() {
         this.snapValueToNearestIndex();
         this.setMessage(
            Component.nullToEmpty(
               this.title
                  + (this.getValueRoundedToIntBetweenMinMax() != TConfigEntryInt.this.getter.get() ? "§a" : "")
                  + (this.isOff() ? CommonComponents.OPTION_OFF.getString() : this.getValueRoundedToIntBetweenMinMax())
            )
         );
      }

      protected void applyValue() {
      }

      private void snapValueToNearestIndex() {
         this.value = (double)((int)Math.round(this.value * this.difference)) / this.difference;
      }

      public int getValueRoundedToIntBetweenMinMax() {
         return this.isOff() && TConfigEntryInt.this.modifiesOffMaxToMin ? this.min : (int)Math.round(this.value * this.difference) + this.min;
      }
   }
}
