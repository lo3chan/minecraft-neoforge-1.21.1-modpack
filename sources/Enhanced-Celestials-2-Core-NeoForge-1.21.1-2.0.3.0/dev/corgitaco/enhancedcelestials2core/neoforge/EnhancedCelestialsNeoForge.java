package dev.corgitaco.enhancedcelestials2core.neoforge;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.core.ECRegistries;
import dev.corgitaco.enhancedcelestials2core.neoforge.client.EnhancedCelestialsNeoForgeClient;
import dev.corgitaco.enhancedcelestials2core.neoforge.platform.NeoForgeRegistrationService;
import dev.corgitaco.enhancedcelestials2core.server.commands.ECCommands;
import java.util.Map;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod("enhancedcelestials2core")
public class EnhancedCelestialsNeoForge {
   public EnhancedCelestialsNeoForge(IEventBus bus) {
      ECRegistries.loadClasses();
      bus.addListener(this::commonSetup);
      if (FMLEnvironment.dist == Dist.CLIENT) {
         bus.addListener(EnhancedCelestialsNeoForgeClient::clientSetup);
      }

      bus.addListener(NeoForgeRegistrationService::registerBuiltinRegistries);
      bus.addListener(event -> NeoForgeRegistrationService.DATAPACK_REGISTRIES.forEach(newRegistryConsumer -> newRegistryConsumer.accept(event)));
      NeoForgeRegistrationService.CACHED.getOrDefault("enhancedcelestials2core", Map.of()).values().forEach(deferredRegister -> deferredRegister.register(bus));
      NeoForge.EVENT_BUS.addListener(this::registerCommands);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      EnhancedCelestials.commonSetup();
   }

   private void registerCommands(RegisterCommandsEvent event) {
      ECCommands.register(event.getDispatcher());
   }
}
