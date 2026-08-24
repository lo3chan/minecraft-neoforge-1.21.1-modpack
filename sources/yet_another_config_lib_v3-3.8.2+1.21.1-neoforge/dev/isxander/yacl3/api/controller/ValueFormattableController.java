package dev.isxander.yacl3.api.controller;

import java.util.function.Function;
import net.minecraft.network.chat.Component;

public interface ValueFormattableController<T, B extends ValueFormattableController<T, B>> extends ControllerBuilder<T> {
   B formatValue(ValueFormatter<T> var1);

   @Deprecated
   default B valueFormatter(Function<T, Component> formatter) {
      return this.formatValue(formatter::apply);
   }
}
