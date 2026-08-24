package vectorwing.farmersdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class BuddingTomatoBlock extends BuddingBushBlock implements BonemealableBlock {
   public BuddingTomatoBlock(Properties properties) {
      super(properties);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if ((Integer)state.getValue(BuddingBushBlock.AGE) == 4) {
         level.setBlock(currentPos, ((TomatoBlock)ModBlocks.TOMATO_CROP.get()).defaultBlockState(), 3);
      }

      return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
   }

   @Override
   public boolean canGrowPastMaxAge() {
      return true;
   }

   @Override
   public void growPastMaxAge(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      level.setBlockAndUpdate(pos, ((TomatoBlock)ModBlocks.TOMATO_CROP.get()).defaultBlockState());
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   protected int getBonemealAgeIncrease(Level level) {
      return Mth.nextInt(level.random, 1, 4);
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      int maxAge = this.getMaxAge();
      int ageGrowth = Math.min(this.getAge(state) + this.getBonemealAgeIncrease(level), 7);
      if (ageGrowth <= maxAge) {
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(AGE, ageGrowth));
      } else {
         int remainingGrowth = ageGrowth - maxAge - 1;
         level.setBlockAndUpdate(
            pos, (BlockState)((TomatoBlock)ModBlocks.TOMATO_CROP.get()).defaultBlockState().setValue(TomatoBlock.VINE_AGE, remainingGrowth)
         );
      }
   }
}
