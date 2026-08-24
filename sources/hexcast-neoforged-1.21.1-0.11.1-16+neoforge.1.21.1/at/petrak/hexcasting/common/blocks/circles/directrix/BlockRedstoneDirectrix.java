package at.petrak.hexcasting.common.blocks.circles.directrix;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.circles.ICircleComponent;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BlockRedstoneDirectrix extends BlockCircleComponent {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final BooleanProperty REDSTONE_POWERED = BlockStateProperties.POWERED;

   public BlockRedstoneDirectrix(Properties p_49795_) {
      super(p_49795_);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(REDSTONE_POWERED, false)).setValue(ENERGIZED, false))
            .setValue(FACING, Direction.NORTH)
      );
   }

   @Override
   public ICircleComponent.ControlFlow acceptControlFlow(
      CastingImage imageIn, CircleCastEnv env, Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world
   ) {
      return new ICircleComponent.ControlFlow.Continue(imageIn, List.of(this.exitPositionFromDirection(pos, this.getRealFacing(bs))));
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
      return normalDirOfOther(pos.relative(this.getRealFacing(bs)), world, recursionLeft);
   }

   @Override
   public float particleHeight(BlockPos pos, BlockState bs, Level world) {
      return 0.5F;
   }

   protected Direction getRealFacing(BlockState bs) {
      Direction facing = (Direction)bs.getValue(FACING);
      return bs.getValue(REDSTONE_POWERED) ? facing : facing.getOpposite();
   }

   public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
      super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
      if (!pLevel.isClientSide) {
         boolean currentlyPowered = (Boolean)pState.getValue(REDSTONE_POWERED);
         if (currentlyPowered != pLevel.hasNeighborSignal(pPos)) {
            pLevel.setBlock(pPos, (BlockState)pState.setValue(REDSTONE_POWERED, !currentlyPowered), 2);
         }
      }
   }

   public void animateTick(BlockState bs, Level pLevel, BlockPos pos, RandomSource rand) {
      if ((Boolean)bs.getValue(REDSTONE_POWERED)) {
         for (int i = 0; i < 2; i++) {
            Vector3f step = ((Direction)bs.getValue(FACING)).step();
            Vec3 center = Vec3.atCenterOf(pos).add(step.x() * 0.5, step.y() * 0.5, step.z() * 0.5);
            double x = center.x + (rand.nextDouble() - 0.5) * 0.5;
            double y = center.y + (rand.nextDouble() - 0.5) * 0.5;
            double z = center.z + (rand.nextDouble() - 0.5) * 0.5;
            pLevel.addParticle(DustParticleOptions.REDSTONE, x, y, z, step.x() * 0.1, step.y() * 0.1, step.z() * 0.1);
         }
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{REDSTONE_POWERED, FACING});
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
