package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;

public interface NetworkingProxy extends Proxy {
   boolean hasChannel(PacketListener var1, Type<?> var2);

   @Override
   default ClientPacketListener getClientPacketListener() {
      throw new RuntimeException("Client connection accessed for wrong side!");
   }

   Connection getConnection(PacketListener var1);

   Packet<ClientCommonPacketListener> toClientboundPacket(CustomPacketPayload var1);

   Packet<ServerCommonPacketListener> toServerboundPacket(CustomPacketPayload var1);

   void finishConfigurationTask(ServerConfigurationPacketListener var1, net.minecraft.server.network.ConfigurationTask.Type var2);
}
