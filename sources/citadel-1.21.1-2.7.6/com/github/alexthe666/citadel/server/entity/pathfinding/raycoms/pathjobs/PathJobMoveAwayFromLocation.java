package com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.pathjobs;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.MNode;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.Pathfinding;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class PathJobMoveAwayFromLocation extends AbstractPathJob {
   protected final BlockPos avoid;
   protected final int avoidDistance;

   public PathJobMoveAwayFromLocation(Level world, BlockPos start, BlockPos avoid, int avoidDistance, int range, LivingEntity entity) {
      super(world, start, avoid, range, entity);
      this.avoid = new BlockPos(avoid);
      this.avoidDistance = avoidDistance;
   }

   @Nullable
   @Override
   protected Path search() {
      if (Pathfinding.isDebug()) {
         Citadel.LOGGER
            .info(
               "Pathfinding from [{},{},{}] away from [{},{},{}]",
               this.start.getX(),
               this.start.getY(),
               this.start.getZ(),
               this.avoid.getX(),
               this.avoid.getY(),
               this.avoid.getZ()
            );
      }

      return super.search();
   }

   @Override
   protected double computeHeuristic(BlockPos pos) {
      return -this.avoid.distSqr(pos);
   }

   @Override
   protected boolean isAtDestination(MNode n) {
      return Math.sqrt(this.avoid.distSqr(n.pos)) > this.avoidDistance;
   }

   @Override
   protected double getNodeResultScore(MNode n) {
      return -this.avoid.distSqr(n.pos);
   }
}
