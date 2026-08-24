package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags.Items;

public class JerboaAIBeg extends Goal {
   private static final TargetingConditions ENTITY_PREDICATE = TargetingConditions.forNonCombat().range(32.0);
   protected final EntityJerboa jerboa;
   private final double speed;
   protected Player closestPlayer;
   private int delayTemptCounter;
   private boolean isRunning;

   public JerboaAIBeg(EntityJerboa jerboa, double speed) {
      this.jerboa = jerboa;
      this.speed = speed;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (this.delayTemptCounter > 0) {
         this.delayTemptCounter--;
         return false;
      } else if (this.jerboa.isInLove()) {
         return false;
      } else {
         this.closestPlayer = AMCompat.getNearestPlayer(this.jerboa.level(), ENTITY_PREDICATE, this.jerboa);
         return this.closestPlayer == null ? false : this.isFood(this.closestPlayer.getMainHandItem()) || this.isFood(this.closestPlayer.getOffhandItem());
      }
   }

   private boolean isFood(ItemStack stack) {
      return stack.is(Items.SEEDS) || this.jerboa.isFood(stack);
   }

   public boolean canContinueToUse() {
      return this.jerboa.getMainHandItem().isEmpty() && this.canUse() && !this.jerboa.isInLove();
   }

   public void start() {
      this.isRunning = true;
   }

   public void stop() {
      this.closestPlayer = null;
      this.jerboa.getNavigation().stop();
      this.delayTemptCounter = 100;
      this.jerboa.setBegging(false);
      this.isRunning = false;
   }

   public void tick() {
      this.jerboa.getLookControl().setLookAt(this.closestPlayer, this.jerboa.getMaxHeadYRot() + 20, this.jerboa.getMaxHeadXRot());
      if (this.jerboa.distanceToSqr(this.closestPlayer) < 12.0) {
         this.jerboa.getNavigation().stop();
         this.jerboa.setBegging(true);
      } else {
         this.jerboa.getNavigation().moveTo(this.closestPlayer, this.speed);
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
