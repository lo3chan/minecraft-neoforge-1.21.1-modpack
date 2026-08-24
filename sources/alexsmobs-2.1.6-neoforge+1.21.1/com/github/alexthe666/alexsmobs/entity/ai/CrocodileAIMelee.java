package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CrocodileAIMelee extends MeleeAttackGoal {
   private final EntityCrocodile crocodile;

   public CrocodileAIMelee(EntityCrocodile crocodile, double speedIn, boolean useLongMemory) {
      super(crocodile, speedIn, useLongMemory);
      this.crocodile = crocodile;
   }

   public boolean canUse() {
      return super.canUse() && this.crocodile.getPassengers().isEmpty();
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() && this.crocodile.getPassengers().isEmpty();
   }

   protected void checkAndPerformAttack(LivingEntity enemy) {
      this.amCheckAndPerformAttack(enemy, this.mob.distanceToSqr(enemy.getX(), enemy.getY(), enemy.getZ()));
   }

   protected void amCheckAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
      double d0 = AMPlatform.attackReachSqr(this.mob, enemy);
      if (distToEnemySqr <= d0) {
         this.resetAttackCooldown();
         this.mob.swing(InteractionHand.MAIN_HAND);
         AMCompat.doHurtTarget(this.mob, enemy);
      }
   }
}
