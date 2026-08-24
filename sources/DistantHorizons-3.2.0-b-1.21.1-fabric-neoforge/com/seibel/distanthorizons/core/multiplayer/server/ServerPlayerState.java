package com.seibel.distanthorizons.core.multiplayer.server;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.AbstractDhServerLevel;
import com.seibel.distanthorizons.core.multiplayer.config.SessionConfig;
import com.seibel.distanthorizons.core.multiplayer.fullData.FullDataPayloadSender;
import com.seibel.distanthorizons.core.multiplayer.fullData.SharedBandwidthLimit;
import com.seibel.distanthorizons.core.network.event.internal.CloseInternalEvent;
import com.seibel.distanthorizons.core.network.event.internal.IncompatibleMessageInternalEvent;
import com.seibel.distanthorizons.core.network.exceptions.RateLimitedException;
import com.seibel.distanthorizons.core.network.messages.base.CloseReasonMessage;
import com.seibel.distanthorizons.core.network.messages.base.LevelInitMessage;
import com.seibel.distanthorizons.core.network.messages.base.RequestLevelInitMessage;
import com.seibel.distanthorizons.core.network.messages.base.SessionConfigMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceRequestMessage;
import com.seibel.distanthorizons.core.network.session.NetworkSession;
import com.seibel.distanthorizons.core.util.ratelimiting.SupplierBasedRateAndConcurrencyLimiter;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class ServerPlayerState implements Closeable {
   private final IMinecraftSharedWrapper MC_SHARED = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);
   private final SessionConfig.AnyChangeListener configAnyChangeListener = new SessionConfig.AnyChangeListener(this::sendConfigMessage);
   private final String serverKeyWithoutId = Config.Server.serverKey.get();
   private final String serverKey = (this.serverKeyWithoutId.isEmpty() ? "" : Config.Server.serverId.get() + "_" + this.serverKeyWithoutId.trim())
      .replaceAll("[^a-zA-Z0-9-_ ]", "")
      .replaceAll(" ", "_");
   public final NetworkSession networkSession;
   @NotNull
   public final SessionConfig sessionConfig = new SessionConfig();
   public final FullDataPayloadSender fullDataPayloadSender;
   private final ConcurrentHashMap<AbstractDhServerLevel, ServerPlayerState.RateLimiterSet> rateLimiterSets = new ConcurrentHashMap<>();

   public IServerPlayerWrapper getServerPlayer() {
      return this.networkSession.serverPlayer;
   }

   public boolean isReady() {
      return this.sessionConfig.constrainingConfig != null;
   }

   public ServerPlayerState.RateLimiterSet getRateLimiterSet(AbstractDhServerLevel level) {
      return this.rateLimiterSets.computeIfAbsent(level, ignored -> new ServerPlayerState.RateLimiterSet());
   }

   public void clearRateLimiterSets() {
      this.rateLimiterSets.clear();
   }

   public ServerPlayerState(IServerPlayerWrapper serverPlayer, SharedBandwidthLimit sharedBandwidthLimit) {
      this.networkSession = new NetworkSession(serverPlayer);
      this.fullDataPayloadSender = new FullDataPayloadSender(this.networkSession, this.sessionConfig::getPlayerBandwidthLimit, sharedBandwidthLimit);
      this.networkSession.registerHandler(SessionConfigMessage.class, sessionConfigMessage -> {
         this.sessionConfig.constrainingConfig = sessionConfigMessage.config;
         this.sendConfigMessage();
      });
      this.networkSession.registerHandler(RequestLevelInitMessage.class, msg -> {
         if (Config.Server.sendLevelKeys.get()) {
            IServerLevelWrapper serverLevelWrapper = this.MC_SHARED.getLevelWrapper(msg.dimensionResourceLocation);
            if (serverLevelWrapper != null) {
               String levelKey = serverLevelWrapper.getKeyedLevelDimensionName();
               this.networkSession.sendMessage(new LevelInitMessage(msg.dimensionResourceLocation, this.serverKey, levelKey));
            }
         }
      });
      this.networkSession.registerHandler(CloseInternalEvent.class, event -> {});
      this.networkSession.registerHandler(IncompatibleMessageInternalEvent.class, event -> {
         this.networkSession.sendMessage(new CloseReasonMessage("Incompatible protocol version"));
         this.close();
      });
   }

   private void sendConfigMessage() {
      double coordinateScale = this.getServerPlayer().getLevel().getDimensionType().getCoordinateScale();
      this.sessionConfig
         .constrainValue(
            Config.Common.WorldGenerator.generationCenterChunkX, (int)(Config.Common.WorldGenerator.generationCenterChunkX.get().intValue() / coordinateScale)
         );
      this.sessionConfig
         .constrainValue(
            Config.Common.WorldGenerator.generationCenterChunkZ, (int)(Config.Common.WorldGenerator.generationCenterChunkZ.get().intValue() / coordinateScale)
         );
      this.networkSession.sendMessage(new SessionConfigMessage(this.sessionConfig));
   }

   @Override
   public void close() {
      this.fullDataPayloadSender.close();
      this.configAnyChangeListener.close();
      this.networkSession.close();
   }

   public class RateLimiterSet {
      public final SupplierBasedRateAndConcurrencyLimiter<FullDataSourceRequestMessage> generationRequestRateLimiter = new SupplierBasedRateAndConcurrencyLimiter<>(
         () -> Config.Server.generationRequestRateLimit.get(),
         msg -> msg.sendResponse(
            new RateLimitedException("Full data request rate limit: " + ServerPlayerState.this.sessionConfig.getGenerationRequestRateLimit())
         )
      );
      public final SupplierBasedRateAndConcurrencyLimiter<FullDataSourceRequestMessage> syncOnLoginRateLimiter = new SupplierBasedRateAndConcurrencyLimiter<>(
         () -> Config.Server.syncOnLoadRateLimit.get(),
         msg -> msg.sendResponse(new RateLimitedException("Sync on login rate limit: " + ServerPlayerState.this.sessionConfig.getSyncOnLoginRateLimit()))
      );
   }
}
