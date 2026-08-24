package fuzs.puzzleslib.api.network.v4.message;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public interface Message<T extends Message.Context<?>> extends CustomPacketPayload {
   default Type<?> type() {
      return NetworkingHelper.getPayloadType(this.getClass());
   }

   Packet<?> toPacket();

   MessageListener<T> getListener();

   public interface Context<T extends PacketListener> {
      T packetListener();

      void reply(CustomPacketPayload var1);

      void disconnect(Component var1);
   }
}
