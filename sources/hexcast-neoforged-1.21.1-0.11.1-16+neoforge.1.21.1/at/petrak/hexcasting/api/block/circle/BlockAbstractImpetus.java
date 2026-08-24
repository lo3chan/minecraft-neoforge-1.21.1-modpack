package at.petrak.hexcasting.api.block.circle;

import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.casting.circles.ICircleComponent;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class BlockAbstractImpetus extends BlockCircleComponent implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;

   public BlockAbstractImpetus(Properties p_49795_) {
      super(p_49795_);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ENERGIZED, false)).setValue(FACING, Direction.NORTH));
   }

   @Override
   public ICircleComponent.ControlFlow acceptControlFlow(
      CastingImage imageIn, CircleCastEnv env, Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world
   ) {
      return new ICircleComponent.ControlFlow.Stop();
   }

   @Override
   public boolean canEnterFromDirection(Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world) {
      return enterDir != ((Direction)bs.getValue(FACING)).getOpposite();
   }

   @Override
   public EnumSet<Direction> possibleExitDirections(BlockPos pos, BlockState bs, Level world) {
      return EnumSet.of((Direction)bs.getValue(FACING));
   }

   @Override
   public Direction normalDir(BlockPos pos, BlockState bs, Level world, int recursionLeft) {
      return normalDirOfOther(pos.relative((Direction)bs.getValue(FACING)), world, recursionLeft);
   }

   @Override
   public float particleHeight(BlockPos pos, BlockState bs, Level world) {
      return 0.5F;
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      if (pLevel.getBlockEntity(pPos) instanceof BlockEntityAbstractImpetus tile && (Boolean)pState.getValue(ENERGIZED)) {
         tile.tickExecution();
      }
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pNewState.is(pState.getBlock()) && pLevel.getBlockEntity(pPos) instanceof BlockEntityAbstractImpetus impetus) {
         impetus.endExecution();
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
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
