package net.blay09.mods.balm.neoforge.core.internal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class NeoForgeCustomRegistryRegistrar {
   private final List<Registry<?>> registries = new ArrayList<>();

   public <T> void add(Registry<T> registry) {
      this.registries.add(registry);
   }

   @SubscribeEvent
   public void registerRegistries(NewRegistryEvent event) {
      for (Registry<?> registry : this.registries) {
         event.register(registry);
      }
   }
}
