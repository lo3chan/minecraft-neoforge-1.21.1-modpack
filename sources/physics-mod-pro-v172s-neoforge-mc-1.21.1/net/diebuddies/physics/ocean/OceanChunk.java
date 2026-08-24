package net.diebuddies.physics.ocean;

import net.diebuddies.physics.ocean.storage.StorageContainer;

public class OceanChunk extends IChunk<OceanProcessor> {
   public OceanChunk(int x, int y, int z, StorageContainer dataStorage) {
      super(x, y, z, dataStorage);
   }

   @Override
   public void setLoadedNeighbourCount(int loadedNeighbourCount) {
      if (this.loadedNeighbourCount != loadedNeighbourCount) {
         if (loadedNeighbourCount == 8) {
            this.world.processChunkColumns.add(Index.oceanLayerChunk(this.x, this.z));
         } else {
            this.world.processChunkColumns.remove(Index.oceanLayerChunk(this.x, this.z));
         }
      }

      super.setLoadedNeighbourCount(loadedNeighbourCount);
   }
}
