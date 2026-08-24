package snownee.jade.util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.common.Tags.EntityTypes;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent.UpdateCause;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;
import snownee.jade.Jade;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.universal.ItemCollector;
import snownee.jade.addon.universal.ItemIterator;
import snownee.jade.addon.universal.ItemStorageProvider;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;
import snownee.jade.command.JadeServerCommand;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.WailaCommonRegistration;
import snownee.jade.impl.config.PluginConfig;
import snownee.jade.impl.lookup.WrappedHierarchyLookup;
import snownee.jade.mixin.CanItemPerformAbilityAccess;
import snownee.jade.network.ReceiveDataPacket;
import snownee.jade.network.RequestBlockPacket;
import snownee.jade.network.RequestEntityPacket;
import snownee.jade.network.ServerPingPacket;
import snownee.jade.network.ShowOverlayPacket;

@Mod("jade")
public final class CommonProxy {
   public CommonProxy(IEventBus modBus) {
      modBus.addListener(this::loadComplete);
      modBus.addListener(this::registerPayloadHandlers);
      NeoForge.EVENT_BUS.addListener(CommonProxy::playerJoin);
      NeoForge.EVENT_BUS.addListener(CommonProxy::registerServerCommand);
      if (isPhysicallyClient()) {
         ClientProxy.init(modBus);
      }
   }

   public static <T> T getDefaultStorage(Accessor<?> accessor, BlockCapability<T, ?> blockCapability, EntityCapability<T, ?> entityCapability) {
      if (accessor instanceof BlockAccessor blockAccessor) {
         return (T)accessor.getLevel()
            .getCapability(blockCapability, blockAccessor.getPosition(), blockAccessor.getBlockState(), blockAccessor.getBlockEntity(), null);
      } else {
         return (T)(accessor instanceof EntityAccessor entityAccessor ? entityAccessor.getEntity().getCapability(entityCapability, null) : null);
      }
   }

   public static <T> boolean hasDefaultStorage(Accessor<?> accessor, BlockCapability<T, ?> blockCapability, EntityCapability<T, ?> entityCapability) {
      return !(accessor instanceof BlockAccessor) && !(accessor instanceof EntityAccessor)
         ? true
         : getDefaultStorage(accessor, blockCapability, entityCapability) != null;
   }

   public static boolean hasDefaultItemStorage(Accessor<?> accessor) {
      return accessor.getTarget() == null && accessor instanceof BlockAccessor blockAccessor && blockAccessor.getBlock() instanceof WorldlyContainerHolder
         ? true
         : hasDefaultStorage(accessor, ItemHandler.BLOCK, ItemHandler.ENTITY);
   }

   public static boolean hasDefaultFluidStorage(Accessor<?> accessor) {
      return hasDefaultStorage(accessor, FluidHandler.BLOCK, FluidHandler.ENTITY);
   }

   public static boolean hasDefaultEnergyStorage(Accessor<?> accessor) {
      return hasDefaultStorage(accessor, EnergyStorage.BLOCK, EnergyStorage.ENTITY);
   }

   public static long bucketVolume() {
      return 1000L;
   }

   public static long blockVolume() {
      return 1000L;
   }

   public static boolean isCorrectConditions(List<LootItemCondition> conditions, ItemStack toolItem) {
      if (conditions.size() != 1) {
         return false;
      } else {
         LootItemCondition condition = (LootItemCondition)conditions.getFirst();
         if (condition instanceof MatchTool matchTool) {
            ItemPredicate itemPredicate = (ItemPredicate)matchTool.predicate().orElse(null);
            return itemPredicate != null && itemPredicate.test(toolItem);
         } else {
            if (condition instanceof AnyOfCondition anyOfCondition) {
               for (LootItemCondition child : anyOfCondition.terms) {
                  if (isCorrectConditions(List.of(child), toolItem)) {
                     return true;
                  }
               }
            } else if (condition instanceof CanItemPerformAbilityAccess canItemPerformAbility) {
               return canItemPerformAbility.getAbility() == ItemAbilities.SHEARS_DIG;
            }

            return false;
         }
      }
   }

