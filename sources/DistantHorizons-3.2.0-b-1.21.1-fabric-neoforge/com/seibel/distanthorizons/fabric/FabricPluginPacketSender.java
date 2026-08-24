package com.seibel.distanthorizons.fabric;

import com.seibel.distanthorizons.common.AbstractPluginPacketSender_fabric;
import com.seibel.distanthorizons.common.CommonPacketPayload_fabric;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_3222;

public class FabricPluginPacketSender extends AbstractPluginPacketSender_fabric {
   @Override
   public void sendToServer(AbstractNetworkMessage message) {
      ClientPlayNetworking.send(new CommonPacketPayload_fabric(message));
   }

   @Override
   public void sendToClient(class_3222 serverPlayer, AbstractNetworkMessage message) {
      ServerPlayNetworking.send(serverPlayer, new CommonPacketPayload_fabric(message));
   }
}
