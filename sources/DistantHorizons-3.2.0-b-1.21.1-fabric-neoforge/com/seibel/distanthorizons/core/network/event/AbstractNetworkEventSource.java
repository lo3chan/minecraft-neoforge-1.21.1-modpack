package com.seibel.distanthorizons.core.network.event;

import com.google.common.cache.CacheBuilder;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.network.event.internal.AbstractInternalEvent;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.core.network.messages.AbstractTrackableMessage;
import com.seibel.distanthorizons.core.network.messages.MessageRegistry;
import com.seibel.distanthorizons.core.network.messages.requests.CancelMessage;
import com.seibel.distanthorizons.core.network.messages.requests.ExceptionMessage;
import com.seibel.distanthorizons.core.network.session.SessionClosedException;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.io.InvalidClassException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class AbstractNetworkEventSource {
   private static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logNetworkEventToFile).build();
   private final ConcurrentHashMap<Class<? extends AbstractNetworkMessage>, ConcurrentMap<AbstractNetworkEventSource, Set<AbstractNetworkEventSource.INetworkMessageConsumer>>> networkHandlerSetByMessageClass = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<Long, AbstractNetworkEventSource.FutureResponseData> pendingFutureById = new ConcurrentHashMap<>();
   private final Set<Long> cancelledFutureIdSet = Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS).build().asMap());

   protected void handleMessage(AbstractNetworkMessage message) {
      boolean handled = false;
      ConcurrentMap<AbstractNetworkEventSource, Set<AbstractNetworkEventSource.INetworkMessageConsumer>> handlersByEventSource = this.networkHandlerSetByMessageClass
         .get(message.getClass());
      if (handlersByEventSource != null) {
         for (Set<AbstractNetworkEventSource.INetworkMessageConsumer> handlerSet : handlersByEventSource.values()) {
            for (AbstractNetworkEventSource.INetworkMessageConsumer handler : handlerSet) {
               handled = true;
               handler.accept(message);
            }
         }
      }

      if (message instanceof AbstractTrackableMessage) {
         AbstractTrackableMessage trackableMessage = (AbstractTrackableMessage)message;
         AbstractNetworkEventSource.FutureResponseData responseData = this.pendingFutureById.get(trackableMessage.futureId);
         if (responseData != null) {
            handled = true;
            if (message instanceof ExceptionMessage) {
               responseData.future.completeExceptionally(((ExceptionMessage)message).exception);
            } else if (message.getClass() != responseData.responseClass) {
               responseData.future
                  .completeExceptionally(
                     new InvalidClassException("Response with invalid type: expected " + responseData.responseClass.getSimpleName() + ", got:" + message)
                  );
            } else {
               responseData.future.complete(trackableMessage);
            }
         } else if (this.cancelledFutureIdSet.remove(trackableMessage.futureId)) {
            handled = true;
         }
      }

      if (!handled && ModInfo.IS_DEV_BUILD) {
         LOGGER.warn("Unhandled message: [{}].", message);
      }
   }

   public abstract <T extends AbstractNetworkMessage> void registerHandler(Class<T> class_, Consumer<T> consumer);

   protected final <T extends AbstractNetworkMessage> void registerHandler(
      AbstractNetworkEventSource eventSource, Class<T> handlerClass, Consumer<T> handlerImplementation
   ) {
      if (!AbstractInternalEvent.class.isAssignableFrom(handlerClass)) {
         MessageRegistry.INSTANCE.getMessageId(handlerClass);
      }

      this.networkHandlerSetByMessageClass
         .computeIfAbsent(handlerClass, missingHandlerClass -> new ConcurrentHashMap<>())
         .computeIfAbsent(eventSource, missingEventSource -> ConcurrentHashMap.newKeySet())
         .add(m -> handlerImplementation.accept((T)m));
   }

   protected void removeAllHandlers(AbstractNetworkEventSource eventSource) {
      for (ConcurrentMap<AbstractNetworkEventSource, Set<AbstractNetworkEventSource.INetworkMessageConsumer>> handlerMap : this.networkHandlerSetByMessageClass
         .values()) {
         handlerMap.remove(eventSource);
      }
   }

   protected <TResponse extends AbstractTrackableMessage> CompletableFuture<TResponse> createRequest(
      AbstractTrackableMessage msg, Class<TResponse> responseClass
   ) {
      CompletableFuture<TResponse> responseFuture = new CompletableFuture<>();
      responseFuture.whenComplete((response, throwable) -> {
         if (throwable instanceof CancellationException) {
            this.cancelledFutureIdSet.add(msg.futureId);
            msg.sendResponse(new CancelMessage());
         }

         if (!(throwable instanceof SessionClosedException)) {
            this.pendingFutureById.remove(msg.futureId);
         }
      });
      this.pendingFutureById.put(msg.futureId, new AbstractNetworkEventSource.FutureResponseData(responseClass, responseFuture));
      return responseFuture;
   }

   public void close() {
      this.networkHandlerSetByMessageClass.clear();
      this.completeAllFuturesExceptionally(new SessionClosedException(this.getClass().getSimpleName() + " is closed."));
   }

   private void completeAllFuturesExceptionally(Throwable cause) {
      for (AbstractNetworkEventSource.FutureResponseData responseData : this.pendingFutureById.values()) {
         responseData.future.completeExceptionally(cause);
      }
   }

   private static class FutureResponseData {
      public final Class<? extends AbstractTrackableMessage> responseClass;
      public final CompletableFuture<AbstractTrackableMessage> future;

      private <T extends AbstractTrackableMessage> FutureResponseData(Class<T> responseClass, CompletableFuture<T> future) {
         this.responseClass = responseClass;
         this.future = future;
      }
   }

   @FunctionalInterface
   private interface INetworkMessageConsumer {
      void accept(AbstractNetworkMessage abstractNetworkMessage);
   }
}
