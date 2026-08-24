package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.GraveDestroyerProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Tombstone2003wiseBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public Tombstone2003wiseBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.TUFF)
            .strength(4.0F, 50.0F)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false)
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
         case NORTH -> Shapes.or(
            box(2.0, 2.0, 6.0, 14.0, 19.0, 12.0),
            new VoxelShape[]{
               box(0.0, 0.0, 3.0, 16.0, 2.0, 14.0),
               box(5.5, 19.0, 5.0, 10.5, 23.0, 10.0),
               box(0.5, 19.0, 6.0, 15.5, 20.0, 12.0),
               box(0.5, 20.0, 6.0, 3.5, 22.0, 12.0),
               box(12.5, 20.0, 6.0, 15.5, 22.0, 12.0),
               box(6.5, 18.0, 5.0, 9.5, 19.0, 6.0)
            }
         );
         case EAST -> Shapes.or(
            box(4.0, 2.0, 2.0, 10.0, 19.0, 14.0),
            new VoxelShape[]{
               box(2.0, 0.0, 0.0, 13.0, 2.0, 16.0),
               box(6.0, 19.0, 5.5, 11.0, 23.0, 10.5),
               box(4.0, 19.0, 0.5, 10.0, 20.0, 15.5),
               box(4.0, 20.0, 0.5, 10.0, 22.0, 3.5),
               box(4.0, 20.0, 12.5, 10.0, 22.0, 15.5),
               box(10.0, 18.0, 6.5, 11.0, 19.0, 9.5)
            }
         );
         case WEST -> Shapes.or(
            box(6.0, 2.0, 2.0, 12.0, 19.0, 14.0),
            new VoxelShape[]{
               box(3.0, 0.0, 0.0, 14.0, 2.0, 16.0),
               box(5.0, 19.0, 5.5, 10.0, 23.0, 10.5),
               box(6.0, 19.0, 0.5, 12.0, 20.0, 15.5),
               box(6.0, 20.0, 12.5, 12.0, 22.0, 15.5),
               box(6.0, 20.0, 0.5, 12.0, 22.0, 3.5),
               box(5.0, 18.0, 6.5, 6.0, 19.0, 9.5)
            }
         );
         default -> Shapes.or(
            box(2.0, 2.0, 4.0, 14.0, 19.0, 10.0),
            new VoxelShape[]{
               box(0.0, 0.0, 2.0, 16.0, 2.0, 13.0),
               box(5.5, 19.0, 6.0, 10.5, 23.0, 11.0),
               box(0.5, 19.0, 4.0, 15.5, 20.0, 10.0),
               box(12.5, 20.0, 4.0, 15.5, 22.0, 10.0),
               box(0.5, 20.0, 4.0, 3.5, 22.0, 10.0),
               box(6.5, 18.0, 10.0, 9.5, 19.0, 11.0)
            }
         );
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
      return (BlockState)((BlockState)super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, flag);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
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

   public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
      boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
      GraveDestroyerProcedure.execute(entity);
      return retval;
   }
}
