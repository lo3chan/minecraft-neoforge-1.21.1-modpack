package net.blay09.mods.balm.api.config.schema;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;

public interface ConfiguredFloat extends ConfiguredProperty<Float> {
   default float get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default float get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, float value) {
      this.setRaw(config, value);
   }

   default void set(float value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
