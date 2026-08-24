package io.wispforest.owo.network;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.RecordEndec;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import io.wispforest.owo.mixin.ServerCommonNetworkHandlerAccessor;
import io.wispforest.owo.network.neoforge.NeoOwoNetworking;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owo.util.ReflectionUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

public class OwoNetChannel {
   static final Map<ResourceLocation, OwoNetChannel> REGISTERED_CHANNELS = new HashMap<>();
   static final Map<ResourceLocation, OwoNetChannel> REQUIRED_CHANNELS = new HashMap<>();
   static final Map<ResourceLocation, OwoNetChannel> OPTIONAL_CHANNELS = new HashMap<>();
   private final ReflectiveEndecBuilder builder;
   private final Map<Class<?>, OwoNetChannel.IndexedEndec<?>> endecsByClass = new HashMap<>();
   final Int2ObjectMap<OwoNetChannel.IndexedEndec<?>> endecsByIndex = new Int2ObjectOpenHashMap();
   private final List<OwoNetChannel.ChannelHandler<Record, ClientAccess>> clientHandlers = new ArrayList<>();
   private final List<OwoNetChannel.ChannelHandler<Record, ServerAccess>> serverHandlers = new ArrayList<>();
   private final Reference2IntMap<Class<?>> deferredClientEndecs = new Reference2IntOpenHashMap();
   final Type<OwoNetChannel.MessagePayload> packetId;
   private final String ownerClassName;
   final boolean required;
   private OwoNetChannel.ClientHandle clientHandle = null;
   private OwoNetChannel.ServerHandle serverHandle = null;

   public static OwoNetChannel create(ResourceLocation id) {
      return new OwoNetChannel(id, ReflectionUtils.getCallingClassName(2), true);
   }

   public static OwoNetChannel createOptional(ResourceLocation id) {
      return new OwoNetChannel(id, ReflectionUtils.getCallingClassName(2), false);
   }

   private OwoNetChannel(ResourceLocation id, String ownerClassName, boolean required) {
      OwoFreezer.checkRegister("Network channels");
      this.builder = new ReflectiveEndecBuilder(builder -> {
         builder.register(Endec.VAR_INT, new Class[]{Integer.class, int.class});
         builder.register(Endec.VAR_LONG, new Class[]{Long.class, long.class});
         MinecraftEndecs.addDefaults(builder);
      });
      if (REGISTERED_CHANNELS.containsKey(id)) {
         throw new IllegalStateException("Channel with id '" + id + "' was already registered from class '" + REGISTERED_CHANNELS.get(id).ownerClassName + "'");
      } else {
         this.deferredClientEndecs.defaultReturnValue(-1);
         this.packetId = new Type(id);
         this.ownerClassName = ownerClassName;
         this.required = required;
         OwoHandshake.enable();
         if (required) {
            OwoHandshake.requireHandshake();
         }

         Endec<OwoNetChannel.MessagePayload> serverEndec = Endec.dispatched(
               index -> ((OwoNetChannel.IndexedEndec)this.endecsByIndex.get(index)).endec,
               msg -> this.endecsByClass.get(msg.getClass()).serverHandlerIndex,
               Endec.VAR_INT
            )
            .xmap(x -> new OwoNetChannel.MessagePayload(this.packetId, x), x -> x.message);
         Endec<OwoNetChannel.MessagePayload> clientEndec = Endec.dispatched(
               index -> ((OwoNetChannel.IndexedEndec)this.endecsByIndex.get(-index)).endec,
               msg -> this.endecsByClass.get(msg.getClass()).clientHandlerIndex,
               Endec.VAR_INT
            )
            .xmap(x -> new OwoNetChannel.MessagePayload(this.packetId, x), x -> x.message);
         NeoOwoNetworking.registerMessageCodecs(this.packetId, CodecUtils.toPacketCodec(serverEndec), CodecUtils.toPacketCodec(clientEndec));
         NeoOwoNetworking.registerServerMessageHandler(
            this.packetId,
            (payload, player) -> this.serverHandlers
               .get(this.endecsByClass.get(payload.message().getClass()).serverHandlerIndex)
               .handle(payload.message, new ServerAccess((ServerPlayer)player))
         );
         if (FMLLoader.getDist() == Dist.CLIENT) {
            NeoOwoNetworking.registerClientMessageHandler(
               this.packetId,
               (payload, player) -> this.clientHandlers
                  .get(this.endecsByClass.get(payload.message.getClass()).clientHandlerIndex)
                  .handle(payload.message, new ClientAccess(((LocalPlayer)player).connection))
            );
         } else {
            NeoOwoNetworking.registerClientMessageHandler(this.packetId, NeoOwoNetworking.PayloadHandler.empty());
         }

         this.clientHandlers.add(null);
         this.serverHandlers.add(null);
         REGISTERED_CHANNELS.put(id, this);
         if (required) {
            REQUIRED_CHANNELS.put(id, this);
         } else {
            OPTIONAL_CHANNELS.put(id, this);
         }
      }
   }

