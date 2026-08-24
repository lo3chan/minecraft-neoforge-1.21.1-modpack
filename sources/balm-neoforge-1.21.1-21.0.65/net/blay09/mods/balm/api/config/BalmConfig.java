package net.blay09.mods.balm.api.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.blay09.mods.balm.api.config.reflection.LoadedReflectionConfig;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.network.ConfigReflection;
import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface BalmConfig {
   @Deprecated
   private static BalmConfigProperty<?> createConfigProperty(BalmConfigData configData, Field categoryField, Field propertyField, BalmConfigData defaultConfig) {
      return new BalmConfigPropertyImpl(configData, categoryField, propertyField, defaultConfig);
   }

   @Deprecated
   private static boolean isPropertyType(Class<?> type) {
      return type.isPrimitive()
         || type == String.class
         || type == Integer.class
         || type == Boolean.class
         || type == Float.class
         || type == Double.class
         || type == List.class
         || type == Set.class
         || type == ResourceLocationException.class
         || Enum.class.isAssignableFrom(type);
   }

   @Deprecated
   private static <T> T createConfigDataInstance(Class<T> clazz) {
      try {
         return clazz.newInstance();
      } catch (IllegalAccessException | InstantiationException var2) {
         throw new IllegalArgumentException("Config class or sub-class missing a public no-arg constructor.", var2);
      }
   }

   void registerConfig(BalmConfigSchema var1);

   BalmConfigSchema getSchema(ResourceLocation var1);

   MutableLoadedConfig getLocalConfig(ResourceLocation var1);

   LoadedConfig getActiveConfig(ResourceLocation var1);

   File getConfigDir();

   default File getConfigFile(BalmConfigSchema schema) {
      ResourceLocation identifier = schema.identifier();
      return new File(this.getConfigDir(), identifier.getNamespace() + "-" + identifier.getPath() + ".toml");
   }

   default MutableLoadedConfig getLocalConfig(BalmConfigSchema schema) {
      return this.getLocalConfig(schema.identifier());
   }

   <T> void updateLocalConfig(Class<T> var1, Consumer<T> var2);

   default LoadedConfig getActiveConfig(BalmConfigSchema schema) {
      return this.getActiveConfig(schema.identifier());
   }

   default BalmConfigSchema registerConfig(Class<?> configDataClass) {
      BalmConfigSchema schema = ConfigReflection.schemaOf(configDataClass);
      this.registerConfig(schema);
      return schema;
   }

   default BalmConfigSchema getSchema(Class<?> configDataClass) {
      return this.getSchema(ConfigReflection.getIdentifier(configDataClass));
   }

   default <T> T getActiveConfig(Class<T> configDataClass) {
      LoadedConfig loadedConfig = this.getActiveConfig(this.getSchema(configDataClass));
      return ConfigReflection.of(configDataClass, loadedConfig).data();
   }

   Collection<BalmConfigSchema> getSchemasByNamespace(String var1);

   Collection<BalmConfigSchema> getSchemas();

   default void saveLocalConfig(BalmConfigSchema schema) {
      this.saveLocalConfig(schema, this.getLocalConfig(schema));
   }

   void saveLocalConfig(BalmConfigSchema var1, MutableLoadedConfig var2);

   void onConfigAvailable(BalmConfigSchema var1, Consumer<MutableLoadedConfig> var2);

   default <T> void onConfigAvailable(Class<T> configDataClass, Consumer<T> handler) {
      this.onConfigAvailable(this.getSchema(configDataClass), config -> handler.accept(this.getActiveConfig(configDataClass)));
   }

   @Deprecated
   default <T extends BalmConfigData> T initializeBackingConfig(Class<T> clazz) {
      this.registerConfig(clazz);
      return this.getBackingConfig(clazz);
   }

   @Deprecated
   default <T extends BalmConfigData> T getBackingConfig(Class<T> clazz) {
      BalmConfigSchema schema = this.getSchema(clazz);
      MutableLoadedConfig localConfig = this.getLocalConfig(schema);
      LoadedReflectionConfig<T> reflectionConfig = ConfigReflection.of(clazz, localConfig);
      return reflectionConfig.data();
   }

   @Deprecated
   default <T extends BalmConfigData> void saveBackingConfig(Class<T> clazz) {
      this.saveLocalConfig(this.getSchema(clazz));
   }

   @Deprecated
   default <T extends BalmConfigData> T getActive(Class<T> clazz) {
      return this.getActiveConfig(clazz);
   }

   @Deprecated
   default <T extends BalmConfigData> void handleSync(Player player, SyncConfigMessage<T> message) {
   }

   @Deprecated
   default <T extends BalmConfigData> void registerConfig(Class<T> clazz, Function<T, SyncConfigMessage<T>> syncMessageFactory) {
      this.registerConfig(clazz);
   }

   @Deprecated
   default <T extends BalmConfigData> void updateConfig(Class<T> clazz, Consumer<T> consumer) {
      this.updateLocalConfig(clazz, consumer);
   }

   @Deprecated
   default <T extends BalmConfigData> void resetToBackingConfig(Class<T> clazz) {
   }

   @Deprecated
   default void resetToBackingConfigs() {
   }

   @Deprecated
   default File getConfigFile(String configName) {
      return new File(this.getConfigDir(), configName + "-common.toml");
   }

   @Deprecated
   default <T extends BalmConfigData> Table<String, String, BalmConfigProperty<?>> getConfigProperties(Class<T> clazz) {
      T backingConfig = this.getBackingConfig(clazz);
      T defaultConfig = (T)createConfigDataInstance(clazz);
      Table<String, String, BalmConfigProperty<?>> properties = HashBasedTable.create();

      for (Field rootField : ConfigReflection.getAllFields(clazz)) {
         String category = "";
         Class<?> fieldType = rootField.getType();
         if (isPropertyType(fieldType)) {
            String property = rootField.getName();
            properties.put(category, property, createConfigProperty(backingConfig, null, rootField, defaultConfig));
         } else {
            category = rootField.getName();

            for (Field propertyField : ConfigReflection.getAllFields(fieldType)) {
               String property = propertyField.getName();
               properties.put(category, property, createConfigProperty(backingConfig, rootField, propertyField, defaultConfig));
            }
         }
      }

      return properties;
   }

   @Deprecated
   default <T extends BalmConfigData> String getConfigName(Class<T> clazz) {
      return ConfigReflection.getIdentifier(clazz).getNamespace();
   }

   @Deprecated
   default List<? extends BalmConfigData> getConfigsByMod(String modId) {
      return this.getSchemasByNamespace(modId)
         .stream()
         .map(this::getActiveConfig)
         .filter(it -> it instanceof BalmConfigData)
         .map(it -> (BalmConfigData)it)
         .toList();
   }
}
