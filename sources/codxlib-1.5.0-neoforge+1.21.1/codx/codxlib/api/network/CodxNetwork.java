package codx.codxlib.api.network;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;

public final class CodxNetwork {
   private static final List<CodxNetwork.Clientbound<?>> CLIENTBOUND = new CopyOnWriteArrayList<>();
   private static final List<CodxNetwork.Serverbound<?>> SERVERBOUND = new CopyOnWriteArrayList<>();
   private static volatile CodxNetwork.PayloadVisitor onRegister;
   private static volatile Consumer<CustomPacketPayload> clientSender = payload -> {};
   private static volatile BiConsumer<ServerPlayer, CustomPacketPayload> serverSender = (player, payload) -> {};

   private CodxNetwork() {
   }

   public static <T extends CustomPacketPayload> void registerClientbound(
      Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, CodxNetwork.ClientHandler<T> handler
   ) {
      CodxNetwork.Clientbound<T> entry = new CodxNetwork.Clientbound<>(type, codec, handler);
      CLIENTBOUND.add(entry);
      CodxNetwork.PayloadVisitor visitor = onRegister;
      if (visitor != null) {
         entry.accept(visitor);
      }
   }

   public static <T extends CustomPacketPayload> void registerServerbound(
      Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, CodxNetwork.ServerHandler<T> handler
   ) {
      CodxNetwork.Serverbound<T> entry = new CodxNetwork.Serverbound<>(type, codec, handler);
      SERVERBOUND.add(entry);
      CodxNetwork.PayloadVisitor visitor = onRegister;
      if (visitor != null) {
         entry.accept(visitor);
      }
   }

   public static void sendToServer(CustomPacketPayload payload) {
      clientSender.accept(payload);
   }

   public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
      serverSender.accept(player, payload);
   }

   public static void setOnRegister(CodxNetwork.PayloadVisitor visitor) {
      onRegister = visitor;

      for (CodxNetwork.Clientbound<?> entry : CLIENTBOUND) {
         entry.accept(visitor);
      }

      for (CodxNetwork.Serverbound<?> entry : SERVERBOUND) {
         entry.accept(visitor);
      }
   }

   public static void visitClientbound(CodxNetwork.PayloadVisitor visitor) {
      for (CodxNetwork.Clientbound<?> entry : CLIENTBOUND) {
         entry.accept(visitor);
      }
   }

   public static void visitServerbound(CodxNetwork.PayloadVisitor visitor) {
      for (CodxNetwork.Serverbound<?> entry : SERVERBOUND) {
         entry.accept(visitor);
      }
   }

   public static void visitAll(CodxNetwork.PayloadVisitor visitor) {
      for (CodxNetwork.Clientbound<?> entry : CLIENTBOUND) {
         entry.accept(visitor);
      }

      for (CodxNetwork.Serverbound<?> entry : SERVERBOUND) {
         entry.accept(visitor);
      }
   }

   public static void setClientSender(Consumer<CustomPacketPayload> sender) {
      clientSender = sender;
   }

   public static void setServerSender(BiConsumer<ServerPlayer, CustomPacketPayload> sender) {
      serverSender = sender;
   }

   @FunctionalInterface
   public interface ClientHandler<T extends CustomPacketPayload> {
      void handle(T var1);
   }

   private static final class Clientbound<T extends CustomPacketPayload> {
      final Type<T> type;
      final StreamCodec<RegistryFriendlyByteBuf, T> codec;
      final CodxNetwork.ClientHandler<T> handler;

      Clientbound(Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, CodxNetwork.ClientHandler<T> handler) {
         this.type = type;
         this.codec = codec;
         this.handler = handler;
      }

      void accept(CodxNetwork.PayloadVisitor visitor) {
         visitor.clientbound(this.type, this.codec, this.handler);
      }
   }

   public interface PayloadVisitor {
      <T extends CustomPacketPayload> void clientbound(Type<T> var1, StreamCodec<RegistryFriendlyByteBuf, T> var2, CodxNetwork.ClientHandler<T> var3);

      <T extends CustomPacketPayload> void serverbound(Type<T> var1, StreamCodec<RegistryFriendlyByteBuf, T> var2, CodxNetwork.ServerHandler<T> var3);
   }

   @FunctionalInterface
   public interface ServerHandler<T extends CustomPacketPayload> {
      void handle(T var1, ServerPlayer var2);
   }

   private static final class Serverbound<T extends CustomPacketPayload> {
      final Type<T> type;
      final StreamCodec<RegistryFriendlyByteBuf, T> codec;
      final CodxNetwork.ServerHandler<T> handler;

      Serverbound(Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, CodxNetwork.ServerHandler<T> handler) {
         this.type = type;
         this.codec = codec;
         this.handler = handler;
      }

      void accept(CodxNetwork.PayloadVisitor visitor) {
         visitor.serverbound(this.type, this.codec, this.handler);
      }
   }
}