   public OwoNetChannel addEndecs(Consumer<ReflectiveEndecBuilder> endecBuilder) {
      endecBuilder.accept(this.builder);
      return this;
   }

   public ReflectiveEndecBuilder builder() {
      return this.builder;
   }

   public <R extends Record> void registerClientbound(Class<R> messageClass, OwoNetChannel.ChannelHandler<R, ClientAccess> handler) {
      this.registerClientbound(messageClass, handler, () -> RecordEndec.create(this.builder, messageClass));
   }

   public <R extends Record> void registerClientboundDeferred(Class<R> messageClass) {
      this.registerClientboundDeferred(messageClass, (Supplier<StructEndec<R>>)(() -> RecordEndec.create(this.builder, messageClass)));
   }

   public <R extends Record> void registerServerbound(Class<R> messageClass, OwoNetChannel.ChannelHandler<R, ServerAccess> handler) {
      this.registerServerbound(messageClass, handler, () -> RecordEndec.create(this.builder, messageClass));
   }

   public <R extends Record> void registerClientbound(Class<R> messageClass, StructEndec<R> endec, OwoNetChannel.ChannelHandler<R, ClientAccess> handler) {
      this.registerClientbound(messageClass, handler, () -> endec);
   }

   public <R extends Record> void registerClientboundDeferred(Class<R> messageClass, StructEndec<R> endec) {
      this.registerClientboundDeferred(messageClass, (Supplier<StructEndec<R>>)(() -> endec));
   }

   public <R extends Record> void registerServerbound(Class<R> messageClass, StructEndec<R> endec, OwoNetChannel.ChannelHandler<R, ServerAccess> handler) {
      this.registerServerbound(messageClass, handler, () -> endec);
   }

   private <R extends Record> void registerClientbound(
      Class<R> messageClass, OwoNetChannel.ChannelHandler<R, ClientAccess> handler, Supplier<StructEndec<R>> endec
   ) {
      int deferredIndex = this.deferredClientEndecs.removeInt(messageClass);
      if (deferredIndex != -1) {
         OwoFreezer.checkRegister("Network handlers");
         this.clientHandlers.set(deferredIndex, handler);
      } else {
         int index = this.clientHandlers.size();
         this.createEndec(messageClass, index, Dist.CLIENT, endec);
         this.clientHandlers.add(handler);
      }
   }

   private <R extends Record> void registerClientboundDeferred(Class<R> messageClass, Supplier<StructEndec<R>> endec) {
      int index = this.clientHandlers.size();
      this.createEndec(messageClass, index, Dist.CLIENT, endec);
      this.clientHandlers.add(null);
      this.deferredClientEndecs.put(messageClass, index);
   }

