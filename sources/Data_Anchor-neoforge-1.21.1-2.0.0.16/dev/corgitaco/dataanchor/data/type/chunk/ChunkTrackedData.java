package dev.corgitaco.dataanchor.data.type.chunk;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract sealed class ChunkTrackedData implements TrackedData<ChunkAccess>, DirtyMarker permits LevelChunkTrackedData, ProtoChunkTrackedData {
   protected final transient TrackedDataKey<? extends ChunkTrackedData> trackedDataKey;
   protected final transient ChunkAccess chunk;

   public ChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, ChunkAccess chunk) {
      this.trackedDataKey = trackedDataKey;
      this.chunk = chunk;
   }

   public ChunkAccess get() {
      return this.chunk;
   }

   @Override
   public void markDirty() {
      this.chunk.setUnsaved(true);
   }

   @Override
   public void clearDirty() {
   }
}
