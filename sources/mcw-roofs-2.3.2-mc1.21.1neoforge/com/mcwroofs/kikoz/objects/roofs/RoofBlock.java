package com.mcwroofs.kikoz.objects.roofs;

import com.mcwroofs.kikoz.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RoofBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{
      Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 8.0, 8.0, 8.0),
      Block.box(0.0, 0.0, 8.0, 8.0, 8.0, 16.0),
      Block.box(0.0, 8.0, 0.0, 8.0, 16.0, 8.0),
      Block.box(0.0, 8.0, 8.0, 8.0, 16.0, 16.0),
      Block.box(8.0, 0.0, 0.0, 16.0, 8.0, 8.0),
      Block.box(8.0, 0.0, 8.0, 16.0, 8.0, 16.0),
      Block.box(8.0, 8.0, 0.0, 16.0, 16.0, 8.0),
      Block.box(8.0, 8.0, 8.0, 16.0, 16.0, 16.0)
   };
   protected static final VoxelShape OCCLUSION = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
   protected static final VoxelShape OCCLUSION_BOTTOM = Block.box(0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
   protected static final VoxelShape[] TOP_SHAPES = createStairShapes(SHAPES[0], SHAPES[2], SHAPES[6], SHAPES[3], SHAPES[7]);
   protected static final VoxelShape[] BOTTOM_SHAPES = createStairShapes(SHAPES[1], SHAPES[4], SHAPES[8], SHAPES[5], SHAPES[9]);
   private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

   private static VoxelShape[] createStairShapes(VoxelShape baseShape, VoxelShape... shapes) {
      VoxelShape[] stairShapes = new VoxelShape[16];

      for (int i = 0; i < 16; i++) {
         stairShapes[i] = baseShape;

         for (int j = 0; j < shapes.length; j++) {
            if ((i & 1 << j) != 0) {
               stairShapes[i] = Shapes.or(stairShapes[i], shapes[j]);
            }
         }
      }

      return stairShapes;
   }

   public RoofBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HALF, Half.BOTTOM))
            .setValue(SHAPE, StairsShape.STRAIGHT)
      );
   }

   protected boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
      return state.getValue(HALF) == Half.BOTTOM ? OCCLUSION : OCCLUSION_BOTTOM;
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext collision) {
      VoxelShape[] shapes = state.getValue(HALF) == Half.TOP ? TOP_SHAPES : BOTTOM_SHAPES;
      int shapeIndex = this.getShapeIndex(state);
      return shapes[SHAPE_BY_STATE[shapeIndex]];
   }

   private int getShapeIndex(BlockState state) {
      return ((StairsShape)state.getValue(SHAPE)).ordinal() * 4 + ((Direction)state.getValue(FACING)).get2DDataValue();
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!state.is(state.getBlock())) {
         level.neighborChanged(this.defaultBlockState(), pos, Blocks.AIR, pos, false);
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!state.is(statetwo.getBlock())) {
         this.onRemove(statetwo, level, pos, statetwo, bool);
      }
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      StairsShape connection = (StairsShape)state.getValue(SHAPE);
      if (item == ItemInit.ROOFING_HAMMER.get()) {
         StairsShape newConnection = StairsShape.STRAIGHT;
         switch (connection) {
            case STRAIGHT:
               newConnection = StairsShape.INNER_LEFT;
               break;
            case INNER_LEFT:
               newConnection = StairsShape.INNER_RIGHT;
               break;
            case INNER_RIGHT:
               newConnection = StairsShape.OUTER_LEFT;
               break;
            case OUTER_LEFT:
               newConnection = StairsShape.OUTER_RIGHT;
               break;
            case OUTER_RIGHT:
               newConnection = StairsShape.STRAIGHT;
         }

         level.setBlock(pos, (BlockState)state.setValue(SHAPE, newConnection), 4);
         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction direction = context.getClickedFace();
      BlockPos blockpos = context.getClickedPos();
      Direction horizontalDirection = context.getHorizontalDirection();
      Half half = direction == Direction.DOWN || direction != Direction.UP && context.getClickLocation().y - blockpos.getY() > 0.5 ? Half.TOP : Half.BOTTOM;
      StairsShape shape = getStairsShape(context.getLevel(), blockpos, horizontalDirection, half);
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, horizontalDirection)).setValue(HALF, half)).setValue(SHAPE, shape);
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return dir.getAxis().isHorizontal()
         ? (BlockState)state.setValue(SHAPE, getStairsShape(access, pos, (Direction)state.getValue(FACING), (Half)state.getValue(HALF)))
         : super.updateShape(state, dir, statetwo, access, pos, postwo);
   }

   private static StairsShape getStairsShape(BlockGetter reader, BlockPos pos, Direction facing, Half half) {
      BlockState blockstate = reader.getBlockState(pos.relative(facing));
      if (isRoof(blockstate) && half == blockstate.getValue(HALF)) {
         Direction direction1 = (Direction)blockstate.getValue(FACING);
         if (direction1.getAxis() != facing.getAxis() && canTakeShape(direction1.getOpposite(), blockstate, reader, pos)) {
            return direction1 == facing.getCounterClockWise() ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = reader.getBlockState(pos.relative(facing.getOpposite()));
      if (isRoof(blockstate1) && half == blockstate1.getValue(HALF)) {
         Direction direction2 = (Direction)blockstate1.getValue(FACING);
         if (direction2.getAxis() != facing.getAxis() && canTakeShape(direction2, blockstate1, reader, pos)) {
            return direction2 == facing.getCounterClockWise() ? StairsShape.INNER_LEFT : StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean canTakeShape(Direction dir, BlockState state, BlockGetter reader, BlockPos pos) {
      BlockState blockstate = reader.getBlockState(pos.relative(dir));
      return !isRoof(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING) || blockstate.getValue(HALF) != state.getValue(HALF);
   }

   public static boolean isRoof(BlockState state) {
      return state.getBlock() instanceof RoofBlock;
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      Direction direction = (Direction)state.getValue(FACING);
      if (mirror == Mirror.LEFT_RIGHT && direction.getAxis() == Axis.Z) {
         return (BlockState)state.setValue(FACING, direction.getOpposite());
      } else {
         return mirror == Mirror.FRONT_BACK && direction.getAxis() == Axis.X
            ? (BlockState)state.setValue(FACING, direction.getOpposite())
            : super.mirror(state, mirror);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
      state.add(new Property[]{FACING, HALF, SHAPE});
   }
}
