package net.blay09.mods.balm.api.permission;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public interface PermissionContext {
   Optional<ServerPlayer> getPlayer();

   Optional<UUID> getPlayerUUID();

   Optional<CommandSourceStack> getCommandSource();
}
