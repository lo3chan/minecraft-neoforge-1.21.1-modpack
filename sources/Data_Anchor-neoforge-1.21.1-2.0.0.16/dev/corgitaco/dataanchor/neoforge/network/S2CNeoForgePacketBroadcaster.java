package dev.corgitaco.dataanchor.neoforge.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;

@AutoService({S2CPacketBroadcaster.class})
public class S2CNeoForgePacketBroadcaster implements S2CPacketBroadcaster {
   @Override
   public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
      PacketDistributor.sendToPlayer(player, msg, new CustomPacketPayload[0]);
   }

   @Override
   public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
      PacketDistributor.sendToServer(msg, new CustomPacketPayload[0]);
   }

   @Override
   public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ServerLevel dimension) {
      PacketDistributor.sendToPlayersInDimension(dimension, msg, new CustomPacketPayload[0]);
   }

   @Override
   public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ServerLevel dimension, double x, double y, double z, double radius) {
      PacketDistributor.sendToPlayersNear(dimension, null, x, y, z, radius, msg, new CustomPacketPayload[0]);
   }

   @Override
   public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
      PacketDistributor.sendToPlayersTrackingEntity(entity, msg, new CustomPacketPayload[0]);
   }

   @Override
   public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, msg, new CustomPacketPayload[0]);
   }

   @Override
   public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
      PacketDistributor.sendToPlayersTrackingChunk((ServerLevel)chunk.getLevel(), chunk.getPos(), msg, new CustomPacketPayload[0]);
   }

   @Override
   public void registerPackets() {
   }
}
