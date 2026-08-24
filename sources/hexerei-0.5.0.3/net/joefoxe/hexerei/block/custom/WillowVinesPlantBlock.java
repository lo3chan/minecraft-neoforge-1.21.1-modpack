package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WillowVinesPlantBlock extends GrowingPlantBodyBlock {
   public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
   public static final MapCodec<WillowVinesPlantBlock> CODEC = simpleCodec(WillowVinesPlantBlock::new);

   protected MapCodec<? extends WillowVinesPlantBlock> codec() {
      return CODEC;
   }

   public WillowVinesPlantBlock(Properties p_154975_) {
      super(p_154975_, Direction.DOWN, SHAPE, false);
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)ModBlocks.WILLOW_VINES.get();
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
