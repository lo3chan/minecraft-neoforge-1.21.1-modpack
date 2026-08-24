package com.github.alexthe666.alexsmobs.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AMNeoSend {
   public static void toServer(CustomPacketPayload payload) {
      PacketDistributor.sendToServer(payload, new CustomPacketPayload[0]);
   }

   public static <T extends CustomPacketPayload> void registerPlay(
      PayloadRegistrar registrar, Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, IPayloadHandler<T> handler
   ) {
      registrar.playBidirectional(type, codec, handler);
   }
}
