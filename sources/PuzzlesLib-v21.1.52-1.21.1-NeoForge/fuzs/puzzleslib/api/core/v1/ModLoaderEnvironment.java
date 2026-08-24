package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.impl.core.ModContext;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public interface ModLoaderEnvironment {
   ModLoaderEnvironment INSTANCE = ServiceProviderHelper.load(ModLoaderEnvironment.class);

   ModLoader getModLoader();

   boolean isClient();

   boolean isServer();

   Path getGameDirectory();

   Path getModsDirectory();

   Path getConfigDirectory();

   String getCurrentMappingsNamespace();

   boolean isDevelopmentEnvironment();

   boolean isDataGeneration();

   default boolean isDevelopmentEnvironmentWithoutDataGeneration(String modId) {
      return this.isDataGeneration() ? false : this.isDevelopmentEnvironment(modId);
   }

   default boolean isDevelopmentEnvironment(String modId) {
      return !this.isDevelopmentEnvironment() ? false : Boolean.getBoolean(modId + ".isDevelopmentEnvironment");
   }

   Map<String, ModContainer> getModList();

   default boolean isModLoaded(String modId) {
      return this.getModList().containsKey(modId);
   }

   default Optional<ModContainer> getModContainer(String modId) {
      return Optional.ofNullable(this.getModList().get(modId));
   }

   default boolean isModPresentServerside(String modId) {
      return ModContext.getModContexts().containsKey(modId) && ModContext.getModContexts().get(modId).isPresentServerside();
   }
}
