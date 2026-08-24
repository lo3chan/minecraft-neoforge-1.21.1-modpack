package com.mcwwindows.kikoz.objects;

import com.mcwwindows.kikoz.init.ItemInit;
import com.mcwwindows.kikoz.init.SoundsInit;
import com.mcwwindows.kikoz.util.WindowPart;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GothicWindow extends WindowBase {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   protected static final VoxelShape EAST = Shapes.or(box(3.0, 0.0, -1.0, 13.0, 16.0, 17.0), new VoxelShape[0]);
   protected static final VoxelShape NORTH = Shapes.or(box(-1.0, 0.0, 3.0, 17.0, 16.0, 13.0), new VoxelShape[0]);
   private boolean wasInteractedWith = false;

   public GothicWindow(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(OPEN, false)).setValue(FACING, Direction.NORTH))
            .setValue(PART, WindowPart.BASE)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case WEST:
            return EAST;
         case EAST:
            return EAST;
         case NORTH:
            return NORTH;
         case SOUTH:
            return NORTH;
         default:
            return NORTH;
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.WindowState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos())
         .setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, FACING, OPEN});
   }

   public boolean isOpen(BlockState state) {
      return (Boolean)state.getValue(OPEN);
   }

   public void openDoor(Level worldIn, BlockState state, BlockPos pos, boolean open) {
      if (state.is(this) && (Boolean)state.getValue(OPEN) != open) {
         worldIn.setBlock(pos, (BlockState)state.setValue(OPEN, open), 10);
      }
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item == this.asItem()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if (item == ItemInit.HAMMER.get()) {
         BlockState newState = (BlockState)state.cycle(PART);
         worldIn.setBlockAndUpdate(pos, newState);
         this.setWasInteractedWith(true, worldIn, pos);
         return ItemInteractionResult.SUCCESS;
      } else {
         this.openWindow(worldIn, pos, !(Boolean)state.getValue(OPEN), (Direction)state.getValue(FACING));
         worldIn.playSound(null, pos, (SoundEvent)SoundsInit.BARS_OPEN.get(), SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
         state = (BlockState)state.cycle(OPEN);
         worldIn.setBlock(pos, state, 10);
         return ItemInteractionResult.SUCCESS;
      }
   }

   public void setWasInteractedWith(boolean interacted, Level level, BlockPos pos) {
      this.wasInteractedWith = interacted;
   }

   @Override
   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @Override
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, Level worldIn, BlockPos currentPos, BlockPos facingPos) {
      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   private void openWindow(Level world, BlockPos pos, boolean bool, Direction dir) {
      BlockState state = world.getBlockState(pos);
      if (state.getBlock() == this && (Boolean)state.getValue(OPEN) != bool && ((Direction)state.getValue(FACING)).equals(dir)) {
         world.setBlockAndUpdate(pos, (BlockState)state.setValue(OPEN, bool));

         for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
               for (int z = -1; z <= 1; z++) {
                  BlockPos newPos = pos.offset(x, y, z);
                  this.openWindow(world, newPos, bool, dir);
               }
            }
         }
      }
   }

   @Override
   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         this.WindowState(state, level, pos);
      }
   }

   @Override
   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return this.wasInteractedWith ? state : this.WindowState(state, level, pos);
   }
}
