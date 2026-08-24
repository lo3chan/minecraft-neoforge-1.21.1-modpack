package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.Objects;
import net.mehvahdjukaar.moonlight.api.platform.configs.IConfigValue;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;

class MemoryConfigValue<T> implements IConfigValue<T> {
   private T value;

   MemoryConfigValue(T value) {
      this.value = value;
   }

   @Override
   public T get() {
      return this.value;
   }

   @Override
   public boolean setValue(T value) {
      boolean changed = !Objects.equals(this.value, value);
      this.value = value;
      return changed;
   }

   @Override
   public ConfigReloadType reloadType() {
      return ConfigReloadType.NONE;
   }

   @Override
   public boolean affectsDynamicPacks() {
      return false;
   }
}
