package com.mcwwindows.kikoz.objects;

import com.mcwwindows.kikoz.init.SoundsInit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Blinds extends WindowBase {
   private static final EnumProperty<Blinds.GaragePart> PART = EnumProperty.create("part", Blinds.GaragePart.class);
   private static final EnumProperty<Blinds.BlindsState> BLINDSSTATE = EnumProperty.create("blindsstate", Blinds.BlindsState.class);
   String infoname;
   boolean hasTextInfo = true;
   protected static final VoxelShape WEST = Shapes.or(box(12.0, 0.0, 0.0, 15.9, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape SOUTH = Shapes.or(box(0.0, 0.0, 0.1, 16.0, 16.0, 4.0), new VoxelShape[0]);
   protected static final VoxelShape EAST = Shapes.or(box(0.1, 0.0, 0.0, 4.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape NORTH = Shapes.or(box(0.0, 0.0, 12.0, 16.0, 16.0, 15.9), new VoxelShape[0]);

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            return null;
      }
   }

   public Blinds(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
               .setValue(BLINDSSTATE, Blinds.BlindsState.OPEN))
            .setValue(PART, Blinds.GaragePart.BOTTOM)
      );
   }

   @Override
   protected BlockState WindowState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      if (above && below) {
         return (BlockState)state.setValue(PART, Blinds.GaragePart.BOTTOM);
      } else if (!above && below) {
         return (BlockState)state.setValue(PART, Blinds.GaragePart.TOP);
      } else {
         return above && !below ? (BlockState)state.setValue(PART, Blinds.GaragePart.BOTTOM) : (BlockState)state.setValue(PART, Blinds.GaragePart.TOP);
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
      builder.add(new Property[]{PART, FACING, BLINDSSTATE});
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item != this.asItem()) {
         Blinds.BlindsState currentState = (Blinds.BlindsState)state.getValue(BLINDSSTATE);
         Blinds.BlindsState nextState = this.cycleBlindsState(currentState);
         worldIn.playSound(null, pos, (SoundEvent)SoundsInit.BLINDS_CLOSE.get(), SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.8F);
         this.toggleBlinds(worldIn, pos, nextState, (Direction)state.getValue(FACING), 1000);
         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Override
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, Level worldIn, BlockPos currentPos, BlockPos facingPos) {
      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public int getLightBlock(BlockState state, BlockGetter reader, BlockPos pos) {
      Blinds.BlindsState currentState = (Blinds.BlindsState)state.getValue(BLINDSSTATE);
      return currentState == Blinds.BlindsState.CLOSED ? reader.getMaxLightLevel() : 0;
   }

   private void toggleBlinds(Level world, BlockPos pos, Blinds.BlindsState targetState, Direction targetDirection, int depth) {
      if (depth > 0) {
         BlockState state = world.getBlockState(pos);
         if (state.getBlock() == this) {
            Blinds.BlindsState currentState = (Blinds.BlindsState)state.getValue(BLINDSSTATE);
            if (currentState != targetState) {
               world.setBlockAndUpdate(pos, (BlockState)state.setValue(BLINDSSTATE, targetState));
               BlockPos[] positions = new BlockPos[]{pos.south(), pos.north(), pos.east(), pos.west(), pos.below(), pos.above()};

               for (BlockPos newPos : positions) {
                  this.toggleBlinds(world, newPos, targetState, targetDirection, depth - 1);
               }
            }
         }
      }
   }

   private Blinds.BlindsState cycleBlindsState(Blinds.BlindsState currentState) {
      switch (currentState) {
         case CLOSED:
            return Blinds.BlindsState.OPEN;
         case OPEN:
            return Blinds.BlindsState.RAISED;
         case RAISED:
         default:
            return Blinds.BlindsState.CLOSED;
      }
   }

   protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
      Blinds.BlindsState raised = (Blinds.BlindsState)state.getValue(BLINDSSTATE);
      Blinds.GaragePart part = (Blinds.GaragePart)state.getValue(PART);
      if (raised != Blinds.BlindsState.RAISED || part != Blinds.GaragePart.BOTTOM) {
         level.levelEvent(player, 2001, pos, getId(state));
      }
   }

   public static enum BlindsState implements StringRepresentable {
      CLOSED("closed"),
      OPEN("open"),
      RAISED("raised");

      private final String name;

      private BlindsState(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String getString() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum GaragePart implements StringRepresentable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      private GaragePart(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String getString() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
