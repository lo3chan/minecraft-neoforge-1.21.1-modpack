package org.dimdev.limlib.api;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;

public interface INetworking {
   <T extends CustomPacketPayload> void sendPacket(ServerPlayer var1, T var2);

   <T extends CustomPacketPayload> void sendPacket(T var1);

   <T extends CustomPacketPayload> void registerServerPacket(
      Type<T> var1, StreamCodec<RegistryFriendlyByteBuf, T> var2, BiFunction<T, ServerPlayer, ? extends CustomPacketPayload> var3
   );

   <T extends CustomPacketPayload> void registerClientPacket(Type<T> var1, StreamCodec<RegistryFriendlyByteBuf, T> var2, Consumer<T> var3);
}
