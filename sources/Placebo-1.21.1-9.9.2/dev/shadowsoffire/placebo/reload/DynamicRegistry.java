package dev.shadowsoffire.placebo.reload;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.codec.CodecMap;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.json.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class DynamicRegistry<R extends CodecProvider<? super R>> extends SimpleJsonResourceReloadListener {
   protected final Logger logger;
   protected final String path;
   protected final boolean synced;
   protected final boolean subtypes;
   protected final CodecMap<R> codecs;
   protected final Codec<DynamicHolder<R>> holderCodec;
   protected final StreamCodec<ByteBuf, DynamicHolder<R>> holderStreamCodec;
   protected final BiMap<ResourceLocation, StreamCodec<RegistryFriendlyByteBuf, ? extends R>> streamCodecs;
   protected BiMap<ResourceLocation, R> registry = ImmutableBiMap.of();
   private final Map<ResourceLocation, R> staged = new HashMap<>();
   private final Map<ResourceLocation, DynamicHolder<R>> holders = new ConcurrentHashMap<>();
   private final Set<RegistryCallback<R>> callbacks = new HashSet<>();

   public DynamicRegistry(Logger logger, String path, boolean synced, boolean subtypes) {
      super(new GsonBuilder().setLenient().create(), path);
      this.logger = logger;
      this.path = path;
      this.synced = synced;
      this.subtypes = subtypes;
      this.codecs = new CodecMap<>(path);
      this.streamCodecs = HashBiMap.create();
      this.registerBuiltinCodecs();
      if (this.codecs.isEmpty()) {
         throw new RuntimeException("Attempted to create a dynamic registry for " + path + " with no built-in codecs!");
      } else {
         this.holderCodec = ResourceLocation.CODEC.xmap(this::holder, DynamicHolder::getId);
         this.holderStreamCodec = ResourceLocation.STREAM_CODEC.map(this::holder, DynamicHolder::getId);
      }
   }

   protected final void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
      this.beginReload(DynamicRegistry.ReloadType.SERVER);
      ConditionalOps<JsonElement> ops = this.makeConditionalOps();
      objects.forEach(
         (key, ele) -> {
            try {
               if (JsonUtil.checkAndLogEmpty(ele, key, this.path, this.logger) && JsonUtil.checkConditions(ele, key, this.path, this.logger, ops)) {
                  JsonObject obj = ele.getAsJsonObject();
                  R deserialized = (R)((Pair)this.codecs.decode(ops, obj).getOrThrow(this::makeCodecException)).getFirst();
                  Preconditions.checkNotNull(deserialized.getCodec(), "A " + this.path + " with id " + key + " is not declaring a codec.");
                  Preconditions.checkNotNull(
                     this.codecs.getKey(deserialized.getCodec()), "A " + this.path + " with id " + key + " is declaring an unregistered codec."
                  );
                  this.register(key, deserialized);
               }
            } catch (Exception var6) {
               this.logger.error("Failed parsing {} file {}.", this.path, key);
               this.logger.error("Underlying Exception: ", var6);
            }
         }
      );
      this.onReload(DynamicRegistry.ReloadType.SERVER);
   }

   protected abstract void registerBuiltinCodecs();

   @MustBeInvokedByOverriders
   protected void beginReload(DynamicRegistry.ReloadType type) {
      this.beginReload();
   }

   @Deprecated(
      forRemoval = true,
      since = "9.9.0"
   )
   protected void beginReload() {
      this.callbacks.forEach(l -> l.beginReload(this));
      this.registry = new DynRegBiMap();
      this.holders.values().forEach(DynamicHolder::unbind);
   }

   @MustBeInvokedByOverriders
   protected void onReload(DynamicRegistry.ReloadType type) {
      this.onReload();
   }

   @Deprecated(
      forRemoval = true,
      since = "9.9.0"
   )
   protected void onReload() {
      this.registry = Maps.unmodifiableBiMap(this.registry);
      this.logger.info("Registered {} {}.", this.registry.size(), this.path);
      this.callbacks.forEach(l -> l.onReload(this));
      this.holders.values().forEach(DynamicHolder::bind);
   }

   public Set<ResourceLocation> getKeys() {
      return this.registry.keySet();
   }

   public Collection<R> getValues() {
      return this.registry.values();
   }

   @Nullable
   public R getValue(ResourceLocation key) {
      return (R)this.registry.get(key);
   }

   @Nullable
   public ResourceLocation getKey(R value) {
      return (ResourceLocation)this.registry.inverse().get(value);
   }

   public R getOrDefault(ResourceLocation key, R defValue) {
      return (R)this.registry.getOrDefault(key, defValue);
   }

   public void registerToBus() {
      if (this.synced) {
         DynamicRegistry.SyncManagement.registerForSync(this);
      }

      NeoForge.EVENT_BUS.addListener(this::addReloader);
   }

   public DynamicHolder<R> holder(@Nullable ResourceLocation id) {
      return id == null ? this.emptyHolder() : this.holders.computeIfAbsent(id, k -> new DynamicHolder<>(this, k));
   }

   public DynamicHolder<R> holder(R value) {
      ResourceLocation key = this.getKey(value);
      return this.holder(key == null ? DynamicHolder.EMPTY : key);
   }

   public DynamicHolder<R> emptyHolder() {
      return this.holder(DynamicHolder.EMPTY);
   }

   public Codec<DynamicHolder<R>> holderCodec() {
      return this.holderCodec;
   }

   public StreamCodec<ByteBuf, DynamicHolder<R>> holderStreamCodec() {
      if (!this.synced) {
         throw new UnsupportedOperationException("Cannot retrieve a stream codec for the non-synced DynamicRegistry: " + this.path);
      } else {
         return this.holderStreamCodec;
      }
   }

   public final void registerCodec(ResourceLocation key, Codec<? extends R> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends R> streamCodec) {
      if (!this.subtypes) {
         throw new UnsupportedOperationException("Attempted to call registerCodec on a registry which does not support subtypes.");
      } else {
         this.registerInternal(key, codec, streamCodec);
      }
   }

   public final void registerCodec(ResourceLocation key, Codec<? extends R> codec) {
      this.registerCodec(key, codec, ByteBufCodecs.fromCodecWithRegistries(codec));
   }

   protected final void registerDefaultCodec(ResourceLocation key, Codec<? extends R> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends R> streamCodec) {
      if (this.codecs.getDefaultCodec() != null) {
         throw new UnsupportedOperationException("Attempted to register a second " + this.path + " default codec with key " + key);
      } else {
         this.registerInternal(key, codec, streamCodec);
         this.codecs.setDefaultCodec(codec);
      }
   }

   protected final void registerDefaultCodec(ResourceLocation key, Codec<? extends R> codec) {
      this.registerDefaultCodec(key, codec, ByteBufCodecs.fromCodecWithRegistries(codec));
   }

   public final boolean addCallback(RegistryCallback<R> callback) {
      return this.callbacks.add(callback);
   }

   public final boolean removeCallback(RegistryCallback<R> callback) {
      return this.callbacks.remove(callback);
   }

   public final String getPath() {
      return this.path;
   }

   public final Codec<R> elementCodec() {
      return this.codecs;
   }

   public final void validateExistingHolders() {
      String error = "";

      for (DynamicHolder<R> holder : this.holders.values()) {
         if (!holder.isBound()) {
            error = error + "Failed to validate dynamic holder %s for registry %s\n".formatted(holder.getId(), this.getPath());
         }
      }

      if (!error.isEmpty()) {
         throw new RuntimeException(error);
      }
   }

   protected final void register(ResourceLocation key, R value) {
      if (this.registry.containsKey(key)) {
         throw new UnsupportedOperationException("Attempted to register a " + this.path + " with a duplicate registry ID! Key: " + key);
      } else {
         this.validateItem(key, value);
         this.registry.put(key, value);
         this.holders.computeIfAbsent(key, k -> new DynamicHolder<>(this, k));
      }
   }

   protected void validateItem(ResourceLocation key, R value) {
   }

   private void addReloader(AddReloadListenerEvent e) {
      e.addListener(this);
   }

   private void processDedicatedClientReload() {
      this.beginReload(DynamicRegistry.ReloadType.DEDICATED_CLIENT);
      this.staged.forEach(this::register);
      this.onReload(DynamicRegistry.ReloadType.DEDICATED_CLIENT);
   }

   private void processIntegratedClientReload() {
      this.staged.clear();
      this.staged.putAll(this.registry);
      this.beginReload(DynamicRegistry.ReloadType.INTEGRATED_CLIENT);
      this.staged.forEach(this::register);
      this.onReload(DynamicRegistry.ReloadType.INTEGRATED_CLIENT);
   }

   private CodecException makeCodecException(String msg) {
      return new CodecException("Codec failure for type %s, message: %s".formatted(this.path, msg));
   }

   private void sync(OnDatapackSyncEvent e) {
      ServerPlayer player = e.getPlayer();
      Consumer<CustomPacketPayload> target = player == null
         ? x$0 -> PacketDistributor.sendToAllPlayers(x$0, new CustomPacketPayload[0])
         : payload -> PacketDistributor.sendToPlayer(player, payload, new CustomPacketPayload[0]);
      target.accept(new ReloadListenerPayloads.Start(this.path));
      this.registry.forEach((k, v) -> target.accept(new ReloadListenerPayloads.Content(this.path, k, Either.left(v))));
      target.accept(new ReloadListenerPayloads.End(this.path));
   }

   private void registerInternal(ResourceLocation key, Codec<? extends R> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends R> streamCodec) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(codec, "Attempted to register a null codec for key " + key);
      Preconditions.checkNotNull(streamCodec, "Attempted to register a null stream codec for key " + key);
      this.codecs.register(key, codec);
      this.streamCodecs.put(key, streamCodec);
   }

   @Internal
   public static class DataGenPopulator<R extends CodecProvider<? super R>> {
      private final DynamicRegistry<R> registry;

      private DataGenPopulator(DynamicRegistry<R> registry) {
         this.registry = registry;
      }

      private DynamicRegistry.DataGenPopulator<R> start() {
         BiMap<ResourceLocation, R> old = this.registry.registry;
         this.registry.beginReload();
         old.forEach(this::register);
         return this;
      }

      public DynamicRegistry.DataGenPopulator<R> register(ResourceLocation id, R object) {
         this.registry.registry.put(id, object);
         return this;
      }

      private DynamicRegistry.DataGenPopulator<R> end() {
         this.registry.onReload();
         return this;
      }

      public static <R extends CodecProvider<? super R>> void runScoped(DynamicRegistry<R> registry, Consumer<DynamicRegistry.DataGenPopulator<R>> consumer) {
         DynamicRegistry.DataGenPopulator<R> populator = new DynamicRegistry.DataGenPopulator<>(registry).start();
         consumer.accept(populator);
         populator.end();
      }
   }

   public static enum ReloadType {
      SERVER,
      INTEGRATED_CLIENT,
      DEDICATED_CLIENT;
   }

   @Internal
   static class SyncManagement {
      private static final Map<String, DynamicRegistry<?>> SYNC_REGISTRY = new LinkedHashMap<>();

      static void registerForSync(DynamicRegistry<?> listener) {
         if (!listener.synced) {
            throw new UnsupportedOperationException("Attempted to register the non-synced JSON Reload Listener " + listener.path + " as a synced listener!");
         } else {
            synchronized (SYNC_REGISTRY) {
               if (SYNC_REGISTRY.containsKey(listener.path)) {
                  throw new UnsupportedOperationException(
                     "Attempted to register the JSON Reload Listener for syncing " + listener.path + " but one already exists!"
                  );
               } else {
                  if (SYNC_REGISTRY.isEmpty()) {
                     NeoForge.EVENT_BUS.addListener(DynamicRegistry.SyncManagement::syncAll);
                  }

                  SYNC_REGISTRY.put(listener.path, listener);
               }
            }
         }
      }

      static void initSync(String path) {
         ifPresent(path, registry -> registry.staged.clear());
         Placebo.LOGGER.info("Starting sync for {}", path);
      }

      static <V extends CodecProvider<? super V>> void writeItem(String path, V value, RegistryFriendlyByteBuf buf) {
         ifPresent(path, registry -> {
            ResourceLocation type = registry.codecs.getKey(value.getCodec());
            buf.writeResourceLocation(type);
            ((StreamCodec)registry.streamCodecs.get(type)).encode(buf, value);
         });
      }

      static <V> V readItem(String path, RegistryFriendlyByteBuf buf) {
         DynamicRegistry<? extends CodecProvider<?>> registry = (DynamicRegistry<? extends CodecProvider<?>>)SYNC_REGISTRY.get(path);
         if (registry == null) {
            throw new RuntimeException("Received sync packet for unknown registry!");
         } else {
            ResourceLocation type = buf.readResourceLocation();
            return (V)((StreamCodec)registry.streamCodecs.get(type)).decode(buf);
         }
      }

      static <V> void acceptItem(String path, ResourceLocation key, V value) {
         ifPresent(path, registry -> registry.staged.put(key, value));
      }

      static void endSync(String path) {
         if (ServerLifecycleHooks.getCurrentServer() != null) {
            ifPresent(path, DynamicRegistry::processIntegratedClientReload);
         } else {
            ifPresent(path, DynamicRegistry::processDedicatedClientReload);
         }

         Placebo.LOGGER.info("Completed sync for {}", path);
      }

      private static void ifPresent(String path, Consumer<DynamicRegistry<?>> consumer) {
         DynamicRegistry<?> value = SYNC_REGISTRY.get(path);
         if (value != null) {
            consumer.accept(value);
         }
      }

      private static void syncAll(OnDatapackSyncEvent e) {
         SYNC_REGISTRY.values().forEach(r -> r.sync(e));
      }
   }
}
