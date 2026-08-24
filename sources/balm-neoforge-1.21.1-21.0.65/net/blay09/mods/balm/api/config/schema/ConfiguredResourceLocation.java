package net.blay09.mods.balm.api.config.schema;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;
import net.minecraft.resources.ResourceLocation;

public interface ConfiguredResourceLocation extends ConfiguredProperty<ResourceLocation> {
   default ResourceLocation get(LoadedConfig config) {
      return this.getRaw(config);
   }

   default ResourceLocation get() {
      return this.get(Balm.getConfig().getActiveConfig(this.parentSchema()));
   }

   default void set(MutableLoadedConfig config, ResourceLocation value) {
      this.setRaw(config, value);
   }

   default void set(ResourceLocation value) {
      this.set(Balm.getConfig().getLocalConfig(this.parentSchema()), value);
   }
}
