package com.seibel.distanthorizons.core.world;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelUnloadEvent;
import com.seibel.distanthorizons.core.file.structure.LocalSaveStructure;
import com.seibel.distanthorizons.core.level.AbstractDhServerLevel;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.multiplayer.server.ServerPlayerState;
import com.seibel.distanthorizons.core.multiplayer.server.ServerPlayerStateManager;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDhServerWorld<TDhServerLevel extends AbstractDhServerLevel> extends AbstractDhWorld implements IDhServerWorld {
   protected final ConcurrentHashMap<ILevelWrapper, TDhServerLevel> dhLevelByLevelWrapper = new ConcurrentHashMap<>();
   public final LocalSaveStructure saveStructure = new LocalSaveStructure();
   private final ServerPlayerStateManager serverPlayerStateManager = new ServerPlayerStateManager();

   public AbstractDhServerWorld(EWorldEnvironment worldEnvironment) {
      super(worldEnvironment);
   }

   @Override
   public ServerPlayerStateManager getServerPlayerStateManager() {
      return this.serverPlayerStateManager;
   }

   @Override
   public void addPlayer(IServerPlayerWrapper serverPlayer) {
      ServerPlayerState playerState = this.serverPlayerStateManager.registerJoinedPlayer(serverPlayer);
      AbstractDhServerLevel serverLevel = (AbstractDhServerLevel)this.getOrLoadServerLevel(serverPlayer.getLevel());
      if (serverLevel != null) {
         serverLevel.addPlayer(serverPlayer);
         Iterator<TDhServerLevel> it = this.dhLevelByLevelWrapper.values().stream().distinct().iterator();

         while (it.hasNext()) {
            TDhServerLevel level = it.next();
            level.registerNetworkHandlers(playerState);
         }

         this.serverPlayerStateManager.handlePluginMessagesFromQueue(playerState);
      }
   }

   @Override
   public void removePlayer(IServerPlayerWrapper serverPlayer) {
      IServerLevelWrapper playerLevel = serverPlayer.getLevel();
      if (playerLevel != null) {
         TDhServerLevel serverLevel = this.getLevel(playerLevel);
         if (serverLevel != null) {
            serverLevel.removePlayer(serverPlayer);
            this.serverPlayerStateManager.unregisterLeftPlayer(serverPlayer);
         }
      }
   }

   @Override
   public void changePlayerLevel(IServerPlayerWrapper player, IServerLevelWrapper originLevel, IServerLevelWrapper destinationLevel) {
      this.getLevel(destinationLevel).addPlayer(player);
      this.getLevel(originLevel).removePlayer(player);
   }

   public TDhServerLevel getLevel(@NotNull ILevelWrapper wrapper) {
      return this.dhLevelByLevelWrapper.get(wrapper);
   }

   @Override
   public Iterable<? extends IDhLevel> getAllLoadedLevels() {
      return new HashSet<>(this.dhLevelByLevelWrapper.values());
   }

   @Override
   public int getLoadedLevelCount() {
      return this.dhLevelByLevelWrapper.size();
   }

   @Override
   public void close() {
      ArrayList<CompletableFuture<Void>> closeFutures = new ArrayList<>();

      for (TDhServerLevel level : this.dhLevelByLevelWrapper.values()) {
         IServerLevelWrapper serverLevelWrapper = level.getServerLevelWrapper();
         if (serverLevelWrapper != null) {
            serverLevelWrapper.onUnload();
            ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelUnloadEvent.class, new DhApiLevelUnloadEvent.EventParam(serverLevelWrapper));
         }

         CompletableFuture<Void> closeFuture = new CompletableFuture<>();
         Thread closeThread = new Thread(() -> {
            level.close();
            closeFuture.complete(null);
         }, "level shutdown");
         closeThread.start();
         closeFutures.add(closeFuture);
      }

      for (CompletableFuture<Void> future : closeFutures) {
         future.join();
      }

      this.dhLevelByLevelWrapper.clear();
      LOGGER.info("Closed DhWorld of type [" + this.environment + "].");
   }
}
