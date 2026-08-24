package net.blay09.mods.balm.api.config.schema;

import java.util.List;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;

public interface ConfiguredList<T> extends ConfiguredProperty<List<T>>, NestedTypeHolder<T> {
   default List<T> get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default List<T> get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, List<T> value) {
      this.setRaw(config, value);
   }

   default void set(List<T> value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
