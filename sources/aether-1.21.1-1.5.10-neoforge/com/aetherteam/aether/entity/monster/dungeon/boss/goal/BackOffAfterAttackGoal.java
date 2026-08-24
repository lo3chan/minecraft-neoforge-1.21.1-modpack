package com.aetherteam.aether.entity.monster.dungeon.boss.goal;

import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class BackOffAfterAttackGoal extends Goal {
   private final Slider slider;

   public BackOffAfterAttackGoal(Slider slider) {
      this.slider = slider;
   }

   public boolean canUse() {
      return this.slider.getMoveDelay() == 1 && this.slider.attackCooldown() > 0;
   }

   public boolean canContinueToUse() {
      return false;
   }

   public void start() {
      LivingEntity target = this.slider.getTarget();
      if (target != null && this.slider.getBoundingBox().inflate(1.5).contains(target.position())) {
         Direction direction = Slider.calculateDirection(this.slider.getX() - target.getX(), 0.0, this.slider.getZ() - target.getZ());
         this.slider.setTargetPoint(this.slider.position().relative(direction, 2.0));
      }
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }
}
