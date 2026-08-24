package dev.corgitaco.enhancedcelestials2shaders.neoforge;

import dev.corgitaco.enhancedcelestials2core.neoforge.platform.NeoForgeRegistrationService;
import java.util.Map;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("enhancedcelestials2shaders")
public class EnhancedCelestialsShadersNeoForge {
   public EnhancedCelestialsShadersNeoForge(IEventBus bus) {
      NeoForgeRegistrationService.CACHED
         .getOrDefault("enhancedcelestials2shaders", Map.of())
         .values()
         .forEach(deferredRegister -> deferredRegister.register(bus));
   }
}
