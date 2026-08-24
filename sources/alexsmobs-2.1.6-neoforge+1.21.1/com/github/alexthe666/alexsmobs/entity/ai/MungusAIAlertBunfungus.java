package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

public class MungusAIAlertBunfungus extends TargetGoal {
   private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
   private static final int ALERT_RANGE_Y = 10;
   private boolean alertSameType;
   private int timestamp;
   private final Class<?>[] toIgnoreDamage;
   @Nullable
   private Class<?>[] toIgnoreAlert;

   public MungusAIAlertBunfungus(PathfinderMob p_26039_, Class<?>... p_26040_) {
      super(p_26039_, true);
      this.toIgnoreDamage = p_26040_;
      this.setFlags(EnumSet.of(Flag.TARGET));
   }

   public boolean canUse() {
      int i = this.mob.getLastHurtByMobTimestamp();
      LivingEntity livingentity = this.mob.getLastHurtByMob();
      if (i != this.timestamp && livingentity != null) {
         if (livingentity.getType() == EntityType.PLAYER && AMCompat.gameRule(this.mob.level(), AMCompat.Rule.UNIVERSAL_ANGER)) {
            return false;
         } else {
            for (Class<?> oclass : this.toIgnoreDamage) {
               if (oclass.isAssignableFrom(livingentity.getClass())) {
                  return false;
               }
            }

            return this.canAttack(livingentity, HURT_BY_TARGETING);
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.mob.setTarget(this.mob.getLastHurtByMob());
      this.targetMob = this.mob.getTarget();
      this.timestamp = this.mob.getLastHurtByMobTimestamp();
      this.unseenMemoryTicks = 300;
      this.alertOthers();
      super.start();
   }

   protected void alertOthers() {
      double d0 = this.getFollowDistance();
      AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0, d0);

      label53:
      for (EntityBunfungus mob : this.mob.level().getEntitiesOfClass(EntityBunfungus.class, aabb, EntitySelector.NO_SPECTATORS)) {
         if (this.mob != mob
            && mob.getTarget() == null
            && !mob.isAlliedTo(this.mob.getLastHurtByMob())
            && mob.defendsMungusAgainst(this.mob.getLastHurtByMob())) {
            if (this.toIgnoreAlert != null) {
               boolean flag = false;
               Class[] var8 = this.toIgnoreAlert;
               int var9 = var8.length;
               int var10 = 0;

               while (true) {
                  if (var10 < var9) {
                     Class<?> oclass = var8[var10];
                     if (mob.getClass() != oclass) {
                        var10++;
                        continue;
                     }

                     flag = true;
                  }

                  if (!flag) {
                     break;
                  }
                  continue label53;
               }
            }

            this.alertOther(mob, this.mob.getLastHurtByMob());
         }
      }
   }

   protected void alertOther(Mob p_26042_, LivingEntity p_26043_) {
      p_26042_.setTarget(p_26043_);
   }
}
