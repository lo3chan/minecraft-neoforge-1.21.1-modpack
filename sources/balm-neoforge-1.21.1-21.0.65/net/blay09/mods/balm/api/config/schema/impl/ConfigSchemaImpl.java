package net.blay09.mods.balm.api.config.schema.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.blay09.mods.balm.api.config.DefaultedConfig;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfigSchemaBuilder;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategoryBuilder;
import net.blay09.mods.balm.api.config.schema.builder.ConfigPropertyBuilder;
import net.minecraft.resources.ResourceLocation;

public class ConfigSchemaImpl implements BalmConfigSchema, ConfigSchemaBuilder {
   private final ResourceLocation identifier;
   private final Map<String, ConfigCategory> categories = new HashMap<>();
   private final Map<String, ConfiguredProperty<?>> rootProperties = new HashMap<>();
   private final Table<String, String, ConfiguredProperty<?>> properties = HashBasedTable.create();

   public ConfigSchemaImpl(ResourceLocation identifier) {
      this.identifier = identifier;
   }

   @Override
   public ResourceLocation identifier() {
      return this.identifier;
   }

   @Override
   public ConfigPropertyBuilder property(String name) {
      return new ConfigPropertyBuilder(this, name);
   }

   @Override
   public ConfigCategoryBuilder category(String name) {
      ConfigCategoryImpl category = new ConfigCategoryImpl(this, name);
      this.categories.put(name, category);
      return category;
   }

   @Override
   public LoadedConfig defaults() {
      return DefaultedConfig.INSTANCE;
   }

   @Override
   public Collection<ConfiguredProperty<?>> rootProperties() {
      return this.rootProperties.values();
   }

   @Override
   public Collection<ConfigCategory> categories() {
      return this.categories.values();
   }

   @Override
   public ConfiguredProperty<?> findProperty(String category, String property) {
      return (ConfiguredProperty<?>)this.properties.get(category, property);
   }

   @Override
   public ConfiguredProperty<?> findRootProperty(String property) {
      return (ConfiguredProperty<?>)this.properties.get("", property);
   }

   public <T extends ConfiguredProperty<?>> T addAndReturn(T property) {
      this.properties.put(property.category(), property.name(), property);
      if (property.category().isEmpty()) {
         this.rootProperties.put(property.name(), property);
      } else if (this.categories.get(property.category()) instanceof ConfigCategoryImpl category) {
         category.addProperty(property);
      }

      return property;
   }
}
