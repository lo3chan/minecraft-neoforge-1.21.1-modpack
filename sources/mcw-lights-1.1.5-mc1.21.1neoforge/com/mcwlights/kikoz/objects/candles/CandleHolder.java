package com.mcwlights.kikoz.objects.candles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleHolder extends LowCandleHolder {
   private static final VoxelShape BASE = Block.box(5.0, 0.0, 5.0, 11.0, 12.0, 11.0);

   public CandleHolder(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true));
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      return BASE;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType type) {
      return false;
   }

   @Override
   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
      double d0 = pos.getX() + 0.5;
      double d1 = pos.getY() + 0.9;
      double d2 = pos.getZ() + 0.5;
      Boolean i = (Boolean)stateIn.getValue(LIT);
      if (i) {
         worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
      }
   }
}
