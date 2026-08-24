package net.mehvahdjukaar.moonlight.api.platform.network.platform;

import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.platform.MoonlightForge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.TypeAndCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;

public class NetworkHelperImpl {
   public static void addNetworkRegistration(Consumer<NetworkHelper.RegisterMessagesEvent> eventListener, int version) {
      Consumer<RegisterPayloadHandlersEvent> eventConsumer = event -> {
         final String versionStr = version + "";
         NetworkHelper.RegisterMessagesEvent registerMessagesEvent = new NetworkHelper.RegisterMessagesEvent() {
            @Override
            public <M extends Message> void registerServerBound(TypeAndCodec<RegistryFriendlyByteBuf, M> messageType) {
               event.registrar(messageType.type().id().getPath())
                  .versioned(versionStr)
                  .executesOn(HandlerThread.MAIN)
                  .playToServer(messageType.type(), messageType.codec(), (m, c) -> m.handle(new NetworkHelperImpl.ContextWrapper(c)));
            }

            @Override
            public <M extends Message> void registerClientBound(TypeAndCodec<RegistryFriendlyByteBuf, M> messageType) {
               event.registrar(messageType.type().id().getPath())
                  .versioned(versionStr)
                  .executesOn(HandlerThread.MAIN)
                  .playToClient(messageType.type(), messageType.codec(), (m, c) -> m.handle(new NetworkHelperImpl.ContextWrapper(c)));
            }

            @Override
            public <M extends Message> void registerClientBoundOptional(TypeAndCodec<RegistryFriendlyByteBuf, M> messageType) {
               NetworkHelper.markOptional(messageType.type());
               event.registrar(messageType.type().id().getPath())
                  .versioned(versionStr)
                  .optional()
                  .executesOn(HandlerThread.MAIN)
                  .playToClient(messageType.type(), messageType.codec(), (m, c) -> m.handle(new NetworkHelperImpl.ContextWrapper(c)));
            }

            @Override
            public <M extends Message> void registerBidirectional(TypeAndCodec<RegistryFriendlyByteBuf, M> messageType) {
               event.registrar(messageType.type().id().getPath())
                  .versioned(versionStr)
                  .executesOn(HandlerThread.MAIN)
                  .playBidirectional(messageType.type(), messageType.codec(), (m, c) -> m.handle(new NetworkHelperImpl.ContextWrapper(c)));
            }
         };
         eventListener.accept(registerMessagesEvent);
      };
      MoonlightForge.getCurrentBus().addListener(eventConsumer);
   }

   public static boolean canSendToPlayer(ServerPlayer player, Type<?> type) {
      return player.connection.hasChannel(type);
   }

   public static boolean serverHasChannel(Type<?> type) {
      return !PlatHelper.getPhysicalSide().isClient() ? false : NetworkHelperImplClient.serverHasChannel(type);
   }

   public static void sendToClientPlayer(ServerPlayer serverPlayer, CustomPacketPayload message) {
      if (!NetworkHelper.isOptional(message.type()) || canSendToPlayer(serverPlayer, message.type())) {
         PacketDistributor.sendToPlayer(serverPlayer, message, new CustomPacketPayload[0]);
      }
   }

   public static void sendToAllClientPlayers(CustomPacketPayload message) {
      PacketDistributor.sendToAllPlayers(message, new CustomPacketPayload[0]);
   }

   public static void sendToAllClientPlayersInRange(ServerLevel level, BlockPos pos, double radius, CustomPacketPayload message) {
      PacketDistributor.sendToPlayersNear(level, null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius, message, new CustomPacketPayload[0]);
   }

   public static void sendToAllClientPlayersTrackingEntity(Entity target, CustomPacketPayload message) {
      PacketDistributor.sendToPlayersTrackingEntity(target, message, new CustomPacketPayload[0]);
   }

   public static void sendToAllClientPlayersTrackingChunk(ServerLevel level, ChunkPos pos, CustomPacketPayload message) {
      PacketDistributor.sendToPlayersTrackingChunk(level, pos, message, new CustomPacketPayload[0]);
   }

   public static void sendToAllClientPlayersTrackingEntityAndSelf(Entity target, Message message) {
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, message, new CustomPacketPayload[0]);
   }

   public static void sendToServer(CustomPacketPayload message) {
      PacketDistributor.sendToServer(message, new CustomPacketPayload[0]);
   }

   private record ContextWrapper(IPayloadContext c) implements Message.Context {
      @Override
      public Message.NetworkDir getDirection() {
         PacketFlow flow = this.c.connection().getDirection();
         return flow == PacketFlow.SERVERBOUND ? Message.NetworkDir.SERVER_BOUND : Message.NetworkDir.CLIENT_BOUND;
      }

      @Override
      public Player getPlayer() {
         return this.c.player();
      }

      @Override
      public void disconnect(Component reason) {
         this.c.disconnect(reason);
      }

      @Override
      public void reply(CustomPacketPayload payload) {
         this.c.reply(payload);
      }
   }
}
