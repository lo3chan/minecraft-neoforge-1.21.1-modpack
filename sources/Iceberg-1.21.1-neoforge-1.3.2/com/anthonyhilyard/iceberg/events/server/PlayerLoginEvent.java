package com.anthonyhilyard.iceberg.events.server;

import com.anthonyhilyard.iceberg.events.ToggleableEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerLoginEvent {
   ToggleableEvent<PlayerLoginEvent> EVENT = ToggleableEvent.create(PlayerLoginEvent.class, listeners -> (serverPlayer, server) -> {
      for (PlayerLoginEvent listener : listeners) {
         listener.playerLogin(serverPlayer, server);
      }
   });

   void playerLogin(ServerPlayer var1, MinecraftServer var2);
}
