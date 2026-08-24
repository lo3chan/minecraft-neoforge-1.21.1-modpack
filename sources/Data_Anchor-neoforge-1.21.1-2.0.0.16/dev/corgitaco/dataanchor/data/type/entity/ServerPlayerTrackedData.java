package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.server.level.ServerPlayer;

public abstract non-sealed class ServerPlayerTrackedData extends PlayerTrackedData implements ServerTrackedData {
   public ServerPlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, ServerPlayer player) {
      this(trackedDataKey, player, false);
   }

   public ServerPlayerTrackedData(TrackedDataKey<? extends PlayerTrackedData> trackedDataKey, ServerPlayer player, boolean persistent) {
      super(trackedDataKey, player, persistent);
   }

   public ServerPlayer get() {
      return (ServerPlayer)super.get();
   }
}
