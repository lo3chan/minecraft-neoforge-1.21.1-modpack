package codx.codxlib.api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class CodxNotify {
   private CodxNotify() {
   }

   public static MutableComponent link(String url) {
      return link(url, url);
   }

   public static MutableComponent link(String label, String url) {
      return Component.literal(label)
         .withStyle(style -> style.withColor(ChatFormatting.AQUA).withUnderlined(true).withClickEvent(new ClickEvent(Action.OPEN_URL, url)));
   }

   public static MutableComponent prefixed(String chatPrefix, String message) {
      return Component.literal("§7" + chatPrefix + " §r").append(message);
   }

   public static void toPlayer(ServerPlayer player, Component message) {
      if (player != null && message != null) {
         player.sendSystemMessage(message);
      }
   }

   public static void toConsole(MinecraftServer server, Component message) {
      if (server != null && message != null) {
         server.sendSystemMessage(message);
      }
   }

   public static void toOperators(MinecraftServer server, Component message) {
      if (server != null && message != null) {
         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (server.isSingleplayer() || player.hasPermissions(2)) {
               player.sendSystemMessage(message);
            }
         }
      }
   }
}
