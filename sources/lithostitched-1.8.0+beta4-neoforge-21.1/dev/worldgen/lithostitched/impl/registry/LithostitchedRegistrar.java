package dev.worldgen.lithostitched.impl.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.platform.neoforge.LithostitchedNeoforge;
import dev.worldgen.lithostitched.platform.neoforge.LithostitchedRegistrations;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class LithostitchedRegistrar {
   public static <T> void registerRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
      LithostitchedRegistrations.DYNAMIC_REGISTRIES.add(LithostitchedNeoforge.registerDynamicRegistry(key, codec));
   }

   public static <T> void register(Registry<T> key, Map<String, T> entries) {
      for (Entry<String, T> entry : entries.entrySet()) {
         Lithostitched.REGISTRAR.register(key, entry.getKey(), entry.getValue());
      }
   }
}
