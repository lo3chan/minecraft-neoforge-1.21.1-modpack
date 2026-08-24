package dev.corgitaco.enhancedcelestials2defaultlunarevents.neoforge;

import dev.corgitaco.enhancedcelestials2core.neoforge.platform.NeoForgeRegistrationService;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.DefaultLunarEventsRegistries;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("enhancedcelestials2defaultlunarevents")
public class EnhancedCelestialsDefaultLunarEventsNeoForge {
   public EnhancedCelestialsDefaultLunarEventsNeoForge(IEventBus bus) {
      DefaultLunarEventsRegistries.loadClasses();
      NeoForgeRegistrationService.CACHED
         .getOrDefault("enhancedcelestials2defaultlunarevents", Map.of())
         .values()
         .forEach(deferredRegister -> deferredRegister.register(bus));
   }
}
