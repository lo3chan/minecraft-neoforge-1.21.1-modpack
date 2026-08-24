package com.seibel.distanthorizons.core.world;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelUnloadEvent;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.level.DhClientServerLevel;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.TimerUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public class DhClientServerWorld extends AbstractDhServerWorld<DhClientServerLevel> implements IDhClientWorld {
   private final Map<DhClientServerLevel, Set<ILevelWrapper>> clientLevelWrapperSetByDhLevel = Collections.synchronizedMap(new HashMap<>());
   private final Timer clientTickTimer = TimerUtil.CreateTimer("ClientTickTimer");

   public DhClientServerWorld() {
      super(EWorldEnvironment.CLIENT_SERVER);
      LOGGER.info("Started DhWorld of type [" + this.environment + "].");
      this.clientTickTimer.scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            DhClientServerWorld.this.clientLevelWrapperSetByDhLevel.keySet().forEach(DhClientServerLevel::clientTick);
         }
      }, 0L, 100L);
   }

   public DhClientServerLevel getOrLoadLevel(@NotNull ILevelWrapper wrapper) {
      if (wrapper instanceof IServerLevelWrapper) {
         return this.dhLevelByLevelWrapper
            .computeIfAbsent(
               wrapper,
               levelWrapper -> {
                  try {
                     DhClientServerLevel level = new DhClientServerLevel(
                        this.saveStructure, (IServerLevelWrapper)levelWrapper, this.getServerPlayerStateManager()
                     );
                     this.clientLevelWrapperSetByDhLevel.computeIfAbsent(level, clientServerLevel -> Collections.synchronizedSet(new HashSet<>()));
                     ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelLoadEvent.class, new DhApiLevelLoadEvent.EventParam(wrapper));
                     return level;
                  } catch (Exception var7) {
                     LOGGER.fatal("Failed to load client-server level, error: [" + var7.getMessage() + "].", var7);
                     String r = "§c";
                     String y = "§e";
                     String cf = "§r";
                     ClientApi.INSTANCE
                        .showChatMessageNextFrame(
                           r
                              + "Distant Horizons: ClientServer level loading failed."
                              + cf
                              + "\nUnable to load level ["
                              + y
                              + levelWrapper.getDhIdentifier()
                              + cf
                              + "], LODs may not appear. See log for more information.\n"
                        );
                     return null;
                  }
               }
            );
      } else {
         if (wrapper instanceof IClientLevelWrapper) {
            ((IClientLevelWrapper)wrapper).markAccessed();
         }

         return this.dhLevelByLevelWrapper
            .computeIfAbsent(
               wrapper,
               levelWrapper -> {
                  if (!(levelWrapper instanceof IClientLevelWrapper)) {
                     LodUtil.assertNotReach("tryGetServerSideWrapper given a non-IClientLevelWrapper.");
                  }

                  IClientLevelWrapper clientLevelWrapper = (IClientLevelWrapper)levelWrapper;
                  IServerLevelWrapper serverLevelWrapper = clientLevelWrapper.tryGetServerSideWrapper();
                  LodUtil.assertTrue(serverLevelWrapper != null);
                  if (!clientLevelWrapper.getDimensionType().equals(serverLevelWrapper.getDimensionType())) {
                     LodUtil.assertNotReach(
                        "tryGetServerSideWrapper returned a level for a different dimension. ClientLevelWrapper dim: ["
                           + clientLevelWrapper.getDhIdentifier()
                           + "] ServerLevelWrapper dim: ["
                           + serverLevelWrapper.getDhIdentifier()
                           + "]."
                     );
                  }

                  DhClientServerLevel level = this.dhLevelByLevelWrapper.get(serverLevelWrapper);
                  if (level == null) {
                     return null;
                  } else {
                     level.startRenderer();
                     clientLevelWrapper.setDhLevel(level);
                     this.clientLevelWrapperSetByDhLevel.get(level).add(wrapper);
                     return level;
                  }
               }
            );
      }
   }

   @Override
   public boolean unloadLevel(@NotNull ILevelWrapper wrapper) {
      if (!this.dhLevelByLevelWrapper.containsKey(wrapper)) {
         return false;
      } else {
         if (wrapper instanceof IServerLevelWrapper) {
            LOGGER.info("Unloading level " + this.dhLevelByLevelWrapper.get(wrapper));
            wrapper.onUnload();
            DhClientServerLevel clientServerLevel = this.dhLevelByLevelWrapper.remove(wrapper);
            clientServerLevel.close();
            this.clientLevelWrapperSetByDhLevel.remove(clientServerLevel);
         } else {
            DhClientServerLevel level = this.dhLevelByLevelWrapper.remove(wrapper);
            Set<ILevelWrapper> wrappers = this.clientLevelWrapperSetByDhLevel.get(level);
            if (wrappers != null) {
               wrappers.remove(wrapper);
            }

            if ((wrappers == null || wrappers.isEmpty()) && level.isRendering()) {
               level.stopRenderer();
            }

            wrapper.onUnload();
         }

         ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelUnloadEvent.class, new DhApiLevelUnloadEvent.EventParam(wrapper));
         return true;
      }
   }

   @Override
   public synchronized void close() {
      ArrayList<CompletableFuture<Void>> closeFutures = new ArrayList<>();
      synchronized (this.clientLevelWrapperSetByDhLevel) {
         for (DhClientServerLevel level : this.clientLevelWrapperSetByDhLevel.keySet()) {
            IServerLevelWrapper serverLevelWrapper = level.getServerLevelWrapper();
            if (serverLevelWrapper != null) {
               serverLevelWrapper.onUnload();
               ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelUnloadEvent.class, new DhApiLevelUnloadEvent.EventParam(serverLevelWrapper));
            }

            IClientLevelWrapper clientLevelWrapper = level.getClientLevelWrapper();
            if (clientLevelWrapper != null) {
               clientLevelWrapper.onUnload();
               ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelUnloadEvent.class, new DhApiLevelUnloadEvent.EventParam(clientLevelWrapper));
            }

            CompletableFuture<Void> closeFuture = new CompletableFuture<>();
            Thread closeThread = new Thread(() -> {
               level.close();
               closeFuture.complete(null);
            }, "level shutdown");
            closeThread.start();
            closeFutures.add(closeFuture);
         }
      }

      for (CompletableFuture<Void> future : closeFutures) {
         future.join();
      }

      this.dhLevelByLevelWrapper.clear();
      this.clientTickTimer.cancel();
      LOGGER.info("Closed DhWorld of type [" + this.environment + "].");
   }
}
