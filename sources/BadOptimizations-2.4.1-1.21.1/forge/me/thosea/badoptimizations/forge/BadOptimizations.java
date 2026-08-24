package forge.me.thosea.badoptimizations.forge;

import forge.me.thosea.badoptimizations.config.BOConfigScreen;
import forge.me.thosea.badoptimizations.utils.PlatformMethods;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("badoptimizations")
public class BadOptimizations {
   public BadOptimizations(IEventBus bus, ModContainer container) {
      if (!PlatformMethods.isOnServer()) {
         BadOptimizations.ClientInit.init(container);
      }
   }

   private static class ClientInit {
      private static void init(ModContainer container) {
         container.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(client, parent) -> new BOConfigScreen(parent));
      }
   }
}