   private <R extends Record> void registerServerbound(
      Class<R> messageClass, OwoNetChannel.ChannelHandler<R, ServerAccess> handler, Supplier<StructEndec<R>> endec
   ) {
      int index = this.serverHandlers.size();
      this.createEndec(messageClass, index, Dist.DEDICATED_SERVER, endec);
      this.serverHandlers.add(handler);
   }

   public boolean canSendToPlayer(ServerPlayer player) {
      return this.canSendToPlayer(player.connection);
   }

   public boolean canSendToPlayer(ServerGamePacketListenerImpl networkHandler) {
      if (this.required) {
         return true;
      } else {
         return OwoHandshake.isValidClient()
            ? getChannelSet(((ServerCommonNetworkHandlerAccessor)networkHandler).owo$getConnection()).contains(this.packetId.id())
            : NetworkRegistry.hasChannel(networkHandler, this.packetId.id());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canSendToServer() {
      if (this.required) {
         return true;
      } else {
         return OwoHandshake.isValidClient()
            ? getChannelSet(Minecraft.getInstance().getConnection().getConnection()).contains(this.packetId.id())
            : NetworkRegistry.hasChannel(Minecraft.getInstance().getConnection(), this.packetId.id());
      }
   }

   private static Set<ResourceLocation> getChannelSet(Connection connection) {
      return ((OwoClientConnectionExtension)connection).owo$getChannelSet();
   }

   public OwoNetChannel.ClientHandle clientHandle() {
      if (FMLLoader.getDist() != Dist.CLIENT) {
         throw new NetworkException("Cannot obtain client handle in environment type '" + FMLLoader.getDist() + "'");
      } else {
         if (this.clientHandle == null) {
            this.clientHandle = new OwoNetChannel.ClientHandle();
         }

         return this.clientHandle;
      }
   }

   public OwoNetChannel.ServerHandle serverHandle(MinecraftServer server) {
      Objects.requireNonNull(server, "The server cannot be null");
      OwoNetChannel.ServerHandle handle = this.getServerHandle();
      handle.targets = Collections.unmodifiableCollection(server.getPlayerList().getPlayers());
      return handle;
   }

   public OwoNetChannel.ServerHandle serverHandle(Collection<ServerPlayer> targets) {
      OwoNetChannel.ServerHandle handle = this.getServerHandle();
      handle.targets = targets;
      return handle;
   }

   public OwoNetChannel.ServerHandle serverHandle(Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         OwoNetChannel.ServerHandle handle = this.getServerHandle();
         handle.targets = Collections.singleton(serverPlayer);
         return handle;
      } else {
         throw new NetworkException("'player' must be a 'ServerPlayerEntity'");
      }
   }

   public OwoNetChannel.ServerHandle serverHandle(BlockEntity entity) {
      if (entity.getLevel().isClientSide) {
         throw new NetworkException("Server handle cannot be obtained on the client");
      } else {
         return this.serverHandle((ServerLevel)entity.getLevel(), entity.getBlockPos());
      }
   }

   public OwoNetChannel.ServerHandle serverHandle(ServerLevel world, BlockPos pos) {
      return this.serverHandle(Collections.unmodifiableCollection(world.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false)));
   }

   private OwoNetChannel.ServerHandle getServerHandle() {
      if (this.serverHandle == null) {
         this.serverHandle = new OwoNetChannel.ServerHandle();
      }

      return this.serverHandle;
   }

