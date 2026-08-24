package com.seibel.distanthorizons.core.generation.queues;

import com.seibel.distanthorizons.core.file.fullDatafile.GeneratedFullDataSourceProvider;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.world.DhApiWorldProxy;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LodRequestModule implements Closeable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final IDhLevel level;
   private final GeneratedFullDataSourceProvider.IOnWorldGenCompleteListener onWorldGenCompleteListener;
   private final ThreadPoolExecutor tickerThread;
   private final GeneratedFullDataSourceProvider dataSourceProvider;
   private final Supplier<? extends AbstractLodRequestState> worldGenStateSupplier;
   private final AtomicReference<AbstractLodRequestState> lodRequestStateRef = new AtomicReference<>();

   public LodRequestModule(
      IDhLevel level,
      GeneratedFullDataSourceProvider.IOnWorldGenCompleteListener onWorldGenCompleteListener,
      GeneratedFullDataSourceProvider dataSourceProvider,
      Supplier<? extends AbstractLodRequestState> worldGenStateSupplier
   ) {
      this.level = level;
      this.onWorldGenCompleteListener = onWorldGenCompleteListener;
      this.dataSourceProvider = dataSourceProvider;
      this.worldGenStateSupplier = worldGenStateSupplier;
      String levelId = this.level.getLevelWrapper().getDhIdentifier();
      this.tickerThread = ThreadUtil.makeSingleDaemonThreadPool("Request Module Ticker [" + levelId + "]");
      this.tickerThread.execute(this::tickLoop);
   }

   private void tickLoop() {
      try {
         Thread.sleep(500L);

         while (!Thread.interrupted()) {
            try {
               Thread.sleep(20L);
               this.tick();
            } catch (InterruptedException var2) {
               throw var2;
            } catch (Exception var3) {
               LOGGER.error("Unexpected error in [" + LodRequestModule.class.getSimpleName() + "] tick loop, error: [" + var3.getMessage() + "].", var3);
            }
         }
      } catch (InterruptedException var4) {
      }
   }

   private void tick() {
      boolean shouldDoWorldGen = this.onWorldGenCompleteListener.shouldDoWorldGen();
      shouldDoWorldGen &= !DhApiWorldProxy.INSTANCE.tryGetReadOnly();
      boolean isWorldGenRunning = this.isWorldGenRunning();
      if (shouldDoWorldGen && !isWorldGenRunning) {
         this.startWorldGen(this.dataSourceProvider, this.worldGenStateSupplier.get());
      } else if (!shouldDoWorldGen && isWorldGenRunning) {
         this.stopWorldGen(this.dataSourceProvider);
      }

      if (this.isWorldGenRunning()) {
         AbstractLodRequestState lodRequestState = this.lodRequestStateRef.get();
         if (lodRequestState != null) {
            DhBlockPos2D targetPosForGeneration = this.onWorldGenCompleteListener.getTargetPosForGeneration();
            if (targetPosForGeneration != null) {
               lodRequestState.startRequestQueueAndSetTargetPos(targetPosForGeneration);
            }
         }
      }
   }

   public void startWorldGen(GeneratedFullDataSourceProvider dataFileHandler, AbstractLodRequestState newWgs) {
      if (!this.lodRequestStateRef.compareAndSet(null, newWgs)) {
         LOGGER.warn("Failed to start world gen due to concurrency");
         newWgs.closeAsync(false);
      }

      dataFileHandler.addWorldGenCompleteListener(this.onWorldGenCompleteListener);
      dataFileHandler.setWorldGenerationQueue(newWgs.retrievalQueue);
   }

   public void stopWorldGen(GeneratedFullDataSourceProvider dataFileHandler) {
      AbstractLodRequestState worldGenState = this.lodRequestStateRef.get();
      if (worldGenState == null) {
         LOGGER.warn("Attempted to stop world gen when it was not running");
      } else {
         while (!this.lodRequestStateRef.compareAndSet(worldGenState, null)) {
            worldGenState = this.lodRequestStateRef.get();
            if (worldGenState == null) {
               return;
            }
         }

         dataFileHandler.clearRetrievalQueue();
         worldGenState.closeAsync(true).join();
         dataFileHandler.removeWorldGenCompleteListener(this.onWorldGenCompleteListener);
      }
   }

   @Override
   public void close() {
      this.tickerThread.shutdownNow();
      AbstractLodRequestState worldGenState = this.lodRequestStateRef.get();
      if (worldGenState != null) {
         while (true) {
            if (!this.lodRequestStateRef.compareAndSet(worldGenState, null)) {
               worldGenState = this.lodRequestStateRef.get();
               if (worldGenState != null) {
                  continue;
               }
            }

            if (worldGenState != null) {
               worldGenState.closeAsync(true).join();
            }
            break;
         }
      }
   }

   public boolean isWorldGenRunning() {
      return this.lodRequestStateRef.get() != null;
   }

   public void addDebugMenuStringsToList(List<String> messageList) {
      AbstractLodRequestState worldGenState = this.lodRequestStateRef.get();
      if (worldGenState != null) {
         String waitingCountStr = F3Screen.NUMBER_FORMAT.format((long)worldGenState.retrievalQueue.getWaitingTaskCount());
         String inProgressCountStr = F3Screen.NUMBER_FORMAT.format((long)worldGenState.retrievalQueue.getInProgressTaskCount());
         String totalCountEstimateStr = F3Screen.NUMBER_FORMAT.format((long)worldGenState.retrievalQueue.getRetrievalEstimatedRemainingChunkCount());
         String message = "World Gen/Import Tasks: " + waitingCountStr + "/" + totalCountEstimateStr + " (in progress " + inProgressCountStr + ")";
         double chunksPerSec = worldGenState.getEstimatedChunksPerSecond();
         if (chunksPerSec > -1.0) {
            message = message + ", " + F3Screen.NUMBER_FORMAT.format(chunksPerSec) + " chunks/sec";
         }

         messageList.add(message);
         worldGenState.retrievalQueue.addDebugMenuStringsToList(messageList);
      }
   }
}
