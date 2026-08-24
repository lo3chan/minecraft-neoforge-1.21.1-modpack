package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public class EndergradeAIBreakFlowers extends MoveToBlockGoal {
   private final EntityEndergrade endergrade;
   private int idleAtFlowerTime = 0;
   private boolean isAboveDestinationBear;

   public EndergradeAIBreakFlowers(EntityEndergrade bird) {
      super(bird, 1.0, 32, 8);
      this.endergrade = bird;
   }

   public boolean canUse() {
      return !this.endergrade.isBaby() && !this.endergrade.hasItemTarget && super.canUse();
   }

   public boolean canContinueToUse() {
      return !this.endergrade.hasItemTarget && super.canContinueToUse();
   }

   public void stop() {
      this.idleAtFlowerTime = 0;
      this.endergrade.stopWandering = false;
   }

   public double acceptedDistance() {
      return 2.0;
   }

   public void tick() {
      super.tick();
      this.endergrade.stopWandering = true;
      BlockPos blockpos = this.getMoveToTarget();
      if (!this.isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
         this.isAboveDestinationBear = false;
         this.tryTicks++;
         this.mob.getMoveControl().setWantedPosition(blockpos.getX() + 0.5, blockpos.getY() - 0.5, blockpos.getZ() + 0.5, 1.0);
      } else {
         this.isAboveDestinationBear = true;
         this.tryTicks--;
      }

      if (this.isReachedTarget() && Math.abs(this.endergrade.getY() - this.blockPos.getY()) <= 2.0) {
         this.endergrade.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5));
         if (this.idleAtFlowerTime >= 20) {
            this.endergrade.bite();
            this.pollinate();
            this.stop();
         } else {
            this.idleAtFlowerTime++;
         }
      }
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.distSqr(new BlockPos((int)positionVec.x(), blockpos.getY(), (int)positionVec.z())) < distance * distance;
   }

   protected boolean isReachedTarget() {
      return this.isAboveDestinationBear;
   }

   private void pollinate() {
      this.endergrade.level().destroyBlock(this.blockPos, true);
      this.stop();
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).is(AMTagRegistry.ENDERGRADE_BREAKABLES);
   }
}
