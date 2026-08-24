package com.mcwroofs.kikoz.objects.roofs;

import com.mcwroofs.kikoz.init.BlockInit;
import com.mcwroofs.kikoz.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AwningBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
   protected static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

   public AwningBlock(BlockState state, Properties prop) {
      super(prop);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(SHAPE, StairsShape.STRAIGHT)
      );
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
      return Shapes.empty();
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      return BASE;
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing.getAxis().isHorizontal()
         ? (BlockState)stateIn.setValue(SHAPE, getShapeProperty(stateIn, worldIn, currentPos))
         : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      ItemStack heldItem = player.getItemInHand(hand);
      if (item == ItemInit.ROOFING_HAMMER.get()) {
         state = (BlockState)state.cycle(SHAPE);
         world.setBlock(pos, state, 2);
      }

      if (item instanceof DyeItem) {
         DyeColor dyeColor = ((DyeItem)item).getDyeColor();
         Block blockToPlace = null;
         switch (dyeColor) {
            case WHITE:
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            case LIGHT_GRAY:
               blockToPlace = (Block)BlockInit.LIGHT_GRAY_STRIPED_AWNING.get();
               break;
            case GRAY:
               blockToPlace = (Block)BlockInit.GRAY_STRIPED_AWNING.get();
               break;
            case BLACK:
               blockToPlace = (Block)BlockInit.BLACK_STRIPED_AWNING.get();
               break;
            case BROWN:
               blockToPlace = (Block)BlockInit.BROWN_STRIPED_AWNING.get();
               break;
            case RED:
               blockToPlace = (Block)BlockInit.RED_STRIPED_AWNING.get();
               break;
            case ORANGE:
               blockToPlace = (Block)BlockInit.ORANGE_STRIPED_AWNING.get();
               break;
            case YELLOW:
               blockToPlace = (Block)BlockInit.YELLOW_STRIPED_AWNING.get();
               break;
            case LIME:
               blockToPlace = (Block)BlockInit.LIME_STRIPED_AWNING.get();
               break;
            case GREEN:
               blockToPlace = (Block)BlockInit.GREEN_STRIPED_AWNING.get();
               break;
            case CYAN:
               blockToPlace = (Block)BlockInit.CYAN_STRIPED_AWNING.get();
               break;
            case LIGHT_BLUE:
               blockToPlace = (Block)BlockInit.LIGHT_BLUE_STRIPED_AWNING.get();
               break;
            case BLUE:
               blockToPlace = (Block)BlockInit.BLUE_STRIPED_AWNING.get();
               break;
            case PURPLE:
               blockToPlace = (Block)BlockInit.PURPLE_STRIPED_AWNING.get();
               break;
            case MAGENTA:
               blockToPlace = (Block)BlockInit.MAGENTA_STRIPED_AWNING.get();
               break;
            case PINK:
               blockToPlace = (Block)BlockInit.PINK_STRIPED_AWNING.get();
               break;
            default:
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         Direction currentFacing = (Direction)state.getValue(FACING);
         StairsShape currentShape = (StairsShape)state.getValue(SHAPE);
         BlockState newState = (BlockState)((BlockState)blockToPlace.defaultBlockState().setValue(FACING, currentFacing)).setValue(SHAPE, currentShape);
         world.setBlockAndUpdate(pos, newState);
         if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos blockpos = context.getClickedPos();
      Direction horizontalDirection = context.getHorizontalDirection();
      BlockState blockstate = (BlockState)this.defaultBlockState().setValue(FACING, horizontalDirection);
      return (BlockState)blockstate.setValue(SHAPE, getShapeProperty(blockstate, context.getLevel(), blockpos));
   }

   private static StairsShape getShapeProperty(BlockState state, BlockGetter worldIn, BlockPos pos) {
      Direction direction = (Direction)state.getValue(FACING);
      BlockState blockstate = worldIn.getBlockState(pos.relative(direction));
      if (isBlockStairs(blockstate)) {
         Direction direction1 = (Direction)blockstate.getValue(FACING);
         if (direction1.getAxis() != direction.getAxis()) {
            return direction1 == direction.getCounterClockWise() ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = worldIn.getBlockState(pos.relative(direction.getOpposite()));
      if (isBlockStairs(blockstate1)) {
         Direction direction2 = (Direction)blockstate1.getValue(FACING);
         if (direction2.getAxis() != direction.getAxis()) {
            return direction2 == direction.getCounterClockWise() ? StairsShape.INNER_LEFT : StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   public static boolean isBlockStairs(BlockState state) {
      return state.getBlock() instanceof AwningBlock;
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      Direction direction = (Direction)state.getValue(FACING);
      StairsShape stairsshape = (StairsShape)state.getValue(SHAPE);
      switch (mirrorIn) {
         case LEFT_RIGHT:
            if (direction.getAxis() == Axis.Z) {
               return (BlockState)state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, this.getMirroredShape(stairsshape));
            }
            break;
         case FRONT_BACK:
            if (direction.getAxis() == Axis.X) {
               return (BlockState)state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, this.getMirroredShape(stairsshape));
            }
      }

      return super.mirror(state, mirrorIn);
   }

   private StairsShape getMirroredShape(StairsShape shape) {
      switch (shape) {
         case INNER_LEFT:
            return StairsShape.INNER_RIGHT;
         case INNER_RIGHT:
            return StairsShape.INNER_LEFT;
         case OUTER_LEFT:
            return StairsShape.OUTER_RIGHT;
         case OUTER_RIGHT:
            return StairsShape.OUTER_LEFT;
         default:
            return shape;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, SHAPE});
   }
}
