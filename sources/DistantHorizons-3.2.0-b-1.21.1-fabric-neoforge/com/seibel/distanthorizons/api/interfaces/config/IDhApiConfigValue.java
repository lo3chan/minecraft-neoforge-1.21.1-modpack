package com.seibel.distanthorizons.api.interfaces.config;

import java.util.function.Consumer;

public interface IDhApiConfigValue<T> {
   T getValue();

   T getTrueValue();

   T getApiValue();

   boolean setValue(T object);

   boolean clearValue();

   boolean getCanBeOverrodeByApi();

   T getDefaultValue();

   T getMaxValue();

   T getMinValue();

   void addChangeListener(Consumer<T> consumer);
}
