package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class FroststalkerAIMelee extends Goal {
   private final EntityFroststalker froststalker;
   private boolean willJump = false;
   private boolean hasJumped = false;
   private boolean clockwise = false;
   private int pursuitTime = 0;
   private int maxPursuitTime = 0;
   private BlockPos pursuitPos = null;
   private int startingOrbit = 0;

   public FroststalkerAIMelee(EntityFroststalker froststalker) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.froststalker = froststalker;
   }

   public boolean canUse() {
      if (this.froststalker.getTarget() != null && this.froststalker.getTarget().isAlive()) {
         return !this.froststalker.isValidLeader(this.froststalker.getTarget())
            ? !this.froststalker.isFleeingFire()
            : this.froststalker.getLastHurtByMob() != null && this.froststalker.getLastHurtByMob().equals(this.froststalker.getTarget());
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      LivingEntity target = this.froststalker.getTarget();
      return target != null && !this.froststalker.isValidLeader(target);
   }

   public void start() {
      this.willJump = this.froststalker.getRandom().nextInt(2) == 0;
      this.hasJumped = false;
      this.clockwise = this.froststalker.getRandom().nextBoolean();
      this.pursuitPos = null;
      this.pursuitTime = 0;
      this.maxPursuitTime = 40 + this.froststalker.getRandom().nextInt(40);
      this.startingOrbit = this.froststalker.getRandom().nextInt(360);
      this.froststalker.frostJump();
   }

   public void tick() {
      this.froststalker.setBipedal(true);
      this.froststalker.standFor(20);
      LivingEntity target = this.froststalker.getTarget();
      boolean flag = false;
      if ((this.hasJumped || this.froststalker.isTackling()) && this.froststalker.onGround()) {
         this.hasJumped = false;
         this.willJump = false;
         this.froststalker.setTackling(false);
      }

      if (target != null && target.isAlive()) {
         if (this.pursuitTime < this.maxPursuitTime) {
            this.pursuitTime++;
            this.pursuitPos = this.getBlockNearTarget(target);
            float extraSpeed = 0.2F * Math.max(5.0F - this.froststalker.distanceTo(target), 0.0F);
            if (this.pursuitPos != null) {
               this.froststalker.getNavigation().moveTo(this.pursuitPos.getX(), this.pursuitPos.getY(), this.pursuitPos.getZ(), 1.0F + extraSpeed);
            } else {
               this.froststalker.getNavigation().moveTo(target, 1.0);
            }
         } else if (this.willJump && this.pursuitTime == this.maxPursuitTime) {
            this.froststalker.lookAt(target, 180.0F, 10.0F);
            if (this.froststalker.distanceTo(target) > 10.0F) {
               this.froststalker.getNavigation().moveTo(target, 1.0);
            } else if (this.froststalker.onGround() && this.froststalker.hasLineOfSight(target)) {
               this.froststalker.setTackling(true);
               this.hasJumped = true;
               Vec3 vector3d = this.froststalker.getDeltaMovement();
               Vec3 vector3d1 = new Vec3(target.getX() - this.froststalker.getX(), 0.0, target.getZ() - this.froststalker.getZ());
               if (vector3d1.lengthSqr() > 1.0E-7) {
                  vector3d1 = vector3d1.normalize().scale(0.9).add(vector3d.scale(0.8));
               }

               this.froststalker.setDeltaMovement(vector3d1.x, 0.6000000238418579, vector3d1.z);
            } else {
               flag = true;
            }
         } else if (!this.froststalker.isTackling()) {
            this.froststalker.getNavigation().moveTo(target, 1.0);
         }

         if (this.froststalker.isTackling()
            && this.froststalker.distanceTo(target) <= this.froststalker.getBbWidth() + target.getBbWidth() + 1.1F
            && this.froststalker.hasLineOfSight(target)) {
            target.hurt(this.froststalker.damageSources().mobAttack(this.froststalker), (float)this.froststalker.getAttributeValue(Attributes.ATTACK_DAMAGE));
            this.start();
         }

         if (!flag
            && this.froststalker.distanceTo(target) <= this.froststalker.getBbWidth() + target.getBbWidth() + 1.1F
            && this.froststalker.hasLineOfSight(target)
            && this.pursuitTime == this.maxPursuitTime) {
            if (!this.froststalker.isTackling()) {
               AMCompat.doHurtTarget(this.froststalker, target);
            }

            this.start();
         }
      }

      if (target != null && !this.froststalker.onGround()) {
         this.froststalker.lookAt(target, 180.0F, 10.0F);
         this.froststalker.yBodyRot = this.froststalker.getYRot();
      }
   }

   public BlockPos getBlockNearTarget(LivingEntity target) {
      float radius = this.froststalker.getRandom().nextInt(5) + 3 + target.getBbWidth();
      int orbit = (int)(this.startingOrbit + (float)this.pursuitTime / this.maxPursuitTime * 360.0F);
      float angle = 0.017453292F * (this.clockwise ? -orbit : orbit);
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos circlePos = AMBlockPos.fromCoords(target.getX() + extraX, target.getEyeY(), target.getZ() + extraZ);

      while (!this.froststalker.level().getBlockState(circlePos).isAir() && circlePos.getY() < AMCompat.maxBuildHeight(this.froststalker.level())) {
         circlePos = circlePos.above();
      }

      while (
         !this.froststalker.level().getBlockState(circlePos.below()).entityCanStandOn(this.froststalker.level(), circlePos.below(), this.froststalker)
            && circlePos.getY() > 1
      ) {
         circlePos = circlePos.below();
      }

      return this.froststalker.getWalkTargetValue(circlePos) > -1.0F ? circlePos : null;
   }

   public void stop() {
      this.froststalker.setTackling(false);
   }
}
