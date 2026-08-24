package com.seibel.distanthorizons.core.multiplayer.client;

import com.google.common.base.Stopwatch;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalResult;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.network.exceptions.RateLimitedException;
import com.seibel.distanthorizons.core.network.exceptions.RequestOutOfRangeException;
import com.seibel.distanthorizons.core.network.exceptions.RequestRejectedException;
import com.seibel.distanthorizons.core.network.exceptions.SectionRequiresSplittingException;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceRequestMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceResponseMessage;
import com.seibel.distanthorizons.core.network.session.SessionClosedException;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV2DTO;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.ratelimiting.SupplierBasedRateLimiter;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.world.DhApiWorldProxy;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import java.awt.Color;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public abstract class AbstractFullDataNetworkRequestQueue implements IDebugRenderable, AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logNetworkEventToFile).maxCountPerSecond(3).build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final AbstractDebugWireframeRenderer DEBUG_RENDERER = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
   private static final int MAX_RETRY_ATTEMPTS = 3;
   protected static final long SHUTDOWN_TIMEOUT_SECONDS = 5L;
   public final ClientNetworkState networkState;
   protected final DhClientLevel level;
   private final boolean changedOnly;
   private volatile CompletableFuture<Void> closingFuture = null;
   protected final ConcurrentMap<Long, AbstractFullDataNetworkRequestQueue.NetRequestTask> waitingTasksBySectionPos = new ConcurrentHashMap<>();
   private final Semaphore pendingTasksSemaphore = new Semaphore(32767, true);
   private final AtomicInteger finishedRequests = new AtomicInteger();
   private final AtomicInteger failedRequests = new AtomicInteger();
   private final ConfigEntry<Boolean> showDebugWireframeConfig;
   private final SupplierBasedRateLimiter<Void> rateLimiter = new SupplierBasedRateLimiter<>(this::getRequestRateLimit);

   public AbstractFullDataNetworkRequestQueue(
      ClientNetworkState networkState, DhClientLevel level, boolean changedOnly, ConfigEntry<Boolean> showDebugWireframeConfig
   ) {
      this.networkState = networkState;
      this.level = level;
      this.changedOnly = changedOnly;
      this.showDebugWireframeConfig = showDebugWireframeConfig;
      DEBUG_RENDERER.register(this, this.showDebugWireframeConfig);
   }

   protected abstract int getRequestRateLimit();

   protected abstract boolean sectionInAllowedGenerationRadius(long l, DhBlockPos2D dhBlockPos2D);

   protected abstract boolean onBeforeRequest(long l, CompletableFuture<DataSourceRetrievalResult> completableFuture);

   protected abstract String getQueueName();

   public CompletableFuture<DataSourceRetrievalResult> submitRequest(long sectionPos, @Nullable Long clientTimestamp) {
      AbstractFullDataNetworkRequestQueue.NetRequestTask requestEntry = this.waitingTasksBySectionPos.compute(sectionPos, (pos, existingNetTask) -> {
         if (existingNetTask != null) {
            return (AbstractFullDataNetworkRequestQueue.NetRequestTask)existingNetTask;
         } else {
            AbstractFullDataNetworkRequestQueue.NetRequestTask newRequestEntry = new AbstractFullDataNetworkRequestQueue.NetRequestTask(pos, clientTimestamp);
            newRequestEntry.future.whenComplete((requestResult, throwable) -> {
               this.waitingTasksBySectionPos.remove(pos);
               if (throwable != null) {
                  if (!(throwable instanceof CancellationException)) {
                     this.failedRequests.incrementAndGet();
                  }
               } else {
                  switch (requestResult.state) {
                     case SUCCESS:
                        this.finishedRequests.incrementAndGet();
                     case REQUIRES_SPLITTING:
                  }
               }
            });
            return newRequestEntry;
         }
      });
      return requestEntry.future;
   }

   public synchronized boolean tick(DhBlockPos2D targetPos) {
      if (DhApiWorldProxy.INSTANCE.tryGetReadOnly()) {
         return false;
      } else if (this.closingFuture == null && this.networkState.isReady()) {
         while (
            this.getInProgressTaskCount() < this.getWaitingTaskCount()
               && this.getInProgressTaskCount() < this.getRequestRateLimit()
               && this.pendingTasksSemaphore.tryAcquire()
         ) {
            if (!this.rateLimiter.tryAcquire()) {
               this.pendingTasksSemaphore.release();
               break;
            }

            this.sendNextRequest(targetPos);
         }

         return true;
      } else {
         return false;
      }
   }

   private void sendNextRequest(DhBlockPos2D targetPos) {
      Entry<Long, AbstractFullDataNetworkRequestQueue.NetRequestTask> nearestMapEntry = this.waitingTasksBySectionPos
         .entrySet()
         .stream()
         .filter(task -> task.getValue().networkDataSourceFuture == null)
         .min(Comparator.comparingInt(mapEntry -> DhSectionPos.getChebyshevSignedBlockDistance(mapEntry.getKey(), targetPos)))
         .orElse(null);
      if (nearestMapEntry == null) {
         this.pendingTasksSemaphore.release();
      } else {
         long requestPos = nearestMapEntry.getKey();
         AbstractFullDataNetworkRequestQueue.NetRequestTask requestTask = nearestMapEntry.getValue();
         if (!this.sectionInAllowedGenerationRadius(requestPos, targetPos)) {
            requestTask.future.cancel(false);
            this.pendingTasksSemaphore.release();
         } else if (!this.onBeforeRequest(requestPos, requestTask.future)) {
            this.pendingTasksSemaphore.release();
         } else {
            Long offsetEntryTimestamp = requestTask.updateTimestamp != null ? requestTask.updateTimestamp + this.networkState.getServerTimeOffset() : null;
            CompletableFuture<FullDataSourceResponseMessage> dataSourceNetworkFuture = this.networkState
               .getSession()
               .sendRequest(
                  new FullDataSourceRequestMessage(this.level.getLevelWrapper(), requestPos, offsetEntryTimestamp), FullDataSourceResponseMessage.class
               );
            requestTask.networkDataSourceFuture = dataSourceNetworkFuture;
            Executor networkCompressionExecutor = ThreadPoolUtil.getNetworkCompressionExecutor();
            if (networkCompressionExecutor != null) {
               dataSourceNetworkFuture.handleAsync((response, throwable) -> {
                  this.handleNetResponse(requestTask, response, throwable);
                  return null;
               }, networkCompressionExecutor);
            }
         }
      }
   }

   private void handleNetResponse(AbstractFullDataNetworkRequestQueue.NetRequestTask requestTask, FullDataSourceResponseMessage response, Throwable throwable) {
      this.pendingTasksSemaphore.release();

      try {
         if (throwable != null) {
            throw throwable;
         }

         if (response.payload == null) {
            LodUtil.assertTrue(this.changedOnly, "Received empty data source response for not changes-only request");
            return;
         }

         FullDataSourceV2DTO dataSourceDto = this.networkState.fullDataPayloadReceiver.decodeDataSource(response.payload);

         try {
            dataSourceDto.applyToChildren = DhSectionPos.getDetailLevel(dataSourceDto.pos) > 6;
            dataSourceDto.applyToParent = DhSectionPos.getDetailLevel(dataSourceDto.pos) < 18;
            this.level.updateBeaconBeamsForSectionPos(dataSourceDto.pos, response.payload.beaconBeams);
            FullDataSourceV2 fullDataSource = dataSourceDto.createDataSource(this.level.getLevelWrapper(), null);
            requestTask.future.complete(DataSourceRetrievalResult.CreateSuccess(dataSourceDto.pos, fullDataSource));
         } catch (Throwable var8) {
            if (dataSourceDto != null) {
               try {
                  dataSourceDto.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (dataSourceDto != null) {
            dataSourceDto.close();
         }
      } catch (SectionRequiresSplittingException var9) {
         requestTask.future.complete(DataSourceRetrievalResult.CreateSplit());
      } catch (CancellationException | SessionClosedException var10) {
         requestTask.future.cancel(false);
      } catch (RequestRejectedException var11) {
         LOGGER.info("Request rejected by the server, message: [" + var11.getMessage() + "].");
         requestTask.future.completeExceptionally(var11);
      } catch (RateLimitedException var12) {
         LOGGER.info("Rate limited by server, re-queueing task [" + DhSectionPos.toString(requestTask.pos) + "], message: [" + var12.getMessage() + "].");
         this.rateLimiter.acquireAll();
         requestTask.networkDataSourceFuture = null;
      } catch (RequestOutOfRangeException var13) {
         LOGGER.debug("Out of range, re-queueing task [" + DhSectionPos.toString(requestTask.pos) + "], message: [" + var13.getMessage() + "].");
         requestTask.networkDataSourceFuture = null;
      } catch (Throwable var14) {
         requestTask.retryAttempts--;
         LOGGER.error(
            "Unexpected error: [" + var14.getMessage() + "] while fetching full data source, attempts left: [" + requestTask.retryAttempts + "] / [" + 3 + "]",
            var14
         );
         if (requestTask.retryAttempts > 0) {
            requestTask.networkDataSourceFuture = null;
         } else {
            requestTask.future.completeExceptionally(var14);
         }
      }
   }

   public void removeRetrievalRequestIf(DhSectionPos.ICancelablePrimitiveLongConsumer removeIf) {
      Iterator<Entry<Long, AbstractFullDataNetworkRequestQueue.NetRequestTask>> farestTaskIterator = this.waitingTasksBySectionPos
         .entrySet()
         .stream()
         .sorted(Comparator.<Entry<Long, AbstractFullDataNetworkRequestQueue.NetRequestTask>>comparingInt(entryx -> {
            Long posx = (Long)entryx.getKey();
            DhBlockPos2D targetPos = this.level.getTargetPosForGeneration();
            return DhSectionPos.getChebyshevSignedBlockDistance(posx, targetPos);
         }).reversed())
         .iterator();

      while (farestTaskIterator.hasNext()) {
         Entry<Long, AbstractFullDataNetworkRequestQueue.NetRequestTask> mapEntry = farestTaskIterator.next();
         long pos = mapEntry.getKey();
         AbstractFullDataNetworkRequestQueue.NetRequestTask entry = mapEntry.getValue();
         if (removeIf.accept(pos)) {
            if (entry.networkDataSourceFuture != null) {
               entry.networkDataSourceFuture.cancel(false);
            }

            entry.future.cancel(false);
         }
      }
   }

   public void addDebugMenuStringsToList(List<String> messageList) {
      messageList.add(this.getQueueName() + " [" + this.level.getClientLevelWrapper().getDhIdentifier() + "]");
      messageList.add(
         "Requests: "
            + this.finishedRequests
            + " / "
            + (this.getWaitingTaskCount() + this.finishedRequests.get())
            + " (failed: "
            + this.failedRequests
            + ", rate limit: "
            + this.getRequestRateLimit()
            + ")"
      );
   }

   public int getWaitingTaskCount() {
      return this.waitingTasksBySectionPos.size();
   }

   public int getInProgressTaskCount() {
      return 32767 - this.pendingTasksSemaphore.availablePermits();
   }

   public CompletableFuture<Void> startClosingAsync(boolean alsoInterruptRunning) {
      return this.closingFuture = CompletableFuture.runAsync(
         () -> {
            Stopwatch stopwatch = Stopwatch.createStarted();

            do {
               for (AbstractFullDataNetworkRequestQueue.NetRequestTask entry : this.waitingTasksBySectionPos.values()) {
                  entry.future.cancel(alsoInterruptRunning);
                  if (entry.networkDataSourceFuture != null && entry.networkDataSourceFuture.cancel(alsoInterruptRunning)) {
                     this.pendingTasksSemaphore.release();
                  }
               }
            } while (!this.pendingTasksSemaphore.tryAcquire(32767) && stopwatch.elapsed(TimeUnit.SECONDS) < 5L);

            if (stopwatch.elapsed(TimeUnit.SECONDS) >= 5L) {
               LOGGER.warn(
                  "The request queue ["
                     + this.getQueueName()
                     + "] for level ["
                     + this.level.getLevelWrapper()
                     + "] did not shutdown in ["
                     + 5L
                     + "] seconds. Some unfinished tasks might be left hanging."
               );
            }
         }
      );
   }

   @Override
   public void close() {
      DEBUG_RENDERER.unregister(this, this.showDebugWireframeConfig);
   }

   @Override
   public void debugRender(AbstractDebugWireframeRenderer renderer) {
      if (MC_CLIENT.getWrappedClientLevel() == this.level.getClientLevelWrapper()) {
         DhBlockPos2D targetPos = this.level.getTargetPosForGeneration();

         for (Entry<Long, AbstractFullDataNetworkRequestQueue.NetRequestTask> mapEntry : this.waitingTasksBySectionPos.entrySet()) {
            long pos = mapEntry.getKey();
            AbstractFullDataNetworkRequestQueue.NetRequestTask task = mapEntry.getValue();
            Color color;
            if (task.networkDataSourceFuture != null) {
               color = Color.RED;
            } else {
               boolean taskInAllowedGenRadius = this.sectionInAllowedGenerationRadius(pos, targetPos);
               if (taskInAllowedGenRadius) {
                  color = Color.GRAY;
               } else {
                  color = Color.DARK_GRAY;
               }
            }

            renderer.renderBox(new AbstractDebugWireframeRenderer.Box(pos, -32.0F, 64.0F, 0.05F, color));
         }
      }
   }

   protected static class NetRequestTask {
      public final long pos;
      public final CompletableFuture<DataSourceRetrievalResult> future = new CompletableFuture<>();
      @Nullable
      public final Long updateTimestamp;
      @CheckForNull
      public CompletableFuture<FullDataSourceResponseMessage> networkDataSourceFuture;
      public int retryAttempts = 3;

      public NetRequestTask(long pos, @Nullable Long updateTimestamp) {
         this.pos = pos;
         this.updateTimestamp = updateTimestamp;
      }
   }
}
