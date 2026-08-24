package com.seibel.distanthorizons.core.world;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelUnloadEvent;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.api.internal.ClientPluginChannelApi;
import com.seibel.distanthorizons.core.file.structure.ClientOnlySaveStructure;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.level.IServerKeyedClientLevel;
import com.seibel.distanthorizons.core.multiplayer.client.ClientNetworkState;
import com.seibel.distanthorizons.core.util.TimerUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class DhClientWorld extends AbstractDhWorld implements IDhClientWorld {
   public final ClientOnlySaveStructure saveStructure;
   public final ClientNetworkState networkState = new ClientNetworkState();
   private final ConcurrentHashMap<String, DhClientLevel> clientLevelByDhId;
   private final Map<String, Set<IClientLevelWrapper>> clientLevelWrapperSetByDhId = new ConcurrentHashMap<>();
   private final Timer clientTickTimer = TimerUtil.CreateTimer("ClientTickTimer");
   public final ClientPluginChannelApi pluginChannelApi = new ClientPluginChannelApi();
   private static final long FIRST_LEVEL_LOAD_DELAY_IN_MS = 1000L;
   private long allowLoadingLevelsAfter = 0L;
   private final Set<Object> levelInitRequestedClientLevels = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

   public DhClientWorld() {
      super(EWorldEnvironment.CLIENT_ONLY);
      this.saveStructure = new ClientOnlySaveStructure();
      this.clientLevelByDhId = new ConcurrentHashMap<>();
      LOGGER.info("Started DhWorld of type " + this.environment);
      this.pluginChannelApi.onJoinServer(this.networkState.getSession());
      this.networkState.sendConfigMessage();
      this.clientTickTimer.scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            DhClientWorld.this.clientLevelByDhId.values().forEach(DhClientLevel::clientTick);
         }
      }, 0L, 100L);
   }

   public DhClientLevel getOrLoadLevel(@NotNull ILevelWrapper wrapper) {
      if (!(wrapper instanceof IClientLevelWrapper)) {
         return null;
      } else {
         IClientLevelWrapper clientLevelWrapper = (IClientLevelWrapper)wrapper;
         clientLevelWrapper.markAccessed();
         DhClientLevel storedLevel = this.clientLevelByDhId.computeIfAbsent(wrapper.getDhIdentifier(), key -> this.createClientLevel(clientLevelWrapper));
         if (storedLevel != null && storedLevel.getClientLevelWrapper() != wrapper) {
            this.unloadLevel(storedLevel.getLevelWrapper());
            storedLevel = this.createClientLevel(clientLevelWrapper);
            if (storedLevel != null) {
               this.clientLevelByDhId.put(wrapper.getDhIdentifier(), storedLevel);
            }
         }

         return storedLevel;
      }
   }

   private DhClientLevel createClientLevel(@NotNull IClientLevelWrapper clientLevelWrapper) {
      try {
         if (!this.ensureLevelKeyWhenAvailable(clientLevelWrapper)) {
            return null;
         } else {
            DhClientLevel level = new DhClientLevel(this.saveStructure, clientLevelWrapper, this.networkState);
            this.clientLevelWrapperSetByDhId
               .computeIfAbsent(clientLevelWrapper.getDhIdentifier(), dhId -> Collections.synchronizedSet(new HashSet<>()))
               .add(clientLevelWrapper);
            ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelLoadEvent.class, new DhApiLevelLoadEvent.EventParam(clientLevelWrapper));
            ClientApi.INSTANCE.loadWaitingChunksForLevel(clientLevelWrapper);
            return level;
         }
      } catch (Exception var6) {
         LOGGER.fatal("Failed to load client level, error: [" + var6.getMessage() + "].", var6);
         String r = "§c";
         String y = "§e";
         String cf = "§r";
         ClientApi.INSTANCE
            .showChatMessageNextFrame(
               r
                  + "Distant Horizons: Client level loading failed."
                  + cf
                  + "\nUnable to load level ["
                  + y
                  + clientLevelWrapper.getDhIdentifier()
                  + cf
                  + "], LODs may not appear. See log for more information. \n"
            );
         return null;
      }
   }

   private boolean ensureLevelKeyWhenAvailable(@NotNull IClientLevelWrapper clientLevelWrapper) {
      if (!this.pluginChannelApi.allowLevelLoading(clientLevelWrapper)) {
         LOGGER.debug("Client levels in this connection are managed by the server, skipping auto-load of: [" + clientLevelWrapper + "]");
         this.sendLevelInitRequestIfNeed(clientLevelWrapper);
         return false;
      } else {
         if (clientLevelWrapper instanceof IServerKeyedClientLevel) {
            this.sendLevelInitRequestIfNeed(clientLevelWrapper);
         }

         if (!(clientLevelWrapper instanceof IServerKeyedClientLevel)) {
            this.sendLevelInitRequestIfNeed(clientLevelWrapper);
            if (this.allowLoadingLevelsAfter == 0L) {
               this.allowLoadingLevelsAfter = System.currentTimeMillis() + 1000L;
            }

            return System.currentTimeMillis() >= this.allowLoadingLevelsAfter;
         } else {
            return true;
         }
      }
   }

   private void sendLevelInitRequestIfNeed(@NotNull IClientLevelWrapper clientLevelWrapper) {
      Object clientLevelObject = clientLevelWrapper.getWrappedMcObject();
      if (clientLevelObject != null && this.levelInitRequestedClientLevels.add(clientLevelObject)) {
         this.networkState.sendLevelInitRequest(clientLevelWrapper.getDimensionName());
      }
   }

   public DhClientLevel getLevel(@NotNull ILevelWrapper wrapper) {
      return !(wrapper instanceof IClientLevelWrapper) ? null : this.clientLevelByDhId.get(wrapper.getDhIdentifier());
   }

   @Override
   public Iterable<? extends IDhLevel> getAllLoadedLevels() {
      return this.clientLevelByDhId.values();
   }

   @Override
   public int getLoadedLevelCount() {
      return this.clientLevelByDhId.size();
   }

   @Override
   public boolean unloadLevel(@NotNull ILevelWrapper wrapper) {
      if (!(wrapper instanceof IClientLevelWrapper)) {
         return false;
      } else if (this.clientLevelByDhId.containsKey(wrapper.getDhIdentifier())) {
         LOGGER.info("Unloading level [" + this.clientLevelByDhId.get(wrapper.getDhIdentifier()) + "].");
         wrapper.onUnload();
         Set<IClientLevelWrapper> wrapperSet = this.clientLevelWrapperSetByDhId.get(wrapper.getDhIdentifier());
         wrapperSet.remove(wrapper);
         if (wrapperSet.isEmpty()) {
            this.clientLevelByDhId.remove(wrapper.getDhIdentifier()).close();
         }

         ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelUnloadEvent.class, new DhApiLevelUnloadEvent.EventParam(wrapper));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void addDebugMenuStringsToList(List<String> messageList) {
      super.addDebugMenuStringsToList(messageList);
      this.networkState.addDebugMenuStringsToList(messageList);
   }

   @Override
   public void close() {
      this.networkState.close();
      this.pluginChannelApi.reset();
      ArrayList<CompletableFuture<Void>> closeFutures = new ArrayList<>();

      for (DhClientLevel dhClientLevel : this.clientLevelByDhId.values()) {
         IClientLevelWrapper clientLevelWrapper = dhClientLevel.getClientLevelWrapper();
         if (clientLevelWrapper != null) {
            clientLevelWrapper.onUnload();
            ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelUnloadEvent.class, new DhApiLevelUnloadEvent.EventParam(clientLevelWrapper));
         }

         CompletableFuture<Void> closeFuture = new CompletableFuture<>();
         Thread closeThread = new Thread(() -> {
            dhClientLevel.close();
            closeFuture.complete(null);
         }, "level shutdown");
         closeThread.start();
         closeFutures.add(closeFuture);
      }

      for (CompletableFuture<Void> future : closeFutures) {
         future.join();
      }

      this.clientLevelByDhId.clear();
      this.clientLevelWrapperSetByDhId.clear();
      this.levelInitRequestedClientLevels.clear();
      this.clientTickTimer.cancel();
      LOGGER.info("Closed DhWorld of type [" + this.environment + "].");
   }
}
