package fuzs.puzzleslib.neoforge.impl.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.event.v1.AddBlockEntityTypeBlocksCallback;
import fuzs.puzzleslib.api.event.v1.BuildCreativeModeTabContentsCallback;
import fuzs.puzzleslib.api.event.v1.CommonSetupCallback;
import fuzs.puzzleslib.api.event.v1.ComputeItemAttributeModifiersCallback;
import fuzs.puzzleslib.api.event.v1.FinalizeItemComponentsCallback;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.api.event.v1.entity.ChangeEntitySizeCallback;
import fuzs.puzzleslib.api.event.v1.entity.EnderPearlTeleportCallback;
import fuzs.puzzleslib.api.event.v1.entity.EntityDamageImmunityCallback;
import fuzs.puzzleslib.api.event.v1.entity.EntityRidingEvents;
import fuzs.puzzleslib.api.event.v1.entity.EntityTickEvents;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.RefreshEntityDimensionsCallback;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityEvents;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.AnimalTameCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.BabyEntitySpawnCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.CalculateLivingVisibilityCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.CheckMobDespawnCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.ComputeEnchantedLootBonusCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingAttackCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingBreathEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingChangeTargetCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingConversionCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingDeathCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingDropsCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingEquipmentChangeCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingExperienceDropCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingFallCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingJumpCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingKnockBackCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingVisibilityCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LookingAtEndermanCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.MobEffectEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.PickProjectileCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.ShieldBlockCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.AfterChangeDimensionCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.AnvilEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.ArrowLooseCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.BreakSpeedCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.ContainerEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.GrindstoneEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.ItemEntityEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PickupExperienceCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerCopyEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerNetworkEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTickEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTrackingEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.StopSleepInBedCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.UseBoneMealCallback;
import fuzs.puzzleslib.api.event.v1.level.BlockEvents;
import fuzs.puzzleslib.api.event.v1.level.ExplosionEvents;
import fuzs.puzzleslib.api.event.v1.level.GatherPotentialSpawnsCallback;
import fuzs.puzzleslib.api.event.v1.level.PlayLevelSoundEvents;
import fuzs.puzzleslib.api.event.v1.level.ServerChunkEvents;
import fuzs.puzzleslib.api.event.v1.level.ServerLevelEvents;
import fuzs.puzzleslib.api.event.v1.level.ServerLevelTickEvents;
import fuzs.puzzleslib.api.event.v1.server.AddDataPackReloadListenersCallback;
import fuzs.puzzleslib.api.event.v1.server.LootTableLoadCallback;
import fuzs.puzzleslib.api.event.v1.server.LootTableLoadEvents;
import fuzs.puzzleslib.api.event.v1.server.RegisterCommandsCallback;
import fuzs.puzzleslib.api.event.v1.server.RegisterConfigurationTasksCallback;
import fuzs.puzzleslib.api.event.v1.server.RegisterPotionBrewingMixesCallback;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.api.event.v1.server.ServerTickEvents;
import fuzs.puzzleslib.api.event.v1.server.SyncDataPackContentsCallback;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.event.CopyOnWriteForwardingList;
import fuzs.puzzleslib.impl.event.EventImplHelper;
import fuzs.puzzleslib.impl.event.PotentialSpawnsList;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.api.event.v1.core.NeoForgeEventInvokerRegistry;
import fuzs.puzzleslib.neoforge.api.event.v1.entity.living.ComputeEnchantedLootBonusEvent;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgePotionBrewingBuilder;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentPatch.SplitResult;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.GrindstoneEvent.OnPlaceItem;
import net.neoforged.neoforge.event.GrindstoneEvent.OnTakeItem;
import net.neoforged.neoforge.event.PlayLevelSoundEvent.AtEntity;
import net.neoforged.neoforge.event.PlayLevelSoundEvent.AtPosition;
import net.neoforged.neoforge.event.TagsUpdatedEvent.UpdateCause;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.EntityEvent.Size;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderPearl;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.EnderManAngerEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable.Result;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent.Close;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent.Open;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StopTracking;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.event.level.BlockEvent.FarmlandTrampleEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent.UnWatch;
import net.neoforged.neoforge.event.level.ChunkWatchEvent.Watch;
import net.neoforged.neoforge.event.level.ExplosionEvent.Detonate;
import net.neoforged.neoforge.event.level.LevelEvent.Load;
import net.neoforged.neoforge.event.level.LevelEvent.PotentialSpawns;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.BakeCallback;
import org.jetbrains.annotations.Nullable;

public final class NeoForgeEventInvokerRegistryImpl implements NeoForgeEventInvokerRegistry {
   private static boolean frozenModBusEvents;

