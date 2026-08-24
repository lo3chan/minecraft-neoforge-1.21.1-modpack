package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.message.MessageCrowMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class CrowAIFollowOwner extends Goal {
   private final EntityCrow crow;
   private final LevelReader world;
   private final double followSpeed;
   private final PathNavigation navigator;
   private final float maxDist;
   private final float minDist;
   private final boolean teleportToLeaves;
   float circlingTime = 0.0F;
   float circleDistance = 1.0F;
   float yLevel = 2.0F;
   boolean clockwise = false;
   private LivingEntity owner;
   private int timeToRecalcPath;
   private float oldWaterCost;
   private int maxCircleTime;

   public CrowAIFollowOwner(EntityCrow p_i225711_1_, double p_i225711_2_, float p_i225711_4_, float p_i225711_5_, boolean p_i225711_6_) {
      this.crow = p_i225711_1_;
      this.world = p_i225711_1_.level();
      this.followSpeed = p_i225711_2_;
      this.navigator = p_i225711_1_.getNavigation();
      this.minDist = p_i225711_4_;
      this.maxDist = p_i225711_5_;
      this.teleportToLeaves = p_i225711_6_;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      LivingEntity lvt_1_1_ = this.crow.getOwner();
      if (lvt_1_1_ == null) {
         return false;
      } else if (lvt_1_1_.isSpectator()) {
         return false;
      } else if (!this.crow.isSitting() && !this.crow.isPassenger()) {
         if (this.crow.getCommand() != 1) {
            return false;
         } else if (this.crow.distanceToSqr(lvt_1_1_) < this.minDist * this.minDist) {
            return false;
         } else {
            this.owner = lvt_1_1_;
            return this.crow.getTarget() == null || !this.crow.getTarget().isAlive();
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return this.crow.isSitting()
         ? false
         : this.crow.getCommand() == 1 && !this.crow.isPassenger() && (this.crow.getTarget() == null || !this.crow.getTarget().isAlive());
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.crow.getPathfindingMalus(PathType.WATER);
      this.crow.setPathfindingMalus(PathType.WATER, 0.0F);
      this.clockwise = this.crow.getRandom().nextBoolean();
      this.yLevel = this.crow.getRandom().nextInt(1);
      this.circlingTime = 0.0F;
      this.maxCircleTime = 20 + this.crow.getRandom().nextInt(100);
      this.circleDistance = 1.0F + this.crow.getRandom().nextFloat() * 2.0F;
   }

   public void stop() {
      this.owner = null;
      this.navigator.stop();
      this.circlingTime = 0.0F;
      this.crow.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
   }

   public void tick() {
      this.crow.getLookControl().setLookAt(this.owner, 10.0F, this.crow.getMaxHeadXRot());
      if (!this.crow.isLeashed() && !this.crow.isPassenger()) {
         double dist = this.crow.distanceToSqr(this.owner);
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (dist >= 144.0 && !this.crow.aiItemFlag) {
               this.crow.setFlying(true);
               this.crow
                  .getMoveControl()
                  .setWantedPosition(this.owner.getX(), this.owner.getY() + this.owner.getEyeHeight() + 0.20000000298023224, this.owner.getZ(), 1.0);
               this.tryToTeleportNearEntity();
               this.circlingTime = 0.0F;
            }
         }

         if (!this.crow.aiItemFlag) {
            if (this.crow.isFlying()) {
               this.circlingTime++;
            }

            if (this.circlingTime > this.maxCircleTime && this.crow.getRidingCrows(this.owner) < 2 && this.crow.canBoardOwner(this.owner)) {
               this.crow
                  .getMoveControl()
                  .setWantedPosition(
                     this.owner.getX(), this.owner.getY() + this.owner.getEyeHeight() + 0.20000000298023224, this.owner.getZ(), 0.699999988079071
                  );
               if (this.crow.distanceTo(this.owner) < 2.0F) {
                  AMCompat.startRiding(this.crow, this.owner, true);
                  if (!this.crow.level().isClientSide()) {
                     AlexsMobs.sendMSGToAll(new MessageCrowMountPlayer(this.crow.getId(), this.owner.getId()));
                  }
               }
            } else {
               Vec3 circlePos = this.getVultureCirclePos(this.owner.position());
               if (circlePos == null) {
                  circlePos = this.owner.position();
               }

               this.crow.setFlying(true);
               this.crow
                  .getMoveControl()
                  .setWantedPosition(circlePos.x(), circlePos.y() + this.owner.getEyeHeight() + 0.20000000298023224, circlePos.z(), 0.699999988079071);
            }
         }
      }
   }

   public Vec3 getVultureCirclePos(Vec3 target) {
      float angle = 0.13962634F * (this.clockwise ? -this.circlingTime : this.circlingTime);
      double extraX = this.circleDistance * Mth.sin(angle);
      double extraZ = this.circleDistance * Mth.cos(angle);
      Vec3 pos = new Vec3(target.x() + extraX, target.y() + this.yLevel, target.z() + extraZ);
      return this.crow.level().isEmptyBlock(AMBlockPos.fromVec3(pos)) ? pos : null;
   }

   private void tryToTeleportNearEntity() {
      BlockPos lvt_1_1_ = this.owner.blockPosition();

      for (int lvt_2_1_ = 0; lvt_2_1_ < 10; lvt_2_1_++) {
         int lvt_3_1_ = this.getRandomNumber(-3, 3);
         int lvt_4_1_ = this.getRandomNumber(-1, 1);
         int lvt_5_1_ = this.getRandomNumber(-3, 3);
         boolean lvt_6_1_ = this.tryToTeleportToLocation(lvt_1_1_.getX() + lvt_3_1_, lvt_1_1_.getY() + lvt_4_1_, lvt_1_1_.getZ() + lvt_5_1_);
         if (lvt_6_1_) {
            return;
         }
      }
   }

   private boolean tryToTeleportToLocation(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
      if (Math.abs(p_226328_1_ - this.owner.getX()) < 2.0 && Math.abs(p_226328_3_ - this.owner.getZ()) < 2.0) {
         return false;
      } else if (!this.isTeleportFriendlyBlock(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
         return false;
      } else {
         this.crow.moveTo(p_226328_1_ + 0.5, p_226328_2_, p_226328_3_ + 0.5, this.crow.getYRot(), this.crow.getXRot());
         this.navigator.stop();
         return true;
      }
   }

   private boolean isTeleportFriendlyBlock(BlockPos p_226329_1_) {
      PathType lvt_2_1_ = AMCompat.pathTypeStatic(this.crow, p_226329_1_);
      if (lvt_2_1_ != PathType.WALKABLE) {
         return false;
      } else {
         BlockState lvt_3_1_ = this.world.getBlockState(p_226329_1_.below());
         if (!this.teleportToLeaves && lvt_3_1_.getBlock() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos lvt_4_1_ = p_226329_1_.subtract(this.crow.blockPosition());
            return this.world.noCollision(this.crow, this.crow.getBoundingBox().move(lvt_4_1_));
         }
      }
   }

   private int getRandomNumber(int p_226327_1_, int p_226327_2_) {
      return this.crow.getRandom().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
   }
}
