package net.mcreator.borninchaosv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
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

public class LifestealerSkullBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<AttachFace> FACE = FaceAttachedHorizontalDirectionalBlock.FACE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public LifestealerSkullBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.BONE_BLOCK)
            .strength(5.0F, 80.0F)
            .lightLevel(s -> 3)
            .noOcclusion()
            .hasPostProcess((bs, br, bp) -> true)
            .emissiveRendering((bs, br, bp) -> true)
            .isRedstoneConductor((bs, br, bp) -> false)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(FACE, AttachFace.WALL))
            .setValue(WATERLOGGED, false)
      );
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
                  yield Shapes.or(
                     box(1.3, 0.23056, 2.933, 14.7, 12.63056, 16.333),
                     new VoxelShape[]{
                        box(1.5, 0.33056, 3.233, 14.5, 12.33056, 16.233),
                        box(4.2, -1.36944, 2.933, 11.8, 0.23056, 6.533),
                        box(4.2, -2.96944, 2.933, 11.8, -1.36944, 6.533)
                     }
                  );
               case WALL:
                  yield Shapes.or(
                     box(1.3, 2.933, 3.36944, 14.7, 16.333, 15.76944),
                     new VoxelShape[]{
                        box(1.5, 3.233, 3.66944, 14.5, 16.233, 15.66944),
                        box(4.2, 2.933, 15.76944, 11.8, 6.533, 17.36944),
                        box(4.2, 2.933, 17.36944, 11.8, 6.533, 18.96944)
                     }
                  );
               case CEILING:
                  yield Shapes.or(
                     box(1.3, 3.36944, 2.933, 14.7, 15.76944, 16.333),
                     new VoxelShape[]{
                        box(1.5, 3.66944, 3.233, 14.5, 15.66944, 16.233),
                        box(4.2, 15.76944, 2.933, 11.8, 17.36944, 6.533),
                        box(4.2, 17.36944, 2.933, 11.8, 18.96944, 6.533)
                     }
                  );
               default:
                  throw new MatchException(null, null);
            }
         }
         case EAST -> {
            switch ((AttachFace)state.getValue(FACE)) {
               case FLOOR:
                  yield Shapes.or(
                     box(-0.333, 0.23056, 1.3, 13.067, 12.63056, 14.7),
                     new VoxelShape[]{
                        box(-0.233, 0.33056, 1.5, 12.767, 12.33056, 14.5),
                        box(9.467, -1.36944, 4.2, 13.067, 0.23056, 11.8),
                        box(9.467, -2.96944, 4.2, 13.067, -1.36944, 11.8)
                     }
                  );
               case WALL:
                  yield Shapes.or(
                     box(0.23056, 2.933, 1.3, 12.63056, 16.333, 14.7),
                     new VoxelShape[]{
                        box(0.33056, 3.233, 1.5, 12.33056, 16.233, 14.5),
                        box(-1.36944, 2.933, 4.2, 0.23056, 6.533, 11.8),
                        box(-2.96944, 2.933, 4.2, -1.36944, 6.533, 11.8)
                     }
                  );
               case CEILING:
                  yield Shapes.or(
                     box(-0.333, 3.36944, 1.3, 13.067, 15.76944, 14.7),
                     new VoxelShape[]{
                        box(-0.233, 3.66944, 1.5, 12.767, 15.66944, 14.5),
                        box(9.467, 15.76944, 4.2, 13.067, 17.36944, 11.8),
                        box(9.467, 17.36944, 4.2, 13.067, 18.96944, 11.8)
                     }
                  );
               default:
                  throw new MatchException(null, null);
            }
         }
         case WEST -> {
            switch ((AttachFace)state.getValue(FACE)) {
               case FLOOR:
                  yield Shapes.or(
                     box(2.933, 0.23056, 1.3, 16.333, 12.63056, 14.7),
                     new VoxelShape[]{
                        box(3.233, 0.33056, 1.5, 16.233, 12.33056, 14.5),
                        box(2.933, -1.36944, 4.2, 6.533, 0.23056, 11.8),
                        box(2.933, -2.96944, 4.2, 6.533, -1.36944, 11.8)
                     }
                  );
               case WALL:
                  yield Shapes.or(
                     box(3.36944, 2.933, 1.3, 15.76944, 16.333, 14.7),
                     new VoxelShape[]{
                        box(3.66944, 3.233, 1.5, 15.66944, 16.233, 14.5),
                        box(15.76944, 2.933, 4.2, 17.36944, 6.533, 11.8),
                        box(17.36944, 2.933, 4.2, 18.96944, 6.533, 11.8)
                     }
                  );
               case CEILING:
                  yield Shapes.or(
                     box(2.933, 3.36944, 1.3, 16.333, 15.76944, 14.7),
                     new VoxelShape[]{
                        box(3.233, 3.66944, 1.5, 16.233, 15.66944, 14.5),
                        box(2.933, 15.76944, 4.2, 6.533, 17.36944, 11.8),
                        box(2.933, 17.36944, 4.2, 6.533, 18.96944, 11.8)
                     }
                  );
               default:
                  throw new MatchException(null, null);
            }
         }
         default -> {
            switch ((AttachFace)state.getValue(FACE)) {
               case FLOOR:
                  yield Shapes.or(
                     box(1.3, 0.23056, -0.333, 14.7, 12.63056, 13.067),
                     new VoxelShape[]{
                        box(1.5, 0.33056, -0.233, 14.5, 12.33056, 12.767),
                        box(4.2, -1.36944, 9.467, 11.8, 0.23056, 13.067),
                        box(4.2, -2.96944, 9.467, 11.8, -1.36944, 13.067)
                     }
                  );
                  break;
               case WALL:
                  yield Shapes.or(
                     box(1.3, 2.933, 0.23056, 14.7, 16.333, 12.63056),
                     new VoxelShape[]{
                        box(1.5, 3.233, 0.33056, 14.5, 16.233, 12.33056),
                        box(4.2, 2.933, -1.36944, 11.8, 6.533, 0.23056),
                        box(4.2, 2.933, -2.96944, 11.8, 6.533, -1.36944)
                     }
                  );
                  break;
               case CEILING:
                  yield Shapes.or(
                     box(1.3, 3.36944, -0.333, 14.7, 15.76944, 13.067),
                     new VoxelShape[]{
                        box(1.5, 3.66944, -0.233, 14.5, 15.66944, 12.767),
                        box(4.2, 15.76944, 9.467, 11.8, 17.36944, 13.067),
                        box(4.2, 17.36944, 9.467, 11.8, 18.96944, 13.067)
                     }
                  );
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

   public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
      return 5.0F;
   }
}
