package net.mehvahdjukaar.amendments.integration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public final class FlanCompat {
   public static boolean canBreak(@NotNull Player player, @NotNull BlockPos pos) {
      return true;
   }

   public static boolean canPlace(@NotNull Player player, @NotNull BlockPos pos) {
      return true;
   }

   public static boolean canReplace(@NotNull Player player, @NotNull BlockPos pos) {
      return true;
   }

   public static boolean canAttack(@NotNull Player player, @NotNull Entity victim) {
      return true;
   }

   public static boolean canInteract(@NotNull Player player, @NotNull BlockPos targetPos) {
      return true;
   }
}
