package org.dimdev.limlib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources.PathResourcesSupplier;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BlockToolModificationEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.tuple.Triple;
import org.dimdev.limlib.api.ICreativeTabHandler;
import org.dimdev.limlib.api.IRegister;
import org.dimdev.limlib.api.ISided;
import org.dimdev.limlib.api.ModCommon;
import org.dimdev.limlib.impl.SidedImpl;
import org.dimdev.limlib.util.DataValue;

public abstract class NeoForgeSided<V extends NeoForgeSided<V, T>, T extends ModCommon<? super V>> extends SidedImpl<V, T> {
   private final List<Consumer<BuildCreativeModeTabContentsEvent>> BUILD_CONTENTS_LISTENERS = new ArrayList<>();
   private final Map<ResourceKey<?>, Map<ResourceLocation, Object>> toRegister = new HashMap<>();
   private final Map<ResourceKey<?>, Map<ResourceLocation, NeoForgeSided.HolderRegistration<?>>> toRegisterHolder = new HashMap<>();
   private final Map<ResourceKey<?>, AddCallback<?>> callbacks = new HashMap<>();
   private final IEventBus bus;
   private ResourceKey<? extends Registry<?>> activeKey;
   private final Map<ResourceKey<?>, List<Runnable>> registerRunnables = new HashMap<>();
   private final List<Registry<?>> registriesToRegister = new ArrayList<>();
   private final List<NeoForgeSided.EntityAttributeRegistration> entityAttributeRegistrations = new ArrayList<>();
   private final List<NeoForgeSided.DataPackRegistryRegistration<?>> dataPackRegistries = new ArrayList<>();
   private final Object2IntMap<ItemLike> fuels = new Object2IntLinkedOpenHashMap();
   private final Map<Block, Block> strippables = new HashMap<>();
   private List<NeoForgeSided.CreativeTabModifier> creativeTabModifiers;
   private final List<NeoForgeSided.ClientPacket<?>> clientPackets = new ArrayList<>();
   private final List<NeoForgeSided.ServerPacket<?>> serverPackets = new ArrayList<>();
   private final List<Triple<ResourceLocation, BiConsumer<Provider, ResourceManager>, Boolean>> loaders = new ArrayList<>();
   private final Map<PackType, List<NeoForgeSided.PackInfo>> packs = new HashMap<>();

