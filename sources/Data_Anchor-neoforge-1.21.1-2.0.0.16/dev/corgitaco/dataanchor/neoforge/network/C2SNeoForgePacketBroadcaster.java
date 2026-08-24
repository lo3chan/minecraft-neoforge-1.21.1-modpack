package dev.corgitaco.dataanchor.neoforge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.C2SPacketBroadcaster;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

@AutoService({C2SPacketBroadcaster.class})
public class C2SNeoForgePacketBroadcaster implements C2SPacketBroadcaster {
   @Override
   public <MSG extends Packet> void sendToServer(MSG msg) {
      PacketDistributor.sendToServer(msg, new CustomPacketPayload[0]);
   }

   @Override
   public void registerPackets() {
   }
}
