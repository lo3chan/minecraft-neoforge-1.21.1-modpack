package com.aetherteam.aether.entity.ai.goal;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry;
import java.util.Comparator;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class MostDamageTargetGoal extends TargetGoal {
   private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
   private final Object2DoubleMap<LivingEntity> attackers = new Object2DoubleOpenHashMap();
   private int lastHurtTimestamp;
   @Nullable
   private LivingEntity primaryTarget;
   private final float calmDownRate;
   private int aiTicks;

   public MostDamageTargetGoal(Mob mob) {
      this(mob, 1.0F);
   }

   public MostDamageTargetGoal(Mob mob, float calmDownRate) {
      super(mob, true);
      this.setFlags(EnumSet.of(Flag.TARGET));
      this.calmDownRate = calmDownRate;
   }

   public boolean canUse() {
      this.tickAggro();
      if (this.attackers.isEmpty()) {
         return false;
      } else {
         int mobTimestamp = this.mob.getLastHurtByMobTimestamp();
         LivingEntity lastAttacker = this.mob.getLastHurtByMob();
         if (lastAttacker != null && mobTimestamp != this.lastHurtTimestamp) {
            this.primaryTarget = this.getStrongestAttacker();
         }

         return this.primaryTarget != null;
      }
   }

   public boolean canContinueToUse() {
      this.tickAggro();
      return this.mob.getLastHurtByMobTimestamp() == this.lastHurtTimestamp
         && this.mob.getTarget() != null
         && !this.mob.getTarget().isDeadOrDying()
         && super.canContinueToUse();
   }

   public void start() {
      this.mob.setTarget(this.primaryTarget);
      this.lastHurtTimestamp = this.mob.getLastHurtByMobTimestamp();
      super.start();
   }

   public void stop() {
      this.primaryTarget = null;
      super.stop();
   }

   public void addAggro(LivingEntity attacker, double damage) {
      this.attackers.mergeDouble(attacker, damage, Double::sum);
   }

   private void tickAggro() {
      if (++this.aiTicks >= 10) {
         this.aiTicks = 0;
         ObjectIterator<Entry<LivingEntity>> iterator = this.attackers.object2DoubleEntrySet().iterator();

         while (iterator.hasNext()) {
            java.util.Map.Entry<LivingEntity, Double> entry = (java.util.Map.Entry<LivingEntity, Double>)iterator.next();
            LivingEntity livingEntity = entry.getKey();
            Double oldAggro = entry.getValue();
            double aggro = oldAggro - this.calmDownRate;
            if (livingEntity.isAlive()
               && (!(aggro <= 0.0) || this.canAttack(livingEntity, HURT_BY_TARGETING))
               && !(livingEntity instanceof Player player && (player.isCreative() || player.isSpectator()))) {
               entry.setValue(aggro);
            } else {
               iterator.remove();
            }
         }
      }
   }

   @Nullable
   private LivingEntity getStrongestAttacker() {
      java.util.Map.Entry<LivingEntity, Double> entry = this.attackers
         .object2DoubleEntrySet()
         .stream()
         .filter(entityEntry -> this.canAttack((LivingEntity)entityEntry.getKey(), HURT_BY_TARGETING))
         .max(Comparator.comparingDouble(java.util.Map.Entry::getValue))
         .orElse(null);
      return entry == null ? null : entry.getKey();
   }

   @Nullable
   public LivingEntity getPrimaryTarget() {
      return this.primaryTarget;
   }
}
