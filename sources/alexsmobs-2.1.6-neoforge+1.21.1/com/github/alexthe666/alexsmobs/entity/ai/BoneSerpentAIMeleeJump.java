package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class BoneSerpentAIMeleeJump extends JumpGoal {
   private final EntityBoneSerpent dolphin;
   private int attackCooldown = 0;
   private boolean inWater;

   public BoneSerpentAIMeleeJump(EntityBoneSerpent dolphin) {
      this.dolphin = dolphin;
   }

   public boolean canUse() {
      if (this.dolphin.getTarget() != null
         && !this.dolphin.onGround()
         && (this.dolphin.isInLava() || this.dolphin.isInWater())
         && this.dolphin.jumpCooldown <= 0) {
         BlockPos blockpos = this.dolphin.blockPosition();
         return true;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      double d0 = this.dolphin.getDeltaMovement().y;
      return this.dolphin.getTarget() != null
         && this.dolphin.jumpCooldown > 0
         && (!(d0 * d0 < 0.029999999329447746) || this.dolphin.getXRot() == 0.0F || !(Math.abs(this.dolphin.getXRot()) < 10.0F) || !this.dolphin.isInWater())
         && !this.dolphin.onGround();
   }

   public boolean isInterruptable() {
      return false;
   }

   public void start() {
      LivingEntity target = this.dolphin.getTarget();
      if (target != null) {
         double distanceXZ = this.dolphin.distanceToSqr(target.getX(), this.dolphin.getY(), target.getZ());
         if (distanceXZ < 150.0) {
            this.dolphin.lookAt(target, 260.0F, 30.0F);
            double smoothX = Mth.clamp(Math.abs(target.getX() - this.dolphin.getX()), 0.0, 1.0);
            double smoothZ = Mth.clamp(Math.abs(target.getZ() - this.dolphin.getZ()), 0.0, 1.0);
            double d0 = (target.getX() - this.dolphin.getX()) * 0.3 * smoothX;
            double d2 = (target.getZ() - this.dolphin.getZ()) * 0.3 * smoothZ;
            float up = 1.0F + this.dolphin.getRandom().nextFloat() * 0.8F;
            this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(d0 * 0.3, up, d2 * 0.3));
            this.dolphin.getNavigation().stop();
            this.dolphin.jumpCooldown = this.dolphin.getRandom().nextInt(32) + 64;
         } else {
            this.dolphin.getNavigation().moveTo(target, 1.0);
         }
      }
   }

   public void stop() {
      this.dolphin.setXRot(0.0F);
      this.attackCooldown = 0;
   }

   public void tick() {
      boolean flag = this.inWater;
      if (!flag) {
         FluidState fluidstate = this.dolphin.level().getFluidState(this.dolphin.blockPosition());
         this.inWater = fluidstate.is(FluidTags.LAVA) || fluidstate.is(FluidTags.WATER);
      }

      if (this.attackCooldown > 0) {
         this.attackCooldown--;
      }

      if (this.inWater && !flag) {
         this.dolphin.playSound(SoundEvents.DOLPHIN_JUMP, 1.0F, 1.0F);
      }

      LivingEntity target = this.dolphin.getTarget();
      if (target != null && this.dolphin.distanceTo(target) < 3.0F && this.attackCooldown <= 0) {
         AMCompat.doHurtTarget(this.dolphin, target);
         this.attackCooldown = 20;
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
