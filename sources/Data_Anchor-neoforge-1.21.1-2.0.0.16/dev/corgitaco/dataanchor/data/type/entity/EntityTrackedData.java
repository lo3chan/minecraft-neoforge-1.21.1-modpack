package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.entity.Entity;

public abstract sealed class EntityTrackedData implements TrackedData<Entity> permits ServerEntityTrackedData, SyncedEntityTrackedData, PlayerTrackedData {
   protected final transient TrackedDataKey<? extends EntityTrackedData> trackedDataKey;
   protected final transient Entity entity;

   public EntityTrackedData(TrackedDataKey<? extends EntityTrackedData> trackedDataKey, Entity entity) {
      this.trackedDataKey = trackedDataKey;
      this.entity = entity;
   }

   public void addDuringPortalTeleport() {
   }

   public Entity get() {
      return this.entity;
   }
}
