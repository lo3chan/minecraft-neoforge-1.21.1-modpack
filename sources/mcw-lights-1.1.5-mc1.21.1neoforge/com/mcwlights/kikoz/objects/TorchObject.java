package com.mcwlights.kikoz.objects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TorchObject extends FaceAttachedHorizontalDirectionalBlock {
   protected final ParticleOptions flame;
   protected static final VoxelShape BOT = Block.box(6.0, 0.0, 6.0, 10.0, 9.0, 10.0);
   protected static final VoxelShape WEST = Block.box(10.0, 0.0, 6.0, 16.0, 16.0, 10.0);
   protected static final VoxelShape EAST = Block.box(0.0, 0.0, 6.0, 6.0, 16.0, 10.0);
   protected static final VoxelShape NORTH = Block.box(6.0, 0.0, 10.0, 10.0, 16.0, 16.0);
   protected static final VoxelShape SOUTH = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 6.0);

   public TorchObject(Properties properties, ParticleOptions flame) {
      super(properties);
      this.flame = flame;
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      return canAttach(world, pos, getConnectedDirection(state).getOpposite()) && state.getValue(FACE) != AttachFace.CEILING;
   }

   public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      Direction direction = (Direction)state.getValue(FACING);
      switch ((AttachFace)state.getValue(FACE)) {
         case FLOOR:
            return BOT;
         case WALL:
            switch (direction) {
               case EAST:
                  return EAST;
               case WEST:
                  return WEST;
               case SOUTH:
                  return SOUTH;
               case NORTH:
                  return NORTH;
               case UP:
                  return BOT;
               case DOWN:
                  return BOT;
            }
         case CEILING:
            return BOT;
         default:
            return null;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, FACE});
   }

   protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
      return null;
   }

   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
      Direction direction = (Direction)stateIn.getValue(FACING);
      double d0 = pos.getX() + 0.5;
      double d1 = pos.getY() + 0.7;
      double d2 = pos.getZ() + 0.5;
      Direction direction1 = direction.getOpposite();
      switch ((AttachFace)stateIn.getValue(FACE)) {
         case FLOOR:
            worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
            worldIn.addParticle(this.flame, d0, d1, d2, 0.0, 0.0, 0.0);
            break;
         case WALL:
            worldIn.addParticle(ParticleTypes.SMOKE, d0 + 0.27 * direction1.getStepX(), d1 + 0.22, d2 + 0.27 * direction1.getStepZ(), 0.0, 0.0, 0.0);
            worldIn.addParticle(this.flame, d0 + 0.27 * direction1.getStepX(), d1 + 0.22, d2 + 0.27 * direction1.getStepZ(), 0.0, 0.0, 0.0);
         case CEILING:
      }
   }
}
