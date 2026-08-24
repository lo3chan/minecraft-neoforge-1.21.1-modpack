package com.seibel.distanthorizons.core.generation.queues;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiDistantGeneratorProgressDisplayLocation;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.level.DhServerLevel;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.util.FormatUtil;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.util.objects.RollingAverage;
import com.seibel.distanthorizons.core.util.threading.PriorityTaskPicker;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class AbstractLodRequestState {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static long firstProgressMessageSentMs = 0L;
   public final IDhLevel dhLevel;
   public final IFullDataSourceRetrievalQueue retrievalQueue;
   private final ThreadPoolExecutor progressUpdaterThread = ThreadUtil.makeSingleDaemonThreadPool("World Gen Progress Updater");
   private boolean progressUpdateThreadRunning = false;

   public AbstractLodRequestState(IDhLevel dhLevel, IFullDataSourceRetrievalQueue retrievalQueue) {
      this.dhLevel = dhLevel;
      this.retrievalQueue = retrievalQueue;
   }

   public void startRequestQueueAndSetTargetPos(DhBlockPos2D targetPosForRequest) {
      this.retrievalQueue.startAndSetTargetPos(targetPosForRequest);
      this.startProgressUpdateThread();
   }

   private void startProgressUpdateThread() {
      if (!this.progressUpdateThreadRunning) {
         this.progressUpdateThreadRunning = true;
         this.progressUpdaterThread.execute(() -> {
            while (this.progressUpdateThreadRunning) {
               try {
                  this.sendRetrievalProgress();
                  int sleepTimeInSec = Config.Common.WorldGenerator.generationProgressDisplayIntervalInSeconds.get();
                  Thread.sleep(sleepTimeInSec * 1000L);
               } catch (Exception var2) {
                  LOGGER.error("Unexpected issue displaying chunk retrieval progress [" + var2.getMessage() + "].", var2);
               }
            }
         });
      }
   }

   private void sendRetrievalProgress() {
      int remainingChunkCount = this.retrievalQueue.getRetrievalEstimatedRemainingChunkCount();
      remainingChunkCount += this.retrievalQueue.getQueuedChunkCount();
      String remainingChunkCountStr = F3Screen.NUMBER_FORMAT.format((long)remainingChunkCount);
      String message = "DH is " + this.retrievalQueue.getRetrievalTypeName() + ". ";
      if (this.dhLevel.getClass() == DhServerLevel.class) {
         message = message + "For " + this.dhLevel.getLevelWrapper().getDimensionName() + " ";
      }

      message = message + remainingChunkCountStr + " left.";
      int msToShowDisableInstructions = Config.Common.WorldGenerator.generationProgressDisableMessageDisplayTimeInSeconds.get() * 1000;
      if (msToShowDisableInstructions > 0) {
         long timeSinceFirstMessageInMs = System.currentTimeMillis() - firstProgressMessageSentMs;
         if (firstProgressMessageSentMs == 0L || timeSinceFirstMessageInMs < msToShowDisableInstructions) {
            message = message + " This message can be hidden in the DH config.";
         }
      }

      double chunksPerSec = this.getEstimatedChunksPerSecond();
      if (chunksPerSec > 0.0) {
         long estimatedRemainingTime = (long)(remainingChunkCount / chunksPerSec);
         message = message + " ETA: " + FormatUtil.formatEta(Duration.ofSeconds(estimatedRemainingTime));
         if (Config.Common.WorldGenerator.generationProgressIncludeChunksPerSecond.get()) {
            message = message + " at " + F3Screen.NUMBER_FORMAT.format(chunksPerSec) + " chunks/sec";
         }
      }

      if (remainingChunkCount != 0) {
         EDhApiDistantGeneratorProgressDisplayLocation displayLocation = Config.Common.WorldGenerator.showGenerationProgress.get();
         if (displayLocation == EDhApiDistantGeneratorProgressDisplayLocation.OVERLAY) {
            ClientApi.INSTANCE.showOverlayMessageNextFrame(message);
         } else if (displayLocation == EDhApiDistantGeneratorProgressDisplayLocation.CHAT) {
            ClientApi.INSTANCE.showChatMessageNextFrame(message);
         } else if (displayLocation == EDhApiDistantGeneratorProgressDisplayLocation.LOG) {
            LOGGER.info(message);
         }

         if (firstProgressMessageSentMs == 0L) {
            firstProgressMessageSentMs = System.currentTimeMillis();
         }
      }
   }

   public double getEstimatedChunksPerSecond() {
      RollingAverage avg = this.retrievalQueue.getRollingAverageChunkGenTimeInMs();
      if (avg == null) {
         return -1.0;
      } else {
         PriorityTaskPicker.Executor executor = ThreadPoolUtil.getWorldGenExecutor();
         int threadCount = 1;
         if (executor != null) {
            threadCount = executor.getPoolSize();
         }

         double chunksPerSecond = 1.0 / avg.getAverage() * 1000.0;
         return threadCount * chunksPerSecond;
      }
   }

   public CompletableFuture<Void> closeAsync(boolean doInterrupt) {
      this.progressUpdateThreadRunning = false;
      return this.retrievalQueue.startClosingAsync(true, doInterrupt).exceptionally(e -> {
         LOGGER.error("Error during first stage of generation queue shutdown, Error: [" + e.getMessage() + "].", e);
         return null;
      }).thenRun(this.retrievalQueue::close).exceptionally(e -> {
         LOGGER.error("Error during second stage of generation queue shutdown, Error: [" + e.getMessage() + "].", e);
         return null;
      });
   }
}
