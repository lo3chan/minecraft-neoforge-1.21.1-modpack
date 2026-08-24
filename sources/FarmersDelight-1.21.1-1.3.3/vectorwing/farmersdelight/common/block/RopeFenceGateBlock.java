package vectorwing.farmersdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class RopeFenceGateBlock extends FenceGateBlock {
   protected static final VoxelShape Z_SHAPE = Block.box(0.0, 0.0, 7.0, 16.0, 14.0, 9.0);
   protected static final VoxelShape X_SHAPE = Block.box(7.0, 0.0, 0.0, 9.0, 14.0, 16.0);
   protected static final VoxelShape Z_COLLISION_SHAPE = Block.box(0.0, 0.0, 7.0, 16.0, 24.0, 9.0);
   protected static final VoxelShape X_COLLISION_SHAPE = Block.box(7.0, 0.0, 0.0, 9.0, 24.0, 16.0);

   public RopeFenceGateBlock(Properties props) {
      super(props, ModSounds.BLOCK_ROPE_FENCE_GATE_OPEN.get(), ModSounds.BLOCK_ROPE_FENCE_GATE_CLOSE.get());
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      Axis axis = facing.getAxis();
      if (((Direction)state.getValue(FACING)).getClockWise().getAxis() != axis) {
         return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
      } else {
         boolean isBorderedByWalls = this.isWall(facingState) && this.isWall(level.getBlockState(currentPos.relative(facing.getOpposite())));
         return (BlockState)state.setValue(IN_WALL, isBorderedByWalls);
      }
   }

   protected boolean isWall(BlockState state) {
      return state.is(BlockTags.WALLS);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return ((Direction)state.getValue(FACING)).getAxis() == Axis.X ? X_SHAPE : Z_SHAPE;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if ((Boolean)state.getValue(OPEN)) {
         return Shapes.empty();
      } else {
         return ((Direction)state.getValue(FACING)).getAxis() == Axis.Z ? Z_COLLISION_SHAPE : X_COLLISION_SHAPE;
      }
   }
}
