package com.aetherteam.aether.entity.ai.goal;

import com.aetherteam.aether.entity.passive.Moa;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

public class MoaFollowGoal extends TemptGoal {
   private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();
   private final TargetingConditions targetingConditions;
   private final Moa moa;
   private final double speedModifier;
   @Nullable
   private Player player;
   private int calmDown;
   private boolean isRunning;

   public MoaFollowGoal(Moa moa, double speedModifier) {
      super(moa, speedModifier, Ingredient.EMPTY, false);
      this.moa = moa;
      this.speedModifier = speedModifier;
      this.targetingConditions = TEMP_TARGETING.copy().selector(livingEntity -> livingEntity.getUUID().equals(this.moa.getFollowing()));
   }

   public boolean canUse() {
      if (this.calmDown > 0) {
         this.calmDown--;
         return false;
      } else {
         this.player = this.moa.level().getNearestPlayer(this.targetingConditions, this.moa);
         if (this.player != null && this.moa.distanceToSqr(this.player) >= 6.25) {
            this.mob.getMoveControl().setWantedPosition(this.player.getX(), this.player.getY(), this.player.getZ(), this.speedModifier);
         }

         return this.player != null;
      }
   }

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public void start() {
      this.isRunning = true;
   }

   public void stop() {
      this.player = null;
      this.moa.getNavigation().stop();
      this.calmDown = reducedTickDelay(100);
      this.isRunning = false;
   }

   public void tick() {
      if (this.player != null) {
         this.moa.getLookControl().setLookAt(this.player, this.moa.getMaxHeadYRot() + 20, this.moa.getMaxHeadXRot());
         if (this.moa.distanceToSqr(this.player) < 6.25) {
            this.moa.getNavigation().stop();
         } else {
            this.moa.getNavigation().moveTo(this.player, this.speedModifier);
         }
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
