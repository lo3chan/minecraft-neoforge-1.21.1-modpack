package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.function.BiFunction;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PaleMushroomBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<PaleMushroomBlock> CODEC = simpleCodec(PaleMushroomBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final IntegerProperty AMOUNT = BlockStateProperties.FLOWER_AMOUNT;
   private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES = Util.memoize(
      (direction, amount) -> {
         VoxelShape[] quadrants = new VoxelShape[]{
            Block.box(8.0, 0.0, 8.0, 16.0, 6.0, 16.0),
            Block.box(8.0, 0.0, 0.0, 16.0, 6.0, 8.0),
            Block.box(0.0, 0.0, 0.0, 8.0, 6.0, 8.0),
            Block.box(0.0, 0.0, 8.0, 8.0, 6.0, 16.0)
         };
         VoxelShape shape = Shapes.empty();

         for (int i = 0; i < amount; i++) {
            int index = Math.floorMod(i - direction.get2DDataValue(), 4);
            shape = Shapes.or(shape, quadrants[index]);
         }

         return shape.singleEncompassing();
      }
   );

   public PaleMushroomBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(AMOUNT, 1));
   }

   protected MapCodec<? extends BushBlock> codec() {
      return CODEC;
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE_BY_PROPERTIES.apply((Direction)state.getValue(FACING), (Integer)state.getValue(AMOUNT));
   }

   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos belowPos = pos.below();
      BlockState belowState = level.getBlockState(belowPos);
      return belowState.is(BlockTags.MUSHROOM_GROW_BLOCK)
         || belowState.is((Block)ModBlocks.INFUSED_DIRT.get())
         || level.getRawBrightness(pos, 0) < 13 && belowState.isSolidRender(level, belowPos);
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
      return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && (Integer)state.getValue(AMOUNT) < 4
         || super.canBeReplaced(state, context);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState existingState = context.getLevel().getBlockState(context.getClickedPos());
      if (existingState.is(this)) {
         return (BlockState)existingState.setValue(AMOUNT, Math.min(4, (Integer)existingState.getValue(AMOUNT) + 1));
      } else {
         BlockState placementState = (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
         return this.canSurvive(placementState, context.getLevel(), context.getClickedPos()) ? placementState : null;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, AMOUNT});
   }

   protected BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      int amount = (Integer)state.getValue(AMOUNT);
      if (amount < 4) {
         level.setBlock(pos, (BlockState)state.setValue(AMOUNT, amount + 1), 2);
      } else {
         popResource(level, pos, new ItemStack(this));
      }
   }
}
