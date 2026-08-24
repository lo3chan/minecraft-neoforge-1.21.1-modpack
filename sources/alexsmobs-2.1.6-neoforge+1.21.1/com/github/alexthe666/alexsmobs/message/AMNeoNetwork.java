package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AMNeoNetwork {
   public static final ResourceLocation CHANNEL = AMCompat.rl("alexsmobs", "main_channel");
   public static final Type<AMNeoNetwork.AMPayload> TYPE = new Type(CHANNEL);
   public static final StreamCodec<RegistryFriendlyByteBuf, AMNeoNetwork.AMPayload> CODEC = StreamCodec.of(
      AMNeoNetwork::encodePayload, AMNeoNetwork::decodePayload
   );
   private static final List<AMNeoNetwork.Registration<?>> REGISTRATIONS = new ArrayList<>();
   private static final Map<Class<?>, Integer> INDEX_BY_TYPE = new HashMap<>();

   public static <MSG> void register(
      Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, AMNetContext> handler
   ) {
      INDEX_BY_TYPE.put(type, REGISTRATIONS.size());
      REGISTRATIONS.add(new AMNeoNetwork.Registration<>(encoder, decoder, handler));
   }

   public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
      AMNeoSend.registerPlay(event.registrar("alexsmobs"), TYPE, CODEC, AMNeoNetwork::handle);
   }

   public static void sendToServer(Object message) {
      AMNeoSend.toServer(wrap(message));
   }

   public static void sendToPlayer(Object message, ServerPlayer player) {
      PacketDistributor.sendToPlayer(player, wrap(message), new CustomPacketPayload[0]);
   }

   private static AMNeoNetwork.AMPayload wrap(Object message) {
      Integer index = INDEX_BY_TYPE.get(message.getClass());
      if (index == null) {
         throw new IllegalArgumentException("Unregistered Alex's Mobs message: " + message.getClass());
      } else {
         return new AMNeoNetwork.AMPayload(index, message);
      }
   }

   private static void encodePayload(RegistryFriendlyByteBuf buf, AMNeoNetwork.AMPayload payload) {
      buf.writeVarInt(payload.index());
      REGISTRATIONS.get(payload.index()).encode(payload.message(), buf);
   }

   private static AMNeoNetwork.AMPayload decodePayload(RegistryFriendlyByteBuf buf) {
      int index = buf.readVarInt();
      if (index >= 0 && index < REGISTRATIONS.size()) {
         return new AMNeoNetwork.AMPayload(index, REGISTRATIONS.get(index).decode(buf));
      } else {
         throw new IllegalArgumentException("Received Alex's Mobs packet with unknown index " + index);
      }
   }

   private static void handle(AMNeoNetwork.AMPayload payload, IPayloadContext context) {
      REGISTRATIONS.get(payload.index()).handle(payload.message(), adapt(context));
   }

   private static AMNetContext adapt(final IPayloadContext context) {
      return new AMNetContext() {
         @Override
         public void setPacketHandled(boolean handled) {
         }

         @Override
         public void enqueueWork(Runnable work) {
            context.enqueueWork(work);
         }

         @Override
         public ServerPlayer getSender() {
            return context.player() instanceof ServerPlayer serverPlayer ? serverPlayer : null;
         }

         @Override
         public boolean isClientSide() {
            return context.flow().isClientbound();
         }
      };
   }

   public record AMPayload(int index, Object message) implements CustomPacketPayload {
      public Type<AMNeoNetwork.AMPayload> type() {
         return AMNeoNetwork.TYPE;
      }
   }

   private record Registration<MSG>(BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, AMNetContext> handler) {
      void encode(Object message, FriendlyByteBuf buf) {
         this.encoder.accept((MSG)message, buf);
      }

      Object decode(FriendlyByteBuf buf) {
         return this.decoder.apply(buf);
      }

      void handle(Object message, AMNetContext context) {
         this.handler.accept((MSG)message, context);
      }
   }
}
