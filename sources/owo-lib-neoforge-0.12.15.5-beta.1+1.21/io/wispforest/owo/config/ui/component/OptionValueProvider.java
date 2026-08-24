package io.wispforest.owo.config.ui.component;

public interface OptionValueProvider {
   boolean isValid();

   Object parsedValue();
}
