package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CapuchinAIMelee extends MeleeAttackGoal {
   private final EntityCapuchinMonkey monkey;

   public CapuchinAIMelee(EntityCapuchinMonkey monkey, double speedIn, boolean useLongMemory) {
      super(monkey, speedIn, useLongMemory);
      this.monkey = monkey;
   }

   public boolean canUse() {
      return super.canUse() && !this.monkey.attackDecision;
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() && !this.monkey.attackDecision;
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
