package dev.isxander.yacl3.gui.controllers.cycling;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

public class CyclingListController<T> implements ICyclingController<T> {
   private final Option<T> option;
   private final ValueFormatter<T> valueFormatter;
   private final ImmutableList<T> values;

   public CyclingListController(Option<T> option, Iterable<? extends T> values) {
      this(option, values, value -> Component.literal(value.toString()));
   }

   public CyclingListController(Option<T> option, Iterable<? extends T> values, Function<T, Component> valueFormatter) {
      this.option = option;
      this.valueFormatter = valueFormatter::apply;
      this.values = ImmutableList.copyOf(values);
   }

   @Internal
   public static <T> CyclingListController<T> createInternal(Option<T> option, Iterable<? extends T> values, ValueFormatter<T> formatter) {
      return new CyclingListController<>(option, values, formatter::format);
   }

   @Override
   public Option<T> option() {
      return this.option;
   }

   @Override
   public Component formatValue() {
      return this.valueFormatter.format(this.option().pendingValue());
   }

   @Override
   public void setPendingValue(int ordinal) {
      this.option().requestSet((T)this.values.get(ordinal));
   }

   @Override
   public int getPendingValue() {
      return this.values.indexOf(this.option().pendingValue());
   }

   @Override
   public int getCycleLength() {
      return this.values.size();
   }
}
