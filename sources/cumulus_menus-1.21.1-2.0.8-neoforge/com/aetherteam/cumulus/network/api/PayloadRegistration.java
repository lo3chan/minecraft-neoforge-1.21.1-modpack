package com.aetherteam.cumulus.network.api;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;

public interface PayloadRegistration {
   default <T extends CustomPacketPayload> PayloadRegistration playToClient(
      Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> reader, PayloadRegistration.PayloadHandler<T> handler
   ) {
      return this;
   }

   <T extends CustomPacketPayload> PayloadRegistration playToServer(
      Type<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2, PayloadRegistration.PayloadHandler<T> var3
   );

   <T extends CustomPacketPayload> PayloadRegistration playBidirectional(
      Type<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2, PayloadRegistration.PayloadHandler<T> var3
   );

   public interface PayloadHandler<T extends CustomPacketPayload> {
      void handle(T var1, Player var2);
   }
}
