package dev.corgitaco.corgilib.neoforge;

import corgitaco.corgilib.CorgiLib;
import dev.corgitaco.corgilib.neoforge.platform.NeoForgePlatform;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;

@Mod("corgilib")
public class CorgiLibNeoForge {
   public CorgiLibNeoForge(IEventBus modEventBus) {
      CorgiLib.init();
      NeoForgePlatform.CACHED.values().forEach(deferredRegister -> deferredRegister.register(modEventBus));
      modEventBus.addListener(
         NewRegistry.class, newRegistry -> NeoForgePlatform.DATAPACK_REGISTRIES.forEach(newRegistryConsumer -> newRegistryConsumer.accept(newRegistry))
      );
      modEventBus.addListener(
         NewRegistryEvent.class, newRegistry -> NeoForgePlatform.NEW_REGISTRIES.forEach(newRegistryConsumer -> newRegistryConsumer.accept(newRegistry))
      );
   }
}
