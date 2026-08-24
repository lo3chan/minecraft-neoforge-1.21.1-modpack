package dev.shadowsoffire.placebo.reload;

import com.mojang.datafixers.util.Either;
import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.network.PayloadProvider;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class ReloadListenerPayloads {
   public record Content<V extends CodecProvider<? super V>>(String path, ResourceLocation key, Either<V, ByteBuf> item) implements CustomPacketPayload {
      public static final Type<ReloadListenerPayloads.Content<?>> TYPE = new Type(Placebo.loc("reload_sync_content"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ReloadListenerPayloads.Content<?>> CODEC = StreamCodec.of(
         ReloadListenerPayloads.Content::write, ReloadListenerPayloads.Content::read
      );

      public Content(String path, ResourceLocation key, V item) {
         this(path, key, Either.left(item));
      }

      public Content(String path, ResourceLocation key, ByteBuf buf) {
         this(path, key, Either.right(buf));
      }

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }

      public static <V extends CodecProvider<? super V>> void write(RegistryFriendlyByteBuf buf, ReloadListenerPayloads.Content<V> payload) {
         buf.writeUtf(payload.path, 50);
         buf.writeResourceLocation(payload.key);
         DynamicRegistry.SyncManagement.writeItem(payload.path, (V)payload.item.orThrow(), buf);
      }

      public static <V extends CodecProvider<? super V>> ReloadListenerPayloads.Content<V> read(RegistryFriendlyByteBuf buf) {
         String path = buf.readUtf(50);
         ResourceLocation key = buf.readResourceLocation();
         int size = buf.writerIndex() - buf.readerIndex();
         ByteBuf itemBuf = Unpooled.buffer(size, size);
         buf.readBytes(itemBuf);
         return new ReloadListenerPayloads.Content<>(path, key, itemBuf);
      }

      public static class Provider<V extends CodecProvider<? super V>> implements PayloadProvider<ReloadListenerPayloads.Content<?>> {
         @Override
         public Type<ReloadListenerPayloads.Content<?>> getType() {
            return ReloadListenerPayloads.Content.TYPE;
         }

         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, ReloadListenerPayloads.Content<?>> getCodec() {
            return ReloadListenerPayloads.Content.CODEC;
         }

         public void handle(ReloadListenerPayloads.Content<?> msg, IPayloadContext ctx) {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf((ByteBuf)msg.item.right().get(), ctx.player().registryAccess(), ConnectionType.NEOFORGE);

            try {
               V value = DynamicRegistry.SyncManagement.readItem(msg.path, buf);
               DynamicRegistry.SyncManagement.acceptItem(msg.path, msg.key, value);
            } catch (Exception var5) {
               Placebo.LOGGER.error("Failure when deserializing a dynamic registry object via network: Registry: {}, Object ID: {}", msg.path, msg.key);
               throw var5;
            }
         }

         @Override
         public List<ConnectionProtocol> getSupportedProtocols() {
            return List.of(ConnectionProtocol.PLAY);
         }

         @Override
         public Optional<PacketFlow> getFlow() {
            return Optional.of(PacketFlow.CLIENTBOUND);
         }

         @Override
         public String getVersion() {
            return "1";
         }
      }
   }

   public record End(String path) implements CustomPacketPayload {
      public static final Type<ReloadListenerPayloads.End> TYPE = new Type(Placebo.loc("reload_sync_end"));
      public static final StreamCodec<FriendlyByteBuf, ReloadListenerPayloads.End> CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8, ReloadListenerPayloads.End::path, ReloadListenerPayloads.End::new
      );

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }

      public static class Provider implements PayloadProvider<ReloadListenerPayloads.End> {
         @Override
         public Type<ReloadListenerPayloads.End> getType() {
            return ReloadListenerPayloads.End.TYPE;
         }

         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, ReloadListenerPayloads.End> getCodec() {
            return ReloadListenerPayloads.End.CODEC;
         }

         public void handle(ReloadListenerPayloads.End msg, IPayloadContext ctx) {
            DynamicRegistry.SyncManagement.endSync(msg.path);
         }

         @Override
         public List<ConnectionProtocol> getSupportedProtocols() {
            return List.of(ConnectionProtocol.PLAY);
         }

         @Override
         public Optional<PacketFlow> getFlow() {
            return Optional.of(PacketFlow.CLIENTBOUND);
         }

         @Override
         public String getVersion() {
            return "1";
         }
      }
   }

   public record Start(String path) implements CustomPacketPayload {
      public static final Type<ReloadListenerPayloads.Start> TYPE = new Type(Placebo.loc("reload_sync_start"));
      public static final StreamCodec<FriendlyByteBuf, ReloadListenerPayloads.Start> CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8, ReloadListenerPayloads.Start::path, ReloadListenerPayloads.Start::new
      );

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }

      public static class Provider implements PayloadProvider<ReloadListenerPayloads.Start> {
         @Override
         public Type<ReloadListenerPayloads.Start> getType() {
            return ReloadListenerPayloads.Start.TYPE;
         }

         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, ReloadListenerPayloads.Start> getCodec() {
            return ReloadListenerPayloads.Start.CODEC;
         }

         public void handle(ReloadListenerPayloads.Start msg, IPayloadContext ctx) {
            DynamicRegistry.SyncManagement.initSync(msg.path);
         }

         @Override
         public List<ConnectionProtocol> getSupportedProtocols() {
            return List.of(ConnectionProtocol.PLAY);
         }

         @Override
         public Optional<PacketFlow> getFlow() {
            return Optional.of(PacketFlow.CLIENTBOUND);
         }

         @Override
         public String getVersion() {
            return "1";
         }
      }
   }
}
