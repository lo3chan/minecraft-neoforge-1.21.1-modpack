package net.mcreator.undeadrevamp.procedures;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class QueenbeeperfumeCanUseRangedItemProcedure {
   public static boolean execute(Entity entity) {
      return entity == null
         ? false
         : (new Object() {
               public boolean checkGamemode(Entity _ent) {
                  if (_ent instanceof ServerPlayer _serverPlayer) {
                     return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                  } else {
                     return _ent.level().isClientSide() && _ent instanceof Player _player
                        ? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                           && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE
                        : false;
                  }
               }
            })
            .checkGamemode(entity);
   }
}
