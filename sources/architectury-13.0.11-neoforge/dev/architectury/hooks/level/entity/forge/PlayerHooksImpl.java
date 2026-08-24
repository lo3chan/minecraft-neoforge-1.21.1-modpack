package dev.architectury.hooks.level.entity.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerHooksImpl {
   public static boolean isFake(Player playerEntity) {
      return playerEntity instanceof ServerPlayer && playerEntity.getClass() != ServerPlayer.class;
   }
}
