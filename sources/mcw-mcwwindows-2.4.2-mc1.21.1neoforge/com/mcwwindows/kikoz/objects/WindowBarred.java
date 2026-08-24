package com.mcwwindows.kikoz.objects;

import com.mcwwindows.kikoz.init.ItemInit;
import com.mcwwindows.kikoz.init.SoundsInit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WindowBarred extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<WindowBarred.WindowState> WINDOWSTATE = EnumProperty.create("windowstate", WindowBarred.WindowState.class);
   protected static final VoxelShape EE = box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
   protected static final VoxelShape NN = box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);
   protected static final VoxelShape FIX = box(0.0, 0.0, 0.0, 0.0, 0.1, 0.0);

   public WindowBarred(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH))
            .setValue(WINDOWSTATE, WindowBarred.WindowState.CLOSED)
      );
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      WindowBarred.WindowState windowState = (WindowBarred.WindowState)state.getValue(WINDOWSTATE);
      Direction direction = (Direction)state.getValue(FACING);
      if (windowState == WindowBarred.WindowState.OPEN_LEFT || windowState == WindowBarred.WindowState.OPEN_RIGHT) {
         return FIX;
      } else {
         return direction != Direction.NORTH && direction != Direction.SOUTH ? NN : EE;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      WindowBarred.WindowState windowState = (WindowBarred.WindowState)state.getValue(WINDOWSTATE);
      if (item == ItemInit.KEY.get()) {
         this.lockWindow(level, pos, windowState != WindowBarred.WindowState.LOCKED, (Direction)state.getValue(FACING), 1000);
         level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.8F);
         state = (BlockState)state.setValue(
            WINDOWSTATE, windowState == WindowBarred.WindowState.LOCKED ? WindowBarred.WindowState.CLOSED : WindowBarred.WindowState.LOCKED
         );
         level.setBlock(pos, state, 10);
         return ItemInteractionResult.SUCCESS;
      } else if (windowState != WindowBarred.WindowState.LOCKED && item != this.asItem()) {
         WindowBarred.WindowState newState;
         if (windowState == WindowBarred.WindowState.CLOSED) {
            newState = this.getHingeDirectionFromContext(pos, (Direction)state.getValue(FACING), hit.getLocation());
            this.toggleWindowState(level, pos, newState);
            level.playSound(null, pos, (SoundEvent)SoundsInit.WINDOW_OPEN.get(), SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.8F);
         } else {
            newState = WindowBarred.WindowState.CLOSED;
            this.toggleWindowState(level, pos, newState);
            level.playSound(null, pos, (SoundEvent)SoundsInit.WINDOW_CLOSE.get(), SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.8F);
         }

         state = (BlockState)state.setValue(WINDOWSTATE, newState);
         level.setBlock(pos, state, 10);
         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   private WindowBarred.WindowState getHingeDirectionFromContext(BlockPos blockpos, Direction direction, Vec3 clickLocation) {
      int j = direction.getStepX();
      int k = direction.getStepZ();
      double d0 = clickLocation.x - blockpos.getX();
      double d1 = clickLocation.z - blockpos.getZ();
      boolean left = (j >= 0 || !(d1 < 0.5)) && (j <= 0 || !(d1 > 0.5)) && (k >= 0 || !(d0 > 0.5)) && (k <= 0 || !(d0 < 0.5));
      return left ? WindowBarred.WindowState.OPEN_RIGHT : WindowBarred.WindowState.OPEN_LEFT;
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return ((Direction)state.getValue(FACING)).getAxis() == Axis.X ? NN : EE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WINDOWSTATE});
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, Level worldIn, BlockPos currentPos, BlockPos facingPos) {
      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   private void toggleWindowState(Level world, BlockPos pos, WindowBarred.WindowState newState) {
      BlockState state = world.getBlockState(pos);
      Direction facing = (Direction)state.getValue(FACING);
      if (state.getValue(WINDOWSTATE) != newState) {
         world.setBlockAndUpdate(pos, (BlockState)state.setValue(WINDOWSTATE, newState));
         this.toggleAdjacentWindows(world, pos.above(), facing, newState, 0, 100);
         this.toggleAdjacentWindows(world, pos.below(), facing, newState, 0, 100);
      }
   }

   private void toggleAdjacentWindows(Level world, BlockPos pos, Direction facing, WindowBarred.WindowState newState, int depth, int maxDepth) {
      BlockState state = world.getBlockState(pos);
      if (depth <= maxDepth && state.getBlock() == this && state.getValue(FACING) == facing) {
         if (state.getValue(WINDOWSTATE) != newState) {
            world.setBlockAndUpdate(pos, (BlockState)state.setValue(WINDOWSTATE, newState));
            this.toggleAdjacentWindows(world, pos.above(), facing, newState, depth + 1, maxDepth);
            this.toggleAdjacentWindows(world, pos.below(), facing, newState, depth + 1, maxDepth);
         }
      }
   }

   private void lockWindow(Level world, BlockPos pos, boolean targetOpen, Direction targetDirection, int depth) {
      if (depth > 0) {
         BlockState state = world.getBlockState(pos);
         if (state.getBlock() == this) {
            boolean isLocked = state.getValue(WINDOWSTATE) == WindowBarred.WindowState.LOCKED;
            Direction direction = (Direction)state.getValue(FACING);
            if (isLocked != targetOpen && direction.equals(targetDirection)) {
               world.setBlockAndUpdate(
                  pos, (BlockState)state.setValue(WINDOWSTATE, targetOpen ? WindowBarred.WindowState.LOCKED : WindowBarred.WindowState.CLOSED)
               );
               Direction[] directions;
               if (direction.getAxis() == Axis.X) {
                  directions = new Direction[]{Direction.SOUTH, Direction.NORTH};
               } else {
                  directions = new Direction[]{Direction.EAST, Direction.WEST};
               }

               for (Direction dir : directions) {
                  this.lockWindow(world, pos.relative(dir), targetOpen, targetDirection, depth - 2);
               }

               for (int y = 1; y <= 2; y++) {
                  this.lockWindow(world, pos.above(y), targetOpen, targetDirection, depth - 2);
                  this.lockWindow(world, pos.below(y), targetOpen, targetDirection, depth - 2);
               }
            }
         }
      }
   }

   public static enum WindowState implements StringRepresentable {
      CLOSED("closed"),
      OPEN_LEFT("open_left"),
      OPEN_RIGHT("open_right"),
      LOCKED("locked");

      private final String name;

      private WindowState(final String name) {
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
