package net.blay09.mods.balm.api.config.schema;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;

public interface ConfiguredLong extends ConfiguredProperty<Long> {
   default long get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default long get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, long value) {
      this.setRaw(config, value);
   }

   default void set(long value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
