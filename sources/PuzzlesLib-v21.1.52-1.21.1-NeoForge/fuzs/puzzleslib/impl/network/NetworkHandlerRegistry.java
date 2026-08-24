package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;

public interface NetworkHandlerRegistry extends NetworkHandler {
   @Deprecated
   @Override
   <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> var1);

   @Deprecated
   @Override
   <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> var1);

   @Deprecated
   @Override
   <T> void sendMessage(PlayerSet var1, ClientboundMessage<T> var2);

   @Deprecated
   @Override
   <T> void sendMessage(ServerboundMessage<T> var1);
}
