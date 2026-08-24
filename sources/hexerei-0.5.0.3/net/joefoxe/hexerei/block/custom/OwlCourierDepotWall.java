package net.joefoxe.hexerei.block.custom;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OwlCourierDepotWall extends OwlCourierDepot implements ITileEntity<OwlCourierDepotTile>, EntityBlock, SimpleWaterloggedBlock {
   VoxelShape shape = Stream.of(Block.box(2.0, 2.0, 0.0, 14.0, 9.0, 3.0), Block.box(3.0, 5.0, 3.0, 13.0, 6.0, 13.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   VoxelShape shape_90 = Stream.of(Block.box(0.0, 2.0, 2.0, 3.0, 9.0, 14.0), Block.box(3.0, 5.0, 3.0, 13.0, 6.0, 13.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   VoxelShape shape_180 = Stream.of(Block.box(2.0, 2.0, 13.0, 14.0, 9.0, 16.0), Block.box(3.0, 5.0, 3.0, 13.0, 6.0, 13.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   VoxelShape shape_270 = Stream.of(Block.box(13.0, 2.0, 2.0, 16.0, 9.0, 14.0), Block.box(3.0, 5.0, 3.0, 13.0, 6.0, 13.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public OwlCourierDepotWall(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState(
         (BlockState)((BlockState)super.defaultBlockState().setValue(WATERLOGGED, false)).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
      );
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return context.getClickedFace() != Direction.UP && context.getClickedFace() != Direction.DOWN
         ? (BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getClickedFace().getOpposite()))
            .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
         : (BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
            .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   @Override
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (!pState.hasProperty(HorizontalDirectionalBlock.FACING)) {
         return this.shape;
      } else {
         Direction dir = (Direction)pState.getValue(HorizontalDirectionalBlock.FACING);

         return switch (dir) {
            case DOWN, UP, NORTH -> this.shape;
            case SOUTH -> this.shape_180;
            case WEST -> this.shape_90;
            case EAST -> this.shape_270;
            default -> throw new MatchException(null, null);
         };
      }
   }
}
