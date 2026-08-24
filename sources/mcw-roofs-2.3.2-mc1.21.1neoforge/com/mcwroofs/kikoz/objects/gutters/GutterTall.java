package com.mcwroofs.kikoz.objects.gutters;

import com.mcwroofs.kikoz.init.BlockInit;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GutterTall extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape[] SHAPES = new VoxelShape[12];
   public static final BooleanProperty ABOVE = BooleanProperty.create("above");
   public static final BooleanProperty BELOW = BooleanProperty.create("below");
   private static final BooleanProperty WATER = BooleanProperty.create("water");

   public GutterTall(BlockState state, Properties prop) {
      super(prop);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATER, false)).setValue(FACING, Direction.NORTH))
               .setValue(ABOVE, false))
            .setValue(BELOW, false)
      );
   }

   private BlockState GutterState(BlockState state, LevelAccessor access, BlockPos pos) {
      boolean above = access.getBlockState(pos.above()).getBlock() == this;
      boolean below = access.getBlockState(pos.below()).getBlock() == this;
      return (BlockState)((BlockState)state.setValue(ABOVE, above)).setValue(BELOW, below);
   }

   public void onPlace(BlockState state, Level world, BlockPos pos, BlockState statetwo, boolean bolean) {
      if (!statetwo.is(state.getBlock())) {
         this.GutterState(state, world, pos);
      }
   }

   public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable Entity entity, ItemStack stack) {
      this.GutterState(state, world, pos);
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState bstate, LevelAccessor world, BlockPos pos, BlockPos postwo) {
      return this.GutterState(state, world, pos);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.GutterState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos())
         .setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   public void placeAt(Level world, BlockPos pos, int into) {
      world.setBlock(pos, this.defaultBlockState(), into);
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      boolean isAbove = (Boolean)state.getValue(ABOVE);
      boolean isBelow = (Boolean)state.getValue(BELOW);
      Direction facing = (Direction)state.getValue(FACING);
      int shapeIndex = this.getShapeIndex(isAbove, isBelow, facing);
      return shapeIndex >= 0 && shapeIndex < SHAPES.length ? SHAPES[shapeIndex] : Shapes.empty();
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      Boolean i = (Boolean)state.getValue(WATER);
      Item item = itemstack.getItem();
      ItemStack heldItem = player.getItemInHand(hand);
      if (item == Items.WATER_BUCKET && !i) {
         state = (BlockState)state.cycle(WATER);
         world.setBlock(pos, state, 2);
         itemstack.shrink(1);
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
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_WHITE.get();
               break;
            case LIGHT_GRAY:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_LIGHT_GRAY.get();
               break;
            case GRAY:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_GRAY.get();
               break;
            case BLACK:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_BLACK.get();
               break;
            case BROWN:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_BROWN.get();
               break;
            case RED:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_RED.get();
               break;
            case ORANGE:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_ORANGE.get();
               break;
            case YELLOW:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_YELLOW.get();
               break;
            case LIME:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_LIME.get();
               break;
            case GREEN:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_GREEN.get();
               break;
            case CYAN:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_CYAN.get();
               break;
            case LIGHT_BLUE:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_LIGHT_BLUE.get();
               break;
            case BLUE:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_BLUE.get();
               break;
            case PURPLE:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_PURPLE.get();
               break;
            case MAGENTA:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_MAGENTA.get();
               break;
            case PINK:
               blockToPlace = (Block)BlockInit.GUTTER_MIDDLE_PINK.get();
               break;
            default:
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         Direction currentFacing = (Direction)state.getValue(FACING);
         Boolean currentWater = (Boolean)state.getValue(WATER);
         Boolean currentAbove = (Boolean)state.getValue(ABOVE);
         Boolean currentBelow = (Boolean)state.getValue(BELOW);
         BlockState newState = (BlockState)((BlockState)((BlockState)((BlockState)blockToPlace.defaultBlockState().setValue(FACING, currentFacing))
                  .setValue(WATER, currentWater))
               .setValue(ABOVE, currentAbove))
            .setValue(BELOW, currentBelow);
         world.setBlockAndUpdate(pos, newState);
         if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   private int getShapeIndex(boolean isAbove, boolean isBelow, Direction facing) {
      if (!isAbove && !isBelow) {
         switch (facing) {
            case WEST:
               return 2;
            case EAST:
               return 0;
            case SOUTH:
               return 1;
            case NORTH:
               return 3;
         }
      } else if (isAbove && isBelow) {
         switch (facing) {
            case WEST:
               return 6;
            case EAST:
               return 4;
            case SOUTH:
               return 5;
            case NORTH:
               return 7;
         }
      } else if (isAbove && !isBelow) {
         switch (facing) {
            case WEST:
               return 2;
            case EAST:
               return 0;
            case SOUTH:
               return 1;
            case NORTH:
               return 3;
         }
      } else {
         switch (facing) {
            case WEST:
               return 10;
            case EAST:
               return 8;
            case SOUTH:
               return 9;
            case NORTH:
               return 11;
         }
      }

      return -1;
   }

   public void onBroken(Level worldIn, BlockPos pos) {
      worldIn.levelEvent(1029, pos, 0);
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

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, ABOVE, BELOW, WATER});
   }

   static {
      SHAPES[0] = Shapes.or(Block.box(6.0, 5.6, 4.0, 10.0, 9.8, 7.4), Block.box(6.0, 5.6, 0.0, 10.0, 16.0, 4.0));
      SHAPES[1] = Shapes.or(Block.box(8.8, 5.6, 6.0, 12.1, 9.8, 10.0), Block.box(12.1, 5.6, 6.0, 16.1, 16.0, 10.0));
      SHAPES[2] = Shapes.or(Block.box(6.0, 5.6, 8.7, 10.0, 9.8, 12.0), Block.box(6.0, 5.6, 12.0, 10.0, 16.0, 16.0));
      SHAPES[3] = Shapes.or(Block.box(4.0, 5.6, 6.0, 7.3, 9.8, 10.0), Block.box(0.0, 5.6, 6.0, 4.0, 16.0, 10.0));
      SHAPES[4] = Shapes.or(Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 4.0), new VoxelShape[0]);
      SHAPES[5] = Shapes.or(Block.box(12.0, 0.0, 6.0, 16.0, 16.0, 10.0), new VoxelShape[0]);
      SHAPES[6] = Shapes.or(Block.box(6.0, 0.0, 12.0, 10.0, 16.0, 16.0), new VoxelShape[0]);
      SHAPES[7] = Shapes.or(Block.box(0.0, 0.0, 6.0, 4.0, 16.0, 10.0), new VoxelShape[0]);
      SHAPES[8] = Shapes.or(
         Block.box(6.0, 0.0, 0.0, 10.0, 6.0, 4.0),
         new VoxelShape[]{
            Block.box(0.0, 12.0, 0.0, 16.0, 15.0, 5.0),
            Block.box(0.0, 11.0, 1.0, 16.0, 12.0, 4.0),
            Block.box(6.0, 6.0, 0.8, 10.0, 8.975, 5.0),
            Block.box(5.0, 9.0, 0.8, 11.0, 14.975, 7.0)
         }
      );
      SHAPES[9] = Shapes.or(
         Block.box(12.0, 0.0, 6.0, 16.0, 6.0, 10.0),
         new VoxelShape[]{
            Block.box(11.0, 12.0, 0.0, 16.0, 15.0, 16.0),
            Block.box(12.0, 11.0, 0.0, 15.0, 12.0, 16.0),
            Block.box(11.0, 6.0, 6.0, 16.0, 9.0, 10.0),
            Block.box(9.0, 9.0, 5.0, 16.0, 15.0, 11.0)
         }
      );
      SHAPES[10] = Shapes.or(
         Block.box(6.0, 0.0, 12.0, 10.0, 6.0, 16.0),
         new VoxelShape[]{
            Block.box(0.0, 12.0, 11.0, 16.0, 15.0, 16.0),
            Block.box(0.0, 11.0, 12.0, 16.0, 12.0, 15.0),
            Block.box(6.0, 6.0, 11.0, 10.0, 9.0, 16.0),
            Block.box(5.0, 9.0, 9.0, 11.0, 15.0, 16.0)
         }
      );
      SHAPES[11] = Shapes.or(
         Block.box(0.0, 0.0, 6.0, 4.0, 6.0, 10.0),
         new VoxelShape[]{
            Block.box(0.0, 12.0, 0.0, 5.0, 15.0, 16.0),
            Block.box(1.0, 11.0, 0.0, 4.0, 12.0, 16.0),
            Block.box(1.0, 6.0, 6.0, 5.0, 9.0, 10.0),
            Block.box(1.0, 9.0, 5.0, 7.0, 15.0, 11.0)
         }
      );
   }
}
