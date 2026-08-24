package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.perk.data.ServerPerkData;
import com.aetherteam.nitrogen.api.users.UserData.Server;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PerkHooks {
   public static void refreshPerks(Player player) {
      if (player instanceof ServerPlayer serverPlayer && !Server.getStoredUsers().containsKey(serverPlayer.getGameProfile().getId())) {
         ServerPerkData.MOA_SKIN_INSTANCE.removePerk(serverPlayer.getServer(), serverPlayer.getGameProfile().getId());
         ServerPerkData.HALO_INSTANCE.removePerk(serverPlayer.getServer(), serverPlayer.getGameProfile().getId());
         ServerPerkData.DEVELOPER_GLOW_INSTANCE.removePerk(serverPlayer.getServer(), serverPlayer.getGameProfile().getId());
      }
   }
}
