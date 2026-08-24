package net.blay09.mods.balm.api.config.schema;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;

public interface ConfiguredBoolean extends ConfiguredProperty<Boolean> {
   default boolean get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default boolean get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, boolean value) {
      this.setRaw(config, value);
   }

   default void set(boolean value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
