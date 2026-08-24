package com.seibel.distanthorizons.core.generation;

import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.coreapi.util.BitShiftUtil;
import java.util.ArrayList;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

public class AdjacentChunkHolder {
   final IChunkWrapper[] chunkArray = new IChunkWrapper[9];

   public AdjacentChunkHolder(IChunkWrapper centerWrapper) {
      this.chunkArray[4] = centerWrapper;
   }

   public AdjacentChunkHolder(IChunkWrapper centerWrapper, @NotNull ArrayList<IChunkWrapper> nearbyChunkList) {
      this.chunkArray[4] = centerWrapper;
      DhChunkPos centerChunkPos = centerWrapper.getChunkPos();
      HashSet<DhChunkPos> requestedAdjacentPositions = new HashSet<>(9);

      for (int xOffset = -1; xOffset <= 1; xOffset++) {
         for (int zOffset = -1; zOffset <= 1; zOffset++) {
            DhChunkPos adjacentPos = new DhChunkPos(centerChunkPos.getX() + xOffset, centerChunkPos.getZ() + zOffset);
            requestedAdjacentPositions.add(adjacentPos);
         }
      }

      for (int chunkIndex = 0; chunkIndex < nearbyChunkList.size(); chunkIndex++) {
         IChunkWrapper chunk = nearbyChunkList.get(chunkIndex);
         if (chunk != null && requestedAdjacentPositions.contains(chunk.getChunkPos())) {
            requestedAdjacentPositions.remove(chunk.getChunkPos());
            this.add(chunk);
         }

         if (requestedAdjacentPositions.isEmpty()) {
            break;
         }
      }
   }

   public void add(IChunkWrapper centerWrapper) {
      DhChunkPos centerPos = this.chunkArray[4].getChunkPos();
      DhChunkPos offsetPos = centerWrapper.getChunkPos();
      int offsetX = offsetPos.getX() - centerPos.getX();
      if (offsetX >= -1 && offsetX <= 1) {
         int offsetZ = offsetPos.getZ() - centerPos.getZ();
         if (offsetZ >= -1 && offsetZ <= 1) {
            this.chunkArray[4 + offsetX + offsetZ + (offsetZ << 1)] = centerWrapper;
         }
      }
   }

   public IChunkWrapper getByBlockPos(int blockX, int blockZ) {
      int chunkX = BitShiftUtil.divideByPowerOfTwo(blockX, 4);
      int chunkZ = BitShiftUtil.divideByPowerOfTwo(blockZ, 4);
      IChunkWrapper centerChunk = this.chunkArray[4];
      DhChunkPos centerPos = centerChunk.getChunkPos();
      if (centerPos.getX() == chunkX && centerPos.getZ() == chunkZ) {
         return centerChunk;
      } else {
         int offsetX = chunkX - centerPos.getX();
         if (offsetX >= -1 && offsetX <= 1) {
            int offsetZ = chunkZ - centerPos.getZ();
            return offsetZ >= -1 && offsetZ <= 1 ? this.chunkArray[4 + offsetX + offsetZ + (offsetZ << 1)] : null;
         } else {
            return null;
         }
      }
   }
}
