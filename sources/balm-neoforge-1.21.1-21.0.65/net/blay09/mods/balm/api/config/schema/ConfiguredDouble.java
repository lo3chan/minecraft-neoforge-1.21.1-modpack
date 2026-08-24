package net.blay09.mods.balm.api.config.schema;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;

public interface ConfiguredDouble extends ConfiguredProperty<Double> {
   default double get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default double get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, double value) {
      this.setRaw(config, value);
   }

   default void set(double value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
