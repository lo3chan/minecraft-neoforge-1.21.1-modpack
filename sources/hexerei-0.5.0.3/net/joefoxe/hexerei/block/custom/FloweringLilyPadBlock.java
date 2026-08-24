package net.joefoxe.hexerei.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FloweringLilyPadBlock extends WaterlilyBlock {
   protected static final VoxelShape LILY_PAD_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);

   public FloweringLilyPadBlock(Properties builder) {
      super(builder);
   }

   public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
      super.entityInside(state, worldIn, pos, entityIn);
      if (worldIn instanceof ServerLevel && entityIn instanceof Boat) {
         worldIn.destroyBlock(new BlockPos(pos), true, entityIn);
      }
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return LILY_PAD_AABB;
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.below();
      return this.mayPlaceOn(worldIn.getBlockState(blockpos), worldIn, blockpos);
   }
}
