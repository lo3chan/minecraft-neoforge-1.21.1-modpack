package dev.architectury.networking;

import dev.architectury.impl.NetworkAggregator;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import dev.architectury.networking.forge.NetworkManagerImpl;
import dev.architectury.networking.transformers.PacketCollector;
import dev.architectury.networking.transformers.PacketSink;
import dev.architectury.networking.transformers.PacketTransformer;
import dev.architectury.networking.transformers.SinglePacketCollector;
import dev.architectury.utils.Env;
import dev.architectury.utils.GameInstance;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Experimental;

public final class NetworkManager {
   @Deprecated(
      forRemoval = true
   )
   public static void registerS2CPayloadType(ResourceLocation id) {
      NetworkAggregator.registerS2CType(id, List.of());
   }

   public static <T extends CustomPacketPayload> void registerS2CPayloadType(Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
      NetworkAggregator.registerS2CType(type, codec, List.of());
   }

   @Deprecated(
      forRemoval = true
   )
   public static void registerS2CPayloadType(ResourceLocation id, List<PacketTransformer> packetTransformers) {
      NetworkAggregator.registerS2CType(id, packetTransformers);
   }

   public static <T extends CustomPacketPayload> void registerS2CPayloadType(
      Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, List<PacketTransformer> packetTransformers
   ) {
      NetworkAggregator.registerS2CType(type, codec, packetTransformers);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void registerReceiver(NetworkManager.Side side, ResourceLocation id, NetworkManager.NetworkReceiver<RegistryFriendlyByteBuf> receiver) {
      registerReceiver(side, id, Collections.emptyList(), receiver);
   }

   @Deprecated(
      forRemoval = true
   )
   @Experimental
   public static void registerReceiver(
      NetworkManager.Side side,
      ResourceLocation id,
      List<PacketTransformer> packetTransformers,
      NetworkManager.NetworkReceiver<RegistryFriendlyByteBuf> receiver
   ) {
      NetworkAggregator.registerReceiver(side, id, packetTransformers, receiver);
   }

   public static <T extends CustomPacketPayload> void registerReceiver(
      NetworkManager.Side side, Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> receiver
   ) {
      registerReceiver(side, id, codec, Collections.emptyList(), receiver);
   }

   @Experimental
   public static <T extends CustomPacketPayload> void registerReceiver(
      NetworkManager.Side side,
      Type<T> id,
      StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
      List<PacketTransformer> packetTransformers,
      NetworkManager.NetworkReceiver<T> receiver
   ) {
      NetworkAggregator.registerReceiver(side, id, codec, packetTransformers, receiver);
   }

   @Deprecated(
      forRemoval = true
   )
   public static Packet<?> toPacket(NetworkManager.Side side, ResourceLocation id, RegistryFriendlyByteBuf buf) {
      SinglePacketCollector sink = new SinglePacketCollector(null);
      collectPackets(sink, side, id, buf);
      return sink.getPacket();
   }

   @Deprecated(
      forRemoval = true
   )
   public static List<Packet<?>> toPackets(NetworkManager.Side side, ResourceLocation id, RegistryFriendlyByteBuf buf) {
      PacketCollector sink = new PacketCollector(null);
      collectPackets(sink, side, id, buf);
      return sink.collect();
   }

   public static <T extends CustomPacketPayload> Packet<?> toPacket(NetworkManager.Side side, T payload, RegistryAccess access) {
      SinglePacketCollector sink = new SinglePacketCollector(null);
      collectPackets(sink, side, payload, access);
      return sink.getPacket();
   }

   public static <T extends CustomPacketPayload> List<Packet<?>> toPackets(NetworkManager.Side side, T payload, RegistryAccess access) {
      PacketCollector sink = new PacketCollector(null);
      collectPackets(sink, side, payload, access);
      return sink.collect();
   }

   @Deprecated(
      forRemoval = true
   )
   public static void collectPackets(PacketSink sink, NetworkManager.Side side, ResourceLocation id, RegistryFriendlyByteBuf buf) {
      NetworkAggregator.collectPackets(sink, side, id, buf);
   }

   public static <T extends CustomPacketPayload> void collectPackets(PacketSink sink, NetworkManager.Side side, T payload, RegistryAccess access) {
      NetworkAggregator.collectPackets(sink, side, payload, access);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void sendToPlayer(ServerPlayer player, ResourceLocation id, RegistryFriendlyByteBuf buf) {
      collectPackets(PacketSink.ofPlayer(player), serverToClient(), id, buf);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void sendToPlayers(Iterable<ServerPlayer> players, ResourceLocation id, RegistryFriendlyByteBuf buf) {
      collectPackets(PacketSink.ofPlayers(players), serverToClient(), id, buf);
   }

   @Deprecated(
      forRemoval = true
   )
   @OnlyIn(Dist.CLIENT)
   public static void sendToServer(ResourceLocation id, RegistryFriendlyByteBuf buf) {
      collectPackets(PacketSink.client(), clientToServer(), id, buf);
   }

   public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
      collectPackets(PacketSink.ofPlayer(player), serverToClient(), payload, player.registryAccess());
   }

   public static <T extends CustomPacketPayload> void sendToPlayers(Iterable<ServerPlayer> players, T payload) {
      Iterator<ServerPlayer> iterator = players.iterator();
      if (iterator.hasNext()) {
         collectPackets(PacketSink.ofPlayers(players), serverToClient(), payload, iterator.next().registryAccess());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static <T extends CustomPacketPayload> void sendToServer(T payload) {
      ClientPacketListener connection = GameInstance.getClient().getConnection();
      if (connection != null) {
         collectPackets(PacketSink.client(), clientToServer(), payload, connection.registryAccess());
      }
   }

   @OnlyIn(Dist.CLIENT)
   @ExpectPlatform
   @Transformed
   public static boolean canServerReceive(ResourceLocation id) {
      return NetworkManagerImpl.canServerReceive(id);
   }

   @ExpectPlatform
   @Transformed
   public static boolean canPlayerReceive(ServerPlayer player, ResourceLocation id) {
      return NetworkManagerImpl.canPlayerReceive(player, id);
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean canServerReceive(Type<?> type) {
      return canServerReceive(type.id());
   }

   public static boolean canPlayerReceive(ServerPlayer player, Type<?> type) {
      return canPlayerReceive(player, type.id());
   }

   @ExpectPlatform
   @Transformed
   public static Packet<ClientGamePacketListener> createAddEntityPacket(Entity entity, ServerEntity serverEntity) {
      return NetworkManagerImpl.createAddEntityPacket(entity, serverEntity);
   }

   @ExpectPlatform
   @Transformed
   private static NetworkAggregator.Adaptor getAdaptor() {
      return NetworkManagerImpl.getAdaptor();
   }

   public static NetworkManager.Side s2c() {
      return NetworkManager.Side.S2C;
   }

   public static NetworkManager.Side c2s() {
      return NetworkManager.Side.C2S;
   }

   public static NetworkManager.Side serverToClient() {
      return NetworkManager.Side.S2C;
   }

   public static NetworkManager.Side clientToServer() {
      return NetworkManager.Side.C2S;
   }

   @FunctionalInterface
   public interface NetworkReceiver<T> {
      void receive(T var1, NetworkManager.PacketContext var2);
   }

   public interface PacketContext {
      Player getPlayer();

      void queue(Runnable var1);

      Env getEnvironment();

      RegistryAccess registryAccess();

      default Dist getEnv() {
         return this.getEnvironment().toPlatform();
      }
   }

   public static enum Side {
      S2C,
      C2S;
   }
}
