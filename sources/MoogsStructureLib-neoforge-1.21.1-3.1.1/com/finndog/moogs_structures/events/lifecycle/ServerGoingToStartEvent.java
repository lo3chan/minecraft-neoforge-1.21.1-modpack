package com.finndog.moogs_structures.events.lifecycle;

import com.finndog.moogs_structures.events.base.EventHandler;
import net.minecraft.server.MinecraftServer;

public record ServerGoingToStartEvent(MinecraftServer server) {
   public static final EventHandler<ServerGoingToStartEvent> EVENT = new EventHandler<>();

   public MinecraftServer getServer() {
      return this.server;
   }
}
