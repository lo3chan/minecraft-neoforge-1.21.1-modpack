package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrowAICircleCrops extends MoveToBlockGoal {
   private final EntityCrow crow;
   private int idleAtFlowerTime = 0;
   private boolean isAboveDestinationBear;
   float circlingTime = 0.0F;
   float circleDistance = 2.0F;
   float maxCirclingTime = 80.0F;
   float yLevel = 2.0F;
   boolean clockwise = false;
   boolean circlePhase = false;

   public CrowAICircleCrops(EntityCrow bird) {
      super(bird, 1.0, 32, 8);
      this.crow = bird;
   }

   public void start() {
      super.start();
      this.circlePhase = true;
      this.clockwise = this.crow.getRandom().nextBoolean();
      this.yLevel = 1 + this.crow.getRandom().nextInt(3);
      this.circleDistance = 1 + this.crow.getRandom().nextInt(3);
   }

   public boolean canUse() {
      return !this.crow.isBaby()
         && AMConfig.crowsStealCrops
         && (this.crow.getTarget() == null || !this.crow.getTarget().isAlive())
         && !this.crow.isTame()
         && this.crow.fleePumpkinFlag == 0
         && !this.crow.aiItemFlag
         && super.canUse();
   }

   public boolean canContinueToUse() {
      return this.blockPos != null
         && AMConfig.crowsStealCrops
         && (this.crow.getTarget() == null || !this.crow.getTarget().isAlive())
         && !this.crow.isTame()
         && !this.crow.aiItemFlag
         && this.crow.fleePumpkinFlag == 0
         && super.canContinueToUse();
   }

   public void stop() {
      this.idleAtFlowerTime = 0;
      this.circlingTime = 0.0F;
      this.tryTicks = 0;
      this.blockPos = BlockPos.ZERO;
   }

   public double acceptedDistance() {
      return 1.0;
   }

   public void tick() {
      if (this.blockPos != null) {
         BlockPos blockpos = this.getMoveToTarget();
         if (this.circlePhase) {
            this.tryTicks = 0;
            BlockPos circlePos = this.getVultureCirclePos(blockpos);
            if (circlePos != null) {
               this.crow.setFlying(true);
               this.crow.getMoveControl().setWantedPosition(circlePos.getX() + 0.5, circlePos.getY() + 0.5, circlePos.getZ() + 0.5, 0.699999988079071);
            }

            this.circlingTime++;
            if (this.circlingTime > 200.0F) {
               this.circlingTime = 0.0F;
               this.circlePhase = false;
            }
         } else {
            super.tick();
            if (this.crow.onGround()) {
               this.crow.setFlying(false);
            }

            if (!this.isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
               this.isAboveDestinationBear = false;
               this.tryTicks++;
               this.mob.getNavigation().moveTo(blockpos.getX() + 0.5, blockpos.getY() - 0.5, blockpos.getZ() + 0.5, 1.0);
            } else {
               this.isAboveDestinationBear = true;
               this.tryTicks--;
            }

            if (this.isReachedTarget()) {
               this.crow.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5));
               if (this.idleAtFlowerTime >= 5) {
                  this.destroyCrop();
                  this.stop();
               } else {
                  this.crow.peck();
                  this.idleAtFlowerTime++;
               }
            }
         }
      }
   }

   public BlockPos getVultureCirclePos(BlockPos target) {
      float angle = 0.13962634F * (this.clockwise ? -this.circlingTime : this.circlingTime);
      double extraX = this.circleDistance * Mth.sin(angle);
      double extraZ = this.circleDistance * Mth.cos(angle);
      BlockPos pos = AMBlockPos.fromCoords(target.getX() + 0.5F + extraX, target.getY() + 1 + this.yLevel, target.getZ() + 0.5F + extraZ);
      return this.crow.level().isEmptyBlock(pos) ? pos : null;
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
   }

   protected boolean isReachedTarget() {
      return this.isAboveDestinationBear;
   }

   private void destroyCrop() {
      if (!this.canSeeBlock(this.blockPos)) {
         this.stop();
         this.tryTicks = 1200;
      } else {
         if (this.crow.level().getBlockState(this.blockPos).getBlock() instanceof CropBlock) {
            if (AMCompat.gameRule(this.crow.level(), AMCompat.Rule.MOB_GRIEFING)) {
               CropBlock block = (CropBlock)this.crow.level().getBlockState(this.blockPos).getBlock();
               int cropAge = block.getAge(this.crow.level().getBlockState(this.blockPos));
               if (cropAge > 0) {
                  this.crow.level().setBlockAndUpdate(this.blockPos, block.getStateForAge(Math.max(0, cropAge - 1)));
               } else {
                  this.crow.level().destroyBlock(this.blockPos, true);
               }
            }
         } else if (AMCompat.gameRule(this.crow.level(), AMCompat.Rule.MOB_GRIEFING)) {
            this.crow.level().destroyBlock(this.blockPos, true);
         }

         this.stop();
         this.tryTicks = 1200;
      }
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.crow.getX(), this.crow.getEyeY(), this.crow.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.crow.level().clip(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this.crow));
      return result.getBlockPos().equals(destinationBlock);
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).is(AMTagRegistry.CROW_FOODBLOCKS);
   }
}
