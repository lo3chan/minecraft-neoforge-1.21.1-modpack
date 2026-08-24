package com.iafenvoy.jupiter.network;

import com.iafenvoy.jupiter._loader.neoforge.network.ClientNetworkHelperImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public interface ClientNetworkHelper {
   ClientNetworkHelper INSTANCE = new ClientNetworkHelperImpl();

   void sendToServer(CustomPacketPayload var1);

   <T extends CustomPacketPayload> void registerReceiver(Type<T> var1, ClientNetworkHelper.Handler<T> var2);

   public interface Handler<T extends CustomPacketPayload> {
      Runnable handle(Minecraft var1, T var2);
   }
}
