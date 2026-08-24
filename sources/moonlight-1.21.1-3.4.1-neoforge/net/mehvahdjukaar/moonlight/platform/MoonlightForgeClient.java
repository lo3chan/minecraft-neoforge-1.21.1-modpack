package net.mehvahdjukaar.moonlight.platform;

import java.util.HashSet;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.DynamicTextureRenderer;
import net.mehvahdjukaar.moonlight.api.client.texture_renderer.RenderedTexturesManager;
import net.mehvahdjukaar.moonlight.api.entity.IControllableVehicle;
import net.mehvahdjukaar.moonlight.api.misc.SidedInstance;
import net.mehvahdjukaar.moonlight.api.misc.fake_level.FakeLevelManager;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.core.ClientConfigs;
import net.mehvahdjukaar.moonlight.core.MoonlightClient;
import net.mehvahdjukaar.moonlight.core.client.config.MoonlightConfigSelectScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class MoonlightForgeClient {
   public static void init(IEventBus modEventBus) {
      modEventBus.addListener(MoonlightForgeClient::afterLoad);
      modEventBus.addListener(EventPriority.LOWEST, MoonlightForgeClient::onTextureStitch);
      NeoForge.EVENT_BUS.addListener(MoonlightForgeClient::onPlayerLoggedOut);
      NeoForge.EVENT_BUS.addListener(MoonlightForgeClient::onInputUpdate);
      NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, MoonlightForgeClient::itemTooltipEvent);
   }

   public static void onPlayerLoggedOut(LoggingOut event) {
      FakeLevelManager.invalidateAll();
      DynamicTextureRenderer.clearCache();
      RenderedTexturesManager.clearCache();
      LocalPlayer player = event.getPlayer();
      if (player != null) {
         SidedInstance.clearAll(player.registryAccess());
      }
   }

   public static void itemTooltipEvent(ItemTooltipEvent event) {
      MoonlightClient.onItemTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip());
   }

   public static void afterLoad(FMLLoadCompleteEvent event) {
      if (ClientConfigs.CUSTOM_CONFIG_SCREEN.get()) {
         Set<String> registered = new HashSet<>();

         for (ModConfigHolder config : ModConfigHolder.getTrackedHolders()) {
            String modId = config.getModId();
            if (registered.add(modId)) {
               ModList.get()
                  .getModContainerById(modId)
                  .ifPresent(c -> c.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(container, parent) -> {
                     Screen screen = MoonlightConfigSelectScreen.create(modId, parent, null);
                     return (Screen)(screen != null ? screen : new ConfigurationScreen(container, parent));
                  }));
            }
         }
      }
   }

   public static void onTextureStitch(TextureAtlasStitchedEvent event) {
      MoonlightClient.afterTextureReload();
   }

   public static void onInputUpdate(MovementInputUpdateEvent event) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.player.getVehicle() instanceof IControllableVehicle listener) {
         Input movementInput = event.getInput();
         listener.onInputUpdate(
            movementInput.left, movementInput.right, movementInput.up, movementInput.down, mc.options.keySprint.isDown(), movementInput.jumping
         );
      }
   }
}
