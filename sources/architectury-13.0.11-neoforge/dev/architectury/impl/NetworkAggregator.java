package dev.architectury.impl;

import com.google.common.base.Suppliers;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.transformers.PacketSink;
import dev.architectury.networking.transformers.PacketTransformer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class NetworkAggregator {
   public static final Supplier<NetworkAggregator.Adaptor> ADAPTOR = Suppliers.memoize(() -> {
      try {
         Method adaptor = NetworkManager.class.getDeclaredMethod("getAdaptor");
         adaptor.setAccessible(true);
         return (NetworkAggregator.Adaptor)adaptor.invoke(null);
      } catch (Throwable var1) {
         throw new RuntimeException(var1);
      }
   });
   public static final Map<ResourceLocation, Type<NetworkAggregator.BufCustomPacketPayload>> C2S_TYPE = new HashMap<>();
   public static final Map<ResourceLocation, Type<NetworkAggregator.BufCustomPacketPayload>> S2C_TYPE = new HashMap<>();
   public static final Map<ResourceLocation, NetworkManager.NetworkReceiver<?>> C2S_RECEIVER = new HashMap<>();
   public static final Map<ResourceLocation, NetworkManager.NetworkReceiver<?>> S2C_RECEIVER = new HashMap<>();
   public static final Map<ResourceLocation, StreamCodec<ByteBuf, ?>> C2S_CODECS = new HashMap<>();
   public static final Map<ResourceLocation, StreamCodec<ByteBuf, ?>> S2C_CODECS = new HashMap<>();
   public static final Map<ResourceLocation, PacketTransformer> C2S_TRANSFORMERS = new HashMap<>();
   public static final Map<ResourceLocation, PacketTransformer> S2C_TRANSFORMERS = new HashMap<>();

   public static void registerReceiver(
      NetworkManager.Side side,
      ResourceLocation id,
      List<PacketTransformer> packetTransformers,
      NetworkManager.NetworkReceiver<RegistryFriendlyByteBuf> receiver
   ) {
      Type<NetworkAggregator.BufCustomPacketPayload> type = new Type(id);
      if (side == NetworkManager.Side.C2S) {
         C2S_TYPE.put(id, type);
         registerC2SReceiver(type, NetworkAggregator.BufCustomPacketPayload.streamCodec(type), packetTransformers, (value, context) -> {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(value.payload()), context.registryAccess());
            receiver.receive(buf, context);
            buf.release();
         });
      } else if (side == NetworkManager.Side.S2C) {
         S2C_TYPE.put(id, type);
         registerS2CReceiver(type, NetworkAggregator.BufCustomPacketPayload.streamCodec(type), packetTransformers, (value, context) -> {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(value.payload()), context.registryAccess());
            receiver.receive(buf, context);
            buf.release();
         });
      }
   }

   public static <T extends CustomPacketPayload> void registerReceiver(
      NetworkManager.Side side,
      Type<T> type,
      StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
      List<PacketTransformer> packetTransformers,
      NetworkManager.NetworkReceiver<T> receiver
   ) {
      Objects.requireNonNull(type, "Cannot register receiver with a null type!");
      packetTransformers = Objects.requireNonNullElse(packetTransformers, List.of());
      Objects.requireNonNull(receiver, "Cannot register a null receiver!");
      if (side == NetworkManager.Side.C2S) {
         registerC2SReceiver(type, codec, packetTransformers, receiver);
      } else if (side == NetworkManager.Side.S2C) {
         registerS2CReceiver(type, codec, packetTransformers, receiver);
      }
   }

   private static <T extends CustomPacketPayload> void registerC2SReceiver(
      Type<T> type,
      StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
      List<PacketTransformer> packetTransformers,
      NetworkManager.NetworkReceiver<T> receiver
   ) {
      PacketTransformer transformer = PacketTransformer.concat(packetTransformers);
      C2S_RECEIVER.put(type.id(), receiver);
      C2S_CODECS.put(type.id(), codec);
      C2S_TRANSFORMERS.put(type.id(), transformer);
      ADAPTOR.get()
         .registerC2S(
            type,
            NetworkAggregator.BufCustomPacketPayload.streamCodec(type),
            (payload, context) -> {
               RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.payload()), context.registryAccess());
               transformer.inbound(
                  NetworkManager.Side.C2S,
                  type.id(),
                  buf,
                  context,
                  (side, id1, buf1) -> {
                     NetworkManager.NetworkReceiver<T> networkReceiver = side == NetworkManager.Side.C2S
                        ? (NetworkManager.NetworkReceiver)C2S_RECEIVER.get(id1)
                        : (NetworkManager.NetworkReceiver)S2C_RECEIVER.get(id1);
                     if (networkReceiver == null) {
                        throw new IllegalArgumentException("Network Receiver not found! " + id1);
                     } else {
                        T actualPayload = (T)codec.decode(buf1);
                        networkReceiver.receive(actualPayload, context);
                     }
                  }
               );
               buf.release();
            }
         );
   }

   private static <T extends CustomPacketPayload> void registerS2CReceiver(
      Type<T> type,
      StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
      List<PacketTransformer> packetTransformers,
      NetworkManager.NetworkReceiver<T> receiver
   ) {
      PacketTransformer transformer = PacketTransformer.concat(packetTransformers);
      S2C_RECEIVER.put(type.id(), receiver);
      S2C_CODECS.put(type.id(), codec);
      S2C_TRANSFORMERS.put(type.id(), transformer);
      ADAPTOR.get()
         .registerS2C(
            type,
            NetworkAggregator.BufCustomPacketPayload.streamCodec(type),
            (payload, context) -> {
               RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.payload()), context.registryAccess());
               transformer.inbound(
                  NetworkManager.Side.S2C,
                  type.id(),
                  buf,
                  context,
                  (side, id1, buf1) -> {
                     NetworkManager.NetworkReceiver<T> networkReceiver = side == NetworkManager.Side.C2S
                        ? (NetworkManager.NetworkReceiver)C2S_RECEIVER.get(id1)
                        : (NetworkManager.NetworkReceiver)S2C_RECEIVER.get(id1);
                     if (networkReceiver == null) {
                        throw new IllegalArgumentException("Network Receiver not found! " + id1);
                     } else {
                        T actualPayload = (T)codec.decode(buf1);
                        networkReceiver.receive(actualPayload, context);
                     }
                  }
               );
               buf.release();
            }
         );
   }

   public static void collectPackets(PacketSink sink, NetworkManager.Side side, ResourceLocation id, RegistryFriendlyByteBuf buf) {
      if (side == NetworkManager.Side.C2S) {
         collectPackets(sink, side, new NetworkAggregator.BufCustomPacketPayload(C2S_TYPE.get(id), ByteBufUtil.getBytes(buf)), buf.registryAccess());
      } else {
         collectPackets(sink, side, new NetworkAggregator.BufCustomPacketPayload(S2C_TYPE.get(id), ByteBufUtil.getBytes(buf)), buf.registryAccess());
      }
   }

   public static <T extends CustomPacketPayload> void collectPackets(PacketSink sink, NetworkManager.Side side, T payload, RegistryAccess access) {
      Type<T> type = payload.type();
      PacketTransformer transformer = side == NetworkManager.Side.C2S ? C2S_TRANSFORMERS.get(type.id()) : S2C_TRANSFORMERS.get(type.id());
      StreamCodec<ByteBuf, T> codec = side == NetworkManager.Side.C2S ? (StreamCodec)C2S_CODECS.get(type.id()) : (StreamCodec)S2C_CODECS.get(type.id());
      RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
      codec.encode(buf, payload);
      if (transformer != null) {
         transformer.outbound(side, type.id(), buf, (side1, id1, buf1) -> {
            if (side == NetworkManager.Side.C2S) {
               Type<NetworkAggregator.BufCustomPacketPayload> type1 = C2S_TYPE.getOrDefault(id1, type);
               sink.accept(toPacket(side1, new NetworkAggregator.BufCustomPacketPayload(type1, ByteBufUtil.getBytes(buf1))));
            } else if (side == NetworkManager.Side.S2C) {
               Type<NetworkAggregator.BufCustomPacketPayload> type1 = S2C_TYPE.getOrDefault(id1, type);
               sink.accept(toPacket(side1, new NetworkAggregator.BufCustomPacketPayload(type1, ByteBufUtil.getBytes(buf1))));
            }
         });
      } else {
         sink.accept(toPacket(side, new NetworkAggregator.BufCustomPacketPayload(type, ByteBufUtil.getBytes(buf))));
      }

      buf.release();
   }

   public static <T extends CustomPacketPayload> Packet<?> toPacket(NetworkManager.Side side, T payload) {
      if (side == NetworkManager.Side.C2S) {
         return ADAPTOR.get().toC2SPacket(payload);
      } else if (side == NetworkManager.Side.S2C) {
         return ADAPTOR.get().toS2CPacket(payload);
      } else {
         throw new IllegalArgumentException("Invalid side: " + side);
      }
   }

   public static void registerS2CType(ResourceLocation id, List<PacketTransformer> packetTransformers) {
      Type<NetworkAggregator.BufCustomPacketPayload> type = new Type(id);
      S2C_TYPE.put(id, type);
      registerS2CType(type, NetworkAggregator.BufCustomPacketPayload.streamCodec(type), packetTransformers);
   }

   public static <T extends CustomPacketPayload> void registerS2CType(
      Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, List<PacketTransformer> packetTransformers
   ) {
      Objects.requireNonNull(type, "Cannot register a null type!");
      packetTransformers = Objects.requireNonNullElse(packetTransformers, List.of());
      S2C_CODECS.put(type.id(), codec);
      S2C_TRANSFORMERS.put(type.id(), PacketTransformer.concat(packetTransformers));
      ADAPTOR.get().registerS2CType(type, (StreamCodec<? super RegistryFriendlyByteBuf, T>)NetworkAggregator.BufCustomPacketPayload.streamCodec(type));
   }

   public interface Adaptor {
      <T extends CustomPacketPayload> void registerC2S(
         Type<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2, NetworkManager.NetworkReceiver<T> var3
      );

      <T extends CustomPacketPayload> void registerS2C(
         Type<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2, NetworkManager.NetworkReceiver<T> var3
      );

      <T extends CustomPacketPayload> Packet<?> toC2SPacket(T var1);

      <T extends CustomPacketPayload> Packet<?> toS2CPacket(T var1);

      <T extends CustomPacketPayload> void registerS2CType(Type<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2);
   }

   public record BufCustomPacketPayload(Type<NetworkAggregator.BufCustomPacketPayload> _type, byte[] payload) implements CustomPacketPayload {
      public Type<? extends CustomPacketPayload> type() {
         return this._type();
      }

      public static StreamCodec<ByteBuf, NetworkAggregator.BufCustomPacketPayload> streamCodec(Type<NetworkAggregator.BufCustomPacketPayload> type) {
         return ByteBufCodecs.BYTE_ARRAY
            .map(bytes -> new NetworkAggregator.BufCustomPacketPayload(type, bytes), NetworkAggregator.BufCustomPacketPayload::payload);
      }
   }
}
