package com.mcwdoors.kikoz.objects;

import com.mcwdoors.kikoz.init.SoundsInit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GarageDoor extends Block {
   public static final EnumProperty<GarageDoor.GaragePart> PART = EnumProperty.create("part", GarageDoor.GaragePart.class);
   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final VoxelShape EAST = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
   private static final VoxelShape NORTH = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);

   public GarageDoor(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, true))
               .setValue(POWERED, false))
            .setValue(PART, GarageDoor.GaragePart.TOP)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, OPEN, POWERED, PART});
   }

   protected VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      Direction facing = (Direction)state.getValue(FACING);
      return facing != Direction.WEST && facing != Direction.EAST ? EAST : NORTH;
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return state.getValue(FACING) != Direction.WEST && state.getValue(FACING) != Direction.EAST ? EAST : NORTH;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item == this.asItem()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         boolean open = !(Boolean)state.getValue(OPEN);
         this.toggleDoor(level, pos, open, (Direction)state.getValue(FACING));
         level.playSound(null, pos, (SoundEvent)SoundsInit.GARAGE.get(), SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.8F);
         return ItemInteractionResult.SUCCESS;
      }
   }

   private void toggleDoor(Level level, BlockPos clickedPos, boolean open, Direction facing) {
      BlockPos topPos = clickedPos;

      while (level.getBlockState(topPos.above()).getBlock() == this) {
         topPos = topPos.above();
      }

      BlockState topState = level.getBlockState(topPos);
      if (topState.getBlock() == this) {
         level.setBlock(topPos, (BlockState)topState.setValue(OPEN, open), 3);
         if (!open) {
            for (BlockPos current = topPos.below(); level.getBlockState(current).isAir(); current = current.below()) {
               level.setBlock(
                  current,
                  (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facing)).setValue(PART, GarageDoor.GaragePart.MIDDLE))
                     .setValue(OPEN, false),
                  3
               );
            }
         } else {
            for (BlockPos current = topPos.below(); level.getBlockState(current).getBlock() == this; current = current.below()) {
               level.setBlock(current, Blocks.AIR.defaultBlockState(), 3);
            }
         }

         for (Direction dir : Plane.HORIZONTAL) {
            BlockPos adjacent = topPos.relative(dir);
            BlockState adjState = level.getBlockState(adjacent);
            if (adjState.getBlock() == this && (Boolean)adjState.getValue(OPEN) != open) {
               this.toggleDoor(level, adjacent, open, (Direction)adjState.getValue(FACING));
            }
         }
      }
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
      if (!level.isClientSide()) {
         boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.below());
         if ((Boolean)state.getValue(POWERED) != powered) {
            this.toggleDoor(level, pos, powered, (Direction)state.getValue(FACING));
            level.setBlock(pos, (BlockState)state.setValue(POWERED, powered), 3);
         }
      }
   }

   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      if (!level.isClientSide() && !player.isCreative() && state.getValue(PART) == GarageDoor.GaragePart.TOP) {
         ItemStack stack = new ItemStack(this);
         popResource(level, pos, stack);
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   protected BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathtype) {
      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   public static enum GaragePart implements StringRepresentable {
      TOP("top"),
      MIDDLE("middle");

      private final String name;

      private GaragePart(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
