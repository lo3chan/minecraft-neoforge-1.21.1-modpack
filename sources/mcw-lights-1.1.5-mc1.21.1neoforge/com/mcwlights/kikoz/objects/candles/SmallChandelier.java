package com.mcwlights.kikoz.objects.candles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallChandelier extends CandleHolder {
   private static final VoxelShape ONE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private static final VoxelShape TWO = Block.box(4.0, 8.0, 4.0, 12.0, 17.0, 12.0);
   private static final VoxelShape BASE = Shapes.or(ONE, TWO);

   public SmallChandelier(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true));
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      return BASE;
   }

   @Override
   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
      double d1 = pos.getY() + 0.6;
      double d0 = pos.getX() - 0.0;
      double d2 = pos.getZ() + 0.5;
      double d3 = pos.getZ() + 1.0;
      double d4 = pos.getX() + 0.5;
      double d5 = pos.getZ() + 0.5;
      double d6 = pos.getX() + 1.0;
      double d7 = pos.getZ() - 0.0;
      double d8 = pos.getX() + 0.5;
      Boolean i = (Boolean)stateIn.getValue(LIT);
      if (i) {
         worldIn.addParticle(ParticleTypes.SMOKE, d4, d1, d3, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d4, d1, d3, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.SMOKE, d6, d1, d5, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d6, d1, d5, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.SMOKE, d8, d1, d7, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d8, d1, d7, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
         worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
      }
   }
}
