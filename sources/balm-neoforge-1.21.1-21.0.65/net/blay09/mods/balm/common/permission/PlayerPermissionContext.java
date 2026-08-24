package net.blay09.mods.balm.common.permission;

import java.util.Optional;
import java.util.UUID;
import net.blay09.mods.balm.api.permission.PermissionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public record PlayerPermissionContext(ServerPlayer player) implements PermissionContext {
   @Override
   public Optional<ServerPlayer> getPlayer() {
      return Optional.of(this.player);
   }

   @Override
   public Optional<UUID> getPlayerUUID() {
      return Optional.of(this.player.getUUID());
   }

   @Override
   public Optional<CommandSourceStack> getCommandSource() {
      return Optional.of(this.player.createCommandSourceStack());
   }
}
