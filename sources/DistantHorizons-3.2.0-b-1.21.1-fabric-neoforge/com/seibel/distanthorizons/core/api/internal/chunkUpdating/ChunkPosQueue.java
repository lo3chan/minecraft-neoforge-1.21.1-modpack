package com.seibel.distanthorizons.core.api.internal.chunkUpdating;

import com.seibel.distanthorizons.core.pos.DhChunkPos;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import org.jetbrains.annotations.Nullable;

public class ChunkPosQueue {
   private final PriorityBlockingQueue<DhChunkPos> closestQueue = new PriorityBlockingQueue<>(
      500, Comparator.comparingDouble(pos -> pos.squaredDistance(this.center))
   );
   private final PriorityBlockingQueue<DhChunkPos> furthestQueue = new PriorityBlockingQueue<>(
      500, Comparator.<DhChunkPos>comparingDouble(pos -> pos.squaredDistance(this.center)).reversed()
   );
   private final ConcurrentHashMap<DhChunkPos, ChunkUpdateData> updateDataByChunkPos = new ConcurrentHashMap<>();
   private DhChunkPos center = new DhChunkPos(0, 0);

   public boolean contains(DhChunkPos pos) {
      return this.updateDataByChunkPos.containsKey(pos);
   }

   public void clear() {
      this.updateDataByChunkPos.clear();
      this.closestQueue.clear();
      this.furthestQueue.clear();
   }

   public void addItem(DhChunkPos pos, ChunkUpdateData updateData) {
      if (!this.updateDataByChunkPos.containsKey(pos)) {
         this.updateDataByChunkPos.put(pos, updateData);
         this.closestQueue.add(pos);
         this.furthestQueue.add(pos);
      }
   }

   public int getQueuedCount() {
      return this.updateDataByChunkPos.size();
   }

   public boolean isEmpty() {
      return this.updateDataByChunkPos.isEmpty();
   }

   public void setCenter(DhChunkPos newCenter) {
      if (!newCenter.equals(this.center)) {
         this.center = newCenter;
         this.closestQueue.clear();
         this.furthestQueue.clear();

         for (DhChunkPos pos : this.updateDataByChunkPos.keySet()) {
            this.closestQueue.add(pos);
            this.furthestQueue.add(pos);
         }
      }
   }

   public ChunkUpdateData popClosest() {
      if (this.closestQueue.isEmpty()) {
         return null;
      } else {
         DhChunkPos closest = this.closestQueue.poll();
         if (closest == null) {
            return null;
         } else {
            this.furthestQueue.remove(closest);
            return this.updateDataByChunkPos.remove(closest);
         }
      }
   }

   @Nullable
   public ChunkUpdateData popFurthest() {
      if (this.furthestQueue.isEmpty()) {
         return null;
      } else {
         DhChunkPos furthest = this.furthestQueue.poll();
         if (furthest == null) {
            return null;
         } else {
            this.closestQueue.remove(furthest);
            return this.updateDataByChunkPos.remove(furthest);
         }
      }
   }
}
