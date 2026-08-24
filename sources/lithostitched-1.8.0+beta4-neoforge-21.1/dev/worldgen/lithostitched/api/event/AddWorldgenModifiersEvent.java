package dev.worldgen.lithostitched.api.event;

import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.event.LithostitchedEvent;
import java.util.function.BiConsumer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public interface AddWorldgenModifiersEvent {
   LithostitchedEvent<AddWorldgenModifiersEvent> EVENT = new LithostitchedEvent<>(callbacks -> (registries, consumer) -> {
      for (AddWorldgenModifiersEvent callback : callbacks) {
         callback.addModifiers(registries, consumer);
      }
   });

   void addModifiers(RegistryAccess var1, BiConsumer<ResourceLocation, WorldgenModifier> var2);
}
