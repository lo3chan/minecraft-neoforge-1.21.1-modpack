package com.seibel.distanthorizons.core.generation.queues;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalResult;
import com.seibel.distanthorizons.core.generation.tasks.ERetrievalResultState;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.multiplayer.client.AbstractFullDataNetworkRequestQueue;
import com.seibel.distanthorizons.core.multiplayer.client.ClientNetworkState;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.renderer.IDebugRenderable;
import com.seibel.distanthorizons.core.util.WorldGenUtil;
import com.seibel.distanthorizons.core.util.objects.RollingAverage;
import java.util.concurrent.CompletableFuture;

public class RemoteWorldRetrievalQueue extends AbstractFullDataNetworkRequestQueue implements IFullDataSourceRetrievalQueue, IDebugRenderable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private int estimatedRemainingTaskCount;
   private int estimatedTotalChunkCount;
   private final RollingAverage rollingAverageChunkGenTimeInMs = new RollingAverage(1000);

   @Override
   public RollingAverage getRollingAverageChunkGenTimeInMs() {
      return this.rollingAverageChunkGenTimeInMs;
   }

   public RemoteWorldRetrievalQueue(ClientNetworkState networkState, DhClientLevel level) {
      super(networkState, level, false, Config.Client.Advanced.Debugging.DebugWireframe.showWorldGenQueue);
   }

   @Override
   public void startAndSetTargetPos(DhBlockPos2D targetPos) {
      super.tick(targetPos);
   }

   @Override
   public byte lowestDataDetail() {
      return 15;
   }

   @Override
   public byte highestDataDetail() {
      return 0;
   }

   @Override
   public String getRetrievalTypeName() {
      return "downloading LODs";
   }

   @Override
   public CompletableFuture<DataSourceRetrievalResult> submitRetrievalTask(long sectionPos, byte requiredDataDetail) {
      long generationStartMsTime = System.currentTimeMillis();
      CompletableFuture<DataSourceRetrievalResult> future = super.submitRequest(sectionPos, null);
      future.thenAccept(result -> {
         if (result.state == ERetrievalResultState.SUCCESS) {
            long totalGenTimeInMs = System.currentTimeMillis() - generationStartMsTime;
            int chunkWidth = DhSectionPos.getChunkWidth(sectionPos);
            int chunkCount = chunkWidth * chunkWidth;
            double timePerChunk = (double)totalGenTimeInMs / chunkCount;
            this.rollingAverageChunkGenTimeInMs.add(timePerChunk);
         }
      });
      return future;
   }

   @Override
   public CompletableFuture<Void> startClosingAsync(boolean cancelCurrentGeneration, boolean alsoInterruptRunning) {
      return super.startClosingAsync(alsoInterruptRunning);
   }

   @Override
   protected int getRequestRateLimit() {
      return this.networkState.sessionConfig.getGenerationRequestRateLimit();
   }

   @Override
   protected boolean sectionInAllowedGenerationRadius(long sectionPos, DhBlockPos2D targetPos) {
      if (this.networkState.sessionConfig.getGenerationMaxChunkRadius() > 0) {
         boolean posInRange = WorldGenUtil.isPosInWorldGenRange(
            sectionPos,
            this.networkState.sessionConfig.getGenerationCenterChunkX(),
            this.networkState.sessionConfig.getGenerationCenterChunkZ(),
            this.networkState.sessionConfig.getGenerationMaxChunkRadius()
         );
         if (!posInRange) {
            return false;
         }
      }

      return DhSectionPos.getChebyshevSignedBlockDistance(sectionPos, targetPos) <= this.networkState.sessionConfig.getMaxGenerationRequestDistance() * 16;
   }

   @Override
   protected boolean onBeforeRequest(long sectionPos, CompletableFuture<DataSourceRetrievalResult> future) {
      if (!Config.Server.Experimental.enableNSizedGeneration.get() && DhSectionPos.getDetailLevel(sectionPos) > 6) {
         future.complete(DataSourceRetrievalResult.CreateSplit());
         return false;
      } else {
         return true;
      }
   }

   @Override
   protected String getQueueName() {
      return "World Remote Generation Queue";
   }

   @Override
   public int getEstimatedRemainingTaskCount() {
      return this.estimatedRemainingTaskCount;
   }

   @Override
   public void setEstimatedRemainingTaskCount(int newEstimate) {
      this.estimatedRemainingTaskCount = newEstimate;
   }

   @Override
   public int getRetrievalEstimatedRemainingChunkCount() {
      return this.estimatedTotalChunkCount;
   }

   @Override
   public void setRetrievalEstimatedRemainingChunkCount(int newEstimate) {
      this.estimatedTotalChunkCount = newEstimate;
   }

   @Override
   public int getQueuedChunkCount() {
      return 0;
   }
}
