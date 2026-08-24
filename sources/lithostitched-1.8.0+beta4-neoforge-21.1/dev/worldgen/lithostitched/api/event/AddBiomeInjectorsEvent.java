package dev.worldgen.lithostitched.api.event;

import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.impl.event.LithostitchedEvent;
import java.util.function.BiConsumer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public interface AddBiomeInjectorsEvent {
   LithostitchedEvent<AddBiomeInjectorsEvent> EVENT = new LithostitchedEvent<>(callbacks -> (registries, consumer) -> {
      for (AddBiomeInjectorsEvent callback : callbacks) {
         callback.addInjectors(registries, consumer);
      }
   });

   void addInjectors(RegistryAccess var1, BiConsumer<ResourceLocation, BiomeInjector> var2);
}
