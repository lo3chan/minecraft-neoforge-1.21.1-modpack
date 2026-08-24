package io.wispforest.owo.network.neoforge;

import io.wispforest.owo.client.screens.ScreenInternals;
import io.wispforest.owo.network.OwoHandshake;
import io.wispforest.owo.network.OwoNetChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NeoOwoNetworking {
   public static final Map<Type<OwoNetChannel.MessagePayload>, SidedPacketCodec<OwoNetChannel.MessagePayload>> PAYLOAD_ID_TO_SIDED_CODEC = new HashMap<>();
   public static final Map<Type<?>, NeoOwoNetworking.PayloadCodec<?>> PAYLOAD_ID_TO_CLIENT_CODEC = new HashMap<>();
   public static final Map<Type<?>, NeoOwoNetworking.PayloadHandler<?>> PAYLOAD_ID_TO_CLIENT_HANDLER = new HashMap<>();
   public static final Map<Type<OwoNetChannel.MessagePayload>, NeoOwoNetworking.PayloadHandler<OwoNetChannel.MessagePayload>> PAYLOAD_ID_TO_SERVER_PAYLOAD_HANDLER = new HashMap<>();
   public static final Map<Type<OwoNetChannel.MessagePayload>, NeoOwoNetworking.PayloadHandler<OwoNetChannel.MessagePayload>> PAYLOAD_ID_TO_CLIENT_PAYLOAD_HANDLER = new HashMap<>();

   public static void onNetworkRegister(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1.0.0");

      for (Entry<Type<?>, NeoOwoNetworking.PayloadCodec<?>> entry : PAYLOAD_ID_TO_CLIENT_CODEC.entrySet()) {
         Type<?> id = entry.getKey();
         NeoOwoNetworking.PayloadHandler<?> handler = Objects.requireNonNull(
            PAYLOAD_ID_TO_CLIENT_HANDLER.get(id), "Unable to register the given client play packet due to missing the needed handler! Id: " + id
         );
         entry.getValue().registerPlayPayload(registrar, handler);
      }

      OwoHandshake.register(registrar);
      ScreenInternals.init(registrar);
      registrar = registrar.optional();

      for (Entry<Type<OwoNetChannel.MessagePayload>, SidedPacketCodec<OwoNetChannel.MessagePayload>> entry : PAYLOAD_ID_TO_SIDED_CODEC.entrySet()) {
         Type<OwoNetChannel.MessagePayload> id = entry.getKey();
         if (!PAYLOAD_ID_TO_SERVER_PAYLOAD_HANDLER.containsKey(id)) {
            throw new IllegalStateException("Unable to get the required Payload Handler as its missing for the Server! Id: " + id);
         }

         if (!PAYLOAD_ID_TO_CLIENT_PAYLOAD_HANDLER.containsKey(id)) {
            throw new IllegalStateException("Unable to get the required Payload Handler as its missing for the Client! Id: " + id);
         }

         registrar.playBidirectional(
            id,
            entry.getValue(),
            (arg, iPayloadContext) -> iPayloadContext.enqueueWork(
               () -> {
                  Player player = iPayloadContext.player();
                  NeoOwoNetworking.PayloadHandler<OwoNetChannel.MessagePayload> handlerx = !player.level().isClientSide()
                     ? PAYLOAD_ID_TO_SERVER_PAYLOAD_HANDLER.get(id)
                     : PAYLOAD_ID_TO_CLIENT_PAYLOAD_HANDLER.get(id);
                  handlerx.accept(arg, iPayloadContext.player());
               }
            )
         );
      }
   }

   public static void registerMessageCodecs(
      Type<OwoNetChannel.MessagePayload> id,
      StreamCodec<FriendlyByteBuf, OwoNetChannel.MessagePayload> serverCodec,
      StreamCodec<FriendlyByteBuf, OwoNetChannel.MessagePayload> clientCodec
   ) {
      if (PAYLOAD_ID_TO_SIDED_CODEC.containsKey(id)) {
         throw new IllegalStateException("Unable to register the given codec as such already exists within codec map! Id: " + id);
      } else {
         PAYLOAD_ID_TO_SIDED_CODEC.put(id, new SidedPacketCodec<>(serverCodec, clientCodec));
      }
   }

   public static <T extends CustomPacketPayload> void registerClientCodec(Type<T> id, StreamCodec<FriendlyByteBuf, T> codec) {
      if (PAYLOAD_ID_TO_CLIENT_CODEC.containsKey(id)) {
         throw new IllegalStateException("Unable to register the given codec as such already exists within codec map! Id: " + id);
      } else {
         PAYLOAD_ID_TO_CLIENT_CODEC.put(id, new NeoOwoNetworking.PayloadCodec(id, codec, Optional.of(PacketFlow.CLIENTBOUND)));
      }
   }

   public static <T extends CustomPacketPayload> void registerClientPayload(Type<T> id, NeoOwoNetworking.PayloadHandler<T> payloadHandler) {
      if (PAYLOAD_ID_TO_CLIENT_HANDLER.containsKey(id)) {
         throw new IllegalStateException("Unable to register the given codec as such already exists within codec map! Id: " + id);
      } else {
         PAYLOAD_ID_TO_CLIENT_HANDLER.put(id, payloadHandler);
      }
   }

   public static void registerServerMessageHandler(Type<OwoNetChannel.MessagePayload> id, NeoOwoNetworking.PayloadHandler<OwoNetChannel.MessagePayload> handler) {
      if (PAYLOAD_ID_TO_SERVER_PAYLOAD_HANDLER.containsKey(id)) {
         throw new IllegalStateException("Unable to register the given server handler as such already exists within handler map! Id: " + id);
      } else {
         PAYLOAD_ID_TO_SERVER_PAYLOAD_HANDLER.put(id, handler);
      }
   }

   public static void registerClientMessageHandler(Type<OwoNetChannel.MessagePayload> id, NeoOwoNetworking.PayloadHandler<OwoNetChannel.MessagePayload> handler) {
      if (PAYLOAD_ID_TO_CLIENT_PAYLOAD_HANDLER.containsKey(id)) {
         throw new IllegalStateException("Unable to register the given client handler as such already exists within handler map! Id: " + id);
      } else {
         PAYLOAD_ID_TO_CLIENT_PAYLOAD_HANDLER.put(id, handler);
      }
   }

   private record PayloadCodec<T extends CustomPacketPayload>(Type<T> id, StreamCodec<FriendlyByteBuf, T> codec, Optional<PacketFlow> possibleSide) {
      public void registerPlayPayload(PayloadRegistrar registrar, NeoOwoNetworking.PayloadHandler<?> handler) {
         this.possibleSide.ifPresentOrElse(side -> {
            if (side.isClientbound()) {
               registrar.playToClient(this.id, this.codec, (arg, context) -> context.enqueueWork(() -> handler.accept(arg, context.player())));
            } else {
               registrar.playToServer(this.id, this.codec, (arg, context) -> context.enqueueWork(() -> handler.accept(arg, context.player())));
            }
         }, () -> registrar.playBidirectional(this.id, this.codec, (arg, context) -> context.enqueueWork(() -> handler.accept(arg, context.player()))));
      }
   }

   public interface PayloadHandler<T extends CustomPacketPayload> extends BiConsumer<T, Player> {
      static <P extends CustomPacketPayload> NeoOwoNetworking.PayloadHandler<P> empty() {
         return (payload, player) -> {};
      }

      void accept(T var1, Player var2);
   }
}
