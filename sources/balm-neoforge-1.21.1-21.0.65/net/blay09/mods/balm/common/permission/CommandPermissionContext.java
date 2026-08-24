package net.blay09.mods.balm.common.permission;

import java.util.Optional;
import java.util.UUID;
import net.blay09.mods.balm.api.permission.PermissionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public record CommandPermissionContext(CommandSourceStack source) implements PermissionContext {
   @Override
   public Optional<ServerPlayer> getPlayer() {
      return Optional.ofNullable(this.source.getPlayer());
   }

   @Override
   public Optional<UUID> getPlayerUUID() {
      return Optional.ofNullable(this.source.getPlayer()).map(Entity::getUUID);
   }

   @Override
   public Optional<CommandSourceStack> getCommandSource() {
      return Optional.of(this.source);
   }
}
