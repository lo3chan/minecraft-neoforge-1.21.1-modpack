package com.iafenvoy.jupiter.network;

import com.iafenvoy.jupiter._loader.neoforge.network.ServerNetworkHelperImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface ServerNetworkHelper {
   ServerNetworkHelper INSTANCE = new ServerNetworkHelperImpl();

   void sendToPlayer(ServerPlayer var1, CustomPacketPayload var2);

   <T extends CustomPacketPayload> void registerPayloadType(Type<T> var1, StreamCodec<FriendlyByteBuf, T> var2);

   <T extends CustomPacketPayload> void registerReceiver(Type<T> var1, ServerNetworkHelper.Handler<T> var2);

   public interface Handler<T extends CustomPacketPayload> {
      Runnable handle(MinecraftServer var1, ServerPlayer var2, T var3);
   }
}
