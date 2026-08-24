package fuzs.puzzleslib.api.network.v4.message.play;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import fuzs.puzzleslib.api.network.v4.message.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;

public interface ClientboundPlayMessage extends Message<ClientboundPlayMessage.Context> {
   @Override
   default Packet<ClientCommonPacketListener> toPacket() {
      return NetworkingHelper.toClientboundPacket(this);
   }

   public interface Context extends Message.Context<ClientPacketListener> {
      Minecraft client();

      LocalPlayer player();

      ClientLevel level();
   }
}
