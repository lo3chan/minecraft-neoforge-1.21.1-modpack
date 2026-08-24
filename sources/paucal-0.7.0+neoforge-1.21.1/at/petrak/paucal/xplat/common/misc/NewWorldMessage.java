package at.petrak.paucal.xplat.common.misc;

import at.petrak.paucal.xplat.common.ModRegistries;
import at.petrak.paucal.xplat.common.advancement.BeContributorTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class NewWorldMessage {
   public static void onLogin(Player player) {
      if (player instanceof ServerPlayer splayer) {
         ((BeContributorTrigger)ModRegistries.BE_CONTRIBUTOR_TRIGGER.get()).trigger(splayer);
      }
   }
}
