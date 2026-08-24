package com.iafenvoy.jupiter._loader.neoforge;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.compat.ExtraConfigManager;
import com.iafenvoy.jupiter.internal.ConfigButtonReplaceStrategy;
import com.iafenvoy.jupiter.internal.JupiterSettings;
import com.iafenvoy.jupiter.render.internal.JupiterConfigListScreen;
import java.util.Optional;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber({Dist.CLIENT})
public class JupiterNeoForgeClient {
   @SubscribeEvent
   public static void processClient(FMLClientSetupEvent event) {
      Jupiter.processClient();
      ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (minecraft, parent) -> new JupiterConfigListScreen(parent));
      ExtraConfigManager.registerScanCallback(JupiterNeoForgeClient::fillExtensionPoints);
   }

   public static void fillExtensionPoints() {
      ConfigButtonReplaceStrategy strategy = JupiterSettings.INSTANCE.general.configButtonReplacement.getValue();
      if (strategy != ConfigButtonReplaceStrategy.NEVER) {
         for (String id : ExtraConfigManager.getProvidedMods()) {
            Optional<? extends ModContainer> optional = ModList.get().getModContainerById(id);
            if (!optional.isEmpty()) {
               ModContainer container = optional.get();
               if (strategy != ConfigButtonReplaceStrategy.UNAVAILABLE_ONLY || !container.getCustomExtension(IConfigScreenFactory.class).isPresent()) {
                  container.registerExtensionPoint(
                     IConfigScreenFactory.class, (IConfigScreenFactory)(c, parent) -> ExtraConfigManager.getScreen(id).apply(parent)
                  );
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void registerClientListener(RegisterClientReloadListenersEvent event) {
      event.registerReloadListener(ConfigManager.getInstance());
   }
}
