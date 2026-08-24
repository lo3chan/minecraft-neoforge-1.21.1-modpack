package io.wispforest.owo.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record ServerAccess(ServerPlayer player) implements OwoNetChannel.EnvironmentAccess<ServerPlayer, MinecraftServer, ServerGamePacketListenerImpl> {
   public MinecraftServer runtime() {
      return this.player.server;
   }

   public ServerGamePacketListenerImpl netHandler() {
      return this.player.connection;
   }
}