   private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
      event.registrar("jade")
         .versioned("5")
         .optional()
         .playToClient(ReceiveDataPacket.TYPE, ReceiveDataPacket.CODEC, (payload, context) -> ReceiveDataPacket.handle(payload, context::enqueueWork))
         .playToClient(ServerPingPacket.TYPE, ServerPingPacket.CODEC, (payload, context) -> ServerPingPacket.handle(payload, context::enqueueWork))
         .playToServer(
            RequestEntityPacket.TYPE,
            RequestEntityPacket.CODEC,
            (payload, context) -> RequestEntityPacket.handle(payload, () -> (ServerPlayer)context.player())
         )
         .playToServer(
            RequestBlockPacket.TYPE, RequestBlockPacket.CODEC, (payload, context) -> RequestBlockPacket.handle(payload, () -> (ServerPlayer)context.player())
         )
         .playToClient(ShowOverlayPacket.TYPE, ShowOverlayPacket.CODEC, (payload, context) -> ShowOverlayPacket.handle(payload, context::enqueueWork));
   }

   public static int showOrHideFromServer(Collection<ServerPlayer> players, boolean show) {
      ShowOverlayPacket msg = new ShowOverlayPacket(show);

      for (ServerPlayer player : players) {
         player.connection.send(msg);
      }

      return players.size();
   }

   private static void playerJoin(PlayerLoggedInEvent event) {
      ServerPlayer player = (ServerPlayer)event.getEntity();
      String configs = PluginConfig.INSTANCE.getServerConfigs();
      List<Block> shearableBlocks = HarvestToolProvider.INSTANCE.getShearableBlocks();
      if (!configs.isEmpty()) {
         Jade.LOGGER.debug("Syncing config to {} ({})", event.getEntity().getGameProfile().getName(), event.getEntity().getGameProfile().getId());
      }

      List<ResourceLocation> blockProviderIds = WailaCommonRegistration.instance().blockDataProviders.mappedIds();
      List<ResourceLocation> entityProviderIds = WailaCommonRegistration.instance().entityDataProviders.mappedIds();
      player.connection.send(new ServerPingPacket(configs, shearableBlocks, blockProviderIds, entityProviderIds));
   }

   @Nullable
   public static String getLastKnownUsername(@Nullable UUID uuid) {
      if (uuid == null) {
         return null;
      } else {
         Optional<GameProfile> optional = SkullBlockEntity.fetchGameProfile(uuid).getNow(Optional.empty());
         return optional.isPresent() ? optional.get().getName() : UsernameCache.getLastKnownUsername(uuid);
      }
   }

   public static File getConfigDirectory() {
      return FMLPaths.CONFIGDIR.get().toFile();
   }

   public static boolean isCorrectToolForDrops(BlockState state, Player player, Level level, BlockPos pos) {
      return EventHooks.doPlayerHarvestCheck(player, state, level, pos);
   }

   public static String getModIdFromItem(ItemStack stack) {
      if (isPhysicallyClient()) {
         CustomModelData modelData = (CustomModelData)stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT);
         if (!CustomModelData.DEFAULT.equals(modelData)) {
            String key = "jade.customModelData.%s.namespace".formatted(modelData.value());
            if (I18n.exists(key)) {
               return I18n.get(key, new Object[0]);
            }
         }
      }

      return stack.getItem().getCreatorModId(stack);
   }

   public static boolean isPhysicallyClient() {
      return FMLEnvironment.dist.isClient();
   }

   private static void registerServerCommand(RegisterCommandsEvent event) {
      JadeServerCommand.register(event.getDispatcher());
   }

   public static ItemCollector<?> createItemCollector(Accessor<?> accessor, Cache<Object, ItemCollector<?>> containerCache) {
      Object target = accessor.getTarget();
      if (!(target instanceof Entity) && !(target instanceof ChestBlockEntity) || target instanceof AbstractChestedHorse) {
         try {
            IItemHandler itemHandler = findItemHandler(accessor);
            if (itemHandler != null) {
               return (ItemCollector<?>)containerCache.get(
                  itemHandler, () -> new ItemCollector<>(JadeForgeUtils.fromItemHandler(itemHandler, target instanceof AbstractChestedHorse ? 2 : 0))
               );
            }
         } catch (Throwable var4) {
            WailaExceptionHandler.handleErr(var4, null, null);
         }
      }

      Container container = findContainer(accessor);
      if (container != null) {
         return container instanceof ChestBlockEntity ? new ItemCollector<>(new ItemIterator.ContainerItemIterator(a -> {
            if (a.getTarget() instanceof ChestBlockEntity be) {
               if (be.getBlockState().getBlock() instanceof ChestBlock chestBlock) {
                  Container compound = ChestBlock.getContainer(chestBlock, be.getBlockState(), Objects.requireNonNull(be.getLevel()), be.getBlockPos(), false);
                  if (compound != null) {
                     return compound;
                  }
               }

               return be;
            } else {
               return null;
            }
         }, 0)) : new ItemCollector<>(new ItemIterator.ContainerItemIterator(0));
      } else {
         return ItemCollector.EMPTY;
      }
   }

   @Nullable
   public static List<ViewGroup<ItemStack>> containerGroup(Container container, Accessor<?> accessor) {
      return containerGroup(container, accessor, CommonProxy::findContainer);
   }

   @Nullable
   public static List<ViewGroup<ItemStack>> containerGroup(Container container, Accessor<?> accessor, Function<Accessor<?>, Container> containerFinder) {
      try {
         return ((ItemCollector)ItemStorageProvider.containerCache
               .get(container, () -> new ItemCollector<>(new ItemIterator.ContainerItemIterator(containerFinder, 0))))
            .update(accessor);
      } catch (Exception var4) {
         return null;
      }
   }

   @Nullable
   public static List<ViewGroup<ItemStack>> storageGroup(Object storage, Accessor<?> accessor) {
      return storageGroup(storage, accessor, CommonProxy::findItemHandler);
   }

   @Nullable
   public static List<ViewGroup<ItemStack>> storageGroup(Object storage, Accessor<?> accessor, Function<Accessor<?>, Object> storageFinder) {
      try {
         return ((ItemCollector)ItemStorageProvider.containerCache
               .get(storage, () -> new ItemCollector<>(JadeForgeUtils.fromItemHandler((IItemHandler)storage, 0, storageFinder))))
            .update(accessor);
      } catch (Exception var4) {
         return null;
      }
   }

   @Nullable
   public static IItemHandler findItemHandler(Accessor<?> accessor) {
      if (accessor instanceof BlockAccessor blockAccessor) {
         return (IItemHandler)accessor.getLevel()
            .getCapability(ItemHandler.BLOCK, blockAccessor.getPosition(), blockAccessor.getBlockState(), blockAccessor.getBlockEntity(), null);
      } else {
         return accessor instanceof EntityAccessor entityAccessor ? (IItemHandler)entityAccessor.getEntity().getCapability(ItemHandler.ENTITY) : null;
      }
   }

   @Nullable
   public static Container findContainer(Accessor<?> accessor) {
      Object target = accessor.getTarget();
      if (target == null && accessor instanceof BlockAccessor blockAccessor && blockAccessor.getBlock() instanceof WorldlyContainerHolder holder) {
         return holder.getContainer(blockAccessor.getBlockState(), accessor.getLevel(), blockAccessor.getPosition());
      } else {
         return target instanceof Container container ? container : null;
      }
   }

   @Nullable
   public static List<ViewGroup<CompoundTag>> wrapFluidStorage(Accessor<?> accessor) {
      IFluidHandler fluidHandler = getDefaultStorage(accessor, FluidHandler.BLOCK, FluidHandler.ENTITY);
      return fluidHandler != null ? JadeForgeUtils.fromFluidHandler(fluidHandler, accessor.nbtOps()) : null;
   }

   @Nullable
   public static List<ViewGroup<CompoundTag>> wrapEnergyStorage(Accessor<?> accessor) {
      IEnergyStorage energyStorage = getDefaultStorage(accessor, EnergyStorage.BLOCK, EnergyStorage.ENTITY);
      if (energyStorage != null) {
         ViewGroup<CompoundTag> group = new ViewGroup<>(List.of(EnergyView.of(energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored())));
         group.getExtraData().putString("Unit", "FE");
         return List.of(group);
      } else {
         return null;
      }
   }

   public static boolean isDevEnv() {
      return !FMLEnvironment.production;
   }

   public static float getEnchantPowerBonus(BlockState state, Level world, BlockPos pos) {
      return WailaClientRegistration.instance().customEnchantPowers.containsKey(state.getBlock())
         ? WailaClientRegistration.instance().customEnchantPowers.get(state.getBlock()).getEnchantPowerBonus(state, world, pos)
         : state.getEnchantPowerBonus(world, pos);
   }

   public static ResourceLocation getId(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block);
   }

   public static ResourceLocation getId(EntityType<?> entityType) {
      return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
   }

   public static ResourceLocation getId(BlockEntityType<?> blockEntityType) {
      return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
   }

   public static String getPlatformIdentifier() {
      return "neoforge";
   }

   public static MutableComponent getProfessionName(VillagerProfession profession) {
      ResourceLocation profName = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
      return Component.translatable(
         EntityType.VILLAGER.getDescriptionId()
            + "."
            + (!"minecraft".equals(profName.getNamespace()) ? profName.getNamespace() + "." : "")
            + profName.getPath()
      );
   }

   public static boolean isBoss(Entity entity) {
      EntityType<?> entityType = entity.getType();
      return entityType.is(EntityTypes.BOSSES) || entityType == EntityType.ENDER_DRAGON || entityType == EntityType.WITHER;
   }

   public static boolean isModLoaded(String modid) {
      try {
         return ModList.get().isLoaded(modid);
      } catch (Throwable var2) {
         return false;
      }
   }

   public static ItemStack getBlockPickedResult(BlockState state, Player player, BlockHitResult hitResult) {
      return state.getCloneItemStack(hitResult, player.level(), hitResult.getBlockPos(), player);
   }

   public static ItemStack getEntityPickedResult(Entity entity, Player player, EntityHitResult hitResult) {
      return (ItemStack)MoreObjects.firstNonNull(entity.getPickedResult(hitResult), ItemStack.EMPTY);
   }

   public static Component getFluidName(JadeFluidObject fluid) {
      return toFluidStack(fluid).getHoverName();
   }

   public static FluidStack toFluidStack(JadeFluidObject fluid) {
      int id = BuiltInRegistries.FLUID.getId(fluid.getType());
      Optional<Reference<Fluid>> holder = BuiltInRegistries.FLUID.getHolder(id);
      if (holder.isEmpty()) {
         return FluidStack.EMPTY;
      } else {
         long amount = fluid.getAmount();
         if (amount > 2147483647L) {
            amount = 2147483647L;
         }

         return new FluidStack((Holder)holder.get(), (int)amount, fluid.getComponents());
      }
   }

   private void loadComplete(FMLLoadCompleteEvent event) {
      for (String className : ModList.get().getAllScanData().stream().flatMap($ -> $.getAnnotations().stream()).filter($ -> {
         if (!$.annotationType().getClassName().equals(WailaPlugin.class.getName())) {
            return false;
         } else {
            String required = $.annotationData().getOrDefault("value", "");
            return required.isEmpty() || ModList.get().isLoaded(required);
         }
      }).map(AnnotationData::memberName).toList()) {
         Jade.LOGGER.info("Start loading plugin from %s".formatted(className));

         try {
            Class<?> clazz = Class.forName(className);
            if (IWailaPlugin.class.isAssignableFrom(clazz)) {
               IWailaPlugin plugin = (IWailaPlugin)clazz.getDeclaredConstructor().newInstance();
               Stopwatch stopwatch = null;
               if (isDevEnv()) {
                  stopwatch = Stopwatch.createStarted();
               }

               WailaCommonRegistration common = WailaCommonRegistration.instance();
               common.startSession();
               plugin.register(common);
               if (isPhysicallyClient()) {
                  WailaClientRegistration client = WailaClientRegistration.instance();
                  client.startSession();
                  plugin.registerClient(client);
                  if (stopwatch != null) {
                     Jade.LOGGER.info("Bootstrapped plugin from %s in %s".formatted(className, stopwatch));
                  }

                  client.endSession();
               }

               common.endSession();
               if (stopwatch != null) {
                  Jade.LOGGER.info("Loaded plugin from %s in %s".formatted(className, stopwatch.stop()));
               }
            }
         } catch (Throwable var10) {
            Jade.LOGGER.error("Error loading plugin at %s".formatted(className), var10);
            Throwables.throwIfInstanceOf(var10, IllegalStateException.class);
            if (className.startsWith("snownee.jade.")) {
               ExceptionUtils.wrapAndThrow(var10);
            }
         }
      }

      Jade.loadComplete();
   }

   public static boolean isMultipartEntity(Entity target) {
      return target.isMultipartEntity();
   }

   public static Entity wrapPartEntityParent(Entity target) {
      return target instanceof PartEntity<?> part ? part.getParent() : target;
   }

   public static int getPartEntityIndex(Entity entity) {
      if (entity instanceof PartEntity<?> part) {
         Entity parent = wrapPartEntityParent(entity);
         PartEntity<?>[] parts = parent.getParts();
         return parts == null ? -1 : List.of(parts).indexOf(part);
      } else {
         return -1;
      }
   }

   public static Entity getPartEntity(Entity parent, int index) {
      if (parent == null) {
         return null;
      } else if (index < 0) {
         return parent;
      } else {
         PartEntity<?>[] parts = parent.getParts();
         return (Entity)(parts != null && index < parts.length ? parts[index] : parent);
      }
   }

   public static void registerTagsUpdatedListener(BiConsumer<RegistryAccess, Boolean> listener) {
      NeoForge.EVENT_BUS.addListener(event -> listener.accept(event.getRegistryAccess(), event.getUpdateCause() == UpdateCause.CLIENT_PACKET_RECEIVED));
   }

   @Nullable
   public static <T> Entry<ResourceLocation, List<ViewGroup<T>>> getServerExtensionData(
      Accessor<?> accessor, WrappedHierarchyLookup<IServerExtensionProvider<T>> lookup
   ) {
      for (IServerExtensionProvider<T> provider : lookup.wrappedGet(accessor)) {
         List<ViewGroup<T>> groups;
         try {
            groups = provider.getGroups(accessor);
         } catch (Exception var6) {
            WailaExceptionHandler.handleErr(var6, provider, null);
            continue;
         }

         if (groups != null) {
            return Map.entry(provider.getUid(), groups);
         }
      }

      return null;
   }
}
