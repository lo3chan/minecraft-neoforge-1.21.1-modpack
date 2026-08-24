package mezz.jei.common.network;

import mezz.jei.common.network.packets.PlayToServerPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public interface IConnectionToServer {
   boolean isJeiOnServer();

   boolean isSameModLoader();

   boolean canSendPacket(Type<?> var1);

   <T extends PlayToServerPacket<T>> void sendPacketToServer(T var1);

   default void onRuntimeStopped() {
   }
}
