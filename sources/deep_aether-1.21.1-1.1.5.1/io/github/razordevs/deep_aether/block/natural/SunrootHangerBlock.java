package io.github.razordevs.deep_aether.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SunrootHangerBlock extends Block {
   protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

   public SunrootHangerBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BOTTOM, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
      state.add(new Property[]{BOTTOM});
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockState blockState = level.getBlockState(pos.above());
      return blockState.is(this) || blockState.isFaceSturdy(level, pos, Direction.UP) || blockState.is(BlockTags.LEAVES);
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState p_154149_, LevelAccessor level, BlockPos pos, BlockPos p_154152_) {
      if (!state.canSurvive(level, pos)) {
         level.scheduleTick(pos, this, 1);
      }

      return level.getBlockState(pos.below()).is(this) ? (BlockState)state.setValue(BOTTOM, false) : (BlockState)state.setValue(BOTTOM, true);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      BlockPos pos = context.getClickedPos();
      BlockState blockstate = context.getLevel().getBlockState(pos.above());
      return !fluidstate.isEmpty()
            || !blockstate.isFaceSturdy(context.getLevel(), pos, Direction.UP) && !blockstate.is(BlockTags.LEAVES) && !blockstate.is(this)
         ? null
         : (BlockState)this.defaultBlockState().setValue(BOTTOM, true);
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         level.destroyBlock(pos, true);
      }
   }

   public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      Direction direction = Direction.DOWN;
      double d0 = pos.getX() + 0.55 - random.nextFloat() * 0.1F;
      double d1 = pos.getY() + 0.55 - random.nextFloat() * 0.1F;
      double d2 = pos.getZ() + 0.55 - random.nextFloat() * 0.1F;
      double d3 = 0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F;
      if (random.nextInt(5) == 1) {
         level.addParticle(
            ParticleTypes.END_ROD,
            d0 + direction.getStepX() * d3,
            d1 + direction.getStepY() * d3,
            d2 + direction.getStepZ() * d3,
            random.nextGaussian() * 0.005,
            random.nextGaussian() * 0.005,
            random.nextGaussian() * 0.005
         );
      }
   }
}
