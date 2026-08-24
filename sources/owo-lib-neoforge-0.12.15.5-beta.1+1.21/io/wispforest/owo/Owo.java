package io.wispforest.owo;

import io.wispforest.owo.command.debug.OwoDebugCommands;
import io.wispforest.owo.network.neoforge.NeoOwoNetworking;
import io.wispforest.owo.ops.LootOps;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owo.util.Wisdom;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Mod("owo")
public class Owo {
   public static final boolean DEBUG;
   public static final Logger LOGGER = LogManager.getLogger("owo");
   public static final Component PREFIX = Component.empty()
      .withStyle(ChatFormatting.GRAY)
      .append(TextOps.withColor("o", 3757541))
      .append(TextOps.withColor("ω", 1287920))
      .append(TextOps.withColor("o", 3757541))
      .append(Component.literal(" > ").withStyle(ChatFormatting.GRAY));
   @Nullable
   private static IEventBus MOD_BUS;

   public Owo(IEventBus modBus) {
      MOD_BUS = modBus;
      LootOps.registerListener();
      modBus.addListener(event -> OwoFreezer.freeze());
      Wisdom.spread();
      modBus.addListener(NeoOwoNetworking::onNetworkRegister);
      if (DEBUG) {
         OwoDebugCommands.register(modBus);
      }
   }

   @Internal
   public static void debugWarn(Logger logger, String message) {
      if (DEBUG) {
         logger.warn(message);
      }
   }

   @Internal
   public static void debugWarn(Logger logger, String message, Object... params) {
      if (DEBUG) {
         logger.warn(message, params);
      }
   }

   public static MinecraftServer currentServer() {
      return ServerLifecycleHooks.getCurrentServer();
   }

   public static IEventBus getModBus() {
      return Objects.requireNonNull(MOD_BUS, "Mod bus attempted to be gotten before time!");
   }

   static {
      boolean debug = !FMLLoader.isProduction();
      if (System.getProperty("owo.debug") != null) {
         debug = Boolean.getBoolean("owo.debug");
      }

      if (Boolean.getBoolean("owo.forceDisableDebug")) {
         LOGGER.warn("Deprecated system property 'owo.forceDisableDebug=true' was used - use 'owo.debug=false' instead");
         debug = false;
      }

      DEBUG = debug;
      MOD_BUS = null;
   }
}
