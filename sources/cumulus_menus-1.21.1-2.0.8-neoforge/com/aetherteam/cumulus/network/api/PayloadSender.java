package com.aetherteam.cumulus.network.api;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public interface PayloadSender {
   default void sendToServer(CustomPacketPayload payload, CustomPacketPayload... payloads) {
   }

   void sendToPlayer(ServerPlayer var1, CustomPacketPayload var2, CustomPacketPayload... var3);

   void sendToPlayersInDimension(ServerLevel var1, CustomPacketPayload var2, CustomPacketPayload... var3);

   void sendToPlayersNear(
      ServerLevel var1,
      @Nullable ServerPlayer var2,
      double var3,
      double var5,
      double var7,
      double var9,
      CustomPacketPayload var11,
      CustomPacketPayload... var12
   );

   void sendToAllPlayers(CustomPacketPayload var1, CustomPacketPayload... var2);

   void sendToPlayersTrackingEntity(Entity var1, CustomPacketPayload var2, CustomPacketPayload... var3);

   void sendToPlayersTrackingEntityAndSelf(Entity var1, CustomPacketPayload var2, CustomPacketPayload... var3);

   void sendToPlayersTrackingChunk(ServerLevel var1, ChunkPos var2, CustomPacketPayload var3, CustomPacketPayload... var4);
}
