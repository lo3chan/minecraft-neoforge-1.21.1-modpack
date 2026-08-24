package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class BoneSerpentAIJump extends JumpGoal {
   private static final int[] JUMP_DISTANCES = new int[]{0, 1, 4, 5, 6, 7};
   private final EntityBoneSerpent dolphin;
   private final int interval;
   private boolean inWater;

   public BoneSerpentAIJump(EntityBoneSerpent dolphin, int p_i50329_2_) {
      this.dolphin = dolphin;
      this.interval = p_i50329_2_;
   }

   public boolean canUse() {
      if (this.dolphin.getRandom().nextInt(this.interval) == 0 && this.dolphin.getTarget() == null) {
         Direction direction = this.dolphin.getMotionDirection();
         int i = direction.getStepX();
         int j = direction.getStepZ();
         BlockPos blockpos = this.dolphin.blockPosition();

         for (int k : JUMP_DISTANCES) {
            if (!this.canJumpTo(blockpos, i, j, k) || !this.isAirAbove(blockpos, i, j, k)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
      BlockPos blockpos = pos.offset(dx * scale, 0, dz * scale);
      return (this.dolphin.level().getFluidState(blockpos).is(FluidTags.WATER) || this.dolphin.level().getFluidState(blockpos).is(FluidTags.LAVA))
         && !this.dolphin.level().getBlockState(blockpos).blocksMotion();
   }

   private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
      return this.dolphin.level().getBlockState(pos.offset(dx * scale, 1, dz * scale)).isAir()
         && this.dolphin.level().getBlockState(pos.offset(dx * scale, 2, dz * scale)).isAir();
   }

   public boolean canContinueToUse() {
      double d0 = this.dolphin.getDeltaMovement().y;
      return this.dolphin.jumpCooldown > 0
         && (!(d0 * d0 < 0.029999999329447746) || this.dolphin.getXRot() == 0.0F || !(Math.abs(this.dolphin.getXRot()) < 10.0F) || !this.dolphin.isInWater())
         && !this.dolphin.onGround();
   }

   public boolean isInterruptable() {
      return false;
   }

   public void start() {
      Direction direction = this.dolphin.getMotionDirection();
      float up = 0.7F + this.dolphin.getRandom().nextFloat() * 0.8F;
      this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(direction.getStepX() * 0.6, up, direction.getStepZ() * 0.6));
      this.dolphin.getNavigation().stop();
      this.dolphin.jumpCooldown = this.dolphin.getRandom().nextInt(32) + 32;
   }

   public void stop() {
      this.dolphin.setXRot(0.0F);
   }

   public void tick() {
      boolean flag = this.inWater;
      if (!flag) {
         FluidState fluidstate = this.dolphin.level().getFluidState(this.dolphin.blockPosition());
         this.inWater = fluidstate.is(FluidTags.LAVA) || fluidstate.is(FluidTags.WATER);
      }

      if (this.inWater && !flag) {
         this.dolphin.playSound(SoundEvents.DOLPHIN_JUMP, 1.0F, 1.0F);
      }

      Vec3 vector3d = this.dolphin.getDeltaMovement();
      if (vector3d.y * vector3d.y < 0.10000000149011612 && this.dolphin.getXRot() != 0.0F) {
         this.dolphin.setXRot(Mth.rotLerp(this.dolphin.getXRot(), 0.0F, 0.2F));
      } else {
         double d0 = Math.sqrt(vector3d.horizontalDistanceSqr());
         double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * 57.2957763671875;
         this.dolphin.setXRot((float)d1);
      }
   }
}
