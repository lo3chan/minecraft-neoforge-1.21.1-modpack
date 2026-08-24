package dev.corgitaco.dataanchor.data.type.blockentity;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.blockentity.network.SyncBlockEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract non-sealed class SyncedBlockEntityTrackedData extends BlockEntityTrackedData implements SyncedTrackedData {
   public SyncedBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
      super(trackedDataKey, blockEntity);
   }

   @Override
   public void sync() {
      if (!this.blockEntity.getLevel().isClientSide) {
         PacketBroadcaster.S2C.trackingChunk(this.syncPacket(), this.blockEntity.getLevel().getChunkAt(this.blockEntity.getBlockPos()));
      }
   }

   @Override
   public void syncToPlayer(ServerPlayer player) {
      PacketBroadcaster.S2C.sendToPlayer(this.syncPacket(), player);
   }

   @Override
   public Packet syncPacket() {
      return new SyncBlockEntityTrackedDataS2C(this.blockEntity.getBlockPos(), this.trackedDataKey, this.writeToNetwork());
   }
}
