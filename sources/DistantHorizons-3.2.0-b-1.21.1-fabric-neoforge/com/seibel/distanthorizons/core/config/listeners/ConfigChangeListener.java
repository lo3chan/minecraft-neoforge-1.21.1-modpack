package com.seibel.distanthorizons.core.config.listeners;

import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import java.io.Closeable;
import java.util.function.Consumer;

public class ConfigChangeListener<T> implements IConfigListener, Closeable {
   private final ConfigEntry<T> configEntry;
   private final Consumer<T> onValueChangeFunc;
   private T previousValue;

   public ConfigChangeListener(ConfigEntry<T> configEntry, Consumer<T> onValueChangeFunc) {
      this.configEntry = configEntry;
      this.onValueChangeFunc = onValueChangeFunc;
      this.configEntry.addListener(this);
      this.previousValue = this.configEntry.get();
   }

   @Override
   public void onConfigValueSet() {
      T newValue = this.configEntry.get();
      if (newValue != this.previousValue) {
         this.previousValue = newValue;
         this.onValueChangeFunc.accept(newValue);
      }
   }

   @Override
   public void onUiModify() {
   }

   @Override
   public void close() {
      this.configEntry.removeListener(this);
   }
}
