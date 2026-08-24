package dev.shadowsoffire.placebo.network;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;

public interface PayloadProvider<T extends CustomPacketPayload> {
   Type<T> getType();

   StreamCodec<? super RegistryFriendlyByteBuf, T> getCodec();

   void handle(T var1, IPayloadContext var2);

   List<ConnectionProtocol> getSupportedProtocols();

   Optional<PacketFlow> getFlow();

   String getVersion();

   default boolean isOptional() {
      return false;
   }

   default HandlerThread getHandlerThread() {
      return HandlerThread.MAIN;
   }
}
