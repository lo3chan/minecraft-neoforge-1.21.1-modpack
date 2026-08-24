package dev.architectury.networking;

import com.google.common.collect.Maps;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@Deprecated(
   forRemoval = true
)
public final class NetworkChannel {
   private final ResourceLocation id;
   private final Map<Class<?>, NetworkChannel.MessageInfo<?>> encoders = Maps.newHashMap();

   private NetworkChannel(ResourceLocation id) {
      this.id = id;
   }

   public static NetworkChannel create(ResourceLocation id) {
      return new NetworkChannel(id);
   }

   public <T> void register(
      Class<T> type,
      BiConsumer<T, FriendlyByteBuf> encoder,
      Function<FriendlyByteBuf, T> decoder,
      BiConsumer<T, Supplier<NetworkManager.PacketContext>> messageConsumer
   ) {
      String s = UUID.nameUUIDFromBytes(type.getName().getBytes(StandardCharsets.UTF_8)).toString().replace("-", "");
      NetworkChannel.MessageInfo<T> info = new NetworkChannel.MessageInfo<>(ResourceLocation.parse(this.id + "/" + s), encoder, decoder, messageConsumer);
      this.encoders.put(type, info);
      NetworkManager.NetworkReceiver<RegistryFriendlyByteBuf> receiver = (buf, context) -> info.messageConsumer.accept(info.decoder.apply(buf), () -> context);
      NetworkManager.registerReceiver(NetworkManager.c2s(), info.packetId, receiver);
      if (Platform.getEnvironment() == Env.CLIENT) {
         NetworkManager.registerReceiver(NetworkManager.s2c(), info.packetId, receiver);
      }
   }

   public static long hashCodeString(String str) {
      long h = 0L;
      int length = str.length();

      for (int i = 0; i < length; i++) {
         h = 31L * h + str.charAt(i);
      }

      return h;
   }

   public <T> Packet<?> toPacket(NetworkManager.Side side, T message, RegistryAccess access) {
      NetworkChannel.MessageInfo<T> messageInfo = Objects.requireNonNull(
         (NetworkChannel.MessageInfo<T>)this.encoders.get(message.getClass()), "Unknown message type! " + message
      );
      RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
      messageInfo.encoder.accept(message, buf);
      return NetworkManager.toPacket(side, messageInfo.packetId, buf);
   }

   public <T> void sendToPlayer(ServerPlayer player, T message) {
      Objects.requireNonNull(player, "Unable to send packet to a 'null' player!")
         .connection
         .send(this.toPacket(NetworkManager.s2c(), message, player.registryAccess()));
   }

   public <T> void sendToPlayers(Iterable<ServerPlayer> players, T message) {
      Iterator<ServerPlayer> iterator = players.iterator();
      if (iterator.hasNext()) {
         Packet<?> packet = this.toPacket(NetworkManager.s2c(), message, iterator.next().registryAccess());

         for (ServerPlayer player : players) {
            Objects.requireNonNull(player, "Unable to send packet to a 'null' player!").connection.send(packet);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public <T> void sendToServer(T message) {
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      if (connection != null) {
         connection.send(this.toPacket(NetworkManager.c2s(), message, connection.registryAccess()));
      } else {
         throw new IllegalStateException("Unable to send packet to the server while not in game!");
      }
   }

   @OnlyIn(Dist.CLIENT)
   public <T> boolean canServerReceive(Class<T> type) {
      return NetworkManager.canServerReceive(this.encoders.get(type).packetId);
   }

   public <T> boolean canPlayerReceive(ServerPlayer player, Class<T> type) {
      return NetworkManager.canPlayerReceive(player, this.encoders.get(type).packetId);
   }

   private record MessageInfo<T>(
      ResourceLocation packetId,
      BiConsumer<T, FriendlyByteBuf> encoder,
      Function<FriendlyByteBuf, T> decoder,
      BiConsumer<T, Supplier<NetworkManager.PacketContext>> messageConsumer
   ) {
   }
}
