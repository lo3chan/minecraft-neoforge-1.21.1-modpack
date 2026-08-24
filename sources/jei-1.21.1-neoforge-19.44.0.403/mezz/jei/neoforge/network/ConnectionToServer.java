package mezz.jei.neoforge.network;

import java.util.UUID;
import mezz.jei.common.network.ClientConnectionHelper;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.network.packets.PacketDeletePlayerItem;
import mezz.jei.common.network.packets.PlayToServerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public final class ConnectionToServer implements IConnectionToServer {
   private static final String NEOFORGE_SERVER_BRAND = "neoforge";
   @Nullable
   private static UUID jeiOnServerCacheUuid = null;
   private static boolean jeiOnServerCacheValue = false;

   @Override
   public boolean isJeiOnServer() {
      return this.canSendPacket(PacketDeletePlayerItem.TYPE);
   }

   @Override
   public boolean isSameModLoader() {
      return ClientConnectionHelper.hasServerBrand("neoforge");
   }

   @Override
   public boolean canSendPacket(Type<?> packetType) {
      Minecraft minecraft = Minecraft.getInstance();
      ClientPacketListener clientPacketListener = minecraft.getConnection();
      if (clientPacketListener != null && clientPacketListener.getConnection().isConnected()) {
         UUID id = clientPacketListener.getId();
         if (!id.equals(jeiOnServerCacheUuid)) {
            jeiOnServerCacheUuid = id;
            jeiOnServerCacheValue = clientPacketListener.hasChannel(PacketDeletePlayerItem.TYPE);
         }

         return jeiOnServerCacheValue && clientPacketListener.hasChannel(packetType);
      } else {
         return false;
      }
   }

   @Override
   public <T extends PlayToServerPacket<T>> void sendPacketToServer(T packet) {
      Minecraft minecraft = Minecraft.getInstance();
      ClientPacketListener netHandler = minecraft.getConnection();
      if (netHandler != null && this.canSendPacket(packet.type())) {
         PacketDistributor.sendToServer(packet, new CustomPacketPayload[0]);
      }
   }

   @Override
   public void onRuntimeStopped() {
      jeiOnServerCacheUuid = null;
      jeiOnServerCacheValue = false;
   }
}
