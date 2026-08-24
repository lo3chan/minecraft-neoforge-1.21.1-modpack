package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WillowVinesBlock extends GrowingPlantHeadBlock {
   protected static final VoxelShape SHAPE = Block.box(4.0, 9.0, 4.0, 12.0, 16.0, 12.0);
   public static final MapCodec<WillowVinesBlock> CODEC = simpleCodec(WillowVinesBlock::new);

   public WillowVinesBlock(Properties p_154966_) {
      super(p_154966_, Direction.DOWN, SHAPE, false, 0.1);
   }

   protected MapCodec<? extends WillowVinesBlock> codec() {
      return CODEC;
   }

   protected int getBlocksToGrowWhenBonemealed(RandomSource p_154968_) {
      return NetherVines.getBlocksToGrowWhenBonemealed(p_154968_);
   }

   protected Block getBodyBlock() {
      return (Block)ModBlocks.WILLOW_VINES_PLANT.get();
   }

   protected boolean canGrowInto(BlockState p_154971_) {
      return NetherVines.isValidGrowthState(p_154971_);
   }

   public boolean canSurvive(BlockState p_53876_, LevelReader p_53877_, BlockPos p_53878_) {
      BlockPos blockpos = p_53878_.relative(this.growthDirection.getOpposite());
      BlockState blockstate = p_53877_.getBlockState(blockpos);
      return !this.canAttachTo(blockstate)
         ? false
         : blockstate.is(this.getHeadBlock())
            || blockstate.is(this.getBodyBlock())
            || blockstate.isFaceSturdy(p_53877_, blockpos, this.growthDirection)
            || blockstate.getBlock() instanceof LeavesBlock;
   }
}
