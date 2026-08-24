package io.wispforest.owo.network;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.ClientCommonNetworkHandlerAccessor;
import io.wispforest.owo.mixin.ServerCommonNetworkHandlerAccessor;
import io.wispforest.owo.network.neoforge.SidedPacketCodec;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.particles.systems.ParticleSystem;
import io.wispforest.owo.particles.systems.ParticleSystemController;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owo.util.ServicesFrozenException;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.ToIntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.util.Tuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class OwoHandshake {
   private static final Endec<Map<ResourceLocation, Integer>> CHANNEL_HASHES_ENDEC = Endec.map(MinecraftEndecs.IDENTIFIER, Endec.INT);
   private static final MutableComponent PREFIX = TextOps.concat(Owo.PREFIX, Component.nullToEmpty("§chandshake failure\n"));
   public static final ResourceLocation CHANNEL_ID = ResourceLocation.fromNamespaceAndPath("owo", "handshake");
   public static final ResourceLocation OFF_CHANNEL_ID = ResourceLocation.fromNamespaceAndPath("owo", "handshake_off");
   private static final boolean ENABLED = System.getProperty("owo.handshake.enabled") != null ? Boolean.getBoolean("owo.handshake.enabled") : Owo.DEBUG;
   private static boolean HANDSHAKE_REQUIRED = false;
   private static boolean QUERY_RECEIVED = false;

   private OwoHandshake() {
   }

   public static void enable() {
      if (OwoFreezer.isFrozen()) {
         throw new ServicesFrozenException("The oωo handshake may only be enabled during mod initialization");
      }
   }

   public static void requireHandshake() {
      if (OwoFreezer.isFrozen()) {
         throw new ServicesFrozenException("The oωo handshake may only be made required during mod initialization");
      } else {
         HANDSHAKE_REQUIRED = true;
      }
   }

   public static void register(PayloadRegistrar registrar) {
      registrar.configurationBidirectional(
         new Type(CHANNEL_ID),
         new SidedPacketCodec(
            CodecUtils.toPacketCodec(
               OwoHandshake.HandshakeResponse.ENDEC.xmap(response -> response, customPayload -> (OwoHandshake.HandshakeResponse)customPayload)
            ),
            CodecUtils.toPacketCodec(
               OwoHandshake.HandshakeRequest.ENDEC.xmap(request -> request, customPayload -> (OwoHandshake.HandshakeRequest)customPayload)
            )
         ),
         (payload, context) -> {
            if (payload instanceof OwoHandshake.HandshakeRequest request) {
               syncClient(request, context);
            } else {
               if (!(payload instanceof OwoHandshake.HandshakeResponse response)) {
                  throw new IllegalStateException("OWO_NEO: HOW DID YOU GET HERE!");
               }

               syncServer(response, context);
            }
         }
      );
      Owo.getModBus().addListener(event -> {
         ServerConfigurationPacketListenerImpl listener = (ServerConfigurationPacketListenerImpl)event.getListener();
         configureStart(listener, ((ServerCommonNetworkHandlerAccessor)listener).owo$server());
      });
      if (!ENABLED) {
         registrar.configurationToClient(OwoHandshake.HandshakeOff.ID, StreamCodec.unit(new OwoHandshake.HandshakeOff()), (payload, context) -> {});
      }
   }

   public static void onDisconnect() {
      QUERY_RECEIVED = false;
      QueuedChannelSet.channels = null;
   }

   public static boolean isValidClient() {
      return ENABLED && QUERY_RECEIVED;
   }

   private static void configureStart(ServerConfigurationPacketListenerImpl handler, MinecraftServer server) {
      if (ENABLED) {
         if (NetworkRegistry.hasChannel(handler, OFF_CHANNEL_ID)) {
            Owo.LOGGER.info("[Handshake] Handshake disabled by client, skipping");
         } else if (!NetworkRegistry.hasChannel(handler, CHANNEL_ID)) {
            if (HANDSHAKE_REQUIRED) {
               handler.disconnect(TextOps.concat(PREFIX, Component.nullToEmpty("incompatible client")));
               Owo.LOGGER.info("[Handshake] Handshake failed, client doesn't understand channel packet");
            }
         } else {
            Map<ResourceLocation, Integer> optionalChannels = formatHashes(OwoNetChannel.OPTIONAL_CHANNELS, OwoHandshake::hashChannel);
            handler.send(new OwoHandshake.HandshakeRequest(optionalChannels));
            Owo.LOGGER.info("[Handshake] Sending channel packet");
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void syncClient(OwoHandshake.HandshakeRequest request, IPayloadContext context) {
      Owo.LOGGER.info("[Handshake] Sending client channels");
      QUERY_RECEIVED = true;
      QueuedChannelSet.channels = filterOptionalServices(request.optionalChannels(), OwoNetChannel.REGISTERED_CHANNELS, OwoHandshake::hashChannel);
      Map<ResourceLocation, Integer> requiredChannels = formatHashes(OwoNetChannel.REQUIRED_CHANNELS, OwoHandshake::hashChannel);
      Map<ResourceLocation, Integer> requiredControllers = formatHashes(ParticleSystemController.REGISTERED_CONTROLLERS, OwoHandshake::hashController);
      Map<ResourceLocation, Integer> optionalChannels = formatHashes(OwoNetChannel.OPTIONAL_CHANNELS, OwoHandshake::hashChannel);
      context.reply(new OwoHandshake.HandshakeResponse(requiredChannels, requiredControllers, optionalChannels));
   }

   private static void syncServer(OwoHandshake.HandshakeResponse response, IPayloadContext context) {
      Owo.LOGGER.info("[Handshake] Receiving client channels");
      StringBuilder disconnectMessage = new StringBuilder();
      boolean isAllGood = verifyReceivedHashes(
         "channels", response.requiredChannels(), OwoNetChannel.REQUIRED_CHANNELS, OwoHandshake::hashChannel, disconnectMessage
      );
      isAllGood &= verifyReceivedHashes(
         "controllers", response.requiredControllers(), ParticleSystemController.REGISTERED_CONTROLLERS, OwoHandshake::hashController, disconnectMessage
      );
      if (!isAllGood) {
         context.disconnect(TextOps.concat(PREFIX, Component.nullToEmpty(disconnectMessage.toString())));
      }

      ((OwoClientConnectionExtension)((ServerCommonNetworkHandlerAccessor)((ServerPayloadContext)context).listener()).owo$getConnection())
         .owo$setChannelSet(filterOptionalServices(response.optionalChannels(), OwoNetChannel.OPTIONAL_CHANNELS, OwoHandshake::hashChannel));
      Owo.LOGGER.info("[Handshake] Handshake completed successfully");
   }

   @OnlyIn(Dist.CLIENT)
   public static void handleReadyClient(ClientConfigurationPacketListenerImpl handler, Minecraft client) {
      if (!NetworkRegistry.hasChannel(handler, CHANNEL_ID) && HANDSHAKE_REQUIRED && ENABLED) {
         client.execute(
            () -> ((ClientCommonNetworkHandlerAccessor)handler)
               .getConnection()
               .disconnect(TextOps.concat(PREFIX, Component.nullToEmpty("incompatible server")))
         );
      }
   }

   private static <T> Set<ResourceLocation> filterOptionalServices(
      Map<ResourceLocation, Integer> remoteMap, Map<ResourceLocation, T> localMap, ToIntFunction<T> hashFunction
   ) {
      Set<ResourceLocation> readableServices = new HashSet<>();

      for (Entry<ResourceLocation, Integer> entry : remoteMap.entrySet()) {
         T service = localMap.get(entry.getKey());
         if (service != null && hashFunction.applyAsInt(service) == entry.getValue()) {
            readableServices.add(entry.getKey());
         }
      }

      return readableServices;
   }

   private static <T> boolean verifyReceivedHashes(
      String serviceNamePlural,
      Map<ResourceLocation, Integer> clientMap,
      Map<ResourceLocation, T> serverMap,
      ToIntFunction<T> hashFunction,
      StringBuilder disconnectMessage
   ) {
      boolean isAllGood = true;
      if (!clientMap.keySet().equals(serverMap.keySet())) {
         isAllGood = false;
         Tuple<Set<ResourceLocation>, Set<ResourceLocation>> leftovers = findCollisions(clientMap.keySet(), serverMap.keySet());
         if (!((Set)leftovers.getA()).isEmpty()) {
            disconnectMessage.append("server is missing ").append(serviceNamePlural).append(":\n");
            ((Set)leftovers.getA()).forEach(identifier -> disconnectMessage.append("§7").append(identifier).append("§r\n"));
         }

         if (!((Set)leftovers.getB()).isEmpty()) {
            disconnectMessage.append("client is missing ").append(serviceNamePlural).append(":\n");
            ((Set)leftovers.getB()).forEach(identifier -> disconnectMessage.append("§7").append(identifier).append("§r\n"));
         }
      }

      boolean hasMismatchedHashes = false;

      for (Entry<ResourceLocation, Integer> entry : clientMap.entrySet()) {
         T actualServiceObject = serverMap.get(entry.getKey());
         if (actualServiceObject != null) {
            int localHash = hashFunction.applyAsInt(actualServiceObject);
            if (localHash != entry.getValue()) {
               if (!hasMismatchedHashes) {
                  disconnectMessage.append(serviceNamePlural).append(" with mismatched hashes:\n");
               }

               disconnectMessage.append("§7").append(entry.getKey()).append("§r\n");
               isAllGood = false;
               hasMismatchedHashes = true;
            }
         }
      }

      return isAllGood;
   }

   private static <T> Map<ResourceLocation, Integer> formatHashes(Map<ResourceLocation, T> values, ToIntFunction<T> hashFunction) {
      Map<ResourceLocation, Integer> hashes = new HashMap<>();

      for (Entry<ResourceLocation, T> entry : values.entrySet()) {
         hashes.put(entry.getKey(), hashFunction.applyAsInt(entry.getValue()));
      }

      return hashes;
   }

   private static Tuple<Set<ResourceLocation>, Set<ResourceLocation>> findCollisions(Set<ResourceLocation> first, Set<ResourceLocation> second) {
      HashSet<ResourceLocation> firstLeftovers = new HashSet<>();
      HashSet<ResourceLocation> secondLeftovers = new HashSet<>();
      first.forEach(identifier -> {
         if (!second.contains(identifier)) {
            firstLeftovers.add(identifier);
         }
      });
      second.forEach(identifier -> {
         if (!first.contains(identifier)) {
            secondLeftovers.add(identifier);
         }
      });
      return new Tuple(firstLeftovers, secondLeftovers);
   }

   private static int hashChannel(OwoNetChannel channel) {
      int serializersHash = 0;
      ObjectIterator var2 = channel.endecsByIndex.int2ObjectEntrySet().iterator();

      while (var2.hasNext()) {
         it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<OwoNetChannel.IndexedEndec<?>> entry = (it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<OwoNetChannel.IndexedEndec<?>>)var2.next();
         serializersHash += entry.getIntKey() * 31 + ((OwoNetChannel.IndexedEndec)entry.getValue()).getRecordClass().getName().hashCode();
      }

      return 31 * channel.packetId.id().hashCode() + serializersHash;
   }

   private static int hashController(ParticleSystemController controller) {
      int serializersHash = 0;
      ObjectIterator var2 = controller.systemsByIndex.int2ObjectEntrySet().iterator();

      while (var2.hasNext()) {
         it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<ParticleSystem<?>> entry = (it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<ParticleSystem<?>>)var2.next();
         serializersHash += entry.getIntKey();
      }

      return 31 * controller.channelId.hashCode() + serializersHash;
   }

   public record HandshakeOff() implements CustomPacketPayload {
      public static final Type<OwoHandshake.HandshakeOff> ID = new Type(OwoHandshake.OFF_CHANNEL_ID);

      public Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }

   public record HandshakeRequest(Map<ResourceLocation, Integer> optionalChannels) implements CustomPacketPayload {
      public static final Type<OwoHandshake.HandshakeRequest> ID = new Type(OwoHandshake.CHANNEL_ID);
      public static final Endec<OwoHandshake.HandshakeRequest> ENDEC = StructEndecBuilder.of(
         OwoHandshake.CHANNEL_HASHES_ENDEC.fieldOf("optionalChannels", OwoHandshake.HandshakeRequest::optionalChannels), OwoHandshake.HandshakeRequest::new
      );

      public Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }

   private record HandshakeResponse(
      Map<ResourceLocation, Integer> requiredChannels, Map<ResourceLocation, Integer> requiredControllers, Map<ResourceLocation, Integer> optionalChannels
   ) implements CustomPacketPayload {
      public static final Type<OwoHandshake.HandshakeResponse> ID = new Type(OwoHandshake.CHANNEL_ID);
      public static final Endec<OwoHandshake.HandshakeResponse> ENDEC = StructEndecBuilder.of(
         OwoHandshake.CHANNEL_HASHES_ENDEC.fieldOf("requiredChannels", OwoHandshake.HandshakeResponse::requiredChannels),
         OwoHandshake.CHANNEL_HASHES_ENDEC.fieldOf("requiredControllers", OwoHandshake.HandshakeResponse::requiredControllers),
         OwoHandshake.CHANNEL_HASHES_ENDEC.fieldOf("optionalChannels", OwoHandshake.HandshakeResponse::optionalChannels),
         OwoHandshake.HandshakeResponse::new
      );

      public Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }
}