   private <R extends Record> void createEndec(Class<R> messageClass, int handlerIndex, Dist target, Supplier<StructEndec<R>> supplier) {
      OwoFreezer.checkRegister("Network handlers");
      OwoNetChannel.IndexedEndec<?> endec = this.endecsByClass.get(messageClass);
      if (endec == null) {
         OwoNetChannel.IndexedEndec<R> indexedEndec = OwoNetChannel.IndexedEndec.create(messageClass, supplier.get(), handlerIndex, target);
         this.endecsByClass.put(messageClass, indexedEndec);
         this.endecsByIndex.put(target == Dist.CLIENT ? -handlerIndex : handlerIndex, indexedEndec);
      } else {
         if (endec.handlerIndex(target) != -1) {
            throw new IllegalStateException("Message class '" + messageClass.getName() + "' is already registered for target environment " + target);
         }

         endec.setHandlerIndex(handlerIndex, target);
         this.endecsByIndex.put(target == Dist.CLIENT ? -handlerIndex : handlerIndex, endec);
      }
   }

   private void verify() {
      if (FMLLoader.getDist() == Dist.CLIENT && !this.deferredClientEndecs.isEmpty()) {
         throw new NetworkException(
            "Some deferred client handlers for channel "
               + this.packetId
               + " haven't been registered: "
               + this.deferredClientEndecs.keySet().stream().map(Class::getName).collect(Collectors.joining(", "))
         );
      }
   }

   static {
      OwoFreezer.registerFreezeCallback(() -> {
         for (OwoNetChannel channel : REGISTERED_CHANNELS.values()) {
            channel.verify();
         }
      });
   }

   public interface ChannelHandler<R extends Record, E extends OwoNetChannel.EnvironmentAccess<?, ?, ?>> {
      void handle(R var1, E var2);
   }

   public class ClientHandle {
      public <R extends Record> void send(R message) {
         Minecraft.getInstance().getConnection().send(new OwoNetChannel.MessagePayload(OwoNetChannel.this.packetId, message));
      }

      @SafeVarargs
      public final <R extends Record> void send(R... messages) {
         for (R message : messages) {
            this.send(message);
         }
      }
   }

   public interface EnvironmentAccess<P extends Player, R, N> {
      P player();

      R runtime();

      N netHandler();
   }

   static final class IndexedEndec<R extends Record> {
      private int clientHandlerIndex = -1;
      private int serverHandlerIndex = -1;
      private final Class<R> recordClass;
      private final StructEndec<R> endec;

      private IndexedEndec(Class<R> recordClass, StructEndec<R> endec) {
         this.endec = endec;
         this.recordClass = recordClass;
      }

      public static <R extends Record> OwoNetChannel.IndexedEndec<R> create(Class<R> rClass, StructEndec<R> endec, int index, Dist target) {
         return new OwoNetChannel.IndexedEndec<>(rClass, endec).setHandlerIndex(index, target);
      }

      public OwoNetChannel.IndexedEndec<R> setHandlerIndex(int index, Dist target) {
         switch (target) {
            case CLIENT:
               this.clientHandlerIndex = index;
               break;
            case DEDICATED_SERVER:
               this.serverHandlerIndex = index;
         }

         return this;
      }

      public int handlerIndex(Dist target) {
         return switch (target) {
            case CLIENT -> this.clientHandlerIndex;
            case DEDICATED_SERVER -> this.serverHandlerIndex;
            default -> throw new MatchException(null, null);
         };
      }

      public Class<R> getRecordClass() {
         return this.recordClass;
      }
   }

   public record MessagePayload(Type<OwoNetChannel.MessagePayload> id, Record message) implements CustomPacketPayload {
      public Type<? extends CustomPacketPayload> type() {
         return this.id;
      }
   }

   public class ServerHandle {
      private Collection<ServerPlayer> targets = Collections.emptySet();

      public <R extends Record> void send(R message) {
         this.targets.forEach(player -> player.connection.send(new OwoNetChannel.MessagePayload(OwoNetChannel.this.packetId, message)));
         this.targets = null;
      }

      @SafeVarargs
      public final <R extends Record> void send(R... messages) {
         this.targets.forEach(player -> {
            for (R message : messages) {
               player.connection.send(new OwoNetChannel.MessagePayload(OwoNetChannel.this.packetId, message));
            }
         });
         this.targets = null;
      }
   }
}
