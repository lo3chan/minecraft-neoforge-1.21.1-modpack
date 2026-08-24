package dev.corgitaco.dataanchor.data.type.level;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.InternalDirtyMarker;
import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.level.network.SyncLevelTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public abstract non-sealed class SyncedLevelTrackedData extends LevelTrackedData implements SyncedTrackedData, DirtyMarker {
   public SyncedLevelTrackedData(TrackedDataKey<? extends SyncedLevelTrackedData> trackedDataKey, Level level) {
      super(trackedDataKey, level);
   }

   @Override
   public void sync() {
      if (!this.level.isClientSide) {
         S2CPacketBroadcaster.S2C.sendToAllPlayersInDimension(this.syncPacket(), (ServerLevel)this.get());
      }
   }

   @Override
   public void syncToPlayer(ServerPlayer player) {
      S2CPacketBroadcaster.S2C.sendToPlayer(this.syncPacket(), player);
   }

   @Override
   public Packet syncPacket() {
      return new SyncLevelTrackedDataS2C(this.trackedDataKey, this.writeToNetwork());
   }

   @Override
   public void markDirty() {
      if (!this.level.isClientSide && this.level instanceof InternalDirtyMarker dirtyMarker) {
         dirtyMarker.dataAnchor$markDirty();
      }
   }

   @Override
   public void clearDirty() {
   }
}
