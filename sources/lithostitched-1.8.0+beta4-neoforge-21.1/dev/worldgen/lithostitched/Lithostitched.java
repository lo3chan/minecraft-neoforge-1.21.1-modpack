package dev.worldgen.lithostitched;

import dev.worldgen.apollib.Apollib;
import dev.worldgen.apollib.config.ApollibConfigHolder;
import dev.worldgen.apollib.registry.ApollibRegistrar;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.config.ConfigState;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lithostitched {
   public static final String MOD_ID = "lithostitched";
   public static final Logger LOGGER = LoggerFactory.getLogger("lithostitched");
   public static final ApollibConfigHolder<ConfigState> CONFIG = Apollib.createConfigHolder(
      id("lithostitched"), ApollibConfigHolder.CONFIG_DIRECTORY.resolve("lithostitched.json"), ConfigState.CODEC, ConfigState.DEFAULT
   );
   public static final ApollibRegistrar REGISTRAR = Apollib.createRegistrar("lithostitched");

   public static void init() {
      CONFIG.load();
      LithostitchedBuiltInRegistries.init();
      REGISTRAR.registerAll();
   }

   public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> resourceKey, String name) {
      return ResourceKey.create(resourceKey, id(name));
   }

   public static ResourceLocation id(String name) {
      return ResourceLocation.fromNamespaceAndPath("lithostitched", name);
   }

   public static ResourceLocation vanillaToLithostitched(ResourceLocation id) {
      return id.getNamespace().equals("minecraft") ? id(id.getPath()) : id;
   }

   public static <T> Registry<T> registry(RegistryAccess registries, ResourceKey<? extends Registry<T>> key) {
      return registries.registryOrThrow(key);
   }

   public static boolean breaksSeedParity() {
      return ((ConfigState)CONFIG.getState()).breaksSeedParity;
   }

   public static void debug(String message, Object... arguments) {
      if (((ConfigState)CONFIG.getState()).logDebugMessages) {
         LOGGER.warn(message, arguments);
      }
   }
}
