package dev.corgitaco.dataanchor.neoforge;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.neoforge.registry.NeoforgeRegistryHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;

@Mod("dataanchor")
public class DataAnchorNeoForge {
   public DataAnchorNeoForge(IEventBus eventBus) {
      DataAnchor.init();
      eventBus.addListener(
         NewRegistry.class, newRegistry -> NeoforgeRegistryHelper.DATAPACK_REGISTRIES.forEach(newRegistryConsumer -> newRegistryConsumer.accept(newRegistry))
      );
      eventBus.addListener(
         NewRegistryEvent.class, newRegistry -> NeoforgeRegistryHelper.NEW_REGISTRIES.forEach(newRegistryConsumer -> newRegistryConsumer.accept(newRegistry))
      );
      NeoforgeRegistryHelper.CACHED.forEach((resourceKey, deferredRegister) -> deferredRegister.register(eventBus));
   }
}
