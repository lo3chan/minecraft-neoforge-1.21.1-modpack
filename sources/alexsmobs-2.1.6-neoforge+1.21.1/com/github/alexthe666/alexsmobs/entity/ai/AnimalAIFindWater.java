package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class AnimalAIFindWater extends Goal {
   private final PathfinderMob creature;
   private BlockPos targetPos;
   private final int executionChance = 30;

   public AnimalAIFindWater(PathfinderMob creature) {
      this.creature = creature;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (this.creature.onGround()
         && !this.creature.level().getFluidState(this.creature.blockPosition()).is(FluidTags.WATER)
         && this.creature instanceof ISemiAquatic
         && ((ISemiAquatic)this.creature).shouldEnterWater()
         && (this.creature.getTarget() != null || this.creature.getRandom().nextInt(30) == 0)) {
         this.targetPos = this.generateTarget();
         return this.targetPos != null;
      } else {
         return false;
      }
   }

   public void start() {
      if (this.targetPos != null) {
         this.creature.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0);
      }
   }

   public void tick() {
      if (this.targetPos != null) {
         this.creature.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0);
      }
   }

   public boolean canContinueToUse() {
      if (this.creature instanceof ISemiAquatic && !((ISemiAquatic)this.creature).shouldEnterWater()) {
         this.creature.getNavigation().stop();
         return false;
      } else {
         return !this.creature.getNavigation().isDone()
            && this.targetPos != null
            && !this.creature.level().getFluidState(this.creature.blockPosition()).is(FluidTags.WATER);
      }
   }

   public BlockPos generateTarget() {
      BlockPos blockpos = null;
      RandomSource random = this.creature.getRandom();
      int range = this.creature instanceof ISemiAquatic ? ((ISemiAquatic)this.creature).getWaterSearchRange() : 14;

      for (int i = 0; i < 15; i++) {
         BlockPos blockPos = this.creature.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);

         while (this.creature.level().isEmptyBlock(blockPos) && blockPos.getY() > 1) {
            blockPos = blockPos.below();
         }

         if (this.creature.level().getFluidState(blockPos).is(FluidTags.WATER)) {
            blockpos = blockPos;
         }
      }

      return blockpos;
   }
}
