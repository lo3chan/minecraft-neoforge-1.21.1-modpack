package net.diebuddies.compat;

import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.diebuddies.physics.ocean.EntityOcean;
import net.minecraft.world.entity.Entity;

public class SableCreate {
   public static EntityOcean hasShipMount(Entity entity) {
      SubLevel subLevel = ((EntityMovementExtension)entity).sable$getTrackingSubLevel();
      return subLevel != null && subLevel instanceof EntityOcean entityOcean ? entityOcean : null;
   }
}
