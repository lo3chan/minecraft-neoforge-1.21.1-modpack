package net.mehvahdjukaar.moonlight.platform;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.mehvahdjukaar.moonlight.api.block.ItemDisplayTile;
import net.mehvahdjukaar.moonlight.api.misc.SidedInstance;
import net.mehvahdjukaar.moonlight.api.misc.fake_level.FakeLevelManager;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.api.platform.platform.ForgeHelperImpl;
import net.mehvahdjukaar.moonlight.api.platform.platform.RegHelperImpl;
import net.mehvahdjukaar.moonlight.api.resources.recipe.platform.ModIngredientTypes;
import net.mehvahdjukaar.moonlight.api.resources.recipe.platform.ResourceConditionsBridge;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.fake_player.FPClientAccess;
import net.mehvahdjukaar.moonlight.core.fake_player.FakeGenericPlayer;
import net.mehvahdjukaar.moonlight.core.misc.platform.ModLootModifiers;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundSendLoginMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.common.world.poi.ExtendPoiTypesEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

@Mod("moonlight")
public class MoonlightForge {
   public static final String MOD_ID = "moonlight";
   @Nullable
   private static WeakReference<IContext> context = null;
   private static WeakReference<IEventBus> lastCurrentBus = null;
   private static final ThreadLocal<WeakReference<IEventBus>> currentModBus = new ThreadLocal<>();
   private static final Map<ResourceKey<PoiType>, Iterable<? extends Block>> OLD_POI_EVENT = new ConcurrentHashMap<>();

   public MoonlightForge(IEventBus bus) {
      RegHelperImpl.runTasksOnInit();
      Moonlight.commonInit();
      NeoForge.EVENT_BUS.register(MoonlightForge.class);
      bus.addListener(MoonlightForge::registerCapabilities);
      ModLootModifiers.register();
      ModIngredientTypes.register();
      ResourceConditionsBridge.init();
      if (PlatHelper.getPhysicalSide().isClient()) {
         MoonlightForgeClient.init(bus);
      }

      bus.addListener(MoonlightForge::addOldPoiEvent);
      PlatHelper.addCommonSetup(
         () -> {
            if (ModList.get().isLoaded("fabric_api")) {
               Set<String> modsThatHaveFabric = new HashSet<>();

               for (IModInfo modInfo : ModList.get().getMods()) {
                  List<IModInfo> jij = modInfo.getOwningFile().getMods();
                  if (jij.stream().anyMatch(m -> m.getModId().equals("fabric_api"))) {
                     modsThatHaveFabric.add(modInfo.getOwningFile().getFile().getFileName());
                  }
               }

               Moonlight.LOGGER
                  .error(
                     "Fabric API detected! This is not a Fabric mod, so please don't report related issues to MoonlightLib or its dependent(s). This can usually happen when using Connector, or when using a mod that does NOT have a native Neoforge implementation. This can easily lead to poor compatibility and other bizarre issues. Proceed at your own risk. \n Mods that bundle Fabric API: {}",
                     modsThatHaveFabric
                  );
            }
         }
      );
   }

   @Deprecated(
      forRemoval = true
   )
   public static void addOldPoiEvent(ExtendPoiTypesEvent event) {
      for (Entry<ResourceKey<PoiType>, Iterable<? extends Block>> e : OLD_POI_EVENT.entrySet()) {
         ResourceKey<PoiType> p = e.getKey();

         for (Block b : e.getValue()) {
            event.addBlockToPoi(p, b);
         }
      }
   }

   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      for (Entry<ResourceKey<BlockEntityType<?>>, BlockEntityType<?>> e : BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet()) {
         String modId = e.getKey().location().getNamespace();
         if (Moonlight.isDependant(modId)) {
            try {
               BlockEntityType<?> beType = e.getValue();
               BlockEntity instance = beType.create(BlockPos.ZERO, ((Block)beType.getValidBlocks().stream().findFirst().get()).defaultBlockState());
               if (instance instanceof ItemDisplayTile) {
                  event.registerBlockEntity(
                     ItemHandler.BLOCK, beType, (sidedContainer, side) -> ForgeHelperImpl.makeDefaultInvHandler((Container)sidedContainer, side)
                  );
               }
            } catch (Exception var6) {
            }
         }
      }
   }

   private static void registerDefaultItemCap(RegisterCapabilitiesEvent event, BlockEntityType<?> beType) {
   }

   @Nullable
   public static IContext getConditionContext() {
      return context == null ? null : context.get();
   }

   @SubscribeEvent
   public static void onResourceReload(AddReloadListenerEvent event) {
      context = new WeakReference<>(event.getConditionContext());
   }

   @SubscribeEvent
   public static void beforeServerStart(ServerAboutToStartEvent event) {
      Moonlight.beforeServerStart(event.getServer().registryAccess());
   }

   @SubscribeEvent
   public static void onServerShuttingDown(ServerStoppingEvent event) {
      FakeLevelManager.invalidateAll();
      SidedInstance.clearAll();
   }

   @SubscribeEvent
   public static void onDataSync(OnDatapackSyncEvent event) {
      if (event.getPlayer() != null) {
         Moonlight.onDataSyncToPlayer(event.getPlayer(), true);
      } else {
         for (ServerPlayer p : event.getPlayerList().getPlayers()) {
            Moonlight.onDataSyncToPlayer(p, false);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         try {
            NetworkHelper.sendToClientPlayer(player, new ClientBoundSendLoginMessage());
         } catch (Exception var3) {
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onDimensionUnload(Unload event) {
      LevelAccessor level = event.getLevel();

      try {
         FakeGenericPlayer.unloadLevel(level);
         if (level.isClientSide()) {
            FPClientAccess.unloadLevel(level);
         }
      } catch (Exception var3) {
      }
   }

   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      Moonlight.onPlayerCloned(event.getOriginal(), event.getEntity(), event.isWasDeath());
   }

   public static IEventBus getCurrentBus() {
      IEventBus currentBus = ModLoadingContext.get().getActiveContainer().getEventBus();
      if (currentBus != null) {
         return currentBus;
      } else {
         WeakReference<IEventBus> threadLocalBus = currentModBus.get();
         if (threadLocalBus != null && threadLocalBus.get() != null) {
            return threadLocalBus.get();
         } else if (lastCurrentBus != null && lastCurrentBus.get() != null) {
            return lastCurrentBus.get();
         } else {
            throw new IllegalStateException("Bus is null. You must call RegHelper.startRegistering(IEventBus) before registering events");
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static void startRegistering(IEventBus bus) {
      lastCurrentBus = new WeakReference<>(bus);
      currentModBus.set(lastCurrentBus);
   }

   public static void addPoi(ResourceKey<PoiType> poi, Iterable<? extends Block> blocks) {
      OLD_POI_EVENT.put(poi, blocks);
   }
}
