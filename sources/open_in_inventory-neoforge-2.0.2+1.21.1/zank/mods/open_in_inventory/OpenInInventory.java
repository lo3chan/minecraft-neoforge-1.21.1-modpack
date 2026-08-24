package zank.mods.open_in_inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.api.ScreenClearedEvent;
import zank.mods.open_in_inventory.impl.OpenActionRegistryImpl;
import zank.mods.open_in_inventory.impl.compat.CommonOpenInInventoryPlugin;
import zank.mods.open_in_inventory.impl.compat.ProvideConfigOpenAction;
import zank.mods.open_in_inventory.impl.crt.ProvideCraftTweakerOpenAction;
import zank.mods.open_in_inventory.impl.handler.ActionHandler;
import zank.mods.open_in_inventory.impl.handler.ClientCommand;
import zank.mods.open_in_inventory.impl.handler.ClientEventHandler;

public abstract class OpenInInventory {
   public static final String ID = "open_in_inventory";
   public static final Logger LOGGER = LogManager.getLogger("open_in_inventory");
   public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
   public static OpenInInventory COMMON;
   public static OpenInInventoryConfig CONFIG = new OpenInInventoryConfig();
   public static Path CONFIG_PATH;
   public static final OpenActionRegistry ACTION_REGISTRY = new OpenActionRegistryImpl();
   public final ActionHandler actionHandler = new ActionHandler();

   public OpenInInventory() {
      this.registerPlugin(OpenInInventoryPlugin.REGISTRY_EXPOSED_CUZ_LAZINESS);
      CONFIG_PATH = Platform.getConfigFolder().resolve("open_in_inventory.json");
      if (Platform.getEnvironment() == Env.CLIENT) {
         ClientTooltipEvent.ITEM.register(this.actionHandler::tooltip);
         ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(this.actionHandler::beforeMouseClicked);
         ClientTickEvent.CLIENT_LEVEL_PRE.register(this.actionHandler::tick);
         ScreenClearedEvent.EVENT.register(this.actionHandler::screenClosed);
         ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(ClientEventHandler::clientStarted);
         ClientCommandRegistrationEvent.EVENT.register(ClientCommand::register);
      }
   }

   protected void registerPlugin(List<OpenInInventoryPlugin> plugins) {
      plugins.add(new CommonOpenInInventoryPlugin());
      plugins.add(new ProvideConfigOpenAction());
      if (Platform.isModLoaded("crafttweaker")) {
         plugins.add(new ProvideCraftTweakerOpenAction());
      }
   }

   public static boolean isScreenBlackListed(Screen screen) {
      if (screen == null) {
         return true;
      } else {
         Set<String> blacklist = CONFIG.screenBlacklist();
         return !blacklist.isEmpty() && blacklist.contains(screen.getClass().getName());
      }
   }

   public static boolean isShiftPressed(Minecraft client) {
      long handle = client.getWindow().getWindow();
      return InputConstants.isKeyDown(handle, 340) || InputConstants.isKeyDown(handle, 344);
   }

   public static void refreshConfig() {
      if (Files.exists(CONFIG_PATH)) {
         try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
            CONFIG = (OpenInInventoryConfig)GSON.fromJson(reader, OpenInInventoryConfig.class);
         } catch (IOException var6) {
            LOGGER.error("Error when reading config", var6);
         }
      }

      try {
         CONFIG.write(CONFIG_PATH);
      } catch (IOException var3) {
         LOGGER.error("Error when writing config", var3);
      }

      OpenActionRegistryImpl registry = (OpenActionRegistryImpl)ACTION_REGISTRY;
      registry.replaceTemplates.clear();

      for (OpenInInventoryPlugin plugin : OpenInInventoryPlugin.REGISTRY_EXPOSED_CUZ_LAZINESS) {
         plugin.registerReplaceTemplate(registry.replaceTemplates);
      }

      for (Entry<String, Collection<String>> entry : registry.replaceTemplates.entrySet()) {
         entry.setValue(List.copyOf(entry.getValue()));
      }

      registry.internal.clear();

      for (OpenInInventoryPlugin plugin : OpenInInventoryPlugin.REGISTRY_EXPOSED_CUZ_LAZINESS) {
         plugin.registerAction(ACTION_REGISTRY);
      }

      for (Entry<Item, List<OpenAction>> entry : registry.internal.entrySet()) {
         entry.setValue(List.copyOf(entry.getValue()));
      }
   }
}
