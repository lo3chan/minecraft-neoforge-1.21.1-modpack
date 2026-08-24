package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TConfigEntryEnumSlider<E extends Enum<E>> extends TConfigEntryNullSafe<E> {
   private final TConfigEntryEnumSlider<E>.EnumSliderWidget<E> widget;
   private boolean appendNullValue = false;

   public TConfigEntryEnumSlider(
      @Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass
   ) {
      super(text, tooltip, getter, setter, defaultValue);
      if (defaultValue == null) {
         this.appendNullValue = true;
      }

      this.widget = new TConfigEntryEnumSlider.EnumSliderWidget<>(this.getText(), getter.get(), this.getTooltip(), enumClass);
   }

   public TConfigEntryEnumSlider(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
      this(text, tooltip, getter, setter, defaultValue, defaultValue.getDeclaringClass());
   }

   public TConfigEntryEnumSlider(@Translatable String text, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
      this(text, null, getter, setter, defaultValue, defaultValue.getDeclaringClass());
   }

   public TConfigEntryEnumSlider(@Translatable String text, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
      this(text, null, getter, setter, defaultValue, enumClass);
   }

   @Override
   public TConfigEntryNullSafe<E> allowNullValue() {
      this.appendNullValue = true;
      return this;
   }

   protected E getValueFromWidget() {
      return this.widget.getValue();
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

   public class EnumSliderWidget<T extends Enum<?>> extends AbstractSliderButton {
      private final T[] enumValues;
      private final String title;

      public EnumSliderWidget(final Component text, final T initialValue, final Tooltip tooltip, Class<T> enumClass) {
         super(0, 0, 20, 20, text, 1.0);
         this.enumValues = enumClass.getEnumConstants();
         this.title = text.getString() + ": ";
         this.setTooltip(tooltip);
         this.setValue(initialValue);
      }

      @Nullable
      private T getValue() {
         return this.getIndex() >= this.enumValues.length ? null : this.enumValues[this.getIndex()];
      }

      private void setValue(T value) {
         this.value = value == null ? 1.0 : (double)value.ordinal() / this.getChoiceCount();
         this.updateMessage();
      }

      protected void updateMessage() {
         this.value = (double)this.getIndex() / this.getChoiceCount();
         T value2 = this.getValue();
         this.setMessage(
            Component.nullToEmpty(this.title + (value2 != TConfigEntryEnumSlider.this.getter.get() ? "§a" : "") + (value2 == null ? "---" : value2))
         );
      }

      protected void applyValue() {
      }

      private int getChoiceCount() {
         return this.enumValues.length - (TConfigEntryEnumSlider.this.appendNullValue ? 0 : 1);
      }

      private int getIndex() {
         return (int)Math.round(this.value * this.getChoiceCount());
      }
   }
}
