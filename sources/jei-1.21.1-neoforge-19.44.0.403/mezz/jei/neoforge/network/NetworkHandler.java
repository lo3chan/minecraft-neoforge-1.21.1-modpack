package mezz.jei.neoforge.network;

import java.util.function.BiConsumer;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IServerConfig;
import mezz.jei.common.network.ClientPacketContext;
import mezz.jei.common.network.IConnectionToClient;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PacketCheatPermission;
import mezz.jei.common.network.packets.PacketDeletePlayerItem;
import mezz.jei.common.network.packets.PacketGiveItemStack;
import mezz.jei.common.network.packets.PacketRecipeTransfer;
import mezz.jei.common.network.packets.PacketRecipeTransferCounted;
import mezz.jei.common.network.packets.PacketRequestCheatPermission;
import mezz.jei.common.network.packets.PacketSetHotbarItemStack;
import mezz.jei.common.network.packets.PlayToClientPacket;
import mezz.jei.common.network.packets.PlayToServerPacket;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;

public class NetworkHandler {
   private final String protocolVersion;
   private final IServerConfig serverConfig;
   private final IConnectionToServer connectionToServer;
   private final IConnectionToClient connectionToClient;

   public NetworkHandler(String protocolVersion, IServerConfig serverConfig) {
      this.protocolVersion = protocolVersion;
      this.serverConfig = serverConfig;
      this.connectionToServer = new ConnectionToServer();
      Internal.setServerConnection(this.connectionToServer);
      this.connectionToClient = new ConnectionToClient();
   }

   public void registerPacketHandlers(PermanentEventSubscriptions subscriptions) {
      subscriptions.register(
         RegisterPayloadHandlersEvent.class,
         ev -> ev.registrar(this.protocolVersion)
            .executesOn(HandlerThread.MAIN)
            .optional()
            .playToServer(PacketDeletePlayerItem.TYPE, PacketDeletePlayerItem.STREAM_CODEC, this.wrapServerHandler(PacketDeletePlayerItem::process))
            .playToServer(PacketGiveItemStack.TYPE, PacketGiveItemStack.STREAM_CODEC, this.wrapServerHandler(PacketGiveItemStack::process))
            .playToServer(PacketRecipeTransfer.TYPE, PacketRecipeTransfer.STREAM_CODEC, this.wrapServerHandler(PacketRecipeTransfer::process))
            .playToServer(
               PacketRecipeTransferCounted.TYPE, PacketRecipeTransferCounted.STREAM_CODEC, this.wrapServerHandler(PacketRecipeTransferCounted::process)
            )
            .playToServer(PacketSetHotbarItemStack.TYPE, PacketSetHotbarItemStack.STREAM_CODEC, this.wrapServerHandler(PacketSetHotbarItemStack::process))
            .playToServer(
               PacketRequestCheatPermission.TYPE, PacketRequestCheatPermission.STREAM_CODEC, this.wrapServerHandler(PacketRequestCheatPermission::process)
            )
            .playToClient(PacketCheatPermission.TYPE, PacketCheatPermission.STREAM_CODEC, this.wrapClientHandler(PacketCheatPermission::process))
      );
   }

   private <T extends PlayToClientPacket<T>> IPayloadHandler<T> wrapClientHandler(BiConsumer<T, ClientPacketContext> consumer) {
      return (t, payloadContext) -> {
         LocalPlayer player = (LocalPlayer)payloadContext.player();
         ClientPacketContext clientPacketContext = new ClientPacketContext(player, this.connectionToServer);
         payloadContext.enqueueWork(() -> consumer.accept((T)t, clientPacketContext));
      };
   }

   private <T extends PlayToServerPacket<T>> IPayloadHandler<T> wrapServerHandler(BiConsumer<T, ServerPacketContext> consumer) {
      return (t, payloadContext) -> {
         ServerPlayer player = (ServerPlayer)payloadContext.player();
         ServerPacketContext serverPacketContext = new ServerPacketContext(player, this.serverConfig, this.connectionToClient);
         payloadContext.enqueueWork(() -> consumer.accept((T)t, serverPacketContext));
      };
   }

   public IConnectionToServer getConnectionToServer() {
      return this.connectionToServer;
   }
}