   public NeoForgeSided(IEventBus bus, T common) {
      super(common);
      this.bus = bus;
      bus.addListener(this::modifyCreativeTabContents);
      bus.addListener(this::buildCreateTabContents);
      bus.addListener(this::onEntityAttributeRegister);
      bus.addListener(this::onDataPackRegister);
      bus.addListener(event -> this.registriesToRegister.forEach(event::register));
      bus.addListener(EventPriority.LOWEST, event -> {
         ResourceKey<? extends Registry<?>> key = event.getRegistryKey();
         this.activeKey = key;

         try {
            List<Runnable> runnables = this.registerRunnables.remove(key);
            if (runnables != null) {
               runnables.forEach(Runnable::run);
            }

            Registry<?> registry = event.getRegistry();
            AddCallback<?> callback = this.callbacks.get(key);
            if (callback != null) {
               registry.addCallback(callback);
            }

            Map<ResourceLocation, Object> map = this.toRegister.get(key);
            if (map != null && !map.isEmpty()) {
               this.populate((Registry<T>)registry, map);
            }

            Map<ResourceLocation, NeoForgeSided.HolderRegistration<?>> holderMap = this.toRegisterHolder.remove(key);
            if (holderMap != null && !holderMap.isEmpty()) {
               this.populateHolders((Registry<T>)registry, holderMap);
            }
         } finally {
            this.activeKey = null;
         }
      });
      bus.addListener(this::registerPackets);
      NeoForge.EVENT_BUS.addListener(this::addReloaders);
      NeoForge.EVENT_BUS.addListener(this::getFuelBurnTime);
      NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::modifyBlockWithTool);
      bus.addListener(this::addPackFinders);
      common.init(this.self());
   }

   private void modifyBlockWithTool(BlockToolModificationEvent event) {
      if (event.getItemAbility() == ItemAbilities.AXE_STRIP && event.getFinalState() == event.getState()) {
         Block target = this.strippables.get(event.getState().getBlock());
         if (target != null) {
            event.setFinalState(target.withPropertiesOf(event.getState()));
         }
      }
   }

   private void getFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
      Integer burnTime = this.fuels.get(event.getItemStack().getItem());
      if (burnTime != null) {
         event.setBurnTime(burnTime);
      }
   }

   private void onDataPackRegister(NewRegistry event) {
      this.dataPackRegistries.forEach(registration -> registration.register(event));
   }

   public <T> void populate(Registry<T> registry, Map<ResourceLocation, Object> map) {
      map.forEach((resourceLocation, obj) -> Registry.register(registry, resourceLocation, obj));
   }

   public <T> void populateHolders(Registry<T> registry, Map<ResourceLocation, NeoForgeSided.HolderRegistration<?>> map) {
      map.forEach((resourceLocation, registration) -> registration.register(registry, resourceLocation));
   }

   @Override
   public void modify(CreativeModeTab tab, ICreativeTabHandler.ModifyTabCallback filler) {
      this.BUILD_CONTENTS_LISTENERS.add(event -> {
         if (event.getTab().equals(tab)) {
            filler.accept(event.getFlags(), this.wrapTabOutput(event), event.hasPermissions());
         }
      });
   }

   private void buildCreateTabContents(BuildCreativeModeTabContentsEvent event) {
      if (this.APPENDS.containsKey(event.getTab())) {
         this.APPENDS.get(event.getTab()).forEach(event::accept);
      }

      for (Consumer<BuildCreativeModeTabContentsEvent> listener : this.BUILD_CONTENTS_LISTENERS) {
         listener.accept(event);
      }
   }

   private ICreativeTabHandler.CreativeTabOutput wrapTabOutput(final BuildCreativeModeTabContentsEvent event) {
      return new ICreativeTabHandler.CreativeTabOutput() {
         @Override
         public void acceptAfter(ItemStack after, ItemStack stack, TabVisibility visibility) {
            event.insertAfter(after, stack, visibility);
         }

         @Override
         public void acceptBefore(ItemStack before, ItemStack stack, TabVisibility visibility) {
            event.insertBefore(before, stack, visibility);
         }
      };
   }

   @Override
   public <T, V extends T> V register(ResourceKey<Registry<T>> key, ResourceLocation id, V obj) {
      if (key.equals(this.activeKey)) {
         return (V)Registry.register((Registry)BuiltInRegistries.REGISTRY.get(key.location()), id, obj);
      } else {
         Map<ResourceLocation, Object> map = this.toRegister.computeIfAbsent(key, a -> new HashMap<>());
         map.putIfAbsent(id, obj);
         return obj;
      }
   }

   @Override
   public <T, V extends T> Holder<T> registerHolder(ResourceKey<Registry<T>> key, ResourceLocation id, V obj) {
      if (key.equals(this.activeKey)) {
         return Registry.registerForHolder((Registry)BuiltInRegistries.REGISTRY.get(key.location()), id, obj);
      } else {
         Map<ResourceLocation, NeoForgeSided.HolderRegistration<?>> map = this.toRegisterHolder.computeIfAbsent(key, a -> new HashMap<>());
         NeoForgeSided.HolderRegistration<T> registration = (NeoForgeSided.HolderRegistration<T>)map.computeIfAbsent(
            id, ignored -> new NeoForgeSided.HolderRegistration<>(obj, NeoForgeSided.BindableDeferredHolder.createBindable(key, id))
         );
         return registration.holder();
      }
   }

   @Override
   public <T> void registerCallback(Registry<T> registry, TriConsumer<Registry<T>, ResourceLocation, T> consumer) {
      this.callbacks.put(registry.key(), new NeoForgeSided.Callback(consumer));
   }

   @Override
   public CreativeModeTab createTab(Function<Builder, Builder> consumer) {
      return consumer.apply(CreativeModeTab.builder()).build();
   }

   @Override
   public void onServerStarting(Consumer<MinecraftServer> consumer) {
      NeoForge.EVENT_BUS.addListener(event -> consumer.accept(event.getServer()));
   }

   @Override
   public void onServerStarted(Consumer<MinecraftServer> consumer) {
      NeoForge.EVENT_BUS.addListener(event -> consumer.accept(event.getServer()));
   }

   @Override
   public void onPlayerQuit(Consumer<ServerPlayer> consumer) {
      NeoForge.EVENT_BUS.addListener(event -> {
         if (event.getEntity() instanceof ServerPlayer player) {
            consumer.accept(player);
         }
      });
   }

   @Override
   public void onServerLevelTick(Consumer<ServerLevel> consumer) {
      NeoForge.EVENT_BUS.addListener(event -> {
         if (event.getLevel() instanceof ServerLevel level) {
            consumer.accept(level);
         }
      });
   }

   @Override
   public void onAttackBlock(ISided.AttackBlockCallback callback) {
      NeoForge.EVENT_BUS.addListener(event -> {
         if (event.getAction() == Action.START && event.getFace() != null) {
            InteractionResult result = callback.attack(event.getEntity(), event.getHand(), event.getPos(), event.getFace());
            if (result != InteractionResult.PASS) {
               event.setCanceled(true);
            }
         }
      });
   }

   @Override
   public void onUseItem(ISided.UseItemCallback callback) {
      NeoForge.EVENT_BUS.addListener(event -> {
         InteractionResult result = callback.use(event.getEntity(), event.getHand());
         if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
         }
      });
   }

   @Override
   public void onUseBlock(ISided.UseBlockCallback callback) {
      NeoForge.EVENT_BUS.addListener(event -> {
         InteractionResult result = callback.use(event.getEntity(), event.getHand(), event.getHitVec());
         if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
         }
      });
   }

   @Override
   public void onBeforeBlockBreak(ISided.BlockBreakCallback callback) {
      NeoForge.EVENT_BUS.addListener(event -> {
         if (event.getLevel() instanceof Level level && callback.shouldCancel(level, event.getPos(), event.getState(), event.getPlayer())) {
            event.setCanceled(true);
         }
      });
   }

   @Override
   public void onBeforeBlockPlace(ISided.BlockPlaceCallback callback) {
      NeoForge.EVENT_BUS.addListener(event -> {
         if (event.getLevel() instanceof Level level && callback.shouldCancel(level, event.getPos(), event.getPlacedBlock(), event.getEntity())) {
            event.setCanceled(true);
         }
      });
   }

   @Override
   public void registerEntityAttributes(
      EntityType<? extends LivingEntity> type, Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> attributes
   ) {
      this.entityAttributeRegistrations.add(new NeoForgeSided.EntityAttributeRegistration(type, attributes));
   }

   private void onEntityAttributeRegister(EntityAttributeCreationEvent event) {
      this.entityAttributeRegistrations.forEach(registration -> event.put(registration.type(), registration.attributes().get().build()));
   }

   @Override
   public void registerRunnable(ResourceKey<? extends Registry<?>> key, Runnable runnable) {
      this.registerRunnables.computeIfAbsent(key, ignored -> new ArrayList<>()).add(runnable);
   }

   @Override
   public <T> DataValue<T> registerDataValue(String name, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
      net.neoforged.neoforge.attachment.AttachmentType.Builder<T> dataValue = AttachmentType.builder(defaultValue).serialize(codec);
      if (streamCodec != null) {
         dataValue.sync(streamCodec);
      }

      return this.register(Keys.ATTACHMENT_TYPES, name, dataValue.build());
   }

   @Override
   public void registerRunDataValue(Runnable runnable) {
      this.registerRunnable(Keys.ATTACHMENT_TYPES, runnable);
   }

   @Override
   public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, ResourceLocation defaultId, boolean sync) {
      Registry<T> registry = new RegistryBuilder(key).sync(sync).defaultKey(defaultId).create();
      this.registriesToRegister.add(registry);
      return registry;
   }

   @Override
   public <T extends CustomPacketPayload> void sendPacket(ServerPlayer player, T packet) {
      PacketDistributor.sendToPlayer(player, packet, new CustomPacketPayload[0]);
   }

   @Override
   public <T extends CustomPacketPayload> void sendPacket(T packet) {
      PacketDistributor.sendToServer(packet, new CustomPacketPayload[0]);
   }

   @Override
   public <T extends CustomPacketPayload> void registerClientPacket(Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Consumer<T> function) {
      this.clientPackets.add(new NeoForgeSided.ClientPacket<>(type, streamCodec, function));
   }

   @Override
   public <T extends CustomPacketPayload> void registerServerPacket(
      Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, BiFunction<T, ServerPlayer, ? extends CustomPacketPayload> function
   ) {
      this.serverPackets.add(new NeoForgeSided.ServerPacket<>(type, streamCodec, function));
   }

   private void registerPackets(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");
      this.clientPackets.forEach(packet -> packet.register(registrar));
      this.serverPackets.forEach(packet -> packet.register(registrar));
   }

   @Override
   public Path getConfigRoot() {
      return FMLPaths.CONFIGDIR.get();
   }

   @Override
   public void addPack(PackType type, String id, String name, boolean defaultedOn) {
      this.packs.computeIfAbsent(type, a -> new ArrayList<>()).add(new NeoForgeSided.PackInfo(id, name, defaultedOn));
   }

   public void addPackFinders(AddPackFindersEvent event) {
      PackType type = event.getPackType();
      String modId = this.common.getModId();
      event.addRepositorySource(source -> this.packs.getOrDefault(type, Collections.emptyList()).stream().map(a -> a.create(modId, type)).forEach(source));
   }

   public void addReloaders(AddReloadListenerEvent event) {
      this.loaders
         .forEach(
            pair -> event.addListener(
               new NeoforgeResourceLoader.Server((ResourceLocation)pair.getLeft(), (BiConsumer<Provider, ResourceManager>)pair.getMiddle())
            )
         );
   }

   @Override
   public void registerServerLoader(String name, BiConsumer<Provider, ResourceManager> consumer, boolean loadAfterTags) {
      this.loaders.add(Triple.of(ResourceLocation.fromNamespaceAndPath(this.common.getModId(), name), consumer, loadAfterTags));
   }

   @Override
   public MinecraftServer getServer() {
      return ServerLifecycleHooks.getCurrentServer();
   }

   @Override
   public boolean isModLoaded(String id) {
      return ModList.get().isLoaded(id);
   }

   @Override
   public boolean isClient() {
      return FMLEnvironment.dist == Dist.CLIENT;
   }

   @Override
   public long bucketAmount() {
      return 1000L;
   }

   @Override
   public void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
      NeoForge.EVENT_BUS.addListener(event -> consumer.accept(event.getDispatcher()));
   }

   @Override
   public <T> void createDynamicRegistry(ResourceKey<Registry<T>> key, Codec<T> codec, Codec<T> networkCodec) {
      this.dataPackRegistries.add(new NeoForgeSided.DataPackRegistryRegistration(key, codec, networkCodec));
   }

   @Override
   public void registerStrippable(Block source, Block target) {
      this.strippables.put(source, target);
   }

   @Override
   public void registerFuel(ItemLike item, int amount) {
      this.fuels.put(item, amount);
   }

   @Override
   public void registryFlammable(Block block, int encouragement, int flammability) {
      ((FireBlock)Blocks.FIRE).setFlammable(block, encouragement, flammability);
   }

   @Override
   public void modifyCreativeTab(ResourceKey<CreativeModeTab> tab, Consumer<IRegister.CreativeTabEntries> consumer) {
      this.creativeTabModifiers().add(new NeoForgeSided.CreativeTabModifier(tab, consumer));
   }

   private List<NeoForgeSided.CreativeTabModifier> creativeTabModifiers() {
      if (this.creativeTabModifiers == null) {
         this.creativeTabModifiers = new ArrayList<>();
      }

      return this.creativeTabModifiers;
   }

   private void modifyCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
      for (NeoForgeSided.CreativeTabModifier modifier : this.creativeTabModifiers()) {
         if (event.getTabKey().equals(modifier.tab())) {
            modifier.consumer().accept(new NeoForgeSided.NeoForgeCreativeTabEntries(event));
         }
      }
   }

   private static class BindableDeferredHolder<R, T extends R> extends DeferredHolder<R, T> {
      private BindableDeferredHolder(ResourceKey<R> key) {
         super(key);
      }

      private static <R, T extends R> NeoForgeSided.BindableDeferredHolder<R, T> createBindable(
         ResourceKey<? extends Registry<R>> registryKey, ResourceLocation id
      ) {
         return new NeoForgeSided.BindableDeferredHolder<>(ResourceKey.create(registryKey, id));
      }

      private void bind() {
         this.bind(false);
      }
   }

   class Callback<T> implements AddCallback<T> {
      private final TriConsumer<Registry<T>, ResourceLocation, T> consumer;

      Callback(TriConsumer<Registry<T>, ResourceLocation, T> consumer) {
         this.consumer = consumer;
      }

      public void onAdd(Registry<T> registry, int id, ResourceKey<T> key, T obj) {
         ResourceKey<? extends Registry<?>> previousKey = NeoForgeSided.this.activeKey;
         NeoForgeSided.this.activeKey = registry.key();

         try {
            this.consumer.accept(registry, key.location(), obj);
         } finally {
            NeoForgeSided.this.activeKey = previousKey;
         }
      }
   }

   private record ClientPacket<T extends CustomPacketPayload>(Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Consumer<T> function) {
      public void register(PayloadRegistrar registrar) {
         registrar.playToClient(this.type, this.streamCodec, (packet, ctx) -> this.function.accept((T)packet));
      }
   }

   private record CreativeTabModifier(ResourceKey<CreativeModeTab> tab, Consumer<IRegister.CreativeTabEntries> consumer) {
   }

   private record DataPackRegistryRegistration<T>(ResourceKey<Registry<T>> key, Codec<T> codec, Codec<T> networkCodec) {
      private void register(NewRegistry event) {
         if (this.networkCodec != null) {
            event.dataPackRegistry(this.key, this.codec, this.networkCodec);
         } else {
            event.dataPackRegistry(this.key, this.codec);
         }
      }
   }

   private record EntityAttributeRegistration(
      EntityType<? extends LivingEntity> type, Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> attributes
   ) {
   }

   private record HolderRegistration<T>(T obj, NeoForgeSided.BindableDeferredHolder<T, ? extends T> holder) {
      private void register(Registry<T> registry, ResourceLocation id) {
         Registry.registerForHolder(registry, id, this.obj);
         this.holder.bind();
      }
   }

   private record NeoForgeCreativeTabEntries(BuildCreativeModeTabContentsEvent event) implements IRegister.CreativeTabEntries {
      public void accept(ItemStack stack, TabVisibility visibility) {
         this.event.accept(stack, visibility);
      }

      @Override
      public void addAfter(ItemStack after, Collection<ItemStack> stacks, TabVisibility visibility) {
         if (after.isEmpty()) {
            this.acceptAll(stacks, visibility);
         } else {
            ItemStack previous = after;

            for (ItemStack stack : stacks) {
               this.event.insertAfter(previous, stack, visibility);
               previous = stack;
            }
         }
      }

      @Override
      public void addBefore(ItemStack before, Collection<ItemStack> stacks, TabVisibility visibility) {
         if (before.isEmpty()) {
            this.acceptAll(stacks, visibility);
         } else {
            for (ItemStack stack : stacks) {
               this.event.insertBefore(before, stack, visibility);
            }
         }
      }
   }

   private record PackInfo(String id, String name, boolean defaultedOn) {
      public Pack create(String modId, PackType type) {
         Path resourcePath = ModList.get().getModFileById(modId).getFile().findResource(new String[]{"resourcepacks", this.id});
         return Pack.readMetaAndCreate(
            new PackLocationInfo(this.id, Component.literal(this.name), PackSource.BUILT_IN, Optional.empty()),
            new PathResourcesSupplier(resourcePath),
            type,
            new PackSelectionConfig(false, Position.BOTTOM, false)
         );
      }
   }

   private record PlayPayloadHandlerReturnable<T extends CustomPacketPayload>(BiFunction<T, ServerPlayer, ? extends CustomPacketPayload> packetFunction)
      implements IPayloadHandler<T> {
      public void handle(T payload, IPayloadContext context) {
         CustomPacketPayload returnPayload = this.packetFunction.apply(payload, (ServerPlayer)context.player());
         if (returnPayload != null) {
            context.handle(returnPayload);
         }
      }
   }

   private record ServerPacket<T extends CustomPacketPayload>(
      Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, BiFunction<T, ServerPlayer, ? extends CustomPacketPayload> function
   ) {
      public void register(PayloadRegistrar registrar) {
         registrar.playToServer(this.type, this.streamCodec, new NeoForgeSided.PlayPayloadHandlerReturnable(this.function));
      }
   }
}
