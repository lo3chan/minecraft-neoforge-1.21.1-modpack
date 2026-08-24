package com.aetherteam.aether.entity.monster.dungeon.boss.goal;

import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class SliderMoveGoal extends Goal {
   private final Slider slider;
   private Vec3 targetPoint;
   private float velocity;

   public SliderMoveGoal(Slider slider) {
      this.slider = slider;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (this.slider.isAwake() && !this.slider.isDeadOrDying() && this.slider.getMoveDelay() <= 0) {
         this.targetPoint = this.slider.findTargetPoint();
         return this.targetPoint != null;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      if (!this.canUse()) {
         return false;
      } else {
         return this.slider.getMoveDelay() > 0 ? false : !this.slider.horizontalCollision && !this.slider.verticalCollision;
      }
   }

   public void start() {
      this.slider.setMoveDirection(null);
      this.slider.playSound(this.slider.getMoveSound(), 2.5F, 1.0F / (this.slider.getRandom().nextFloat() * 0.2F + 0.9F));
   }

   public void tick() {
      if (this.targetPoint == null) {
         this.stop();
      } else {
         Direction moveDir = getMoveDirection(this.slider, this.targetPoint);
         if (axisDistance(this.targetPoint, this.slider.position(), moveDir) <= 0.0) {
            this.stop();
         } else {
            if (this.velocity < this.slider.getMaxVelocity()) {
               this.velocity = Math.min(this.slider.getMaxVelocity(), this.velocity + this.slider.getVelocityIncrease());
            }

            this.slider.setDeltaMovement(new Vec3(moveDir.getStepX() * this.velocity, moveDir.getStepY() * this.velocity, moveDir.getStepZ() * this.velocity));
         }
      }
   }

   public void stop() {
      this.slider.setMoveDelay(this.slider.calculateMoveDelay());
      this.slider.setTargetPoint(null);
      this.targetPoint = null;
      this.velocity = 0.0F;
      this.slider.setDeltaMovement(Vec3.ZERO);
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   private static Direction getMoveDirection(Slider slider, Vec3 targetPoint) {
      Direction moveDir = slider.getMoveDirection();
      if (moveDir == null) {
         double x = targetPoint.x - slider.getX();
         double y = targetPoint.y - slider.getY();
         double z = targetPoint.z - slider.getZ();
         moveDir = Slider.calculateDirection(x, y, z);
         slider.setMoveDirection(moveDir);
      }

      return moveDir;
   }

   private static double axisDistance(Vec3 target, Vec3 start, Direction direction) {
      double x = target.x() - start.x();
      double y = target.y() - start.y();
      double z = target.z() - start.z();
      return x * direction.getStepX() + y * direction.getStepY() + z * direction.getStepZ();
   }
}
