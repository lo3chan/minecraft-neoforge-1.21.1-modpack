package com.seibel.distanthorizons.core.network.session;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.network.event.AbstractNetworkEventSource;
import com.seibel.distanthorizons.core.network.event.internal.CloseInternalEvent;
import com.seibel.distanthorizons.core.network.event.internal.ProtocolErrorInternalEvent;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.core.network.messages.AbstractTrackableMessage;
import com.seibel.distanthorizons.core.network.messages.base.CloseReasonMessage;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IPluginPacketSender;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class NetworkSession extends AbstractNetworkEventSource {
   private static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logNetworkEventToFile).build();
   private static final IPluginPacketSender PACKET_SENDER = SingletonInjector.INSTANCE.get(IPluginPacketSender.class);
   private static final AtomicInteger lastId = new AtomicInteger();
   public final int id = lastId.getAndIncrement();
   private final AtomicReference<Throwable> closeReason = new AtomicReference<>();
   @Nullable
   public final IServerPlayerWrapper serverPlayer;

   public Throwable getCloseReason() {
      return this.closeReason.get();
   }

   public boolean isClosed() {
      return this.closeReason.get() != null;
   }

   public NetworkSession(@Nullable IServerPlayerWrapper serverPlayer) {
      this.serverPlayer = serverPlayer;
      this.registerHandler(CloseReasonMessage.class, msg -> this.close(new SessionClosedException(msg.reason)));
      this.registerHandler(ProtocolErrorInternalEvent.class, event -> {
         if (event.replyWithCloseReason) {
            this.sendMessage(new CloseReasonMessage("Internal error on other side"));
         }

         this.close(event.reason);
      });
   }

   public void tryHandleMessage(AbstractNetworkMessage message) {
      if (this.closeReason.get() == null) {
         message.setSession(this);

         try {
            LOGGER.debug("Received message: [" + message + "].");
            this.handleMessage(message);
         } catch (Throwable var3) {
            LOGGER.error("Failed to handle the message. New messages will be ignored.", var3);
            LOGGER.error("Message: [" + message + "]");
            this.close(var3);
         }
      }
   }

   @Override
   public <T extends AbstractNetworkMessage> void registerHandler(Class<T> handlerClass, Consumer<T> handlerImplementation) {
      if (this.closeReason.get() == null) {
         this.registerHandler(this, handlerClass, handlerImplementation);
      }
   }

   public <TResponse extends AbstractTrackableMessage> CompletableFuture<TResponse> sendRequest(AbstractTrackableMessage msg, Class<TResponse> responseClass) {
      msg.setSession(this);
      CompletableFuture<TResponse> responseFuture = this.createRequest(msg, responseClass);
      this.sendMessage(msg);
      return responseFuture;
   }

   public void sendMessage(AbstractNetworkMessage message) {
      if (this.closeReason.get() == null) {
         LOGGER.debug("Sending message: [" + message + "]");
         message.setSession(this);

         try {
            if (this.serverPlayer != null) {
               PACKET_SENDER.sendToClient(this.serverPlayer, message);
            } else {
               PACKET_SENDER.sendToServer(message);
            }
         } catch (Throwable var3) {
            LOGGER.info("Failed to send a message", var3);
            LOGGER.info("Message: [" + message + "]");
            this.close(var3);
         }
      }
   }

   public void close(Throwable closeReason) {
      if (this.closeReason.compareAndSet(null, closeReason)) {
         try {
            this.handleMessage(new CloseInternalEvent());
         } catch (Throwable var3) {
         }

         super.close();
      }
   }
}
