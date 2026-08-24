package fuzs.puzzleslib.api.network.v4;

import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.api.network.v4.message.Message;
import fuzs.puzzleslib.api.util.v1.EntityHelper;
import java.util.Objects;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.jetbrains.annotations.ApiStatus.Experimental;

public final class MessageSender {
   private MessageSender() {
   }

   public static void broadcast(Message<? extends Message.Context<? extends ServerCommonPacketListener>> message) {
      Objects.requireNonNull(message, "message is null");
      broadcast(message);
   }

   @Experimental
   public static void broadcast(CustomPacketPayload payload) {
      Objects.requireNonNull(payload, "payload is null");
      if (NetworkingHelper.hasChannel(NetworkingHelper.getClientPacketListener(), payload.type())) {
         broadcast(NetworkingHelper.toServerboundPacket(payload));
      }
   }

   public static void broadcast(Packet<?> packet) {
      Objects.requireNonNull(packet, "packet is null");
      ClientGamePacketListener packetListener = NetworkingHelper.getClientPacketListener();
      Connection connection = NetworkingHelper.getConnection(packetListener);
      connection.send(packet);
   }

   public static void broadcast(PlayerSet playerSet, Message<? extends Message.Context<? extends ClientCommonPacketListener>> message) {
      Objects.requireNonNull(playerSet, "player set is null");
      Objects.requireNonNull(message, "message is null");
      broadcast(playerSet, message);
   }

   @Experimental
   public static void broadcast(PlayerSet playerSet, CustomPacketPayload payload) {
      Objects.requireNonNull(playerSet, "player set is null");
      Objects.requireNonNull(payload, "payload is null");
      broadcast(serverPlayerConsumer -> playerSet.apply(serverPlayer -> {
         if (NetworkingHelper.hasChannel(serverPlayer.connection, payload.type())) {
            serverPlayerConsumer.accept(serverPlayer);
         }
      }), NetworkingHelper.toClientboundPacket(payload));
   }

   public static void broadcast(PlayerSet playerSet, Packet<?> packet) {
      Objects.requireNonNull(playerSet, "player set is null");
      Objects.requireNonNull(packet, "packet is null");
      playerSet.apply(serverPlayer -> {
         if (!EntityHelper.isFakePlayer(serverPlayer)) {
            serverPlayer.connection.send(packet);
         }
      });
   }
}
