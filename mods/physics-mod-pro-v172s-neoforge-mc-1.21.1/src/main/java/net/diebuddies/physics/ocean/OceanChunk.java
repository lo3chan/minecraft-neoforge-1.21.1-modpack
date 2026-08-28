/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.ocean;

import net.diebuddies.physics.ocean.IChunk;
import net.diebuddies.physics.ocean.Index;
import net.diebuddies.physics.ocean.OceanProcessor;
import net.diebuddies.physics.ocean.storage.StorageContainer;

public class OceanChunk
extends IChunk<OceanProcessor> {
    public OceanChunk(int x, int y, int z, StorageContainer dataStorage) {
        super(x, y, z, dataStorage);
    }

    @Override
    public void setLoadedNeighbourCount(int loadedNeighbourCount) {
        if (this.loadedNeighbourCount != loadedNeighbourCount) {
            if (loadedNeighbourCount == 8) {
                ((OceanProcessor)this.world).processChunkColumns.add(Index.oceanLayerChunk(this.x, this.z));
            } else {
                ((OceanProcessor)this.world).processChunkColumns.remove(Index.oceanLayerChunk(this.x, this.z));
            }
        }
        super.setLoadedNeighbourCount(loadedNeighbourCount);
    }
}

