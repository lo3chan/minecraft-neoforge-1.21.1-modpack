package net.blay09.mods.balm.api.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JavaOps;
import java.util.ArrayList;
import java.util.List;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;

public record LoadedTableConfig(Table<String, String, Object> table) implements MutableLoadedConfig, PropertyAwareConfig {
   public LoadedTableConfig() {
      this(HashBasedTable.create());
   }

   public static Pair<LoadedTableConfig, List<Throwable>> of(BalmConfigSchema schema, Table<String, String, Object> table) {
      HashBasedTable<String, String, Object> validatedTable = HashBasedTable.create();
      ArrayList<Throwable> errors = new ArrayList<>();

      for (ConfiguredProperty<?> rootProperty : schema.rootProperties()) {
         try {
            Object value = validate(rootProperty, table);
            validatedTable.put(rootProperty.category(), rootProperty.name(), value);
         } catch (Throwable var10) {
            validatedTable.put(rootProperty.category(), rootProperty.name(), rootProperty.defaultValue());
            errors.add(var10);
         }
      }

      for (ConfigCategory category : schema.categories()) {
         for (ConfiguredProperty<?> property : category.properties()) {
            try {
               Object value = validate(property, table);
               validatedTable.put(property.category(), property.name(), value);
            } catch (Throwable var9) {
               validatedTable.put(property.category(), property.name(), property.defaultValue());
               errors.add(var9);
            }
         }
      }

      return Pair.of(new LoadedTableConfig(validatedTable), errors);
   }

   private static <T> T validate(ConfiguredProperty<T> property, Table<String, String, Object> table) {
      Object value = table.get(property.category(), property.name());
      return (T)((Pair)property.codec().decode(JavaOps.INSTANCE, value).getOrThrow()).getFirst();
   }

   @Override
   public <T> void setRaw(ConfiguredProperty<T> property, T value) {
      if (property.type().isAssignableFrom(value.getClass())) {
         this.table.put(property.category(), property.name(), value);
      } else {
         throw new IllegalArgumentException(
            "Invalid type for property "
               + property.name()
               + " in category "
               + property.category()
               + ": "
               + value.getClass().getName()
               + ", expected "
               + property.type().getName()
         );
      }
   }

   @Override
   public MutableLoadedConfig copy() {
      return new LoadedTableConfig(HashBasedTable.create(this.table));
   }

   @Override
   public MutableLoadedConfig mutable(BalmConfigSchema schema) {
      return this;
   }

   @Override
   public <T> T getRaw(ConfiguredProperty<T> property) {
      Object value = this.table.get(property.category(), property.name());
      if (value == null) {
         return property.defaultValue();
      } else {
         return (T)(!property.type().isAssignableFrom(value.getClass()) ? property.defaultValue() : value);
      }
   }

   @Override
   public boolean hasProperty(ConfiguredProperty<?> property) {
      return this.table.contains(property.category(), property.name());
   }
}
