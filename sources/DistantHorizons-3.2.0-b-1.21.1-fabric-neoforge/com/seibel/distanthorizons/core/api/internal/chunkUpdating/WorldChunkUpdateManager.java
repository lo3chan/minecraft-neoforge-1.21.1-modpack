package com.seibel.distanthorizons.core.api.internal.chunkUpdating;

import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.world.AbstractDhWorld;
import com.seibel.distanthorizons.core.world.EWorldEnvironment;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.Nullable;

public class WorldChunkUpdateManager {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final WorldChunkUpdateManager INSTANCE = new WorldChunkUpdateManager();
   public static final Set<String> LOGGED_GET_ERROR_MESSAGES = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final ConcurrentHashMap<ILevelWrapper, ChunkUpdateQueueManager> updateQueueByLevelWrapper = new ConcurrentHashMap<>();

   private WorldChunkUpdateManager() {
   }

   @Nullable
   public ChunkUpdateQueueManager getByLevelWrapper(ILevelWrapper levelWrapper) {
      AbstractDhWorld world = SharedApi.getAbstractDhWorld();
      if (world == null) {
         return null;
      } else if (world.environment == EWorldEnvironment.CLIENT_ONLY && !(levelWrapper instanceof IClientLevelWrapper)) {
         return null;
      } else if ((world.environment == EWorldEnvironment.SERVER_ONLY || world.environment == EWorldEnvironment.CLIENT_SERVER)
         && !(levelWrapper instanceof IServerLevelWrapper)) {
         return null;
      } else {
         ChunkUpdateQueueManager queueManager = this.updateQueueByLevelWrapper.get(levelWrapper);
         return queueManager != null
            ? queueManager
            : this.updateQueueByLevelWrapper
               .compute(
                  levelWrapper,
                  (newLevelWrapper, oldQueueManager) -> (ChunkUpdateQueueManager)(oldQueueManager != null ? oldQueueManager : new ChunkUpdateQueueManager())
               );
      }
   }

   public void processEachQueue() {
      this.updateQueueByLevelWrapper.forEach((levelWrapper, updateManager) -> updateManager.processQueue());
   }

   public int getTotalQueuedCount() {
      AtomicInteger queueCountRef = new AtomicInteger(0);
      this.updateQueueByLevelWrapper.forEach((levelWrapper, updateManager) -> queueCountRef.addAndGet(updateManager.getQueuedCount()));
      return queueCountRef.get();
   }

   public void clear() {
      this.updateQueueByLevelWrapper.clear();
   }

   public ArrayList<String> getDebugMenuString() {
      ArrayList<String> stringList = new ArrayList<>();
      stringList.add("");
      AtomicInteger totalQueueCountRef = new AtomicInteger(0);
      AtomicInteger activeQueueCountRef = new AtomicInteger(0);
      this.updateQueueByLevelWrapper.forEach((levelWrapper, updateManager) -> {
         if (!updateManager.updateQueuesEmpty()) {
            updateManager.lastMsTimeShownActiveInF3Screen = System.currentTimeMillis();
            activeQueueCountRef.incrementAndGet();
         }

         long timeSinceQueueLastShownActiveMs = System.currentTimeMillis() - updateManager.lastMsTimeShownActiveInF3Screen;
         if (timeSinceQueueLastShownActiveMs < 4000L) {
            stringList.add(levelWrapper.getDimensionName() + ": " + updateManager.getDebugMenuString());
         }

         totalQueueCountRef.incrementAndGet();
      });
      stringList.set(0, "Chunk Update Queues: " + activeQueueCountRef.get() + "/" + totalQueueCountRef.get());
      return stringList;
   }
}
