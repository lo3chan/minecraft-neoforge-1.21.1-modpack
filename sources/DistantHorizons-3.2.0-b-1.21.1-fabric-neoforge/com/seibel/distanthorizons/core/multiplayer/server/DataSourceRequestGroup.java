package com.seibel.distanthorizons.core.multiplayer.server;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceRequestMessage;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.CheckForNull;

class DataSourceRequestGroup {
   public final long pos;
   private boolean worldGenTaskComplete = false;
   @CheckForNull
   public FullDataSourceV2 fullDataSource = null;
   public final ConcurrentMap<Long, DataSourceRequestGroup.RequestData> requestMessages = new ConcurrentHashMap<>();
   public final Semaphore pendingAdditionSemaphore = new Semaphore(32767, true);
   public final AtomicBoolean isClosed = new AtomicBoolean();

   void markWorldGenTaskComplete() {
      this.worldGenTaskComplete = true;
   }

   boolean isWorldGenTaskComplete() {
      return this.worldGenTaskComplete;
   }

   DataSourceRequestGroup(long pos) {
      this.pos = pos;
   }

   public boolean tryClose() {
      if (!this.isClosed.compareAndSet(false, true)) {
         return false;
      } else {
         this.pendingAdditionSemaphore.acquireUninterruptibly(32767);
         return true;
      }
   }

   public boolean tryAddRequest(DataSourceRequestGroup.RequestData requestData) {
      if (!this.pendingAdditionSemaphore.tryAcquire()) {
         return false;
      } else {
         this.requestMessages.put(requestData.futureId(), requestData);
         this.pendingAdditionSemaphore.release();
         return true;
      }
   }

   public DataSourceRequestGroup.RequestData tryRemoveRequest(
      long requestId, DataSourceRequestGroup.IHangingRequestTransferConsumer hangingRequestTransferConsumer
   ) {
      DataSourceRequestGroup.RequestData removed = this.requestMessages.remove(requestId);
      if (this.requestMessages.isEmpty() && this.tryClose()) {
         hangingRequestTransferConsumer.accept(this.requestMessages.values());
      }

      return removed;
   }

   @FunctionalInterface
   interface IHangingRequestTransferConsumer extends Consumer<Collection<DataSourceRequestGroup.RequestData>> {
   }

   static class RequestData {
      public final ServerPlayerState serverPlayerState;
      public final ServerPlayerState.RateLimiterSet rateLimiterSet;
      public final FullDataSourceRequestMessage message;

      public long futureId() {
         return this.message.futureId;
      }

      public long sectionPos() {
         return this.message.sectionPos;
      }

      RequestData(ServerPlayerState serverPlayerState, FullDataSourceRequestMessage message, ServerPlayerState.RateLimiterSet rateLimiterSet) {
         this.serverPlayerState = serverPlayerState;
         this.rateLimiterSet = rateLimiterSet;
         this.message = message;
      }
   }
}
