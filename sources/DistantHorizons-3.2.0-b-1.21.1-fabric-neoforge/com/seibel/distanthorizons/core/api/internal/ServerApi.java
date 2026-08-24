package com.seibel.distanthorizons.core.api.internal;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.core.world.AbstractDhWorld;
import com.seibel.distanthorizons.core.world.DhApiWorldProxy;
import com.seibel.distanthorizons.core.world.DhClientServerWorld;
import com.seibel.distanthorizons.core.world.DhServerWorld;
import com.seibel.distanthorizons.core.world.IDhServerWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import org.jetbrains.annotations.NotNull;

public class ServerApi {
   public static final ServerApi INSTANCE = new ServerApi();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   private ServerApi() {
   }

   public void serverLoadEvent(boolean isDedicatedEnvironment) {
      LOGGER.debug("Server World loading with (dedicated?:" + isDedicatedEnvironment + ")");
      SharedApi.setDhWorld((AbstractDhWorld)(isDedicatedEnvironment ? new DhServerWorld() : new DhClientServerWorld()));
   }

   public void serverUnloadEvent() {
      LOGGER.debug("Server World " + SharedApi.getAbstractDhWorld() + " unloading");
      AbstractDhWorld dhWorld = SharedApi.getAbstractDhWorld();
      if (dhWorld != null) {
         dhWorld.close();
         SharedApi.setDhWorld(null);
      }
   }

   public void serverLevelLoadEvent(IServerLevelWrapper levelWrapper) {
      LOGGER.debug("Server Level " + levelWrapper + " loading");
      AbstractDhWorld serverWorld = SharedApi.getAbstractDhWorld();
      if (serverWorld != null) {
         serverWorld.getOrLoadLevel(levelWrapper);
      }
   }

   public void serverLevelUnloadEvent(IServerLevelWrapper level) {
      LOGGER.debug("Server Level " + level + " unloading");
      AbstractDhWorld serverWorld = SharedApi.getAbstractDhWorld();
      if (serverWorld != null) {
         serverWorld.unloadLevel(level);
      }
   }

   public void serverChunkLoadEvent(IChunkWrapper chunkWrapper, ILevelWrapper level) {
      SharedApi.INSTANCE.applyChunkUpdate(chunkWrapper, level, true);
   }

   public void serverChunkSaveEvent(IChunkWrapper chunkWrapper, ILevelWrapper level) {
      SharedApi.INSTANCE.applyChunkUpdate(chunkWrapper, level, true);
   }

   public void serverPlayerJoinEvent(IServerPlayerWrapper player) {
      if (!DhApiWorldProxy.INSTANCE.tryGetReadOnly()) {
         IDhServerWorld serverWorld = SharedApi.tryGetDhServerWorld();
         LOGGER.info("Player [" + player.getName() + "] joined.");
         if (serverWorld != null) {
            serverWorld.addPlayer(player);
         }
      }
   }

   public void serverPlayerDisconnectEvent(IServerPlayerWrapper player) {
      if (!DhApiWorldProxy.INSTANCE.tryGetReadOnly()) {
         IDhServerWorld serverWorld = SharedApi.tryGetDhServerWorld();
         LOGGER.info("Player [" + player.getName() + "] disconnected.");
         if (serverWorld != null) {
            serverWorld.removePlayer(player);
         }
      }
   }

   public void serverPlayerLevelChangeEvent(IServerPlayerWrapper player, IServerLevelWrapper originLevel, IServerLevelWrapper destinationLevel) {
      if (!DhApiWorldProxy.INSTANCE.tryGetReadOnly()) {
         IDhServerWorld serverWorld = SharedApi.tryGetDhServerWorld();
         LOGGER.info(
            "Player ["
               + player.getName()
               + "] changed level: ["
               + originLevel.getKeyedLevelDimensionName()
               + "] -> ["
               + destinationLevel.getKeyedLevelDimensionName()
               + "]."
         );
         if (serverWorld != null) {
            serverWorld.changePlayerLevel(player, originLevel, destinationLevel);
         }
      }
   }

   public void pluginMessageReceived(IServerPlayerWrapper player, @NotNull AbstractNetworkMessage message) {
      if (!DhApiWorldProxy.INSTANCE.tryGetReadOnly()) {
         IDhServerWorld serverWorld = SharedApi.tryGetDhServerWorld();
         if (serverWorld != null) {
            serverWorld.getServerPlayerStateManager().handlePluginMessage(player, message);
         }
      }
   }
}
