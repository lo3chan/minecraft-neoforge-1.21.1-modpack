package net.mcreator.borninchaosv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NightmareStalkerSkullBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<AttachFace> FACE = FaceAttachedHorizontalDirectionalBlock.FACE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public NightmareStalkerSkullBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.BONE_BLOCK)
            .strength(1.1F, 10.0F)
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(FACE, AttachFace.WALL))
            .setValue(WATERLOGGED, false)
      );
   }

   public Integer getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
      return ARGB32.opaque(-15921906);
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return state.getFluidState().isEmpty();
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case NORTH -> {
            switch ((AttachFace)state.getValue(FACE)) {
               case FLOOR:
                  yield Shapes.or(box(3.5, 0.0, 7.6, 12.5, 8.0, 15.5), box(5.5, 0.0, 1.0, 10.5, 5.1, 7.6));
               case WALL:
                  yield Shapes.or(box(3.5, 7.6, 8.0, 12.5, 15.5, 16.0), box(5.5, 1.0, 10.9, 10.5, 7.6, 16.0));
               case CEILING:
                  yield Shapes.or(box(3.5, 8.0, 7.6, 12.5, 16.0, 15.5), box(5.5, 10.9, 1.0, 10.5, 16.0, 7.6));
               default:
                  throw new MatchException(null, null);
            }
         }
         case EAST -> {
            switch ((AttachFace)state.getValue(FACE)) {
               case FLOOR:
                  yield Shapes.or(box(0.5, 0.0, 3.5, 8.4, 8.0, 12.5), box(8.4, 0.0, 5.5, 15.0, 5.1, 10.5));
               case WALL:
                  yield Shapes.or(box(0.0, 7.6, 3.5, 8.0, 15.5, 12.5), box(0.0, 1.0, 5.5, 5.1, 7.6, 10.5));
               case CEILING:
                  yield Shapes.or(box(0.5, 8.0, 3.5, 8.4, 16.0, 12.5), box(8.4, 10.9, 5.5, 15.0, 16.0, 10.5));
               default:
                  throw new MatchException(null, null);
            }
         }
         case WEST -> {
            switch ((AttachFace)state.getValue(FACE)) {
               case FLOOR:
                  yield Shapes.or(box(7.6, 0.0, 3.5, 15.5, 8.0, 12.5), box(1.0, 0.0, 5.5, 7.6, 5.1, 10.5));
               case WALL:
                  yield Shapes.or(box(8.0, 7.6, 3.5, 16.0, 15.5, 12.5), box(10.9, 1.0, 5.5, 16.0, 7.6, 10.5));
               case CEILING:
                  yield Shapes.or(box(7.6, 8.0, 3.5, 15.5, 16.0, 12.5), box(1.0, 10.9, 5.5, 7.6, 16.0, 10.5));
               default:
                  throw new MatchException(null, null);
            }
         }
         default -> {
            switch ((AttachFace)state.getValue(FACE)) {
               case FLOOR:
                  yield Shapes.or(box(3.5, 0.0, 0.5, 12.5, 8.0, 8.4), box(5.5, 0.0, 8.4, 10.5, 5.1, 15.0));
                  break;
               case WALL:
                  yield Shapes.or(box(3.5, 7.6, 0.0, 12.5, 15.5, 8.0), box(5.5, 1.0, 0.0, 10.5, 7.6, 5.1));
                  break;
               case CEILING:
                  yield Shapes.or(box(3.5, 8.0, 0.5, 12.5, 16.0, 8.4), box(5.5, 10.9, 8.4, 10.5, 16.0, 15.0));
                  break;
               default:
                  throw new MatchException(null, null);
            }
         }
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, FACE, WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
      return (BlockState)((BlockState)((BlockState)super.getStateForPlacement(context)
               .setValue(FACE, this.faceForDirection(context.getNearestLookingDirection())))
            .setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, flag);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   private AttachFace faceForDirection(Direction direction) {
      if (direction.getAxis() == Axis.Y) {
         return direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR;
      } else {
         return AttachFace.WALL;
      }
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
   }
}
