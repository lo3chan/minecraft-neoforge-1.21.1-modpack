package net.blay09.mods.balm.api.config.schema;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;

public interface ConfiguredEnum<T extends Enum<T>> extends ConfiguredProperty<T> {
   default T get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default T get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, T value) {
      this.setRaw(config, value);
   }

   default void set(T value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
