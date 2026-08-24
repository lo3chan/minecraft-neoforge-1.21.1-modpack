package com.seibel.distanthorizons.api.methods.events.abstractEvents;

import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEvent;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

public abstract class DhApiChunkProcessingEvent implements IDhApiEvent<DhApiChunkProcessingEvent.EventParam> {
   public abstract void blockOrBiomeChangedDuringChunkProcessing(DhApiEventParam<DhApiChunkProcessingEvent.EventParam> dhApiEventParam);

   @Override
   public final void fireEvent(DhApiEventParam<DhApiChunkProcessingEvent.EventParam> event) {
      this.blockOrBiomeChangedDuringChunkProcessing(event);
   }

   public static class EventParam implements IDhApiEventParam {
      public final IDhApiLevelWrapper levelWrapper;
      public final int chunkX;
      public final int chunkZ;
      public int relativeBlockPosX;
      public int blockPosY;
      public int relativeBlockPosZ;
      public IDhApiBlockStateWrapper currentBlock;
      public IDhApiBiomeWrapper currentBiome;
      private IDhApiBlockStateWrapper newBlock;
      private IDhApiBiomeWrapper newBiome;

      public EventParam(IDhApiLevelWrapper newLevelWrapper, int chunkX, int chunkZ) {
         this.levelWrapper = newLevelWrapper;
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
      }

      public void updateForPosition(
         int relativeBlockPosX, int blockPosY, int relativeBlockPosZ, IDhApiBlockStateWrapper currentBlock, IDhApiBiomeWrapper currentBiome
      ) {
         this.relativeBlockPosX = relativeBlockPosX;
         this.blockPosY = blockPosY;
         this.relativeBlockPosZ = relativeBlockPosZ;
         this.newBlock = null;
         this.newBiome = null;
         this.currentBlock = currentBlock;
         this.currentBiome = currentBiome;
      }

      public void setBlockOverride(IDhApiBlockStateWrapper block) {
         this.newBlock = block;
      }

      public IDhApiBlockStateWrapper getBlockOverride() {
         return this.newBlock;
      }

      public void setBiomeOverride(IDhApiBiomeWrapper biome) {
         this.newBiome = biome;
      }

      public IDhApiBiomeWrapper getBiomeOverride() {
         return this.newBiome;
      }

      public DhApiChunkProcessingEvent.EventParam copy() {
         return this;
      }

      @Override
      public boolean getCopyBeforeFire() {
         return false;
      }
   }
}
