package com.mcwlights.kikoz.objects.candles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripleCandle extends DoubleCandle {
   private static final VoxelShape NS = Block.box(0.0, 0.0, 5.0, 16.0, 14.0, 11.0);
   private static final VoxelShape WE = Block.box(5.0, 0.0, 0.0, 11.0, 14.0, 16.0);

   public TripleCandle(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, true));
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return NS;
         case SOUTH:
            return NS;
         case EAST:
            return WE;
         case WEST:
         default:
            return WE;
      }
   }

   @Override
   public void animateTick(BlockState state, Level worldIn, BlockPos pos, RandomSource rand) {
      double d0 = pos.getX() + 0.5;
      double d1 = pos.getY() + 0.85;
      double d2 = pos.getZ() + 0.5;
      double d3 = pos.getZ() + 0.5;
      double d4 = pos.getX() + 0.5;
      double d6 = pos.getZ() + 0.5;
      double d7 = pos.getX() + 0.5;
      double d8 = pos.getY() + 1.0;
      Direction direction = (Direction)state.getValue(FACING);
      switch (direction) {
         case NORTH:
            d0 += 0.3;
            d4 -= 0.3;
            break;
         case SOUTH:
            d0 += 0.3;
            d4 -= 0.3;
            break;
         case EAST:
            d2 += 0.3;
            d3 -= 0.3;
            break;
         case WEST:
            d2 += 0.3;
            d3 -= 0.3;
      }

      Boolean i = (Boolean)state.getValue(LIT);
      if (i) {
         worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.SMOKE, d4, d1, d3, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d4, d1, d3, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.SMOKE, d7, d8, d6, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d7, d8, d6, 0.0, 0.0, 0.0);
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, LIT});
   }
}
