package fuzs.puzzleslib.api.network.v3;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.codec.StreamCodecRegistry;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistry;
import fuzs.puzzleslib.impl.network.codec.StreamCodecRegistryImpl;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public interface NetworkHandler {
   static NetworkHandler.Builder builder(String modId) {
      return ModContext.get(modId).getNetworkHandler();
   }

   @Deprecated
   static NetworkHandler.Builder builder(ResourceLocation channelName) {
      return builder(channelName.getNamespace());
   }

   <T> Packet<ClientCommonPacketListener> toClientboundPacket(ClientboundMessage<T> var1);

   <T> Packet<ServerCommonPacketListener> toServerboundPacket(ServerboundMessage<T> var1);

   <T> void sendMessage(PlayerSet var1, ClientboundMessage<T> var2);

   <T> void sendMessage(ServerboundMessage<T> var1);

   @Deprecated
   default <T> void sendToServer(ServerboundMessage<T> message) {
      this.sendMessage(message);
   }

   @Deprecated
   default <T> void sendTo(ServerPlayer player, ClientboundMessage<T> message) {
      this.sendMessage(PlayerSet.ofPlayer(player), message);
   }

   @Deprecated
   default <T> void sendToAll(MinecraftServer server, ClientboundMessage<T> message) {
      this.sendMessage(PlayerSet.ofAll(server), message);
   }

   @Deprecated
   default <T> void sendToAll(MinecraftServer server, @Nullable ServerPlayer excludePlayer, ClientboundMessage<T> message) {
      PlayerSet playerSet;
      if (excludePlayer != null) {
         playerSet = PlayerSet.ofOthers(excludePlayer);
      } else {
         playerSet = PlayerSet.ofAll(server);
      }

      this.sendMessage(playerSet, message);
   }

   @Deprecated
   default <T> void sendToAll(Collection<ServerPlayer> playerList, @Nullable ServerPlayer excludePlayer, ClientboundMessage<T> message) {
      Objects.requireNonNull(playerList, "player list is null");

      for (ServerPlayer serverPlayer : playerList) {
         if (serverPlayer != excludePlayer) {
            this.sendTo(serverPlayer, message);
         }
      }
   }

   @Deprecated
   default <T> void sendToAll(ServerLevel level, ClientboundMessage<T> message) {
      this.sendMessage(PlayerSet.inLevel(level), message);
   }

   @Deprecated
   default <T> void sendToAllNear(Vec3i pos, ServerLevel level, ClientboundMessage<T> message) {
      this.sendMessage(PlayerSet.nearPosition(pos, level), message);
   }

   @Deprecated
   default <T> void sendToAllNear(double posX, double posY, double posZ, ServerLevel level, ClientboundMessage<T> message) {
      this.sendMessage(PlayerSet.nearPosition(posX, posY, posZ, level), message);
   }

   @Deprecated
   default <T> void sendToAllNear(
      @Nullable ServerPlayer excludePlayer, double posX, double posY, double posZ, double distance, ServerLevel level, ClientboundMessage<T> message
   ) {
      this.sendMessage(PlayerSet.nearPosition(excludePlayer, posX, posY, posZ, distance, level), message);
   }

   @Deprecated
   default <T> void sendToAllTracking(BlockEntity blockEntity, ClientboundMessage<T> message) {
      this.sendMessage(PlayerSet.nearBlockEntity(blockEntity), message);
   }

   @Deprecated
   default <T> void sendToAllTracking(LevelChunk chunk, ClientboundMessage<T> message) {
      this.sendMessage(PlayerSet.nearChunk(chunk), message);
   }

   @Deprecated
   default <T> void sendToAllTracking(ServerLevel level, ChunkPos chunkPos, ClientboundMessage<T> message) {
      this.sendMessage(PlayerSet.nearChunk(level, chunkPos), message);
   }

   @Deprecated
   default <T> void sendToAllTracking(Entity entity, ClientboundMessage<T> message, boolean includeSelf) {
      if (!includeSelf && entity instanceof ServerPlayer serverPlayer) {
         this.sendMessage(PlayerSet.nearPlayer(serverPlayer), message);
      } else {
         this.sendMessage(PlayerSet.nearEntity(entity), message);
      }
   }

   public interface Builder extends NetworkHandlerRegistry, StreamCodecRegistry<NetworkHandler.Builder> {
      default <B extends ByteBuf, V> NetworkHandler.Builder registerSerializer(Class<V> type, StreamCodec<? super B, V> streamCodec) {
         StreamCodecRegistryImpl.INSTANCE.registerSerializer(type, streamCodec);
         return this;
      }

      default <B extends ByteBuf, V> NetworkHandler.Builder registerContainerProvider(
         Class<V> type, Function<Type[], StreamCodec<? super B, ? extends V>> factory
      ) {
         StreamCodecRegistryImpl.INSTANCE.registerContainerProvider(type, factory);
         return this;
      }

      <T extends Record & ClientboundMessage<T>> NetworkHandler.Builder registerClientbound(Class<T> var1);

      <T extends Record & ServerboundMessage<T>> NetworkHandler.Builder registerServerbound(Class<T> var1);

      default <T extends MessageV2<T>> NetworkHandler.Builder registerLegacyClientbound(Class<T> clazz, Supplier<T> factory) {
         return this.registerLegacyClientbound(clazz, friendlyByteBuf -> {
            T message = factory.get();
            message.read(friendlyByteBuf);
            return message;
         });
      }

      default <T extends MessageV2<T>> NetworkHandler.Builder registerLegacyServerbound(Class<T> clazz, Supplier<T> factory) {
         return this.registerLegacyServerbound(clazz, friendlyByteBuf -> {
            T message = factory.get();
            message.read(friendlyByteBuf);
            return message;
         });
      }

      <T extends MessageV2<T>> NetworkHandler.Builder registerLegacyClientbound(Class<T> var1, StreamDecoder<FriendlyByteBuf, T> var2);

      <T extends MessageV2<T>> NetworkHandler.Builder registerLegacyServerbound(Class<T> var1, StreamDecoder<FriendlyByteBuf, T> var2);

      NetworkHandler.Builder optional();
   }
}
