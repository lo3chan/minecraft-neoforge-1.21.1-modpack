package com.mrcrayfish.configured.client;

import com.mrcrayfish.configured.Config;
import com.mrcrayfish.configured.Constants;
import com.mrcrayfish.configured.api.ConfigType;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.api.ModContext;
import com.mrcrayfish.configured.api.util.ConfigScreenHelper;
import com.mrcrayfish.configured.client.screen.TooltipScreen;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(
   modid = "configured",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ClientConfigured {
   public static void generateConfigFactories() {
      Constants.LOG.info("Creating config GUI factories...");
      ModList.get()
         .forEachModContainer(
            (modId, container) -> {
               if (!container.getCustomExtension(IConfigScreenFactory.class).isPresent() || Config.isForceConfiguredMenu()) {
                  Map<ConfigType, Set<IModConfig>> modConfigMap = ClientHandler.createConfigMap(new ModContext(modId));
                  if (!modConfigMap.isEmpty()) {
                     int count = modConfigMap.values().stream().mapToInt(Set::size).sum();
                     Constants.LOG.info("Registering config factory for mod {}. Found {} config(s)", modId, count);
                     String displayName = container.getModInfo().getDisplayName();
                     container.registerExtensionPoint(
                        IConfigScreenFactory.class,
                        (IConfigScreenFactory)(mc, screen) -> ConfigScreenHelper.createSelectionScreen(screen, Component.literal(displayName), modConfigMap)
                     );
                  }
               }
            }
         );
   }

   @SubscribeEvent
   private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
      event.register(ClientHandler.KEY_OPEN_MOD_LIST);
   }

   @SubscribeEvent
   private static void onRegisterTooltipComponentFactory(RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(TooltipScreen.ListMenuTooltipComponent.class, TooltipScreen.ListMenuTooltipComponent::asClientTextTooltip);
   }
}
