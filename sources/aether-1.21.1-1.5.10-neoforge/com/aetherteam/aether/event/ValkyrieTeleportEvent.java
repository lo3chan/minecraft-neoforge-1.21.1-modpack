package com.aetherteam.aether.event;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class ValkyrieTeleportEvent extends EntityTeleportEvent implements ICancellableEvent {
   public ValkyrieTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
      super(entity, targetX, targetY, targetZ);
   }
}
