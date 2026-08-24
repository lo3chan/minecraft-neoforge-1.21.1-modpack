package com.mcwroofs.kikoz.objects.gutters;

import com.mcwroofs.kikoz.init.BlockInit;
import com.mcwroofs.kikoz.init.ItemInit;
import com.mcwroofs.kikoz.objects.roofs.AwningBlock;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RainGutter extends AwningBlock {
   private static final BooleanProperty WATER = BooleanProperty.create("water");
   private static final VoxelShape[] SHAPES = new VoxelShape[12];

   private int getShapeIndex(StairsShape shape, Direction facing) {
      switch (shape) {
         case OUTER_RIGHT:
            return switch (facing) {
               case WEST -> 10;
               case EAST -> 8;
               case SOUTH -> 9;
               case NORTH -> 11;
               default -> -1;
            };
         case INNER_RIGHT:
            return switch (facing) {
               case WEST -> 6;
               case EAST -> 4;
               case SOUTH -> 5;
               case NORTH -> 7;
               default -> -1;
            };
         case OUTER_LEFT:
            return switch (facing) {
               case WEST -> 9;
               case EAST -> 11;
               case SOUTH -> 8;
               case NORTH -> 10;
               default -> -1;
            };
         case INNER_LEFT:
            return switch (facing) {
               case WEST -> 5;
               case EAST -> 7;
               case SOUTH -> 4;
               case NORTH -> 6;
               default -> -1;
            };
         default:
            return switch (facing) {
               case WEST -> 2;
               case EAST -> 0;
               case SOUTH -> 1;
               case NORTH -> 3;
               default -> -1;
            };
      }
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      StairsShape shape = (StairsShape)state.getValue(SHAPE);
      Direction facing = (Direction)state.getValue(FACING);
      int shapeIndex = this.getShapeIndex(shape, facing);
      return shapeIndex >= 0 && shapeIndex < SHAPES.length ? SHAPES[shapeIndex] : Shapes.empty();
   }

   public RainGutter(BlockState state, Properties prop) {
      super(state, prop);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATER, false)).setValue(FACING, Direction.NORTH))
            .setValue(SHAPE, StairsShape.STRAIGHT)
      );
   }

   @Override
   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      Boolean i = (Boolean)state.getValue(WATER);
      ItemStack heldItem = player.getItemInHand(hand);
      if (item == ItemInit.ROOFING_HAMMER.get()) {
         state = (BlockState)state.cycle(SHAPE);
         world.setBlock(pos, state, 2);
      }

      if (item == Items.WATER_BUCKET && !i) {
         state = (BlockState)state.cycle(WATER);
         world.setBlock(pos, state, 2);
         player.setItemInHand(hand, new ItemStack(Items.BUCKET));
      }

      if (item == Items.BUCKET && i) {
         state = (BlockState)state.cycle(WATER);
         world.setBlock(pos, state, 2);
         itemstack.shrink(1);
         player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
      }

      if (item == Items.GLASS_BOTTLE && i) {
         state = (BlockState)state.cycle(WATER);
         world.setBlock(pos, state, 2);
         player.setItemInHand(hand, new ItemStack(Items.POTION));
      }

      if (item instanceof DyeItem) {
         DyeColor dyeColor = ((DyeItem)item).getDyeColor();
         Block blockToPlace = null;
         switch (dyeColor) {
            case WHITE:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_WHITE.get();
               break;
            case LIGHT_GRAY:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_LIGHT_GRAY.get();
               break;
            case GRAY:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_GRAY.get();
               break;
            case BLACK:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_BLACK.get();
               break;
            case BROWN:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_BROWN.get();
               break;
            case RED:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_RED.get();
               break;
            case ORANGE:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_ORANGE.get();
               break;
            case YELLOW:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_YELLOW.get();
               break;
            case LIME:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_LIME.get();
               break;
            case GREEN:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_GREEN.get();
               break;
            case CYAN:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_CYAN.get();
               break;
            case LIGHT_BLUE:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_LIGHT_BLUE.get();
               break;
            case BLUE:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_BLUE.get();
               break;
            case PURPLE:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_PURPLE.get();
               break;
            case MAGENTA:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_MAGENTA.get();
               break;
            case PINK:
               blockToPlace = (Block)BlockInit.GUTTER_BASE_PINK.get();
               break;
            default:
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         Direction currentFacing = (Direction)state.getValue(FACING);
         Boolean currentWater = (Boolean)state.getValue(WATER);
         StairsShape currentAbove = (StairsShape)state.getValue(SHAPE);
         BlockState newState = (BlockState)((BlockState)((BlockState)blockToPlace.defaultBlockState().setValue(FACING, currentFacing))
               .setValue(WATER, currentWater))
            .setValue(SHAPE, currentAbove);
         world.setBlockAndUpdate(pos, newState);
         if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public static boolean isBlockStairs(BlockState state) {
      return state.getBlock() instanceof RainGutter;
   }

   public boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      BlockPos blockpos = pos.above(30);
      if (state.hasProperty(WATER)) {
         if (world.isRainingAt(blockpos) && !(Boolean)state.getValue(WATER)) {
            world.setBlockAndUpdate(pos, (BlockState)state.setValue(WATER, true));
            this.scheduleSpread(world, pos);
         }

         if (!world.isRainingAt(blockpos) && (Boolean)state.getValue(WATER)) {
            world.setBlockAndUpdate(pos, (BlockState)state.setValue(WATER, false));
            this.scheduleClear(world, pos);
         }
      }
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      BlockPos blockpos = pos.above(30);
      if (world.isRainingAt(blockpos)) {
         if (state.hasProperty(WATER) && !(Boolean)state.getValue(WATER)) {
            world.setBlockAndUpdate(pos, (BlockState)state.setValue(WATER, true));
            this.scheduleSpread(world, pos);
         }
      } else if (state.hasProperty(WATER) && (Boolean)state.getValue(WATER)) {
         world.setBlockAndUpdate(pos, (BlockState)state.setValue(WATER, false));
         this.scheduleClear(world, pos);
      }
   }

   private void scheduleSpread(ServerLevel world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);

      for (BlockPos neighbor : this.getNeighborPositions(pos)) {
         BlockState neighborState = world.getBlockState(neighbor);
         if (neighborState.getBlock() == state.getBlock() && neighborState.hasProperty(WATER) && !(Boolean)neighborState.getValue(WATER)) {
            world.scheduleTick(neighbor, this, 6);
         }
      }
   }

   private void scheduleClear(ServerLevel world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);

      for (BlockPos neighbor : this.getNeighborPositions(pos)) {
         BlockState neighborState = world.getBlockState(neighbor);
         if (neighborState.getBlock() == state.getBlock() && neighborState.hasProperty(WATER) && (Boolean)neighborState.getValue(WATER)) {
            world.scheduleTick(neighbor, this, 6);
         }
      }
   }

   private List<BlockPos> getNeighborPositions(BlockPos pos) {
      return List.of(pos.north(), pos.south(), pos.east(), pos.west());
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WATER, FACING, SHAPE});
   }

   static {
      SHAPES[0] = Shapes.or(
         Block.box(15.0, 12.0, 0.0, 16.0, 15.0, 16.0),
         new VoxelShape[]{Block.box(11.0, 12.0, 0.0, 12.0, 15.0, 16.0), Block.box(12.0, 11.0, 0.0, 15.0, 12.0, 16.0)}
      );
      SHAPES[1] = Shapes.or(
         Block.box(0.0, 12.0, 15.0, 16.0, 15.0, 16.0),
         new VoxelShape[]{Block.box(0.0, 12.0, 11.0, 16.0, 15.0, 12.0), Block.box(0.0, 11.0, 12.0, 16.0, 12.0, 15.0)}
      );
      SHAPES[2] = Shapes.or(
         Block.box(4.0, 12.0, 0.0, 5.0, 15.0, 16.0), new VoxelShape[]{Block.box(0.0, 12.0, 0.0, 1.0, 15.0, 16.0), Block.box(1.0, 11.0, 0.0, 4.0, 12.0, 16.0)}
      );
      SHAPES[3] = Shapes.or(
         Block.box(0.0, 12.0, 4.0, 16.0, 15.0, 5.0), new VoxelShape[]{Block.box(0.0, 12.0, 0.0, 16.0, 15.0, 1.0), Block.box(0.0, 11.0, 1.0, 16.0, 12.0, 4.0)}
      );
      SHAPES[4] = Shapes.or(
         Block.box(15.0, 12.0, 0.0, 16.0, 15.0, 16.0),
         new VoxelShape[]{
            Block.box(11.0, 12.0, 0.0, 12.0, 15.0, 12.0),
            Block.box(12.0, 11.0, 0.0, 15.0, 12.0, 15.0),
            Block.box(0.0, 12.0, 11.0, 11.0, 15.0, 12.0),
            Block.box(0.0, 12.0, 15.0, 15.0, 15.0, 16.0),
            Block.box(0.0, 11.0, 12.0, 12.0, 12.0, 15.0)
         }
      );
      SHAPES[5] = Shapes.or(
         Block.box(0.0, 12.0, 15.0, 16.0, 15.0, 16.0),
         new VoxelShape[]{
            Block.box(4.0, 12.0, 11.0, 16.0, 15.0, 12.0),
            Block.box(1.0, 11.0, 12.0, 16.0, 12.0, 15.0),
            Block.box(4.0, 12.0, 0.0, 5.0, 15.0, 11.0),
            Block.box(0.0, 12.0, 0.0, 1.0, 15.0, 15.0),
            Block.box(1.0, 11.0, 0.0, 4.0, 12.0, 12.0)
         }
      );
      SHAPES[6] = Shapes.or(
         Block.box(0.0, 12.0, 0.0, 1.0, 15.0, 16.0),
         new VoxelShape[]{
            Block.box(4.0, 12.0, 4.0, 5.0, 15.0, 16.0),
            Block.box(1.0, 11.0, 1.0, 4.0, 12.0, 16.0),
            Block.box(5.0, 12.0, 4.0, 16.0, 15.0, 5.0),
            Block.box(1.0, 12.0, 0.0, 16.0, 15.0, 1.0),
            Block.box(4.0, 11.0, 1.0, 16.0, 12.0, 4.0)
         }
      );
      SHAPES[7] = Shapes.or(
         Block.box(0.0, 12.0, 0.0, 16.0, 15.0, 1.0),
         new VoxelShape[]{
            Block.box(0.0, 12.0, 4.0, 12.0, 15.0, 5.0),
            Block.box(0.0, 11.0, 1.0, 15.0, 12.0, 4.0),
            Block.box(11.0, 12.0, 5.0, 12.0, 15.0, 16.0),
            Block.box(15.0, 12.0, 1.0, 16.0, 15.0, 16.0),
            Block.box(12.0, 11.0, 4.0, 15.0, 12.0, 16.0)
         }
      );
      SHAPES[8] = Shapes.or(
         Block.box(11.0, 12.0, 11.0, 12.0, 15.0, 16.0),
         new VoxelShape[]{
            Block.box(12.0, 11.0, 12.0, 15.0, 12.0, 16.0),
            Block.box(15.0, 12.0, 15.0, 16.0, 15.0, 16.0),
            Block.box(12.0, 12.0, 11.0, 16.0, 15.0, 12.0),
            Block.box(15.0, 11.0, 12.0, 16.0, 12.0, 15.0)
         }
      );
      SHAPES[9] = Shapes.or(
         Block.box(0.0, 12.0, 11.0, 5.0, 15.0, 12.0),
         new VoxelShape[]{
            Block.box(0.0, 11.0, 12.0, 4.0, 12.0, 15.0),
            Block.box(0.0, 12.0, 15.0, 1.0, 15.0, 16.0),
            Block.box(4.0, 12.0, 12.0, 5.0, 15.0, 16.0),
            Block.box(1.0, 11.0, 15.0, 4.0, 12.0, 16.0)
         }
      );
      SHAPES[10] = Shapes.or(
         Block.box(4.0, 12.0, 0.0, 5.0, 15.0, 5.0),
         new VoxelShape[]{
            Block.box(1.0, 11.0, 0.0, 4.0, 12.0, 4.0),
            Block.box(0.0, 12.0, 0.0, 1.0, 15.0, 1.0),
            Block.box(0.0, 12.0, 4.0, 4.0, 15.0, 5.0),
            Block.box(0.0, 11.0, 1.0, 1.0, 12.0, 4.0)
         }
      );
      SHAPES[11] = Shapes.or(
         Block.box(11.0, 12.0, 4.0, 16.0, 15.0, 5.0),
         new VoxelShape[]{
            Block.box(12.0, 11.0, 1.0, 16.0, 12.0, 4.0),
            Block.box(15.0, 12.0, 0.0, 16.0, 15.0, 1.0),
            Block.box(11.0, 12.0, 0.0, 12.0, 15.0, 4.0),
            Block.box(12.0, 11.0, 0.0, 15.0, 12.0, 1.0)
         }
      );
   }
}
