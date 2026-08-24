package net.blay09.mods.balm.api.config.schema;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;

public interface ConfiguredString extends ConfiguredProperty<String> {
   default String get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default String get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, String value) {
      this.setRaw(config, value);
   }

   default void set(String value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