   public static void registerLoadingHandlers() {
      INSTANCE.register(CommonSetupCallback.class, FMLCommonSetupEvent.class, (callback, event) -> event.enqueueWork(callback::onCommonSetup));
      INSTANCE.register(LoadCompleteCallback.class, FMLLoadCompleteEvent.class, (callback, evt) -> evt.enqueueWork(callback::onLoadComplete));
      INSTANCE.register(RegistryEntryAddedCallback.class, ModifyRegistriesEvent.class, NeoForgeEventInvokerRegistryImpl::onRegistryEntryAdded);
      INSTANCE.register(
         FinalizeItemComponentsCallback.class,
         ModifyDefaultComponentsEvent.class,
         (callback, evt) -> evt.getAllItems().forEach(item -> callback.onFinalizeItemComponents(item, function -> evt.modify(item, builder -> {
            SplitResult splitResult = ((DataComponentPatch)function.apply(item.components())).split();
            splitResult.added().stream().forEach(builder::set);
            splitResult.removed().forEach(builder::remove);
         })))
      );
      INSTANCE.register(
         ComputeItemAttributeModifiersCallback.class,
         ModifyDefaultComponentsEvent.class,
         (callback, evt) -> evt.getAllItems()
            .forEach(
               item -> {
                  ItemAttributeModifiers itemAttributeModifiers = (ItemAttributeModifiers)item.components()
                     .getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                  CopyOnWriteForwardingList<net.minecraft.world.item.component.ItemAttributeModifiers.Entry> entries = new CopyOnWriteForwardingList<>(
                     itemAttributeModifiers.modifiers()
                  );
                  callback.onComputeItemAttributeModifiers(item, entries);
                  if (entries.delegate() != itemAttributeModifiers.modifiers()) {
                     evt.modify(
                        item,
                        builder -> builder.set(
                           DataComponents.ATTRIBUTE_MODIFIERS,
                           new ItemAttributeModifiers(ImmutableList.copyOf(entries), itemAttributeModifiers.showInTooltip())
                        )
                     );
                  }
               }
            )
      );
      INSTANCE.register(
         AddBlockEntityTypeBlocksCallback.class,
         BlockEntityTypeAddBlocksEvent.class,
         (callback, evt) -> callback.onAddBlockEntityTypeBlocks((x$0, xva$1) -> evt.modify(x$0, new Block[]{xva$1}))
      );
      INSTANCE.register(BuildCreativeModeTabContentsCallback.class, BuildCreativeModeTabContentsEvent.class, (callback, event, context) -> {
         Objects.requireNonNull(context, "context is null");
         ResourceKey<CreativeModeTab> resourceKey = (ResourceKey<CreativeModeTab>)context;
         if (resourceKey == event.getTabKey()) {
            callback.onBuildCreativeModeTabContents(event.getTab(), event.getParameters(), event);
         }
      });
      INSTANCE.register(RegisterConfigurationTasksCallback.class, RegisterConfigurationTasksEvent.class, (callback, event) -> {
         ServerConfigurationPacketListenerImpl listener = (ServerConfigurationPacketListenerImpl)event.getListener();
         callback.onRegisterConfigurationTasks((MinecraftServer)listener.getMainThreadEventLoop(), listener, event::register);
      });
   }

   private static <T> void onRegistryEntryAdded(RegistryEntryAddedCallback<T> callback, ModifyRegistriesEvent evt, @Nullable Object context) {
      Objects.requireNonNull(context, "context is null");
      ResourceKey<? extends Registry<T>> resourceKey = (ResourceKey<? extends Registry<T>>)context;
      Registry<T> registry = evt.getRegistry(resourceKey);
      boolean[] loadComplete = new boolean[1];
      registry.addCallback((AddCallback)(callbackRegistry, id, key, value) -> {
         if (!loadComplete[0]) {
            try {
               callback.onRegistryEntryAdded(callbackRegistry, key.location(), (T)value, onRegistryEntryAdded(registry));
            } catch (Exception var8x) {
               PuzzlesLib.LOGGER.error("Failed to run registry entry added callback", var8x);
            }
         }
      });
      registry.addCallback((BakeCallback)registryx -> loadComplete[0] = true);
      Queue<Consumer<BiConsumer<ResourceLocation, Supplier<T>>>> callbacks = new LinkedList<>();

      for (Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
         callbacks.offer(consumer -> {
            try {
               callback.onRegistryEntryAdded(registry, entry.getKey().location(), entry.getValue(), consumer);
            } catch (Exception var5x) {
               PuzzlesLib.LOGGER.error("Failed to run registry entry added callback", var5x);
            }
         });
      }

      IEventBus eventBus = NeoForgeModContainerHelper.getModEventBus("puzzleslib");
      eventBus.addListener(evtx -> {
         if (evtx.getRegistryKey() == resourceKey) {
            Consumer<BiConsumer<ResourceLocation, Supplier<T>>> consumer;
            while ((consumer = callbacks.poll()) != null) {
               consumer.accept(onRegistryEntryAdded(evtx.getRegistry()));
            }
         }
      });
   }

   private static <T> BiConsumer<ResourceLocation, Supplier<T>> onRegistryEntryAdded(Registry<T> registry) {
      return (resourceLocation, supplier) -> {
         try {
            T t = supplier.get();
            Objects.requireNonNull(t, "entry is null");
            Registry.register(registry, resourceLocation, t);
         } catch (Exception var4) {
            PuzzlesLib.LOGGER.error("Failed to register new entry", var4);
         }
      };
   }

   public static void freezeModBusEvents() {
      frozenModBusEvents = true;
   }

