package fuzs.puzzleslib.api.network.v4.message.play;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import fuzs.puzzleslib.api.network.v4.message.Message;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public interface ServerboundPlayMessage extends Message<ServerboundPlayMessage.Context> {
   @Override
   default Packet<ServerCommonPacketListener> toPacket() {
      return NetworkingHelper.toServerboundPacket(this);
   }

   public interface Context extends Message.Context<ServerGamePacketListenerImpl> {
      MinecraftServer server();

      ServerPlayer player();

      ServerLevel level();
   }
}
