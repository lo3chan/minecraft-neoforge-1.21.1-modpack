package codx.codxlib.neoforge;

import codx.codxlib.api.command.CodxCommands;
import codx.codxlib.api.network.CodxNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.neoforged.neoforge.common.NeoForge;

public final class CodxLibNeoForgeClient {
   private CodxLibNeoForgeClient() {
   }

   public static void init() {
      NeoForge.EVENT_BUS.addListener(event -> CodxCommands.buildClientInto(event.getDispatcher(), source -> source::sendSystemMessage));
      CodxNetwork.setClientSender(payload -> {
         ClientPacketListener connection = Minecraft.getInstance().getConnection();
         if (connection != null) {
            connection.send(new ServerboundCustomPayloadPacket(payload));
         }
      });
   }
}
