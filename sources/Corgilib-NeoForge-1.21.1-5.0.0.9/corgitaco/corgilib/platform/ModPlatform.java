package corgitaco.corgilib.platform;

import com.mojang.serialization.Codec;
import corgitaco.corgilib.CorgiLib;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface ModPlatform {
   ModPlatform PLATFORM = load(ModPlatform.class);

   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();

   Collection<String> getModIDS();

   Path configDir();

   default Path modConfigDir() {
      return this.configDir().resolve("corgilib");
   }

   static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      CorgiLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }

   <T> Supplier<T> register(Registry<T> var1, String var2, Supplier<T> var3);

   <T> Supplier<Registry<T>> createSimpleBuiltin(ResourceKey<Registry<T>> var1);

   <T> void registerDatapackRegistry(ResourceKey<Registry<T>> var1, Supplier<Codec<T>> var2);
}
