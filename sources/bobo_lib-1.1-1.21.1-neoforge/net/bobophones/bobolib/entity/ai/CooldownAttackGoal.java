package net.bobophones.bobolib.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class CooldownAttackGoal extends Goal {
   private final Mob mob;
   private final int attack_cd;
   private final int damage_cd;
   private final double speed;
   private LivingEntity target;
   private int ticks = 0;
   private boolean damage = false;

   public CooldownAttackGoal(Mob mob, double speed, int attack_cd, int damage_cd) {
      this.mob = mob;
      this.speed = speed;
      this.attack_cd = attack_cd;
      this.damage_cd = damage_cd;
   }

   public boolean canUse() {
      LivingEntity target = this.mob.getTarget();
      return target != null && target.isAlive();
   }

   public boolean canContinueToUse() {
      LivingEntity target = this.mob.getTarget();
      return target != null && target.isAlive();
   }

   public void start() {
      this.target = this.mob.getTarget();
      this.mob.getNavigation().moveTo(this.target, this.speed);
      this.mob.setAggressive(true);
      this.ticks = 0;
      this.damage = false;
   }

   public void stop() {
      this.target = null;
      this.mob.setAggressive(false);
      this.SetAttacking(false);
   }

   public void tick() {
      if (this.target != null) {
         this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
         this.mob.getNavigation().moveTo(this.target, this.speed);
         double dist = this.mob.distanceToSqr(this.target);
         if (this.ticks > 0) {
            this.SetAttacking(true);
            if (this.ticks < this.attack_cd - this.damage_cd && this.ticks > this.damage_cd && !this.damage && this.can_attack()) {
               this.damage = true;
               this.mob.doHurtTarget(this.target);
            }

            this.ticks--;
         } else {
            this.damage = false;
            this.SetAttacking(false);
            if (this.can_attack()) {
               this.ticks = this.attack_cd;
            }
         }
      }
   }

   private boolean can_attack() {
      return this.mob.isWithinMeleeAttackRange(this.target);
   }

   protected void SetAttacking(boolean value) {
   }
}
