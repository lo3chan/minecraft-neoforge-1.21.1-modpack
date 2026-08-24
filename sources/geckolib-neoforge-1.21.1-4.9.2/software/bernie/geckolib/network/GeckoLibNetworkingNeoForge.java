package software.bernie.geckolib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import software.bernie.geckolib.network.packet.MultiloaderPacket;
import software.bernie.geckolib.service.GeckoLibNetworking;

public class GeckoLibNetworkingNeoForge implements GeckoLibNetworking {
   private static PayloadRegistrar registrar = null;

   public static void init(IEventBus modBus) {
      modBus.addListener(event -> {
         registrar = event.registrar("geckolib").optional();
         GeckoLibNetworking.init();
         registrar = null;
      });
   }

   @Override
   public <B extends FriendlyByteBuf, P extends MultiloaderPacket> void registerPacketInternal(
      Type<P> payloadType, StreamCodec<B, P> codec, boolean isClientBound
   ) {
      if (isClientBound) {
         registrar.playToClient(payloadType, codec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
      } else {
         registrar.playToServer(payloadType, codec, (packet, context) -> packet.receiveMessage(context.player(), context::enqueueWork));
      }
   }

   @Override
   public void sendToAllPlayersTrackingEntity(MultiloaderPacket packet, Entity trackingEntity) {
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(trackingEntity, packet, new CustomPacketPayload[0]);
   }

   @Override
   public void sendToAllPlayersTrackingBlock(MultiloaderPacket packet, ServerLevel level, BlockPos pos) {
      PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), packet, new CustomPacketPayload[0]);
   }

   @Override
   public void sendToPlayer(MultiloaderPacket packet, ServerPlayer player) {
      PacketDistributor.sendToPlayer(player, packet, new CustomPacketPayload[0]);
   }
}
