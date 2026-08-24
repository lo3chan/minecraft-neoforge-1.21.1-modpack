package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TConfigEntryEnumButton<E extends Enum<E>> extends TConfigEntryNullSafe<E> {
   private final TConfigEntryEnumButton<E>.EnumButtonWidget<E> widget;
   private boolean appendNullValue = false;

   public TConfigEntryEnumButton(
      @Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass
   ) {
      super(text, tooltip, getter, setter, defaultValue);
      if (defaultValue == null) {
         this.appendNullValue = true;
      }

      this.widget = new TConfigEntryEnumButton.EnumButtonWidget<>(this.getText(), getter.get(), this.getTooltip(), enumClass);
   }

   public TConfigEntryEnumButton(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
      this(text, tooltip, getter, setter, defaultValue, defaultValue.getDeclaringClass());
   }

   public TConfigEntryEnumButton(@Translatable String text, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
      this(text, null, getter, setter, defaultValue, enumClass);
   }

   public TConfigEntryEnumButton(@Translatable String text, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
      this(text, null, getter, setter, defaultValue, defaultValue.getDeclaringClass());
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

   public class EnumButtonWidget<T extends Enum<?>> extends Button {
      private final T[] enumValues;
      private final String title;
      private int index;

      public EnumButtonWidget(final Component text, final T initialValue, final Tooltip tooltip, Class<T> enumClass) {
         super(0, 0, 20, 20, text, button -> {}, Button.DEFAULT_NARRATION);
         this.enumValues = enumClass.getEnumConstants();
         this.title = text.getString() + ": ";
         this.setTooltip(tooltip);
         this.setValue(initialValue);
      }

      public int getIndex() {
         return this.index;
      }

      private int getChoiceCount() {
         return this.enumValues.length - (TConfigEntryEnumButton.this.appendNullValue ? 0 : 1);
      }

      @Nullable
      private T getValue() {
         return this.index >= this.enumValues.length ? null : this.enumValues[this.index];
      }

      private void setValue(T value) {
         this.index = value == null ? this.enumValues.length : value.ordinal();
         this.updateMessage();
      }

      protected void updateMessage() {
         T value = this.getValue();
         this.setMessage(Component.nullToEmpty(this.title + (value != TConfigEntryEnumButton.this.getter.get() ? "§a" : "") + (value == null ? "---" : value)));
      }

      public void onPress() {
         this.index++;
         if (this.index > this.getChoiceCount()) {
            this.index = 0;
         }

         this.updateMessage();
      }
   }
}
