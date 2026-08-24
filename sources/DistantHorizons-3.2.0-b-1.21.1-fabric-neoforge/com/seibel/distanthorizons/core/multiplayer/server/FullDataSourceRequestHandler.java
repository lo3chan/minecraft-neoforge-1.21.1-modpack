package com.seibel.distanthorizons.core.multiplayer.server;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorMode;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.file.fullDatafile.GeneratedFullDataSourceProvider;
import com.seibel.distanthorizons.core.level.AbstractDhServerLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.multiplayer.fullData.FullDataPayload;
import com.seibel.distanthorizons.core.network.exceptions.RequestRejectedException;
import com.seibel.distanthorizons.core.network.exceptions.SectionRequiresSplittingException;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceRequestMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceResponseMessage;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class FullDataSourceRequestHandler implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logNetworkEventToFile).build();
   private final AbstractDhServerLevel serverLevel;
   private final ThreadPoolExecutor tickerThread;
   private final ConcurrentMap<Long, DataSourceRequestGroup> requestGroupsByPos = new ConcurrentHashMap<>();
   private final ConcurrentMap<Long, DataSourceRequestGroup> requestGroupsByFutureId = new ConcurrentHashMap<>();

   private String getLevelIdentifier() {
      return this.serverLevel.getLevelWrapper().getDhIdentifier();
   }

   private GeneratedFullDataSourceProvider fullDataSourceProvider() {
      return this.serverLevel.serverside.fullDataFileHandler;
   }

   private List<BeaconBeamDTO> getAllBeamsForPos(long pos) {
      return this.serverLevel.beaconBeamRepo.getAllBeamsForPos(pos);
   }

   public FullDataSourceRequestHandler(AbstractDhServerLevel serverLevel) {
      this.serverLevel = serverLevel;
      String levelId = this.serverLevel.getServerLevelWrapper().getDhIdentifier();
      this.tickerThread = ThreadUtil.makeSingleDaemonThreadPool("DataSource Request Ticker [" + levelId + "]");
      this.tickerThread.execute(this::tickLoop);
   }

   public void queueLodSyncForRequestMessage(
      ServerPlayerState serverPlayerState, FullDataSourceRequestMessage message, ServerPlayerState.RateLimiterSet rateLimiterSet
   ) {
      if (!serverPlayerState.sessionConfig.getSynchronizeOnLoad()) {
         message.sendResponse(new RequestRejectedException("Operation is disabled in config."));
      } else if (rateLimiterSet.syncOnLoginRateLimiter.tryAcquire(message)) {
         AbstractExecutorService fileHandlerExecutor = ThreadPoolUtil.getFileHandlerExecutor();
         if (fileHandlerExecutor == null) {
            LOGGER.warn("Unable to send FullDataSourceResponseMessage - getFileHandlerExecutor() is null");
         } else {
            AbstractExecutorService networkCompressionExecutor = ThreadPoolUtil.getNetworkCompressionExecutor();
            if (networkCompressionExecutor == null) {
               LOGGER.warn("Unable to send FullDataSourceResponseMessage - getNetworkCompressionExecutor() is null");
            } else {
               CompletableFuture<FullDataSourceV2> getServerDatasourceFuture = CompletableFuture.supplyAsync(
                  () -> {
                     try {
                        long clientTimestamp = message.clientTimestamp != null ? message.clientTimestamp : -1L;
                        Long serverTimestamp = this.fullDataSourceProvider().getTimestampForPos(message.sectionPos);
                        if (serverTimestamp != null && serverTimestamp > clientTimestamp) {
                           return this.fullDataSourceProvider().get(message.sectionPos);
                        } else {
                           rateLimiterSet.syncOnLoginRateLimiter.release();
                           message.sendResponse(new FullDataSourceResponseMessage(null));
                           return null;
                        }
                     } catch (Exception var6x) {
                        LOGGER.error(
                           "Unexpected issue getting server-side LOD for request at pos ["
                              + DhSectionPos.toString(message.sectionPos)
                              + "], error: ["
                              + var6x.getMessage()
                              + "].",
                           var6x
                        );
                        return null;
                     }
                  },
                  fileHandlerExecutor
               );
               getServerDatasourceFuture.thenAcceptAsync(
                  fullDataSource -> {
                     try {
                        if (fullDataSource == null) {
                           return;
                        }

                        FullDataPayload payload = new FullDataPayload(fullDataSource, this.getAllBeamsForPos(message.sectionPos));
                        fullDataSource.close();
                        serverPlayerState.fullDataPayloadSender.sendInChunks(payload, () -> {
                           message.sendResponse(new FullDataSourceResponseMessage(payload));
                           rateLimiterSet.syncOnLoginRateLimiter.release();
                        });
                     } catch (Exception var6x) {
                        LOGGER.error(
                           "Unexpected issue sending request for pos [" + DhSectionPos.toString(message.sectionPos) + "], error: [" + var6x.getMessage() + "].",
                           var6x
                        );
                     }
                  },
                  networkCompressionExecutor
               );
            }
         }
      }
   }

   public void queueWorldGenForRequestMessage(
      ServerPlayerState serverPlayerState, FullDataSourceRequestMessage message, ServerPlayerState.RateLimiterSet rateLimiterSet
   ) {
      if (!serverPlayerState.sessionConfig.isDistantGenerationEnabled()) {
         message.sendResponse(new RequestRejectedException("Operation is disabled in config."));
      } else if (rateLimiterSet.generationRequestRateLimiter.tryAcquire(message)) {
         this.doQueueWorldGenForRequestMessage(new DataSourceRequestGroup.RequestData(serverPlayerState, message, rateLimiterSet));
      }
   }

   private void doQueueWorldGenForRequestMessage(DataSourceRequestGroup.RequestData requestData) {
      while (true) {
         AtomicBoolean createdNewGroup = new AtomicBoolean(false);
         DataSourceRequestGroup requestGroup = this.requestGroupsByPos
            .computeIfAbsent(
               requestData.sectionPos(),
               pos -> {
                  DataSourceRequestGroup newGroup = new DataSourceRequestGroup(pos);

                  try {
                     newGroup.tryAddRequest(requestData);
                     createdNewGroup.set(true);
                     this.tryFulfillDataSourceRequestGroup(newGroup, pos);
                     LOGGER.debug("[" + this.getLevelIdentifier() + "] Created request group for pos [" + DhSectionPos.toString(pos) + "].");
                     return newGroup;
                  } catch (Exception var6) {
                     LOGGER.error(
                        "Unable to queue request for pos: [" + DhSectionPos.toString(requestData.sectionPos()) + "], error: [" + var6.getMessage() + "].", var6
                     );
                     return newGroup;
                  }
               }
            );
         if (createdNewGroup.get() || requestGroup.tryAddRequest(requestData)) {
            this.requestGroupsByFutureId.put(requestData.futureId(), requestGroup);
            return;
         }

         Thread.yield();
      }
   }

   public void cancelRequest(long requestId) {
      DataSourceRequestGroup requestGroup = this.requestGroupsByFutureId.remove(requestId);
      if (requestGroup != null) {
         DataSourceRequestGroup.RequestData removedRequest = requestGroup.tryRemoveRequest(requestId, requestsToTransfer -> {
            LOGGER.debug("[" + this.getLevelIdentifier() + "] Cancelled request group [" + DhSectionPos.toString(requestGroup.pos) + "].");
            this.requestGroupsByPos.remove(requestGroup.pos);
            if (!requestsToTransfer.isEmpty()) {
               for (DataSourceRequestGroup.RequestData requestToTransfer : requestsToTransfer) {
                  this.doQueueWorldGenForRequestMessage(requestToTransfer);
               }
            } else {
               this.fullDataSourceProvider().removeRetrievalRequestIf(pos -> pos == requestGroup.pos);
            }
         });
         if (removedRequest != null) {
            removedRequest.rateLimiterSet.generationRequestRateLimiter.release();
         }
      }
   }

   private void tryFulfillDataSourceRequestGroup(DataSourceRequestGroup requestGroup, long pos) {
      GeneratedFullDataSourceProvider provider = this.fullDataSourceProvider();
      provider.getAsync(pos)
         .thenAccept(
            fullDataSource -> {
               if (provider.generationStepsAreFullyGenerated(fullDataSource.columnGenerationSteps)) {
                  requestGroup.fullDataSource = fullDataSource;
               } else {
                  fullDataSource.close();
                  if (DhSectionPos.getDetailLevel(pos)
                     > (
                        Config.Common.WorldGenerator.distantGeneratorMode.get() == EDhApiDistantGeneratorMode.INTERNAL_SERVER
                           ? 6
                           : this.serverLevel.serverside.fullDataFileHandler.lowestDataDetailLevel()
                     )) {
                     this.requestGroupsByPos.remove(pos);
                     if (!requestGroup.tryClose()) {
                        return;
                     }

                     for (DataSourceRequestGroup.RequestData requestData : requestGroup.requestMessages.values()) {
                        this.requestGroupsByFutureId.remove(requestData.futureId());
                        requestData.rateLimiterSet.generationRequestRateLimiter.release();
                        requestData.message.sendResponse(new SectionRequiresSplittingException());
                     }
                  } else if (requestGroup.isWorldGenTaskComplete()) {
                     this.tryFulfillDataSourceRequestGroup(requestGroup, pos);
                  } else {
                     this.fullDataSourceProvider().queuePositionForRetrieval(pos);
                  }
               }
            }
         );
   }

   public void onWorldGenTaskComplete(long pos) {
      DataSourceRequestGroup requestGroup = this.requestGroupsByPos.get(pos);
      if (requestGroup != null) {
         requestGroup.markWorldGenTaskComplete();
         this.tryFulfillDataSourceRequestGroup(requestGroup, pos);
      }
   }

   private void tickLoop() {
      try {
         while (!Thread.interrupted()) {
            Thread.sleep(20L);
            this.tick();
         }
      } catch (InterruptedException var2) {
      }
   }

   private void tick() {
      for (Entry<Long, DataSourceRequestGroup> entry : this.requestGroupsByPos.entrySet()) {
         DataSourceRequestGroup requestGroup = entry.getValue();
         if (requestGroup.fullDataSource != null) {
            LOGGER.debug("[" + this.getLevelIdentifier() + "] Fulfilled request group [" + DhSectionPos.toString(entry.getKey()) + "]");
            this.requestGroupsByPos.remove(entry.getKey());
            if (requestGroup.tryClose()) {
               AbstractExecutorService executor = ThreadPoolUtil.getNetworkCompressionExecutor();
               if (executor == null) {
                  LOGGER.warn("Unable to send FullDataSourceResponseMessage - getNetworkCompressionExecutor() is null");
               } else {
                  CompletableFuture.runAsync(() -> {
                     FullDataPayload payload = new FullDataPayload(requestGroup.fullDataSource, this.getAllBeamsForPos(entry.getKey()));
                     requestGroup.fullDataSource.close();

                     for (DataSourceRequestGroup.RequestData requestData : requestGroup.requestMessages.values()) {
                        this.requestGroupsByFutureId.remove(requestData.futureId());
                        requestData.serverPlayerState.fullDataPayloadSender.sendInChunks(payload, () -> {
                           requestData.message.sendResponse(new FullDataSourceResponseMessage(payload));
                           requestData.rateLimiterSet.generationRequestRateLimiter.release();
                        });
                     }
                  }, executor);
               }
            }
         }
      }
   }

   @Override
   public void close() {
      this.tickerThread.shutdownNow();
   }
}
