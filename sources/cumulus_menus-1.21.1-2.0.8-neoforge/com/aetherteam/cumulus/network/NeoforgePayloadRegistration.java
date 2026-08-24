package com.aetherteam.cumulus.network;

import com.aetherteam.cumulus.network.api.PayloadRegistration;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record NeoforgePayloadRegistration(PayloadRegistrar registrar) implements PayloadRegistration {
   @Override
   public <T extends CustomPacketPayload> PayloadRegistration playToClient(
      Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> reader, PayloadRegistration.PayloadHandler<T> handler
   ) {
      this.registrar.playToClient(type, reader, convert(handler));
      return this;
   }

   @Override
   public <T extends CustomPacketPayload> PayloadRegistration playBidirectional(
      Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> reader, PayloadRegistration.PayloadHandler<T> handler
   ) {
      this.registrar.playBidirectional(type, reader, convert(handler));
      return this;
   }

   @Override
   public <T extends CustomPacketPayload> PayloadRegistration playToServer(
      Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> reader, PayloadRegistration.PayloadHandler<T> handler
   ) {
      this.registrar.playToServer(type, reader, convert(handler));
      return this;
   }

   private static <T extends CustomPacketPayload> IPayloadHandler<T> convert(PayloadRegistration.PayloadHandler<T> handler) {
      return (payload, context) -> handler.handle((T)payload, context.player());
   }
}
