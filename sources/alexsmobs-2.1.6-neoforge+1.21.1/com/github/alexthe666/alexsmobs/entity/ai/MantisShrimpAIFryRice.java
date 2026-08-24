package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MantisShrimpAIFryRice extends MoveToBlockGoal {
   private final EntityMantisShrimp mantisShrimp;
   private boolean wasLitPrior = false;
   private int cookingTicks = 0;

   public MantisShrimpAIFryRice(EntityMantisShrimp entityMantisShrimp) {
      super(entityMantisShrimp, 1.0, 8);
      this.mantisShrimp = entityMantisShrimp;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public void stop() {
      this.cookingTicks = 0;
      if (!this.wasLitPrior) {
         BlockPos blockpos = this.getMoveToTarget().below();
         BlockState state = this.mantisShrimp.level().getBlockState(blockpos);
         if (state.getBlock() instanceof AbstractFurnaceBlock && !this.wasLitPrior) {
            this.mantisShrimp.level().setBlockAndUpdate(blockpos, (BlockState)state.setValue(AbstractFurnaceBlock.LIT, false));
         }
      }

      super.stop();
   }

   public void tick() {
      super.tick();
      BlockPos blockpos = this.getMoveToTarget().below();
      if (this.isReachedTarget()) {
         BlockState state = this.mantisShrimp.level().getBlockState(blockpos);
         if (this.mantisShrimp.punchProgress == 0.0F) {
            this.mantisShrimp.punch();
         }

         if (state.getBlock() instanceof AbstractFurnaceBlock && !this.wasLitPrior) {
            this.mantisShrimp.level().setBlockAndUpdate(blockpos, (BlockState)state.setValue(AbstractFurnaceBlock.LIT, true));
         }

         this.cookingTicks++;
         if (this.cookingTicks > 200) {
            this.cookingTicks = 0;
            ItemStack rice = new ItemStack((ItemLike)AMItemRegistry.SHRIMP_FRIED_RICE.get());
            rice.setCount(this.mantisShrimp.getMainHandItem().getCount());
            this.mantisShrimp.setItemInHand(InteractionHand.MAIN_HAND, rice);
         }
      } else {
         this.cookingTicks = 0;
      }
   }

   public boolean canUse() {
      return this.mantisShrimp.getMainHandItem().is(AMTagRegistry.SHRIMP_RICE_FRYABLES) && !this.mantisShrimp.isSitting() && super.canUse();
   }

   public boolean canContinueToUse() {
      return this.mantisShrimp.getMainHandItem().is(AMTagRegistry.SHRIMP_RICE_FRYABLES) && !this.mantisShrimp.isSitting() && super.canContinueToUse();
   }

   public double acceptedDistance() {
      return 3.9000000953674316;
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      if (!worldIn.isEmptyBlock(pos.above())) {
         return false;
      } else {
         BlockState blockstate = worldIn.getBlockState(pos);
         if (blockstate.getBlock() instanceof AbstractFurnaceBlock) {
            this.wasLitPrior = (Boolean)blockstate.getValue(AbstractFurnaceBlock.LIT);
            return true;
         } else {
            return blockstate.is(BlockTags.CAMPFIRES);
         }
      }
   }
}
