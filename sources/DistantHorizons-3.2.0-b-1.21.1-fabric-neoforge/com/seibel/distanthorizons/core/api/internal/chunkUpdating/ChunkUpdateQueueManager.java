package com.seibel.distanthorizons.core.api.internal.chunkUpdating;

import com.google.common.cache.CacheBuilder;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.generation.DhLightingEngine;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.world.EWorldEnvironment;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;

public class ChunkUpdateQueueManager {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftSharedWrapper MC_SHARED = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);
   public static final int MAX_UPDATING_CHUNK_COUNT_PER_THREAD_AND_PLAYER = 1000;
   public static final int MIN_MS_BETWEEN_OVERLOADED_LOG_MESSAGE = 30000;
   private final Set<DhChunkPos> ignoredChunkPosSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private static long lastOverloadedLogMessageMsTime = 0L;
   public final ChunkPosQueue updateQueue;
   public final ChunkPosQueue preUpdateQueue;
   public final ConcurrentMap<DhChunkPos, IChunkWrapper> queuedChunkWrapperByChunkPos = CacheBuilder.newBuilder()
      .expireAfterWrite(20L, TimeUnit.SECONDS)
      .build()
      .asMap();
   public int maxSize = 500;
   public long lastMsTimeShownActiveInF3Screen = System.currentTimeMillis();

   public ChunkUpdateQueueManager() {
      this.updateQueue = new ChunkPosQueue();
      this.preUpdateQueue = new ChunkPosQueue();
   }

   public boolean contains(DhChunkPos pos) {
      return this.updateQueue.contains(pos) || this.ignoredChunkPosSet.contains(pos) || this.preUpdateQueue.contains(pos);
   }

   public void clear() {
      this.updateQueue.clear();
      this.preUpdateQueue.clear();
      this.ignoredChunkPosSet.clear();
   }

   public int getQueuedCount() {
      return this.updateQueue.getQueuedCount() + this.preUpdateQueue.getQueuedCount();
   }

   public boolean updateQueuesEmpty() {
      return this.updateQueue.isEmpty() && this.preUpdateQueue.isEmpty();
   }

   public void addItemToPreUpdateQueue(DhChunkPos pos, ChunkUpdateData updateData) {
      this.addItemToQueue(pos, updateData, this.preUpdateQueue);
   }

   public void addItemToUpdateQueue(DhChunkPos pos, ChunkUpdateData updateData) {
      this.addItemToQueue(pos, updateData, this.updateQueue);
   }

   private void addItemToQueue(DhChunkPos pos, ChunkUpdateData updateData, ChunkPosQueue queue) {
      int remainingSlots = this.maxSize - this.getQueuedCount();
      if (remainingSlots <= 0) {
         ChunkUpdateData removedData = queue.popFurthest();
         if (removedData != null) {
            this.queuedChunkWrapperByChunkPos.remove(removedData.chunkWrapper.getChunkPos());
         }
      }

      queue.addItem(pos, updateData);
      this.queuedChunkWrapperByChunkPos.putIfAbsent(pos, updateData.chunkWrapper);
      remainingSlots = this.maxSize - this.getQueuedCount();
      if (remainingSlots <= 0) {
         this.sendOverloadMessage();
      }
   }

   private void sendOverloadMessage() {
      long msBetweenLastLog = System.currentTimeMillis() - lastOverloadedLogMessageMsTime;
      if (msBetweenLastLog >= 30000L) {
         lastOverloadedLogMessageMsTime = System.currentTimeMillis();
         String message = "§6Distant Horizons overloaded, too many chunks queued for LOD processing. §r\nThis may result in holes in your LODs. \nFix: move through the world slower, decrease your vanilla render distance, slow down your world pre-generator (IE Chunky), or increase the Distant Horizons' CPU thread counts. \nMax queue count ["
            + this.maxSize
            + "] (["
            + 1000
            + "] per thread+players).";
         boolean showWarningInChat = Config.Common.Logging.Warning.showUpdateQueueOverloadedChatWarning.get();
         if (showWarningInChat) {
            ClientApi.INSTANCE.showChatMessageNextFrame(message);
         }

         EWorldEnvironment environment = SharedApi.getEnvironment();
         if (showWarningInChat || environment == EWorldEnvironment.SERVER_ONLY) {
            LOGGER.warn(message);
         }
      }
   }

   @Nullable
   public IChunkWrapper tryGetChunk(DhChunkPos pos) {
      IChunkWrapper existingWrapper = this.queuedChunkWrapperByChunkPos.get(pos);
      return existingWrapper == null ? null : existingWrapper.copy();
   }

   public void addPosToIgnore(DhChunkPos chunkPos) {
      this.ignoredChunkPosSet.add(chunkPos);
   }

   public void removePosToIgnore(DhChunkPos chunkPos) {
      this.ignoredChunkPosSet.remove(chunkPos);
   }

   public void processQueue() {
      int maxUpdateSizeMultiplier;
      if (MC_CLIENT != null && MC_CLIENT.playerExists()) {
         this.setCenter(MC_CLIENT.getPlayerChunkPos());
         maxUpdateSizeMultiplier = MC_CLIENT.clientConnectedToDedicatedServer() ? 1 : MC_SHARED.getPlayerCount();
      } else {
         maxUpdateSizeMultiplier = 1 + MC_SHARED.getPlayerCount();
      }

      this.maxSize = 1000 * Config.Common.MultiThreading.numberOfThreads.get() * maxUpdateSizeMultiplier;
      this.processQueuedChunkPreUpdate();
      this.processQueuedChunkUpdate();
      AbstractExecutorService executor = ThreadPoolUtil.getChunkToLodBuilderExecutor();
      if (executor != null && !this.updateQueuesEmpty()) {
         try {
            executor.execute(this::processQueue);
         } catch (RejectedExecutionException var4) {
         }
      }
   }

   private void processQueuedChunkPreUpdate() {
      ChunkUpdateData preUpdateData = this.preUpdateQueue.popClosest();
      if (preUpdateData != null) {
         IDhLevel dhLevel = preUpdateData.dhLevel;
         IChunkWrapper chunkWrapper = preUpdateData.chunkWrapper;
         chunkWrapper.createDhHeightMaps();

         try {
            boolean checkChunkHash = !Config.Common.LodBuilding.disableUnchangedChunkCheck.get();
            if (checkChunkHash) {
               int oldChunkHash = dhLevel.getChunkHash(chunkWrapper.getChunkPos());
               int newChunkHash = chunkWrapper.getBlockBiomeHashCode();
               boolean hasNewChunkHash = oldChunkHash != newChunkHash;
               if (!hasNewChunkHash) {
                  return;
               }
            }

            this.addItemToUpdateQueue(chunkWrapper.getChunkPos(), preUpdateData);
         } catch (Exception var8) {
            LOGGER.error("Unexpected error when pre-updating chunk at pos: [" + chunkWrapper.getChunkPos() + "]", var8);
         }
      }
   }

   private void processQueuedChunkUpdate() {
      ChunkUpdateData updateData = this.updateQueue.popClosest();
      if (updateData != null) {
         IChunkWrapper chunkWrapper = updateData.chunkWrapper;
         IDhLevel dhLevel = updateData.dhLevel;
         ILevelWrapper levelWrapper = dhLevel.getLevelWrapper();
         ArrayList<IChunkWrapper> nearbyChunkList = this.tryGetNeighborChunkListForChunk(chunkWrapper);

         try {
            DhLightingEngine.INSTANCE.bakeChunkBlockLighting(chunkWrapper, nearbyChunkList, levelWrapper.hasSkyLight() ? 15 : 0);
            dhLevel.updateBeaconBeamsForChunk(chunkWrapper, nearbyChunkList);
            int newChunkHash = chunkWrapper.getBlockBiomeHashCode();
            dhLevel.updateChunkAsync(chunkWrapper, newChunkHash);
         } catch (Exception var7) {
            LOGGER.error("Unexpected error when updating chunk at pos: [" + chunkWrapper.getChunkPos() + "]", var7);
         }

         this.queuedChunkWrapperByChunkPos.remove(updateData.chunkWrapper.getChunkPos());
      }
   }

   private ArrayList<IChunkWrapper> tryGetNeighborChunkListForChunk(IChunkWrapper chunkWrapper) {
      ArrayList<IChunkWrapper> neighborChunkList = new ArrayList<>(9);

      for (int xOffset = -1; xOffset <= 1; xOffset++) {
         for (int zOffset = -1; zOffset <= 1; zOffset++) {
            if (xOffset == 0 && zOffset == 0) {
               neighborChunkList.add(chunkWrapper);
            } else {
               DhChunkPos neighborPos = new DhChunkPos(chunkWrapper.getChunkPos().getX() + xOffset, chunkWrapper.getChunkPos().getZ() + zOffset);
               IChunkWrapper neighborChunk = this.tryGetChunk(neighborPos);
               if (neighborChunk != null) {
                  neighborChunkList.add(neighborChunk);
               }
            }
         }
      }

      return neighborChunkList;
   }

   public void setCenter(DhChunkPos newCenter) {
      this.updateQueue.setCenter(newCenter);
      this.preUpdateQueue.setCenter(newCenter);
   }

   public String getDebugMenuString() {
      String y = "§e";
      String o = "§6";
      String cf = "§r";
      String preUpdatingCountStr = F3Screen.NUMBER_FORMAT.format((long)this.preUpdateQueue.getQueuedCount());
      String updatingCountStr = F3Screen.NUMBER_FORMAT.format((long)this.updateQueue.getQueuedCount());
      String queuedCountStr = F3Screen.NUMBER_FORMAT.format((long)this.getQueuedCount());
      String maxUpdateCountStr = F3Screen.NUMBER_FORMAT.format((long)this.maxSize);
      return "Queued chunk updates: ("
         + y
         + preUpdatingCountStr
         + cf
         + " + "
         + o
         + updatingCountStr
         + cf
         + ") ["
         + queuedCountStr
         + "/"
         + maxUpdateCountStr
         + "]";
   }
}
