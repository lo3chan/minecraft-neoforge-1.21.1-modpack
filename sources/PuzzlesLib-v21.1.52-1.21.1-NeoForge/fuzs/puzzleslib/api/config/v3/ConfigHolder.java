package fuzs.puzzleslib.api.config.v3;

import fuzs.puzzleslib.impl.config.ConfigHolderRegistry;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;

public interface ConfigHolder {
   static ConfigHolder.Builder builder(String modId) {
      return ModContext.get(modId).getConfigHolder();
   }

   <T extends ConfigCore> ConfigDataHolder<T> getHolder(Class<T> var1);

   default <T extends ConfigCore> T get(Class<T> clazz) {
      return this.getHolder(clazz).getConfig();
   }

   static UnaryOperator<String> getSimpleNameFactory() {
      return modId -> modId + ".toml";
   }

   static UnaryOperator<String> getDefaultNameFactory(String configType) {
      return modId -> modId + "-" + configType + ".toml";
   }

   static UnaryOperator<String> getDirectoryNameFactory(String configType, String directory) {
      return modId -> Paths.get(directory, getDefaultNameFactory(configType).apply(modId)).toString();
   }

   static void registerConfigurationScreen(String modId, String... otherModIds) {
      ProxyImpl.get().registerConfigurationScreen(modId, otherModIds);
   }

   public interface Builder extends ConfigHolderRegistry {
      <T extends ConfigCore> ConfigHolder.Builder client(Class<T> var1);

      <T extends ConfigCore> ConfigHolder.Builder common(Class<T> var1);

      <T extends ConfigCore> ConfigHolder.Builder server(Class<T> var1);

      <T extends ConfigCore> ConfigHolder.Builder setFileName(Class<T> var1, UnaryOperator<String> var2);
   }
}
