package net.blay09.mods.balm.neoforge.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.network.ClientboundMessageRegistration;
import net.blay09.mods.balm.api.network.MessageRegistration;
import net.blay09.mods.balm.api.network.ServerboundMessageRegistration;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.common.StaticNamespaceResolver;
import net.blay09.mods.balm.mixin.ChunkMapAccessor;
import net.blay09.mods.balm.mixin.TrackedEntityAccessor;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record NeoForgeBalmNetworking(NamespaceResolver namespaceResolver) implements BalmNetworking {
   private static final Logger logger = LoggerFactory.getLogger(NeoForgeBalmNetworking.class);
   private static IPayloadContext replyContext;
   private static final Set<String> clientOnlyMods = Collections.synchronizedSet(new HashSet<>());
   private static final Set<String> serverOnlyMods = Collections.synchronizedSet(new HashSet<>());
   private static final Map<String, String> networkVersions = new ConcurrentHashMap<>();

   @Override
   public void allowClientOnly(String modId) {
      clientOnlyMods.add(modId);
   }

   @Override
   public void allowServerOnly(String modId) {
      serverOnlyMods.add(modId);
   }

   @Override
   public void openMenu(Player player, MenuProvider menuProvider) {
      if (player instanceof ServerPlayer serverPlayer) {
         if (menuProvider instanceof BalmMenuProvider<?> balmMenuProvider) {
            this.openGui(serverPlayer, balmMenuProvider);
         } else {
            serverPlayer.openMenu(menuProvider);
         }
      }
   }

   @Override
   public void defineNetworkVersion(String modId, String version) {
      networkVersions.put(modId, version);
   }

   private <T> void openGui(ServerPlayer player, BalmMenuProvider<T> menuProvider) {
      player.openMenu(menuProvider, buf -> menuProvider.getScreenStreamCodec().encode(buf, menuProvider.getScreenOpeningData(player)));
   }

   @Override
   public <T extends CustomPacketPayload> void reply(T message) {
      if (replyContext == null) {
         throw new IllegalStateException("No context to reply to");
      } else {
         replyContext.reply(message);
      }
   }

   @Override
   public <T extends CustomPacketPayload> void sendTo(Player player, T message) {
      if (player instanceof ServerPlayer serverPlayer && this.isMessageSupported(serverPlayer, message)) {
         PacketDistributor.sendToPlayer(serverPlayer, message, new CustomPacketPayload[0]);
      }
   }

   @Override
   public <T extends CustomPacketPayload> void sendToTracking(ServerLevel level, BlockPos pos, T message) {
      for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false)) {
         if (this.isMessageSupported(player, message)) {
            PacketDistributor.sendToPlayer(player, message, new CustomPacketPayload[0]);
         }
      }
   }

   @Override
   public <T extends CustomPacketPayload> void sendToTracking(Entity entity, T message) {
      if (entity.level() instanceof ServerLevel level) {
         TrackedEntityAccessor trackedEntity = (TrackedEntityAccessor)((ChunkMapAccessor)level.getChunkSource().chunkMap).getEntityMap().get(entity.getId());

         for (ServerPlayerConnection connection : trackedEntity.getSeenBy()) {
            ServerPlayer player = connection.getPlayer();
            if (this.isMessageSupported(player, message)) {
               PacketDistributor.sendToPlayer(player, message, new CustomPacketPayload[0]);
            }
         }
      }
   }

   @Override
   public <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, T message) {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         if (this.isMessageSupported(player, message)) {
            PacketDistributor.sendToPlayer(player, message, new CustomPacketPayload[0]);
         }
      }
   }

   @Override
   public <T extends CustomPacketPayload> void sendToServer(T message) {
      if (!Balm.getProxy().isConnected()) {
         logger.debug("Skipping message {} because we're not connected to a server", message);
      } else {
         if (this.isMessageSupportedByServer(message)) {
            PacketDistributor.sendToServer(message, new CustomPacketPayload[0]);
         }
      }
   }

   @Override
   public <T extends CustomPacketPayload> void registerClientboundPacket(
      Type<T> type, Class<T> clazz, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<Player, T> handler
   ) {
      ClientboundMessageRegistration<RegistryFriendlyByteBuf, T> messageRegistration = new ClientboundMessageRegistration<>(type, codec, handler);
      NeoForgeBalmNetworking.Registrations registrations = this.getActiveRegistrations();
      registrations.playMessagesByType.put(type, messageRegistration);
   }

   @Override
   public <T extends CustomPacketPayload> void registerServerboundPacket(
      Type<T> type, Class<T> clazz, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<ServerPlayer, T> handler
   ) {
      ServerboundMessageRegistration<RegistryFriendlyByteBuf, T> messageRegistration = new ServerboundMessageRegistration<>(type, codec, handler);
      NeoForgeBalmNetworking.Registrations registrations = this.getActiveRegistrations();
      registrations.playMessagesByType.put(type, messageRegistration);
   }

   @Override
   public BalmNetworking scoped(String modId) {
      return new NeoForgeBalmNetworking(new StaticNamespaceResolver(modId));
   }

   private NeoForgeBalmNetworking.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespaceResolver.getDefaultNamespace(), NeoForgeBalmNetworking.Registrations.class);
   }

   @Override
   public boolean isMessageSupported(ServerPlayer player, CustomPacketPayload payload) {
      return player.connection.hasChannel(payload);
   }

   @Override
   public boolean isMessageSupportedByServer(CustomPacketPayload payload) {
      ClientGamePacketListener packetListener = Balm.safeClientAccess().getPacketListener();
      return packetListener != null && NetworkRegistry.hasChannel(packetListener, payload.type().id());
   }

   public static class Registrations {
      private final String modId;
      private final Map<Type<? extends CustomPacketPayload>, MessageRegistration<RegistryFriendlyByteBuf, ? extends CustomPacketPayload>> playMessagesByType = new ConcurrentHashMap<>();

      public Registrations(String modId) {
         this.modId = modId;
      }

      @SubscribeEvent
      public void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
         String networkVersion = NeoForgeBalmNetworking.networkVersions.get(this.modId);
         PayloadRegistrar registrar = event.registrar(networkVersion != null ? networkVersion : this.modId);
         if (NeoForgeBalmNetworking.clientOnlyMods.contains(this.modId) || NeoForgeBalmNetworking.serverOnlyMods.contains(this.modId)) {
            registrar = registrar.optional();
         }

         for (Entry<Type<? extends CustomPacketPayload>, MessageRegistration<RegistryFriendlyByteBuf, ? extends CustomPacketPayload>> entry : this.playMessagesByType
            .entrySet()) {
            MessageRegistration<RegistryFriendlyByteBuf, ?> messageRegistration = entry.getValue();
            if (messageRegistration instanceof ServerboundMessageRegistration<RegistryFriendlyByteBuf, ? extends CustomPacketPayload> serverboundMessageRegistration
               )
             {
               registrar = this.playToServer(registrar, serverboundMessageRegistration);
            } else if (messageRegistration instanceof ClientboundMessageRegistration<RegistryFriendlyByteBuf, ? extends CustomPacketPayload> clientboundMessageRegistration
               )
             {
               registrar = this.playToClient(registrar, clientboundMessageRegistration);
            }
         }
      }

      private <TPayload extends CustomPacketPayload> PayloadRegistrar playToServer(
         PayloadRegistrar registrar, ServerboundMessageRegistration<RegistryFriendlyByteBuf, TPayload> registration
      ) {
         return registrar.playToServer(registration.getType(), registration.getCodec(), this.createPayloadHandler(registration));
      }

      private <TPayload extends CustomPacketPayload> PayloadRegistrar playToClient(
         PayloadRegistrar registrar, ClientboundMessageRegistration<RegistryFriendlyByteBuf, TPayload> registration
      ) {
         return registrar.playToClient(registration.getType(), registration.getCodec(), this.createPayloadHandler(registration));
      }

      private <TBuffer extends FriendlyByteBuf, TPayload extends CustomPacketPayload> IPayloadHandler<TPayload> createPayloadHandler(
         ServerboundMessageRegistration<TBuffer, TPayload> serverboundMessageRegistration
      ) {
         return new MainThreadPayloadHandler((payload, context) -> {
            NeoForgeBalmNetworking.replyContext = context;
            serverboundMessageRegistration.getHandler().accept((ServerPlayer)context.player(), (TPayload)payload);
            NeoForgeBalmNetworking.replyContext = null;
         });
      }

      private <TBuffer extends FriendlyByteBuf, TPayload extends CustomPacketPayload> IPayloadHandler<TPayload> createPayloadHandler(
         ClientboundMessageRegistration<TBuffer, TPayload> clientboundMessageRegistration
      ) {
         return new MainThreadPayloadHandler((payload, context) -> {
            NeoForgeBalmNetworking.replyContext = context;
            clientboundMessageRegistration.getHandler().accept(context.player(), (TPayload)payload);
            NeoForgeBalmNetworking.replyContext = null;
         });
      }
   }
}
