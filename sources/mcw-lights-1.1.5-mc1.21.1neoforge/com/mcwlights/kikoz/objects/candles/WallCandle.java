package com.mcwlights.kikoz.objects.candles;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallCandle extends CandleHolder {
   public static final DirectionProperty FACING = DirectionProperty.create("facing", Plane.HORIZONTAL);
   private static final VoxelShape NORTH = Block.box(4.5, 3.0, 7.0, 11.5, 14.0, 16.0);
   private static final VoxelShape WEST = Block.box(7.0, 3.0, 4.5, 16.0, 14.0, 11.5);
   private static final VoxelShape SOUTH = Block.box(4.5, 3.0, 0.0, 11.5, 14.0, 9.0);
   private static final VoxelShape EAST = Block.box(0.0, 3.0, 4.5, 9.0, 14.0, 11.5);

   public WallCandle(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, true));
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
         default:
            return WEST;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, LIT});
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @Override
   public void animateTick(BlockState state, Level worldIn, BlockPos pos, RandomSource rand) {
      double d0 = pos.getX() + 0.5;
      double d1 = pos.getY() + 1.0;
      double d2 = pos.getZ() + 0.5;
      Direction direction = (Direction)state.getValue(FACING);
      switch (direction) {
         case NORTH:
            d2 += 0.15;
            break;
         case SOUTH:
            d2 -= 0.15;
            break;
         case EAST:
            d0 -= 0.15;
            break;
         case WEST:
            d0 += 0.15;
      }

      Boolean i = (Boolean)state.getValue(LIT);
      if (i) {
         worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
      }
   }
}
