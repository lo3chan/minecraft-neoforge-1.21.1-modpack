package com.seibel.distanthorizons.core.multiplayer.client;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.generation.tasks.DataSourceRetrievalResult;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import java.util.concurrent.CompletableFuture;

public class SyncOnLoadRequestQueue extends AbstractFullDataNetworkRequestQueue {
   public SyncOnLoadRequestQueue(DhClientLevel level, ClientNetworkState networkState) {
      super(networkState, level, true, Config.Client.Advanced.Debugging.DebugWireframe.showNetworkSyncOnLoadQueue);
   }

   @Override
   protected int getRequestRateLimit() {
      return this.networkState.sessionConfig.getSyncOnLoginRateLimit();
   }

   @Override
   protected boolean sectionInAllowedGenerationRadius(long sectionPos, DhBlockPos2D targetPos) {
      return DhSectionPos.getChebyshevSignedBlockDistance(sectionPos, targetPos) <= this.networkState.sessionConfig.getMaxSyncOnLoadDistance() * 16;
   }

   @Override
   protected boolean onBeforeRequest(long sectionPos, CompletableFuture<DataSourceRetrievalResult> future) {
      return true;
   }

   @Override
   protected String getQueueName() {
      return "Sync On Login Queue";
   }

   @Override
   public boolean tick(DhBlockPos2D targetPos) {
      return !this.networkState.sessionConfig.getSynchronizeOnLoad() ? false : super.tick(targetPos);
   }
}
