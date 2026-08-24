package net.blay09.mods.balm.neoforge.config;

import com.google.common.collect.Table;
import net.blay09.mods.balm.api.config.LoadedTableConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public record LoadedNeoForgeConfig(BalmConfigSchema schema, ModConfig modConfig, Table<String, String, ConfigValue<?>> properties)
   implements MutableLoadedConfig {
   @Override
   public <T> void setRaw(ConfiguredProperty<T> property, T value) {
      ConfigValue<T> backingProperty = (ConfigValue<T>)this.properties.get(property.category(), property.name());
      if (backingProperty != null) {
         Object mappedValue = NeoForgeBalmConfig.mapConfigValueToNeoForge(value);
         backingProperty.set(mappedValue);
      }
   }

   @Override
   public MutableLoadedConfig copy() {
      LoadedTableConfig newConfig = new LoadedTableConfig();
      newConfig.applyFrom(this.schema, this);
      return newConfig;
   }

   @Override
   public <T> T getRaw(ConfiguredProperty<T> property) {
      ConfigValue<?> backingProperty = (ConfigValue<?>)this.properties.get(property.category(), property.name());
      if (backingProperty != null) {
         Object value = backingProperty.get();
         return (T)NeoForgeBalmConfig.mapConfigValueFromNeoForge(property, value);
      } else {
         return property.defaultValue();
      }
   }

   @Override
   public MutableLoadedConfig mutable(BalmConfigSchema schema) {
      return this;
   }
}
