package net.bettercombat.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class CombatFlags {
   public static final String DISABLED_TAG = "bettercombat_disabled";
   public static final int TAG_DISABLED = 1;
   public static final int API_DISABLED = 2;

   private CombatFlags() {
   }

   public static byte get(Player player) {
      return ((EntityPlayer_BetterCombat)player).getCombatFlags();
   }

   public static void set(ServerPlayer player, byte flags) {
      ((EntityPlayer_BetterCombat)player).setCombatFlags(flags);
   }

   public static boolean isAttackDisabled(Player player) {
      return (get(player) & 3) != 0;
   }

   public static void setAttacksDisabled(ServerPlayer player, boolean disabled) {
      byte flags = get(player);
      set(player, (byte)(disabled ? flags | 2 : flags & -3));
   }
}
