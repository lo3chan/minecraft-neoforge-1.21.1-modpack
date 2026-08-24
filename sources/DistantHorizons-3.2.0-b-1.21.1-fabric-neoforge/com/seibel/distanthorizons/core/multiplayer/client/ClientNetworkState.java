package com.seibel.distanthorizons.core.multiplayer.client;

import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.listeners.ConfigChangeListener;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.multiplayer.config.SessionConfig;
import com.seibel.distanthorizons.core.multiplayer.fullData.FullDataPayloadReceiver;
import com.seibel.distanthorizons.core.network.event.internal.CloseInternalEvent;
import com.seibel.distanthorizons.core.network.event.internal.IncompatibleMessageInternalEvent;
import com.seibel.distanthorizons.core.network.messages.base.LevelInitMessage;
import com.seibel.distanthorizons.core.network.messages.base.RequestLevelInitMessage;
import com.seibel.distanthorizons.core.network.messages.base.SessionConfigMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataPartialUpdateMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceResponseMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSplitMessage;
import com.seibel.distanthorizons.core.network.session.NetworkSession;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import java.io.Closeable;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientNetworkState implements Closeable {
   protected static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logNetworkEventToFile).build();
   protected static final DhLogger CONFIG_CHANGE_LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logConnectionConfigChangesToFile).build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   public final FullDataPayloadReceiver fullDataPayloadReceiver = new FullDataPayloadReceiver();
   private final SessionConfig.AnyChangeListener configAnyChangeListener = new SessionConfig.AnyChangeListener(this::sendConfigMessage);
   private final NetworkSession networkSession = new NetworkSession(null);
   @NotNull
   public SessionConfig sessionConfig = new SessionConfig();
   private volatile boolean configReceived = false;
   private ClientNetworkState.EServerSupportStatus serverSupportStatus = ClientNetworkState.EServerSupportStatus.NONE;
   @Nullable
   private Integer closestProtocolVersion;
   private long serverTimeOffset = 0L;
   private final ClientCongestionControl congestionControl = new ClientCongestionControl(() -> {
      if (Config.Server.enableAdaptiveTransferSpeed.get()) {
         this.sendConfigMessage(false);
      }
   });
   private final ConfigChangeListener<Boolean> adaptiveTransferSpeedListener = new ConfigChangeListener<>(
      Config.Server.enableAdaptiveTransferSpeed, isEnabled -> {
         if (isEnabled) {
            this.congestionControl.reset();
         }

         this.sendConfigMessage();
      }
   );

   public NetworkSession getSession() {
      return this.networkSession;
   }

   public boolean isReady() {
      return this.configReceived;
   }

   public long getServerTimeOffset() {
      return this.serverTimeOffset;
   }

   public ClientNetworkState() {
      this.networkSession.registerHandler(IncompatibleMessageInternalEvent.class, event -> {
         if (this.closestProtocolVersion == null || Math.abs(event.protocolVersion - 15) < this.closestProtocolVersion) {
            this.closestProtocolVersion = event.protocolVersion;
            if (15 < event.protocolVersion) {
               ClientApi.INSTANCE.showChatMessageNextFrame("§6Distant Horizons: Your mod is outdated. Update to receive LODs on this server.");
            } else {
               ClientApi.INSTANCE.showChatMessageNextFrame("§6Distant Horizons: The server's mod is outdated. Ask the server's owner to update.");
            }
         }
      });
      this.networkSession.registerHandler(LevelInitMessage.class, message -> {
         if (this.serverSupportStatus == ClientNetworkState.EServerSupportStatus.NONE) {
            this.serverSupportStatus = ClientNetworkState.EServerSupportStatus.LEVELS_ONLY;
         }

         this.serverTimeOffset = message.serverTime - System.currentTimeMillis();
         LOGGER.info("Server time offset: [" + this.serverTimeOffset + "] ms");
      });
      this.networkSession.registerHandler(CloseInternalEvent.class, message -> this.configReceived = false);
      this.networkSession.registerHandler(FullDataPartialUpdateMessage.class, msg -> {});
      if (MC_CLIENT.connectedToReplay()) {
         this.networkSession.registerHandler(SessionConfigMessage.class, message -> {});
         this.networkSession.registerHandler(FullDataSourceResponseMessage.class, message -> {});
         this.networkSession.registerHandler(FullDataSplitMessage.class, message -> {});
      } else {
         this.networkSession.registerHandler(SessionConfigMessage.class, message -> {
            this.serverSupportStatus = ClientNetworkState.EServerSupportStatus.FULL;
            String configChanges = this.sessionConfig.getDifferencesAsString(message.config);
            CONFIG_CHANGE_LOGGER.info("Connection config has been changed: [" + configChanges + "].");
            this.sessionConfig = message.config;
            this.configReceived = true;
         });
         this.networkSession.registerHandler(FullDataSplitMessage.class, this.fullDataPayloadReceiver::receiveChunk);
         this.networkSession.registerHandler(FullDataSplitMessage.class, this.congestionControl::onPayloadReceived);
      }
   }

   public void sendLevelInitRequest(String clientLevelKey) {
      this.getSession().sendMessage(new RequestLevelInitMessage(clientLevelKey));
   }

   public void sendConfigMessage() {
      this.sendConfigMessage(true);
   }

   public void sendConfigMessage(boolean blocking) {
      SessionConfig sessionConfig = new SessionConfig();
      if (Config.Server.enableAdaptiveTransferSpeed.get()) {
         sessionConfig.constrainValue(Config.Server.playerBandwidthLimit, this.congestionControl.getDesiredRate());
      }

      if (blocking) {
         this.configReceived = false;
      }

      this.getSession().sendMessage(new SessionConfigMessage(sessionConfig));
   }

   public void addDebugMenuStringsToList(List<String> messageList) {
      if (this.networkSession.isClosed()) {
         messageList.add("NetworkSession closed: " + this.networkSession.getCloseReason().getMessage());
      } else if (this.serverSupportStatus == ClientNetworkState.EServerSupportStatus.NONE && this.closestProtocolVersion != null) {
         messageList.add("Incompatible protocol version: [" + this.closestProtocolVersion + "], required: [" + 15 + "]");
      } else {
         messageList.add(this.serverSupportStatus.message);
      }
   }

   @Override
   public void close() {
      this.fullDataPayloadReceiver.close();
      this.adaptiveTransferSpeedListener.close();
      this.configAnyChangeListener.close();
      this.networkSession.close();
   }

   private static enum EServerSupportStatus {
      NONE("Server does not support DH"),
      LEVELS_ONLY("Server supports shared level keys"),
      FULL("Server has full DH support");

      public final String message;

      private EServerSupportStatus(String message) {
         this.message = message;
      }
   }
}
