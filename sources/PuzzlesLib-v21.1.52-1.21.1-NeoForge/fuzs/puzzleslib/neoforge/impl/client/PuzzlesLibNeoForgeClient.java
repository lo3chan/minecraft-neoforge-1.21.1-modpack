package fuzs.puzzleslib.neoforge.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import fuzs.puzzleslib.impl.content.client.PuzzlesLibClientDevelopment;
import fuzs.puzzleslib.neoforge.impl.client.commands.NeoForgeConfigCommand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(
   value = "puzzleslib",
   dist = {Dist.CLIENT}
)
public class PuzzlesLibNeoForgeClient {
   public PuzzlesLibNeoForgeClient() {
      ClientModConstructor.construct("puzzleslib", PuzzlesLibClient::new);
      if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration("puzzleslib")) {
         ClientModConstructor.construct(PuzzlesLibMod.id("client/development"), PuzzlesLibClientDevelopment::new);
      }

      registerEventHandlers(NeoForge.EVENT_BUS);
   }

   private static void registerEventHandlers(IEventBus eventBus) {
      if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration("puzzleslib")) {
         eventBus.addListener(
            evt -> NeoForgeConfigCommand.register(evt.getDispatcher(), (commandSourceStack, component) -> commandSourceStack.sendSuccess(() -> component, true))
         );
      }
   }
}
