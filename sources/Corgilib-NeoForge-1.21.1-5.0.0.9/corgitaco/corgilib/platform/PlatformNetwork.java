package corgitaco.corgilib.platform;

import corgitaco.corgilib.network.Packet;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;

public interface PlatformNetwork {
   PlatformNetwork NETWORK = ModPlatform.load(PlatformNetwork.class);

   <P extends Packet> void sendToClient(ServerPlayer var1, P var2);

   default <P extends Packet> void sendToAllClients(List<ServerPlayer> players, P packet) {
      for (ServerPlayer player : players) {
         this.sendToClient(player, packet);
      }
   }

   <P extends Packet> void sendToServer(P var1);
}
