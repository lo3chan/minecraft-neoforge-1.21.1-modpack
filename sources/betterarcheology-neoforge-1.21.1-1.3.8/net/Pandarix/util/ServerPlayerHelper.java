package net.Pandarix.util;

import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;

public class ServerPlayerHelper {
   public static Optional<ServerPlayer> tryGetServerPlayer(Player player) {
      return player instanceof ServerPlayer serverPlayer ? Optional.of(serverPlayer) : Optional.empty();
   }

   public static void tryOpenScreen(Player pPlayer, MenuProvider pMenuProvider) {
      tryGetServerPlayer(pPlayer).ifPresent(serverPlayer -> serverPlayer.openMenu(pMenuProvider));
   }
}
