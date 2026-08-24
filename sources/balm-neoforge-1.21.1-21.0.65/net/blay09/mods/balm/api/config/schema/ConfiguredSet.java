package net.blay09.mods.balm.api.config.schema;

import java.util.Set;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;

public interface ConfiguredSet<T> extends ConfiguredProperty<Set<T>>, NestedTypeHolder<T> {
   default Set<T> get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default Set<T> get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, Set<T> value) {
      this.setRaw(config, value);
   }

   default void set(Set<T> value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
