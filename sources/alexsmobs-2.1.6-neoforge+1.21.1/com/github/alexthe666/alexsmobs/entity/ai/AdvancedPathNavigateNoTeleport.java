package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.alexsmobs.citadel.server.entity.pathfinding.raycoms.PathingStuckHandler;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class AdvancedPathNavigateNoTeleport extends AdvancedPathNavigate {
   private final boolean wide;

   public AdvancedPathNavigateNoTeleport(Mob entity, Level world, AdvancedPathNavigate.MovementType type, boolean climbing, boolean wide) {
      super(entity, world, type, entity.getBbWidth(), entity.getBbHeight(), PathingStuckHandler.createStuckHandler());
      this.getPathingOptions().setCanClimb(climbing);
      this.wide = wide;
   }

   public AdvancedPathNavigateNoTeleport(Mob entity, Level world, boolean wide) {
      this(entity, world, AdvancedPathNavigate.MovementType.WALKING, false, wide);
   }

   @Override
   protected boolean canUpdatePath() {
      return true;
   }

   @Override
   protected float calculateMaxDistanceToWaypoint() {
      return this.wide ? this.mob.getBbWidth() * 0.75F : super.calculateMaxDistanceToWaypoint();
   }
}
