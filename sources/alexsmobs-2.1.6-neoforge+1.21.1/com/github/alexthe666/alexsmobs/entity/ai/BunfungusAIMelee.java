package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class BunfungusAIMelee extends Goal {
   private final EntityBunfungus chungus;
   private LivingEntity target;
   private boolean hasJumped = false;
   private int jumpCooldown = 0;

   public BunfungusAIMelee(EntityBunfungus chungus) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.chungus = chungus;
   }

   public boolean canUse() {
      if (this.chungus.getTarget() != null && this.chungus.getTarget().isAlive()) {
         this.hasJumped = false;
         return true;
      } else {
         return false;
      }
   }

   public void tick() {
      if (this.jumpCooldown > 0) {
         this.jumpCooldown--;
      }

      double dist = this.chungus.distanceTo(this.chungus.getTarget()) - this.chungus.getTarget().getBbWidth();
      if (dist < 2.0) {
         if (this.hasJumped) {
            if (!this.chungus.onGround()) {
               this.chungus.getTarget().hurt(this.chungus.damageSources().mobAttack(this.chungus), 10.0F);
            }

            this.hasJumped = false;
         } else if (this.chungus.getRandom().nextBoolean()) {
            this.chungus.setAnimation(EntityBunfungus.ANIMATION_SLAM);
         } else {
            this.chungus.setAnimation(EntityBunfungus.ANIMATION_BELLY);
         }
      } else if (!(dist < 5.0) && this.chungus.hasLineOfSight(this.chungus.getTarget()) && this.jumpCooldown <= 0 && !this.chungus.isInWaterOrBubble()) {
         this.chungus.getNavigation().stop();
         if (this.chungus.onGround()) {
            Vec3 vector3d = this.chungus.getDeltaMovement();
            Vec3 vector3d1 = new Vec3(this.chungus.getTarget().getX() - this.chungus.getX(), 0.0, this.chungus.getTarget().getZ() - this.chungus.getZ());
            if (vector3d1.lengthSqr() > 1.0E-7) {
               vector3d1 = vector3d1.normalize().scale(0.9).add(vector3d.scale(0.8));
            }

            this.chungus.onJump();
            this.chungus.setDeltaMovement(vector3d1.x, 0.6000000238418579, vector3d1.z);
            this.chungus.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
            this.chungus.yBodyRot = this.chungus.getYRot();
            this.hasJumped = true;
            this.jumpCooldown = 10;
         }
      } else {
         this.chungus.getNavigation().moveTo(this.chungus.getTarget(), 1.0);
      }
   }
}
