package dev.corgitaco.dataanchor.test.data.player;

import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.SyncedPlayerTrackedData;
import net.minecraft.world.entity.player.Player;

public class TestSyncedPlayerTrackedData extends SyncedPlayerTrackedData implements TickableTrackedData {
   private int yum = 0;

   public TestSyncedPlayerTrackedData(TrackedDataKey<? extends SyncedPlayerTrackedData> trackedDataKey, Player player) {
      super(trackedDataKey, player);
   }

   @Override
   public void tick() {
   }

   public void setYum(int yum) {
      this.yum = yum;
      this.sync();
   }
}
