package net.mehvahdjukaar.moonlight.api.platform.network;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.platform.network.platform.NetworkHelperImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.TypeAndCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus.Internal;

public class NetworkHelper {
   private static final Set<ResourceLocation> OPTIONAL_PAYLOADS = new HashSet<>();

   @Internal
   public static void markOptional(Type<?> type) {
      OPTIONAL_PAYLOADS.add(type.id());
   }

   public static boolean isOptional(Type<?> type) {
      return OPTIONAL_PAYLOADS.contains(type.id());
   }

   public static void sendToAllClientPlayersInDefaultRange(ServerLevel level, BlockPos pos, CustomPacketPayload message) {
      sendToAllClientPlayersInRange(level, pos, 64.0, message);
   }

   public static void sendToAllClientPlayersInParticleRange(ServerLevel level, BlockPos pos, CustomPacketPayload message) {
      sendToAllClientPlayersInRange(level, pos, 32.0, message);
   }

   public static void sendToAllClientPlayersInDistantParticleRange(ServerLevel level, BlockPos pos, CustomPacketPayload message) {
      sendToAllClientPlayersInRange(level, pos, 512.0, message);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void sentToAllClientPlayersTrackingEntity(Entity target, CustomPacketPayload message) {
      sendToAllClientPlayersTrackingEntity(target, message);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void sentToAllClientPlayersTrackingEntityAndSelf(Entity target, Message message) {
      sendToAllClientPlayersTrackingEntityAndSelf(target, message);
   }

   public static void addNetworkRegistration(Consumer<NetworkHelper.RegisterMessagesEvent> var0, int var1) {
      NetworkHelperImpl.addNetworkRegistration(var0, var1);
   }

   public static boolean canSendToPlayer(ServerPlayer var0, Type<?> var1) {
      return NetworkHelperImpl.canSendToPlayer(var0, var1);
   }

   public static boolean serverHasChannel(Type<?> var0) {
      return NetworkHelperImpl.serverHasChannel(var0);
   }

   public static void sendToClientPlayer(ServerPlayer var0, CustomPacketPayload var1) {
      NetworkHelperImpl.sendToClientPlayer(var0, var1);
   }

   public static void sendToAllClientPlayers(CustomPacketPayload var0) {
      NetworkHelperImpl.sendToAllClientPlayers(var0);
   }

   public static void sendToAllClientPlayersInRange(ServerLevel var0, BlockPos var1, double var2, CustomPacketPayload var4) {
      NetworkHelperImpl.sendToAllClientPlayersInRange(var0, var1, var2, var4);
   }

   public static void sendToAllClientPlayersTrackingEntity(Entity var0, CustomPacketPayload var1) {
      NetworkHelperImpl.sendToAllClientPlayersTrackingEntity(var0, var1);
   }

   public static void sendToAllClientPlayersTrackingChunk(ServerLevel var0, ChunkPos var1, CustomPacketPayload var2) {
      NetworkHelperImpl.sendToAllClientPlayersTrackingChunk(var0, var1, var2);
   }

   public static void sendToAllClientPlayersTrackingEntityAndSelf(Entity var0, Message var1) {
      NetworkHelperImpl.sendToAllClientPlayersTrackingEntityAndSelf(var0, var1);
   }

   public static void sendToServer(CustomPacketPayload var0) {
      NetworkHelperImpl.sendToServer(var0);
   }

   public interface RegisterMessagesEvent {
      <M extends Message> void registerServerBound(TypeAndCodec<RegistryFriendlyByteBuf, M> var1);

      <M extends Message> void registerClientBound(TypeAndCodec<RegistryFriendlyByteBuf, M> var1);

      <M extends Message> void registerClientBoundOptional(TypeAndCodec<RegistryFriendlyByteBuf, M> var1);

      <M extends Message> void registerBidirectional(TypeAndCodec<RegistryFriendlyByteBuf, M> var1);
   }
}
