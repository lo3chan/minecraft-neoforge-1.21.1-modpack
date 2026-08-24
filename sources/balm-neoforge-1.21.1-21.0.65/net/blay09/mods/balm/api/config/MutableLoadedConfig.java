package net.blay09.mods.balm.api.config;

import java.util.function.Predicate;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;

public interface MutableLoadedConfig extends LoadedConfig {
   <T> void setRaw(ConfiguredProperty<T> var1, T var2);

   default void applyFrom(BalmConfigSchema schema, LoadedConfig config, Predicate<ConfiguredProperty<?>> propertyFilter) {
      for (ConfiguredProperty<?> rootProperty : schema.rootProperties()) {
         if (propertyFilter.test(rootProperty)) {
            this.setRaw(rootProperty, config.getRaw(rootProperty));
         }
      }

      for (ConfigCategory category : schema.categories()) {
         for (ConfiguredProperty<?> property : category.properties()) {
            if (propertyFilter.test(property)) {
               this.setRaw(property, config.getRaw(property));
            }
         }
      }
   }

   default void applyFrom(BalmConfigSchema schema, LoadedConfig config) {
      this.applyFrom(schema, config, it -> true);
   }

   MutableLoadedConfig copy();
}
