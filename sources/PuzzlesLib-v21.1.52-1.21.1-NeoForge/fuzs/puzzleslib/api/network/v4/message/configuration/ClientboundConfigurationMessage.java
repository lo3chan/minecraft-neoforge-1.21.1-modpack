package fuzs.puzzleslib.api.network.v4.message.configuration;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import fuzs.puzzleslib.api.network.v4.message.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;

public interface ClientboundConfigurationMessage extends Message<ClientboundConfigurationMessage.Context> {
   @Override
   default Packet<ClientCommonPacketListener> toPacket() {
      return NetworkingHelper.toClientboundPacket(this);
   }

   public interface Context extends Message.Context<ClientConfigurationPacketListenerImpl> {
      Minecraft client();
   }
}
