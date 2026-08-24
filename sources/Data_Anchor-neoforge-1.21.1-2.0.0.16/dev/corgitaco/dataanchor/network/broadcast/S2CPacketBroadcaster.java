package dev.corgitaco.dataanchor.network.broadcast;

import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;

public interface S2CPacketBroadcaster extends PacketBroadcaster {
   S2CPacketBroadcaster INSTANCE = S2C;

   <MSG extends Packet> void sendToPlayer(MSG var1, ServerPlayer var2);

   <MSG extends Packet> void sendToAllPlayers(MSG var1);

   default <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ServerPlayer player) {
      this.sendToAllPlayersInDimension(msg, player.serverLevel());
   }

   <MSG extends Packet> void sendToAllPlayersInDimension(MSG var1, ServerLevel var2);

   default <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ServerLevel dimension, BlockPos position, double radius) {
      this.sendNearPositionInDimension(msg, dimension, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, radius);
   }

   default <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ServerLevel dimension, Position position, double radius) {
      this.sendNearPositionInDimension(msg, dimension, position.x(), position.y(), position.z(), radius);
   }

   <MSG extends Packet> void sendNearPositionInDimension(MSG var1, ServerLevel var2, double var3, double var5, double var7, double var9);

   <MSG extends Packet> void trackingEntity(MSG var1, Entity var2);

   <MSG extends Packet> void trackingEntityAndSelf(MSG var1, Entity var2);

   <MSG extends Packet> void trackingChunk(MSG var1, LevelChunk var2);
}
