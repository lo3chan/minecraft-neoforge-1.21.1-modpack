package com.seibel.distanthorizons.neoforge;

import com.seibel.distanthorizons.common.AbstractPluginPacketSender_neoforge;
import com.seibel.distanthorizons.common.CommonPacketPayload$Codec_neoforge;
import com.seibel.distanthorizons.common.CommonPacketPayload_neoforge;
import com.seibel.distanthorizons.common.wrappers.misc.ServerPlayerWrapper_neoforge;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NeoforgePluginPacketSender extends AbstractPluginPacketSender_neoforge {
   private static BiConsumer<IServerPlayerWrapper, AbstractNetworkMessage> packetConsumer;

   public static void setPacketHandler(RegisterPayloadHandlersEvent event, Consumer<AbstractNetworkMessage> consumer) {
      setPacketHandler(event, (player, buffer) -> consumer.accept(buffer));
   }

   public static void setPacketHandler(RegisterPayloadHandlersEvent event, BiConsumer<IServerPlayerWrapper, AbstractNetworkMessage> consumer) {
      packetConsumer = consumer;
      PayloadRegistrar registrar = event.registrar("1").optional();
      registrar.playBidirectional(
         CommonPacketPayload_neoforge.TYPE,
         new CommonPacketPayload$Codec_neoforge(),
         (payload, context) -> {
            ServerPlayerWrapper_neoforge serverPlayer = Optional.of(context.player())
               .map(player -> player instanceof ServerPlayer ? (ServerPlayer)player : null)
               .map(ServerPlayerWrapper_neoforge::getWrapper)
               .orElse(null);
            if (payload.message() != null) {
               packetConsumer.accept(serverPlayer, payload.message());
            }
         }
      );
   }

   @Override
   public void sendToServer(AbstractNetworkMessage message) {
      PacketDistributor.sendToServer(new CommonPacketPayload_neoforge(message), new CustomPacketPayload[0]);
   }

   @Override
   public void sendToClient(ServerPlayer serverPlayer, AbstractNetworkMessage message) {
      PacketDistributor.sendToPlayer(serverPlayer, new CommonPacketPayload_neoforge(message), new CustomPacketPayload[0]);
   }
}
