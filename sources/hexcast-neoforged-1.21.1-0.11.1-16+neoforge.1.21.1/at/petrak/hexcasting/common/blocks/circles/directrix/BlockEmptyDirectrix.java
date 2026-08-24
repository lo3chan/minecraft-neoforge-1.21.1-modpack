package at.petrak.hexcasting.common.blocks.circles.directrix;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.circles.ICircleComponent;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockEmptyDirectrix extends BlockCircleComponent {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;

   public BlockEmptyDirectrix(Properties p_49795_) {
      super(p_49795_);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ENERGIZED, false)).setValue(FACING, Direction.NORTH));
   }

   @Override
   public ICircleComponent.ControlFlow acceptControlFlow(
      CastingImage imageIn, CircleCastEnv env, Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world
   ) {
      Direction sign = world.random.nextBoolean() ? (Direction)bs.getValue(FACING) : ((Direction)bs.getValue(FACING)).getOpposite();
      return new ICircleComponent.ControlFlow.Continue(imageIn, List.of(this.exitPositionFromDirection(pos, sign)));
   }

   @Override
   public boolean canEnterFromDirection(Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world) {
      return true;
   }

   @Override
   public EnumSet<Direction> possibleExitDirections(BlockPos pos, BlockState bs, Level world) {
      return EnumSet.of((Direction)bs.getValue(FACING), ((Direction)bs.getValue(FACING)).getOpposite());
   }

   @Override
   public Direction normalDir(BlockPos pos, BlockState bs, Level world, int recursionLeft) {
      return Direction.UP;
   }

   @Override
   public float particleHeight(BlockPos pos, BlockState bs, Level world) {
      return 0.5F;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return BlockCircleComponent.placeStateDirAndSneak(this.defaultBlockState(), pContext);
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(FACING, pRot.rotate((Direction)pState.getValue(FACING)));
   }

   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
   }
}
