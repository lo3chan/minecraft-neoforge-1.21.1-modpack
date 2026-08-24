package com.mcwwindows.kikoz.objects;

import com.mcwwindows.kikoz.init.SoundsInit;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Shutter extends Block {
   protected static final VoxelShape SOUTH_LEFT = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 11.0);
   protected static final VoxelShape SOUTH_RIGHT = Block.box(14.0, 0.0, 5.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape WEST_LEFT = Block.box(5.0, 0.0, 14.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape WEST_RIGHT = Block.box(0.0, 0.0, 14.0, 11.0, 16.0, 16.0);
   protected static final VoxelShape EAST_LEFT = Block.box(0.0, 0.0, 0.0, 11.0, 16.0, 2.0);
   protected static final VoxelShape EAST_RIGHT = Block.box(5.0, 0.0, 0.0, 16.0, 16.0, 2.0);
   protected static final VoxelShape NORTH_LEFT = Block.box(0.0, 0.0, 5.0, 2.0, 16.0, 16.0);
   protected static final VoxelShape NORTH_RIGHT = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 11.0);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

   public Shutter(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false))
            .setValue(HINGE, DoorHingeSide.LEFT)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      DoorHingeSide hinge = (DoorHingeSide)state.getValue(HINGE);
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            if (hinge == DoorHingeSide.RIGHT) {
               return NORTH_RIGHT;
            }

            return NORTH_LEFT;
         case SOUTH:
            if (hinge == DoorHingeSide.RIGHT) {
               return SOUTH_RIGHT;
            }

            return SOUTH_LEFT;
         case WEST:
            if (hinge == DoorHingeSide.RIGHT) {
               return WEST_RIGHT;
            }

            return WEST_LEFT;
         case EAST:
         default:
            return hinge == DoorHingeSide.RIGHT ? EAST_RIGHT : EAST_LEFT;
      }
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
      return Shapes.empty();
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item == this.asItem()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         this.toggleShutter(level, pos, !(Boolean)state.getValue(OPEN), (Direction)state.getValue(FACING), 100);
         level.playSound(null, pos, (SoundEvent)SoundsInit.WINDOW_OPEN.get(), SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.8F);
         state = (BlockState)state.cycle(OPEN);
         level.setBlock(pos, state, 2);
         return ItemInteractionResult.SUCCESS;
      }
   }

   private DoorHingeSide getHinge(BlockPlaceContext context) {
      BlockPos blockpos = context.getClickedPos();
      Direction direction = context.getHorizontalDirection();
      int j = direction.getStepX();
      int k = direction.getStepZ();
      Vec3 vector3d = context.getClickLocation();
      double d0 = vector3d.x - blockpos.getX();
      double d1 = vector3d.z - blockpos.getZ();
      return j < 0 && d1 < 0.5 || j > 0 && d1 > 0.5 || k < 0 && d0 > 0.5 || k > 0 && d0 < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(HINGE, this.getHinge(context)))
         .setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, OPEN, HINGE});
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return super.updateShape(state, dir, statetwo, access, pos, postwo);
   }

   private void toggleShutter(Level world, BlockPos pos, boolean targetOpen, Direction targetDirection, int depth) {
      if (depth > 0) {
         BlockState state = world.getBlockState(pos);
         if (state.getBlock() == this) {
            boolean open = (Boolean)state.getValue(OPEN);
            Direction direction = (Direction)state.getValue(FACING);
            if (open != targetOpen && direction.equals(targetDirection)) {
               world.setBlockAndUpdate(pos, (BlockState)state.setValue(OPEN, targetOpen));
               BlockPos[] positions = new BlockPos[]{pos.south(1), pos.north(1), pos.east(1), pos.west(1), pos.below(1), pos.below(2), pos.above(1)};

               for (BlockPos newPos : positions) {
                  this.toggleShutter(world, newPos, targetOpen, targetDirection, depth - 2);
               }
            }
         }
      }
   }
}
