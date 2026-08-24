package com.aetherteam.aether.entity.ai.goal;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.neoforged.neoforge.event.EventHooks;

public class EatAetherGrassGoal extends Goal {
   private static final Predicate<BlockState> IS_TALL_GRASS = BlockStatePredicate.forBlock(Blocks.SHORT_GRASS);
   private final Mob mob;
   private int eatAnimationTick;

   public EatAetherGrassGoal(Mob mob) {
      this.mob = mob;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
   }

   public boolean canUse() {
      if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
         return false;
      } else {
         BlockPos blockpos = this.mob.blockPosition();
         return IS_TALL_GRASS.test(this.mob.level().getBlockState(blockpos))
            ? true
            : this.mob.level().getBlockState(blockpos.below()).is((Block)AetherBlocks.AETHER_GRASS_BLOCK.get());
      }
   }

   public void start() {
      this.eatAnimationTick = this.adjustedTickDelay(40);
      this.mob.level().broadcastEntityEvent(this.mob, (byte)10);
      this.mob.getNavigation().stop();
   }

   public void stop() {
      this.eatAnimationTick = 0;
   }

   public boolean canContinueToUse() {
      return this.eatAnimationTick > 0;
   }

   public int getEatAnimationTick() {
      return this.eatAnimationTick;
   }

   public void tick() {
      this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      if (this.eatAnimationTick == this.adjustedTickDelay(4)) {
         BlockPos blockPos = this.mob.blockPosition();
         if (IS_TALL_GRASS.test(this.mob.level().getBlockState(blockPos))) {
            if (EventHooks.canEntityGrief(this.mob.level(), this.mob)) {
               this.mob.level().destroyBlock(blockPos, false);
            }

            this.mob.ate();
         } else {
            BlockPos blockPos1 = blockPos.below();
            if (this.mob.level().getBlockState(blockPos1).is((Block)AetherBlocks.AETHER_GRASS_BLOCK.get())) {
               if (EventHooks.canEntityGrief(this.mob.level(), this.mob)) {
                  this.mob.level().levelEvent(2001, blockPos1, Block.getId(((Block)AetherBlocks.AETHER_GRASS_BLOCK.get()).defaultBlockState()));
                  this.mob
                     .level()
                     .setBlock(
                        blockPos1,
                        (BlockState)((Block)AetherBlocks.AETHER_DIRT.get())
                           .defaultBlockState()
                           .setValue(
                              AetherBlockStateProperties.DOUBLE_DROPS,
                              (Boolean)this.mob.level().getBlockState(blockPos1).getValue(AetherBlockStateProperties.DOUBLE_DROPS)
                           ),
                        2
                     );
               }

               this.mob.ate();
            }
         }
      }
   }
}
