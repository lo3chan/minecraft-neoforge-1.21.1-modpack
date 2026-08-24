package dev.corgitaco.dataanchor.data.type.chunk;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.chunk.network.SyncLevelChunkTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract non-sealed class SyncedLevelChunkTrackedData extends LevelChunkTrackedData implements SyncedTrackedData {
   public SyncedLevelChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, LevelChunk chunk) {
      super(trackedDataKey, chunk);
   }

   @Override
   public void sync() {
      Level level = this.get().getLevel();
      if (!level.isClientSide) {
         S2CPacketBroadcaster.S2C.trackingChunk(this.syncPacket(), this.get());
      }
   }

   @Override
   public void syncToPlayer(ServerPlayer player) {
      S2CPacketBroadcaster.S2C.sendToPlayer(this.syncPacket(), player);
   }

   @Override
   public Packet syncPacket() {
      return new SyncLevelChunkTrackedDataS2C(this.trackedDataKey, this.chunk.getPos(), this.writeToNetwork());
   }
}
