package io.github.razordevs.deep_aether.block.building;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DoubleTorchBlock extends Block {
   public static final MapCodec<DoubleTorchBlock> CODEC = simpleCodec(DoubleTorchBlock::new);
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   protected static final int AABB_STANDING_OFFSET = 2;
   protected static final VoxelShape LOWER_AABB = Shapes.join(Shapes.empty(), Shapes.box(0.4375, 0.0, 0.4375, 0.5625, 1.0, 0.5625), BooleanOp.OR);

   protected MapCodec<? extends Block> codec() {
      return CODEC;
   }

   public DoubleTorchBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   public VoxelShape makeTopShape() {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.join(shape, Shapes.box(0.4375, 0.0, 0.4375, 0.5625, 0.125, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.5, 0.4375, 0.3125, 0.5, 0.875, 0.6875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.3125, 0.4375, 0.5, 0.6875, 0.875, 0.5), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.4375, 0.6875), BooleanOp.OR);
      return Shapes.join(shape, Shapes.box(0.375, 0.125, 0.375, 0.625, 0.3125, 0.625), BooleanOp.OR);
   }

   protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
      return ((DoubleBlockHalf)blockState.getValue(HALF)).equals(DoubleBlockHalf.LOWER) ? LOWER_AABB : this.makeTopShape();
   }

   protected BlockState updateShape(BlockState blockState, Direction direction, BlockState state, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos pos) {
      DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)blockState.getValue(HALF);
      if (direction.getAxis() != Axis.Y
         || doubleblockhalf == DoubleBlockHalf.LOWER != (direction == Direction.UP)
         || state.is(this) && state.getValue(HALF) != doubleblockhalf) {
         return doubleblockhalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(blockState, direction, state, levelAccessor, blockPos, pos);
      } else {
         return Blocks.AIR.defaultBlockState();
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos blockpos = context.getClickedPos();
      Level level = context.getLevel();
      return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)
         ? super.getStateForPlacement(context)
         : null;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF});
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      BlockPos blockpos = blockPos.above();
      level.setBlock(blockpos, (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos pos) {
      if (blockState.getValue(HALF) != DoubleBlockHalf.UPPER) {
         return canSupportCenter(levelReader, pos.below(), Direction.UP);
      } else {
         BlockState blockstate = levelReader.getBlockState(pos.below());
         return blockState.getBlock() != this ? false : blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
      }
   }

   public static void placeAt(LevelAccessor accessor, BlockState state, BlockPos pos, int i) {
      BlockPos blockpos = pos.above();
      accessor.setBlock(pos, (BlockState)state.setValue(HALF, DoubleBlockHalf.LOWER), i);
      accessor.setBlock(blockpos, (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), i);
   }

   public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      if (!level.isClientSide && player.isCreative()) {
         preventDropFromBottomPart(level, blockPos, blockState, player);
      }

      return super.playerWillDestroy(level, blockPos, blockState, player);
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
      super.playerDestroy(level, player, blockPos, Blocks.AIR.defaultBlockState(), blockEntity, stack);
   }

   protected static void preventDropFromBottomPart(Level level, BlockPos blockPos, BlockState value, Player player) {
      if (((DoubleBlockHalf)value.getValue(HALF)).equals(DoubleBlockHalf.UPPER)) {
         BlockPos blockpos = blockPos.below();
         BlockState blockstate = level.getBlockState(blockpos);
         if (blockstate.is(value.getBlock()) && ((DoubleBlockHalf)blockstate.getValue(HALF)).equals(DoubleBlockHalf.LOWER)) {
            BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
            level.setBlock(blockpos, blockstate1, 35);
            level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
         }
      }
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
      if (!((DoubleBlockHalf)blockState.getValue(HALF)).equals(DoubleBlockHalf.LOWER)) {
         double d0 = blockPos.getX() + 0.5;
         double d1 = blockPos.getY() + 1.0;
         double d2 = blockPos.getZ() + 0.5;
         if (randomSource.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, d0, d1, d2, 0.0, 0.01, 0.0);
         }
      }
   }

   public static int calcLightLevel(BlockState state) {
      return ((DoubleBlockHalf)state.getValue(HALF)).equals(DoubleBlockHalf.UPPER) ? 15 : 0;
   }
}
