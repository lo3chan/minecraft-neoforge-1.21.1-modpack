package dev.corgitaco.dataanchor.data.type.blockentity;

import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract non-sealed class ServerBlockEntityTrackedData extends BlockEntityTrackedData implements ServerTrackedData {
   public ServerBlockEntityTrackedData(TrackedDataKey<? extends BlockEntityTrackedData> trackedDataKey, BlockEntity blockEntity) {
      super(trackedDataKey, blockEntity);
   }
}
