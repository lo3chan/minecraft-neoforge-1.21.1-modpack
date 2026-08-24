package fuzs.puzzleslib.impl.config;

import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface ConfigHolderRegistry extends ConfigHolder {
   @Internal
   @Override
   <T extends ConfigCore> ConfigDataHolder<T> getHolder(Class<T> var1);

   @Internal
   @Override
   default <T extends ConfigCore> T get(Class<T> clazz) {
      return ConfigHolder.super.get(clazz);
   }
}
