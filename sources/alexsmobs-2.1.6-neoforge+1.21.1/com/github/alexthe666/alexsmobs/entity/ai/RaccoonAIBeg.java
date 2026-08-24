package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class RaccoonAIBeg extends Goal {
   private static final TargetingConditions ENTITY_PREDICATE = TargetingConditions.forNonCombat().range(32.0);
   protected final EntityRaccoon raccoon;
   private final double speed;
   protected Player closestPlayer;
   private int delayTemptCounter;
   private boolean isRunning;

   public RaccoonAIBeg(EntityRaccoon raccoon, double speed) {
      this.raccoon = raccoon;
      this.speed = speed;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (this.delayTemptCounter > 0) {
         this.delayTemptCounter--;
         return false;
      } else if (!this.raccoon.getMainHandItem().isEmpty()) {
         return false;
      } else {
         this.closestPlayer = AMCompat.getNearestPlayer(this.raccoon.level(), ENTITY_PREDICATE, this.raccoon);
         return this.closestPlayer == null
            ? false
            : EntityRaccoon.isRaccoonFood(this.closestPlayer.getMainHandItem()) || EntityRaccoon.isRaccoonFood(this.closestPlayer.getOffhandItem());
      }
   }

   public boolean canContinueToUse() {
      return this.raccoon.getMainHandItem().isEmpty() && this.canUse();
   }

   public void start() {
      this.isRunning = true;
   }

   public void stop() {
      this.closestPlayer = null;
      this.raccoon.getNavigation().stop();
      this.delayTemptCounter = 100;
      this.raccoon.setBegging(false);
      this.isRunning = false;
   }

   public void tick() {
      this.raccoon.getLookControl().setLookAt(this.closestPlayer, this.raccoon.getMaxHeadYRot() + 20, this.raccoon.getMaxHeadXRot());
      if (this.raccoon.distanceToSqr(this.closestPlayer) < 12.0) {
         this.raccoon.getNavigation().stop();
         this.raccoon.setBegging(true);
      } else {
         this.raccoon.getNavigation().moveTo(this.closestPlayer, this.speed);
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