   public static void registerEventHandlers() {
      INSTANCE.register(
         PlayerInteractEvents.UseBlock.class,
         RightClickBlock.class,
         (callback, evt) -> callback.onUseBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getHitVec()).ifInterrupt(interactionResult -> {
            evt.setCancellationResult(interactionResult);
            evt.setCanceled(true);
         })
      );
      INSTANCE.register(PlayerInteractEvents.AttackBlock.class, LeftClickBlock.class, (callback, evt) -> {
         if (callback.onAttackBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getPos(), evt.getFace()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(
         PlayerInteractEvents.UseItem.class,
         RightClickItem.class,
         (callback, evt) -> callback.onUseItem(evt.getEntity(), evt.getLevel(), evt.getHand()).ifInterrupt(interactionResult -> {
            evt.setCancellationResult(interactionResult);
            evt.setCanceled(true);
         })
      );
      INSTANCE.register(
         PlayerInteractEvents.UseEntity.class,
         EntityInteract.class,
         (callback, evt) -> callback.onUseEntity(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget()).ifInterrupt(interactionResult -> {
            evt.setCancellationResult(interactionResult);
            evt.setCanceled(true);
         })
      );
      INSTANCE.register(
         PlayerInteractEvents.UseEntityAt.class,
         EntityInteractSpecific.class,
         (callback, evt) -> callback.onUseEntityAt(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget(), evt.getLocalPos())
            .ifInterrupt(interactionResult -> {
               evt.setCancellationResult(interactionResult);
               evt.setCanceled(true);
            })
      );
      INSTANCE.register(PlayerInteractEvents.AttackEntity.class, AttackEntityEvent.class, (callback, evt) -> {
         if (callback.onAttackEntity(evt.getEntity(), evt.getEntity().level(), InteractionHand.MAIN_HAND, evt.getTarget()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(PickupExperienceCallback.class, PickupXp.class, (callback, evt) -> {
         if (callback.onPickupExperience(evt.getEntity(), evt.getOrb()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(UseBoneMealCallback.class, BonemealEvent.class, (callback, evt) -> {
         EventResult result = callback.onUseBoneMeal(evt.getLevel(), evt.getPos(), evt.getState(), evt.getStack());
         if (result.isInterrupt()) {
            evt.setSuccessful(result.getAsBoolean());
         }
      });
      INSTANCE.register(LivingExperienceDropCallback.class, LivingExperienceDropEvent.class, (callback, evt) -> {
         DefaultedInt droppedExperience = DefaultedInt.fromEvent(evt::setDroppedExperience, evt::getDroppedExperience, evt::getOriginalExperience);
         if (callback.onLivingExperienceDrop(evt.getEntity(), evt.getAttackingPlayer(), droppedExperience).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(BlockEvents.Break.class, BreakEvent.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            if (evt.getPlayer() instanceof ServerPlayer serverPlayer) {
               if (!(evt.getState().getBlock() instanceof GameMasterBlock) || serverPlayer.canUseGameMasterBlocks()) {
                  GameType gameType = serverPlayer.gameMode.getGameModeForPlayer();
                  if (!serverPlayer.blockActionRestricted((Level)evt.getLevel(), evt.getPos(), gameType)) {
                     EventResult result = callback.onBreakBlock(serverLevel, evt.getPos(), evt.getState(), serverPlayer, serverPlayer.getMainHandItem());
                     if (result.isInterrupt()) {
                        evt.setCanceled(true);
                     }
                  }
               }
            }
         }
      });
      INSTANCE.register(BlockEvents.DropExperience.class, BlockDropsEvent.class, (callback, evt) -> {
         if (evt.getBreaker() instanceof ServerPlayer serverPlayer) {
            MutableInt var4 = MutableInt.fromEvent(evt::setDroppedExperience, evt::getDroppedExperience);
            callback.onDropExperience(evt.getLevel(), evt.getPos(), evt.getState(), serverPlayer, evt.getTool(), var4);
         }
      });
      INSTANCE.register(BlockEvents.FarmlandTrample.class, FarmlandTrampleEvent.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            if (callback.onFarmlandTrample(serverLevel, evt.getPos(), evt.getState(), evt.getFallDistance(), evt.getEntity()).isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      });
      INSTANCE.register(PlayerTickEvents.Start.class, Pre.class, (callback, evt) -> callback.onStartPlayerTick(evt.getEntity()));
      INSTANCE.register(PlayerTickEvents.End.class, Post.class, (callback, evt) -> callback.onEndPlayerTick(evt.getEntity()));
      INSTANCE.register(LivingFallCallback.class, LivingFallEvent.class, (callback, evt) -> {
         MutableFloat fallDistance = MutableFloat.fromEvent(evt::setDistance, evt::getDistance);
         MutableFloat damageMultiplier = MutableFloat.fromEvent(evt::setDamageMultiplier, evt::getDamageMultiplier);
         if (callback.onLivingFall(evt.getEntity(), fallDistance, damageMultiplier).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(
         RegisterCommandsCallback.class,
         RegisterCommandsEvent.class,
         (callback, evt) -> callback.onRegisterCommands(evt.getDispatcher(), evt.getBuildContext(), evt.getCommandSelection())
      );
      INSTANCE.register(
         LootTableLoadCallback.class,
         LootTableLoadEvent.class,
         (callback, event) -> callback.onLootTableLoad(event.getName(), new ForwardingLootTableBuilder(event.getTable()), event.getRegistries())
      );
      INSTANCE.register(LootTableLoadEvents.Replace.class, LootTableLoadEvent.class, (callback, evt) -> {
         MutableValue<LootTable> table = MutableValue.fromEvent(evt::setTable, evt::getTable);
         callback.onReplaceLootTable(evt.getName(), table);
      });
      INSTANCE.register(
         LootTableLoadEvents.Modify.class,
         LootTableLoadEvent.class,
         (callback, evt) -> callback.onModifyLootTable(
            evt.getName(),
            evt.getTable()::addPool,
            index -> index == 0 && evt.getTable().removePool("main") != null ? true : evt.getTable().removePool("pool" + index) != null
         )
      );
      INSTANCE.register(AnvilEvents.Use.class, AnvilRepairEvent.class, (callback, evt) -> {
         if (!evt.getEntity().level().isClientSide) {
            MutableFloat breakChance = MutableFloat.fromEvent(evt::setBreakChance, evt::getBreakChance);
            callback.onAnvilUse(evt.getEntity(), evt.getLeft(), evt.getRight(), evt.getOutput(), breakChance);
         }
      });
      INSTANCE.register(ItemEntityEvents.Touch.class, net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre.class, (callback, evt) -> {
         EventResult result = callback.onItemTouch(evt.getPlayer(), evt.getItemEntity());
         if (result.isInterrupt()) {
            evt.setCanPickup(result.getAsBoolean() ? TriState.TRUE : TriState.FALSE);
         }
      });
      INSTANCE.register(
         ItemEntityEvents.Pickup.class,
         net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Post.class,
         (callback, evt) -> callback.onItemPickup(evt.getPlayer(), evt.getItemEntity(), evt.getOriginalStack())
      );
      INSTANCE.register(ComputeEnchantedLootBonusCallback.class, ComputeEnchantedLootBonusEvent.class, (callback, evt) -> {
         MutableInt enchantmentLevel = MutableInt.fromEvent(evt::setEnchantmentLevel, evt::getEnchantmentLevel);
         callback.onComputeEnchantedLootBonus(evt.getEntity(), evt.getDamageSource(), evt.getEnchantment(), enchantmentLevel);
      });
      INSTANCE.register(AnvilEvents.Update.class, AnvilUpdateEvent.class, (callback, evt) -> {
         DefaultedValue<ItemStack> output = DefaultedValue.fromEventWithValue(evt::setOutput, evt::getOutput, evt.getOutput());
         DefaultedInt enchantmentCost = DefaultedInt.fromEventWithValue(evt::setCost, () -> (int)evt.getCost(), (int)evt.getCost());
         DefaultedInt materialCost = DefaultedInt.fromEventWithValue(evt::setMaterialCost, evt::getMaterialCost, evt.getMaterialCost());
         EventResult result = callback.onAnvilUpdate(evt.getLeft(), evt.getRight(), output, evt.getName(), enchantmentCost, materialCost, evt.getPlayer());
         if (result.isInterrupt()) {
            if (!result.getAsBoolean()) {
               evt.setCanceled(true);
            }
         } else {
            evt.setOutput(output.getAsDefault());
            evt.setCost(enchantmentCost.getAsDefaultInt());
            evt.setMaterialCost(materialCost.getAsDefaultInt());
         }
      });
      INSTANCE.register(LivingDropsCallback.class, LivingDropsEvent.class, (callback, evt) -> {
         if (callback.onLivingDrops(evt.getEntity(), evt.getSource(), evt.getDrops(), evt.isRecentlyHit()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(EntityTickEvents.Start.class, net.neoforged.neoforge.event.tick.EntityTickEvent.Pre.class, (callback, evt) -> {
         if (callback.onStartEntityTick(evt.getEntity()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(
         EntityTickEvents.End.class, net.neoforged.neoforge.event.tick.EntityTickEvent.Post.class, (callback, evt) -> callback.onEndEntityTick(evt.getEntity())
      );
      INSTANCE.register(ArrowLooseCallback.class, ArrowLooseEvent.class, (callback, evt) -> {
         MutableInt charge = MutableInt.fromEvent(evt::setCharge, evt::getCharge);
         if (callback.onArrowLoose(evt.getEntity(), evt.getBow(), evt.getLevel(), charge, evt.hasAmmo()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(LivingHurtCallback.class, net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre.class, (callback, evt) -> {
         MutableFloat damageAmount = MutableFloat.fromEvent(evt.getContainer()::setNewDamage, evt.getContainer()::getNewDamage);
         if (callback.onLivingHurt(evt.getEntity(), evt.getContainer().getSource(), damageAmount).isInterrupt()) {
            evt.getContainer().setNewDamage(0.0F);
         }
      });
      INSTANCE.register(UseItemEvents.Start.class, Start.class, (callback, evt) -> {
         MutableInt useDuration = MutableInt.fromEvent(evt::setDuration, evt::getDuration);
         if (callback.onUseItemStart(evt.getEntity(), evt.getItem(), useDuration).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(UseItemEvents.Tick.class, Tick.class, (callback, evt) -> {
         MutableInt useItemRemaining = MutableInt.fromEvent(evt::setDuration, evt::getDuration);
         if (callback.onUseItemTick(evt.getEntity(), evt.getItem(), useItemRemaining).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(UseItemEvents.Stop.class, Stop.class, (callback, evt) -> {
         if (callback.onUseItemStop(evt.getEntity(), evt.getItem(), evt.getDuration()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(UseItemEvents.Finish.class, Finish.class, (callback, evt) -> {
         MutableValue<ItemStack> stack = MutableValue.fromEvent(evt::setResultStack, evt::getResultStack);
         callback.onUseItemFinish(evt.getEntity(), stack, evt.getDuration(), evt.getItem());
      });
      INSTANCE.register(ShieldBlockCallback.class, LivingShieldBlockEvent.class, (callback, evt) -> {
         if (evt.getBlocked()) {
            DefaultedFloat blockedDamage = DefaultedFloat.fromEvent(evt::setBlockedDamage, evt::getBlockedDamage, evt::getOriginalBlockedDamage);
            if (callback.onShieldBlock(evt.getEntity(), evt.getDamageSource(), blockedDamage).isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      });
      INSTANCE.register(
         TagsUpdatedCallback.class,
         TagsUpdatedEvent.class,
         (callback, evt) -> callback.onTagsUpdated(evt.getRegistryAccess(), evt.getUpdateCause() == UpdateCause.CLIENT_PACKET_RECEIVED)
      );
      INSTANCE.register(ExplosionEvents.Start.class, net.neoforged.neoforge.event.level.ExplosionEvent.Start.class, (callback, evt) -> {
         if (callback.onExplosionStart(evt.getLevel(), evt.getExplosion()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(
         ExplosionEvents.Detonate.class,
         Detonate.class,
         (callback, evt) -> callback.onExplosionDetonate(evt.getLevel(), evt.getExplosion(), evt.getAffectedBlocks(), evt.getAffectedEntities())
      );
      INSTANCE.register(
         SyncDataPackContentsCallback.class,
         OnDatapackSyncEvent.class,
         (callback, evt) -> evt.getRelevantPlayers().forEach(player -> callback.onSyncDataPackContents(player, evt.getPlayer() != null))
      );
      INSTANCE.register(ServerLifecycleEvents.Starting.class, ServerAboutToStartEvent.class, (callback, evt) -> callback.onServerStarting(evt.getServer()));
      INSTANCE.register(ServerLifecycleEvents.Started.class, ServerStartedEvent.class, (callback, evt) -> callback.onServerStarted(evt.getServer()));
      INSTANCE.register(ServerLifecycleEvents.Stopping.class, ServerStoppingEvent.class, (callback, evt) -> callback.onServerStopping(evt.getServer()));
      INSTANCE.register(ServerLifecycleEvents.Stopped.class, ServerStoppedEvent.class, (callback, evt) -> callback.onServerStopped(evt.getServer()));
      INSTANCE.register(PlayLevelSoundEvents.AtPosition.class, AtPosition.class, (callback, evt) -> {
         MutableValue<Holder<SoundEvent>> sound = MutableValue.fromEvent(evt::setSound, evt::getSound);
         MutableValue<SoundSource> source = MutableValue.fromEvent(evt::setSource, evt::getSource);
         DefaultedFloat volume = DefaultedFloat.fromEvent(evt::setNewVolume, evt::getNewVolume, evt::getOriginalVolume);
         DefaultedFloat pitch = DefaultedFloat.fromEvent(evt::setNewPitch, evt::getNewPitch, evt::getOriginalPitch);
         if (callback.onPlaySoundAtPosition(evt.getLevel(), evt.getPosition(), sound, source, volume, pitch).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(PlayLevelSoundEvents.AtEntity.class, AtEntity.class, (callback, evt) -> {
         MutableValue<Holder<SoundEvent>> sound = MutableValue.fromEvent(evt::setSound, evt::getSound);
         MutableValue<SoundSource> source = MutableValue.fromEvent(evt::setSource, evt::getSource);
         DefaultedFloat volume = DefaultedFloat.fromEvent(evt::setNewVolume, evt::getNewVolume, evt::getOriginalVolume);
         DefaultedFloat pitch = DefaultedFloat.fromEvent(evt::setNewPitch, evt::getNewPitch, evt::getOriginalPitch);
         if (callback.onPlaySoundAtEntity(evt.getLevel(), evt.getEntity(), sound, source, volume, pitch).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(ServerEntityEvents.Join.class, EntityJoinLevelEvent.class, (callback, event) -> {
         if (event.getLevel() instanceof ServerLevel serverLevel) {
            MobSpawnType spawnType = event.getEntity() instanceof Mob mob ? mob.getSpawnType() : null;
            if (callback.onEntityJoin(event.getEntity(), serverLevel, event.loadedFromDisk(), spawnType).isInterrupt()) {
               event.setCanceled(true);
            }
         }
      });
      INSTANCE.register(ServerEntityEvents.Load.class, EntityJoinLevelEvent.class, (callback, event) -> {
         if (event.getLevel() instanceof ServerLevel serverLevel) {
            boolean var7 = event.getEntity().isRemoved();
            MobSpawnType spawnType = event.getEntity() instanceof Mob mob ? mob.getSpawnType() : null;
            callback.onEntityLoad(event.getEntity(), serverLevel, event.loadedFromDisk(), spawnType);
            if (!var7 && event.getEntity().isRemoved()) {
               event.setCanceled(true);
            }
         }
      });
      INSTANCE.register(ServerEntityEvents.Unload.class, EntityLeaveLevelEvent.class, (callback, event) -> {
         if (event.getLevel() instanceof ServerLevel serverLevel) {
            callback.onEntityUnload(event.getEntity(), serverLevel);
         }
      });
      INSTANCE.register(ServerEntityLevelEvents.Load.class, EntityJoinLevelEvent.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            if (callback.onEntityLoad(evt.getEntity(), serverLevel).isInterrupt()) {
               if (evt.getEntity() instanceof Player) {
                  throw new UnsupportedOperationException("Cannot prevent player from loading in!");
               }

               evt.setCanceled(true);
            }
         }
      });
      INSTANCE.register(ServerEntityLevelEvents.Spawn.class, EntityJoinLevelEvent.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel && !evt.loadedFromDisk()) {
            Entity patt1$temp;
            if (callback.onEntitySpawn(patt1$temp = evt.getEntity(), serverLevel, patt1$temp instanceof Mob mob ? mob.getSpawnType() : null).isInterrupt()) {
               if (evt.getEntity() instanceof Player) {
                  throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
               }

               evt.setCanceled(true);
            }
         }
      });
      INSTANCE.register(ServerEntityLevelEvents.Unload.class, EntityLeaveLevelEvent.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            callback.onEntityUnload(evt.getEntity(), serverLevel);
         }
      });
      INSTANCE.register(LivingDeathCallback.class, LivingDeathEvent.class, (callback, evt) -> {
         if (callback.onLivingDeath(evt.getEntity(), evt.getSource()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(PlayerTrackingEvents.Start.class, StartTracking.class, (callback, evt) -> {
         if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            callback.onStartTracking(evt.getTarget(), serverPlayer);
         }
      });
      INSTANCE.register(PlayerTrackingEvents.Stop.class, StopTracking.class, (callback, evt) -> {
         if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            callback.onStopTracking(evt.getTarget(), serverPlayer);
         }
      });
      INSTANCE.register(PlayerNetworkEvents.LoggedIn.class, PlayerLoggedInEvent.class, (callback, evt) -> {
         if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            callback.onLoggedIn(serverPlayer);
         }
      });
      INSTANCE.register(PlayerNetworkEvents.LoggedOut.class, PlayerLoggedOutEvent.class, (callback, evt) -> {
         if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            callback.onLoggedOut(serverPlayer);
         }
      });
      INSTANCE.register(AfterChangeDimensionCallback.class, PlayerChangedDimensionEvent.class, (callback, evt) -> {
         if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerLevel var5 = serverPlayer.server.getLevel(evt.getFrom());
            ServerLevel newLevel = serverPlayer.server.getLevel(evt.getTo());
            Objects.requireNonNull(var5, "original level is null");
            Objects.requireNonNull(newLevel, "new level is null");
            callback.onAfterChangeDimension(serverPlayer, var5, newLevel);
         }
      });
      INSTANCE.register(BabyEntitySpawnCallback.class, BabyEntitySpawnEvent.class, (callback, evt) -> {
         MutableValue<AgeableMob> child = MutableValue.fromEvent(evt::setChild, evt::getChild);
         if (callback.onBabyEntitySpawn(evt.getParentA(), evt.getParentB(), child).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(AnimalTameCallback.class, AnimalTameEvent.class, (callback, evt) -> {
         if (callback.onAnimalTame(evt.getAnimal(), evt.getTamer()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(LivingAttackCallback.class, LivingIncomingDamageEvent.class, (callback, evt) -> {
         if (callback.onLivingAttack(evt.getEntity(), evt.getSource(), evt.getAmount()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(PlayerCopyEvents.Copy.class, Clone.class, (callback, evt) -> {
         if (evt.getOriginal() instanceof ServerPlayer originalServerPlayer) {
            if (evt.getEntity() instanceof ServerPlayer newServerPlayer) {
               callback.onCopy(originalServerPlayer, newServerPlayer, !evt.isWasDeath());
            }
         }
      });
      INSTANCE.register(PlayerCopyEvents.Respawn.class, PlayerRespawnEvent.class, (callback, evt) -> {
         if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            callback.onRespawn(serverPlayer, evt.isEndConquered());
         }
      });
      INSTANCE.register(
         ServerTickEvents.Start.class,
         net.neoforged.neoforge.event.tick.ServerTickEvent.Pre.class,
         (callback, evt) -> callback.onStartServerTick(evt.getServer())
      );
      INSTANCE.register(
         ServerTickEvents.End.class, net.neoforged.neoforge.event.tick.ServerTickEvent.Post.class, (callback, evt) -> callback.onEndServerTick(evt.getServer())
      );
      INSTANCE.register(ServerLevelTickEvents.Start.class, net.neoforged.neoforge.event.tick.LevelTickEvent.Pre.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            callback.onStartLevelTick(serverLevel.getServer(), serverLevel);
         }
      });
      INSTANCE.register(ServerLevelTickEvents.End.class, net.neoforged.neoforge.event.tick.LevelTickEvent.Post.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            callback.onEndLevelTick(serverLevel.getServer(), serverLevel);
         }
      });
      INSTANCE.register(ServerLevelEvents.Load.class, Load.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            callback.onLevelLoad(serverLevel.getServer(), serverLevel);
         }
      });
      INSTANCE.register(ServerLevelEvents.Unload.class, Unload.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            callback.onLevelUnload(serverLevel.getServer(), serverLevel);
         }
      });
      INSTANCE.register(ServerChunkEvents.Load.class, net.neoforged.neoforge.event.level.ChunkEvent.Load.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            if (evt.getChunk() instanceof LevelChunk levelChunk) {
               callback.onChunkLoad(serverLevel, levelChunk);
            }
         }
      });
      INSTANCE.register(ServerChunkEvents.Unload.class, net.neoforged.neoforge.event.level.ChunkEvent.Unload.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ServerLevel serverLevel) {
            if (evt.getChunk() instanceof LevelChunk levelChunk) {
               callback.onChunkUnload(serverLevel, levelChunk);
            }
         }
      });
      INSTANCE.register(ItemEntityEvents.Toss.class, ItemTossEvent.class, (callback, evt) -> {
         if (callback.onItemToss(evt.getPlayer(), evt.getEntity()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(LivingKnockBackCallback.class, LivingKnockBackEvent.class, (callback, evt) -> {
         DefaultedDouble strength = DefaultedDouble.fromEvent(v -> evt.setStrength((float)v), evt::getStrength, evt::getOriginalStrength);
         DefaultedDouble ratioX = DefaultedDouble.fromEvent(evt::setRatioX, evt::getRatioX, evt::getOriginalRatioX);
         DefaultedDouble ratioZ = DefaultedDouble.fromEvent(evt::setRatioZ, evt::getRatioZ, evt::getOriginalRatioZ);
         if (callback.onLivingKnockBack(evt.getEntity(), strength, ratioX, ratioZ).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(ProjectileImpactCallback.class, ProjectileImpactEvent.class, (callback, evt) -> {
         if (callback.onProjectileImpact(evt.getProjectile(), evt.getRayTraceResult()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(BreakSpeedCallback.class, BreakSpeed.class, (callback, evt) -> {
         DefaultedFloat breakSpeed = DefaultedFloat.fromEvent(evt::setNewSpeed, evt::getNewSpeed, evt::getOriginalSpeed);
         if (callback.onBreakSpeed(evt.getEntity(), evt.getState(), breakSpeed).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(MobEffectEvents.Affects.class, Applicable.class, (callback, evt) -> {
         EventResult result = callback.onMobEffectAffects(evt.getEntity(), evt.getEffectInstance());
         if (result.isInterrupt()) {
            evt.setResult(result.getAsBoolean() ? Result.APPLY : Result.DO_NOT_APPLY);
         }
      });
      INSTANCE.register(
         MobEffectEvents.Apply.class,
         Added.class,
         (callback, evt) -> callback.onMobEffectApply(evt.getEntity(), evt.getEffectInstance(), evt.getOldEffectInstance(), evt.getEffectSource())
      );
      INSTANCE.register(MobEffectEvents.Remove.class, Remove.class, (callback, evt) -> {
         if (callback.onMobEffectRemove(evt.getEntity(), evt.getEffectInstance()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(MobEffectEvents.Expire.class, Expired.class, (callback, evt) -> callback.onMobEffectExpire(evt.getEntity(), evt.getEffectInstance()));
      INSTANCE.register(LivingJumpCallback.class, LivingJumpEvent.class, (callback, evt) -> EventImplHelper.onLivingJump(callback, evt.getEntity()));
      INSTANCE.register(
         CalculateLivingVisibilityCallback.class,
         LivingVisibilityEvent.class,
         (callback, evt) -> callback.onCalculateLivingVisibility(
            evt.getEntity(),
            evt.getLookingEntity(),
            MutableDouble.fromEvent(visibilityModifier -> evt.modifyVisibility(visibilityModifier / evt.getVisibilityModifier()), evt::getVisibilityModifier)
         )
      );
      INSTANCE.register(
         LivingVisibilityCallback.class,
         LivingVisibilityEvent.class,
         (callback, evt) -> callback.onLivingVisibility(
            evt.getEntity(),
            evt.getLookingEntity(),
            MutableDouble.fromEvent(visibilityModifier -> evt.modifyVisibility(visibilityModifier / evt.getVisibilityModifier()), evt::getVisibilityModifier)
         )
      );
      INSTANCE.register(
         LivingChangeTargetCallback.class,
         LivingChangeTargetEvent.class,
         (callback, evt) -> {
            DefaultedValue<LivingEntity> target = DefaultedValue.fromEvent(
               evt::setNewAboutToBeSetTarget, evt::getNewAboutToBeSetTarget, evt::getOriginalAboutToBeSetTarget
            );
            if (callback.onLivingChangeTarget(evt.getEntity(), target).isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      );
      INSTANCE.register(
         CheckMobDespawnCallback.class,
         MobDespawnEvent.class,
         (callback, evt) -> {
            if (evt.getLevel() instanceof ServerLevel serverLevel) {
               EventResult result = callback.onCheckMobDespawn(evt.getEntity(), serverLevel);
               if (result.isInterrupt()) {
                  evt.setResult(
                     result.getAsBoolean()
                        ? net.neoforged.neoforge.event.entity.living.MobDespawnEvent.Result.ALLOW
                        : net.neoforged.neoforge.event.entity.living.MobDespawnEvent.Result.DENY
                  );
               }
            }
         }
      );
      INSTANCE.register(
         GatherPotentialSpawnsCallback.class,
         PotentialSpawns.class,
         (callback, evt) -> {
            if (evt.getLevel() instanceof ServerLevel serverLevel) {
               PotentialSpawnsList var4 = new PotentialSpawnsList<>(evt::getSpawnerDataList, spawnerData -> {
                  evt.addSpawnerData(spawnerData);
                  return true;
               }, evt::removeSpawnerData);
               callback.onGatherPotentialSpawns(
                  serverLevel, serverLevel.structureManager(), serverLevel.getChunkSource().getGenerator(), evt.getMobCategory(), evt.getPos(), var4
               );
            }
         }
      );
      INSTANCE.register(EntityRidingEvents.Start.class, EntityMountEvent.class, (callback, evt) -> {
         if (!evt.isDismounting()) {
            if (evt.getEntityMounting().canRide(evt.getEntityBeingMounted())) {
               if (evt.getEntityBeingMounted().canAddPassenger(evt.getEntityMounting())) {
                  if (callback.onStartRiding(evt.getLevel(), evt.getEntityMounting(), evt.getEntityBeingMounted()).isInterrupt()) {
                     evt.setCanceled(true);
                  }
               }
            }
         }
      });
      INSTANCE.register(EntityRidingEvents.Stop.class, EntityMountEvent.class, (callback, evt) -> {
         if (!evt.isMounting()) {
            if (callback.onStopRiding(evt.getLevel(), evt.getEntity(), evt.getEntityBeingMounted()).isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      });
      INSTANCE.register(GrindstoneEvents.Update.class, OnPlaceItem.class, (callback, evt) -> {
         ItemStack originalOutput = evt.getOutput();
         int originalExperienceReward = evt.getXp();
         MutableValue<ItemStack> output = MutableValue.fromEvent(evt::setOutput, evt::getOutput);
         MutableInt experienceReward = MutableInt.fromEvent(evt::setXp, evt::getXp);
         Player player = EventImplHelper.getGrindstoneUsingPlayer(evt.getTopItem(), evt.getBottomItem()).orElseThrow(NullPointerException::new);
         EventResult result = callback.onGrindstoneUpdate(evt.getTopItem(), evt.getBottomItem(), output, experienceReward, player);
         if (result.isInterrupt()) {
            if (!result.getAsBoolean()) {
               evt.setCanceled(true);
            }
         } else {
            evt.setOutput(originalOutput);
            evt.setXp(originalExperienceReward);
         }
      });
      INSTANCE.register(GrindstoneEvents.Use.class, OnTakeItem.class, (callback, evt) -> {
         DefaultedValue<ItemStack> topInput = DefaultedValue.fromValue(evt.getTopItem());
         if (!evt.getNewTopItem().isEmpty()) {
            topInput.accept(evt.getNewTopItem());
         }

         DefaultedValue<ItemStack> bottomInput = DefaultedValue.fromValue(evt.getBottomItem());
         if (!evt.getNewBottomItem().isEmpty()) {
            bottomInput.accept(evt.getNewBottomItem());
         }

         Player player = EventImplHelper.getGrindstoneUsingPlayer(evt.getTopItem(), evt.getBottomItem()).orElseThrow(NullPointerException::new);
         callback.onGrindstoneUse(topInput, bottomInput, player);
         topInput.getAsOptional().ifPresent(evt::setNewTopItem);
         bottomInput.getAsOptional().ifPresent(evt::setNewBottomItem);
      });
      INSTANCE.register(LivingBreathEvents.Breathe.class, (callback, context) -> {});
      INSTANCE.register(LivingBreathEvents.Drown.class, (callback, context) -> {});
      INSTANCE.register(ServerChunkEvents.Watch.class, Watch.class, (callback, evt) -> callback.onChunkWatch(evt.getPlayer(), evt.getChunk(), evt.getLevel()));
      INSTANCE.register(
         ServerChunkEvents.Unwatch.class, UnWatch.class, (callback, evt) -> callback.onChunkUnwatch(evt.getPlayer(), evt.getPos(), evt.getLevel())
      );
      INSTANCE.register(
         LivingEquipmentChangeCallback.class,
         LivingEquipmentChangeEvent.class,
         (callback, evt) -> callback.onLivingEquipmentChange(evt.getEntity(), evt.getSlot(), evt.getFrom(), evt.getTo())
      );
      INSTANCE.register(
         LivingConversionCallback.class,
         net.neoforged.neoforge.event.entity.living.LivingConversionEvent.Post.class,
         (callback, evt) -> callback.onLivingConversion(evt.getEntity(), evt.getOutcome())
      );
      INSTANCE.register(ContainerEvents.Open.class, Open.class, (callback, evt) -> {
         if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            callback.onContainerOpen(serverPlayer, evt.getContainer());
         }
      });
      INSTANCE.register(ContainerEvents.Close.class, Close.class, (callback, evt) -> {
         if (evt.getEntity() instanceof ServerPlayer serverPlayer) {
            callback.onContainerClose(serverPlayer, evt.getContainer());
         }
      });
      INSTANCE.register(LookingAtEndermanCallback.class, EnderManAngerEvent.class, (callback, evt) -> {
         if (callback.onLookingAtEnderManCallback(evt.getEntity(), evt.getPlayer()).isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      INSTANCE.register(
         RegisterPotionBrewingMixesCallback.class,
         RegisterBrewingRecipesEvent.class,
         (callback, evt) -> callback.onRegisterPotionBrewingMixes(new NeoForgePotionBrewingBuilder(evt.getBuilder()))
      );
      INSTANCE.register(
         AddDataPackReloadListenersCallback.class,
         AddReloadListenerEvent.class,
         (callback, evt) -> callback.onAddDataPackReloadListeners((resourceLocation, factory) -> {
            Provider registryLookup = evt.getServerResources().getRegistryLookup();
            evt.addListener(ForwardingReloadListenerHelper.fromReloadListener(resourceLocation, factory.apply(registryLookup, evt.getRegistryAccess())));
         })
      );
      INSTANCE.register(ChangeEntitySizeCallback.class, Size.class, (callback, evt) -> {
         EventResultHolder<EntityDimensions> result = callback.onChangeEntitySize(evt.getEntity(), evt.getPose(), evt.getOldSize());
         result.ifInterrupt(evt::setNewSize);
      });
      INSTANCE.register(RefreshEntityDimensionsCallback.class, Size.class, (callback, event) -> {
         EventResultHolder<EntityDimensions> eventResult = callback.onRefreshEntityDimensions(event.getEntity(), event.getPose(), event.getOldSize());
         eventResult.ifInterrupt(event::setNewSize);
      });
      INSTANCE.register(PickProjectileCallback.class, LivingGetProjectileEvent.class, (callback, event) -> {
         MutableValue<ItemStack> ammoItemStack = MutableValue.fromEvent(event::setProjectileItemStack, event::getProjectileItemStack);
         callback.onPickProjectile(event.getEntity(), event.getProjectileWeaponItemStack(), ammoItemStack);
      });
      INSTANCE.register(
         EnderPearlTeleportCallback.class,
         EnderPearl.class,
         (callback, event) -> {
            EventResult eventResult = callback.onEnderPearlTeleport(
               event.getPlayer(),
               event.getTarget(),
               event.getPearlEntity(),
               MutableFloat.fromEvent(event::setAttackDamage, event::getAttackDamage),
               event.getHitResult()
            );
            if (eventResult.isInterrupt()) {
               event.setCanceled(true);
            }
         }
      );
      INSTANCE.register(StopSleepInBedCallback.class, PlayerWakeUpEvent.class, (callback, event) -> {
         if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            callback.onStopSleepInBed(serverPlayer, !event.wakeImmediately() && !event.updateLevel());
         }
      });
      INSTANCE.register(EntityDamageImmunityCallback.class, EntityInvulnerabilityCheckEvent.class, (callback, event) -> {
         MutableBoolean isInvulnerable = MutableBoolean.fromEvent(event::setInvulnerable, event::isInvulnerable);
         callback.onEntityDamageImmunity(event.getEntity(), event.getSource(), isInvulnerable);
      });
   }

   @Override
   public <T, E extends Event> void register(
      Class<T> clazz, Class<E> event, NeoForgeEventInvokerRegistry.NeoForgeEventContextConsumer<T, E> converter, boolean joinInvokers
   ) {
      Objects.requireNonNull(clazz, "type is null");
      Objects.requireNonNull(event, "event type is null");
      Objects.requireNonNull(converter, "converter is null");
      Preconditions.checkArgument(!Modifier.isAbstract(event.getModifiers()), event + " is abstract");
      IEventBus eventBus;
      if (IModBusEvent.class.isAssignableFrom(event)) {
         Preconditions.checkState(!frozenModBusEvents, "Mod bus events already frozen");
         eventBus = NeoForgeModContainerHelper.getOptionalActiveModEventBus().orElse(null);
      } else {
         eventBus = NeoForge.EVENT_BUS;
      }

      EventInvokerImpl.register(clazz, new NeoForgeEventInvokerRegistryImpl.ForgeEventInvoker<>(eventBus, event, converter), joinInvokers);
   }

   private record ForgeEventInvoker<T, E extends Event>(
      @Nullable IEventBus eventBus, Class<E> event, NeoForgeEventInvokerRegistry.NeoForgeEventContextConsumer<T, E> converter
   ) implements EventInvoker<T>, EventInvokerImpl.EventInvokerLike<T> {
      private static final Map<EventPhase, EventPriority> PHASE_TO_PRIORITY = Map.of(
         EventPhase.FIRST,
         EventPriority.HIGHEST,
         EventPhase.BEFORE,
         EventPriority.HIGH,
         EventPhase.DEFAULT,
         EventPriority.NORMAL,
         EventPhase.AFTER,
         EventPriority.LOW,
         EventPhase.LAST,
         EventPriority.LOWEST
      );

      @Override
      public EventInvoker<T> asEventInvoker(@Nullable Object context) {
         return (EventInvoker<T>)(context != null ? (phase, callback) -> this.register(phase, callback, context) : this);
      }

      @Override
      public void register(EventPhase phase, T callback) {
         this.register(phase, callback, null);
      }

      private void register(EventPhase phase, T callback, @Nullable Object context) {
         Objects.requireNonNull(phase, "phase is null");
         Objects.requireNonNull(callback, "callback is null");
         IEventBus eventBus = this.getEventBus(context);
         EventPriority eventPriority = PHASE_TO_PRIORITY.getOrDefault(phase, EventPriority.NORMAL);
         Object eventContext = this.eventBus != eventBus ? null : context;
         eventBus.addListener(eventPriority, false, this.event, evt -> this.converter.accept(callback, (E)evt, eventContext));
      }

      private IEventBus getEventBus(@Nullable Object context) {
         if (this.eventBus == null) {
            Objects.requireNonNull(context, "mod id context is null");
            return NeoForgeModContainerHelper.getModEventBus((String)context);
         } else {
            return this.eventBus;
         }
      }
   }
}
