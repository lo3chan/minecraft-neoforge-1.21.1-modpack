package net.mehvahdjukaar.moonlight.api.platform.network.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class NetworkHelperImplClient {
   public static boolean serverHasChannel(Type<?> type) {
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      return connection != null && connection.hasChannel(type);
   }
}
