package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IFollower;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

public class FlyingAIFollowOwner extends Goal {
   private final TamableAnimal tameable;
   private LivingEntity owner;
   private final LevelReader world;
   private final double followSpeed;
   private final PathNavigation navigator;
   private int timeToRecalcPath;
   private final float maxDist;
   private final float minDist;
   private float oldWaterCost;
   private final boolean teleportToLeaves;
   private final IFollower follower;

   public FlyingAIFollowOwner(TamableAnimal tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
      this.tameable = tameable;
      this.world = tameable.level();
      this.followSpeed = speed;
      this.navigator = tameable.getNavigation();
      this.minDist = minDist;
      this.maxDist = maxDist;
      this.teleportToLeaves = teleportToLeaves;
      this.follower = (IFollower)tameable;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      LivingEntity livingentity = this.tameable.getOwner();
      if (livingentity == null) {
         return false;
      } else if (livingentity.isSpectator()) {
         return false;
      } else if (this.tameable.isOrderedToSit()) {
         return false;
      } else if (!(this.tameable.distanceToSqr(livingentity) < this.minDist * this.minDist) && !this.isInCombat()) {
         this.owner = livingentity;
         return this.follower.shouldFollow();
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return !this.tameable.isOrderedToSit() && !this.isInCombat() ? this.tameable.distanceToSqr(this.owner) > this.maxDist * this.maxDist : false;
   }

   private boolean isInCombat() {
      Entity owner = this.tameable.getOwner();
      return owner == null ? false : this.tameable.distanceTo(owner) < 30.0F && this.tameable.getTarget() != null && this.tameable.getTarget().isAlive();
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.tameable.getPathfindingMalus(PathType.WATER);
      this.tameable.setPathfindingMalus(PathType.WATER, 0.0F);
   }

   public void stop() {
      this.owner = null;
      this.navigator.stop();
      this.tameable.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
   }

   public void tick() {
      this.tameable.getLookControl().setLookAt(this.owner, 10.0F, this.tameable.getMaxHeadXRot());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         if (!this.tameable.isLeashed() && !this.tameable.isPassenger()) {
            if (this.tameable.distanceToSqr(this.owner) >= 144.0) {
               this.tryToTeleportNearEntity();
            }

            this.follower.followEntity(this.tameable, this.owner, this.followSpeed);
         }
      }
   }

   private void tryToTeleportNearEntity() {
      BlockPos blockpos = this.owner.blockPosition();

      for (int i = 0; i < 10; i++) {
         int j = this.getRandomNumber(-3, 3);
         int k = this.getRandomNumber(-1, 1);
         int l = this.getRandomNumber(-3, 3);
         boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
         if (flag) {
            return;
         }
      }
   }

   private boolean tryToTeleportToLocation(int x, int y, int z) {
      if (Math.abs(x - this.owner.getX()) < 2.0 && Math.abs(z - this.owner.getZ()) < 2.0) {
         return false;
      } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
         return false;
      } else {
         this.tameable.moveTo(x + 0.5, y, z + 0.5, this.tameable.getYRot(), this.tameable.getXRot());
         this.navigator.stop();
         return true;
      }
   }

   private boolean isTeleportFriendlyBlock(BlockPos pos) {
      if (this.world.getBlockState(pos).isAir()) {
         BlockPos blockpos = pos.subtract(this.tameable.blockPosition());
         return this.world.noCollision(this.tameable, this.tameable.getBoundingBox().move(blockpos));
      } else {
         PathType pathnodetype = AMCompat.pathTypeStatic(this.tameable, pos);
         if (pathnodetype != PathType.WALKABLE) {
            return false;
         } else {
            BlockState blockstate = this.world.getBlockState(pos.below());
            if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
               return false;
            } else {
               BlockPos blockpos = pos.subtract(this.tameable.blockPosition());
               return this.world.noCollision(this.tameable, this.tameable.getBoundingBox().move(blockpos));
            }
         }
      }
   }

   private int getRandomNumber(int min, int max) {
      return this.tameable.getRandom().nextInt(max - min + 1) + min;
   }
}
