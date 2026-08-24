package at.petrak.hexcasting.common.blocks.circles.directrix;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.circles.ICircleComponent;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapBoolDirectrixEmptyStack;
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapBoolDirectrixNotBool;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockBooleanDirectrix extends BlockCircleComponent {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final EnumProperty<BlockBooleanDirectrix.State> STATE = EnumProperty.create("state", BlockBooleanDirectrix.State.class);

   public BlockBooleanDirectrix(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ENERGIZED, false))
               .setValue(STATE, BlockBooleanDirectrix.State.NEITHER))
            .setValue(FACING, Direction.NORTH)
      );
   }

   @Override
   public ICircleComponent.ControlFlow acceptControlFlow(
      CastingImage imageIn, CircleCastEnv env, Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world
   ) {
      ArrayList<Iota> stack = new ArrayList<>(imageIn.getStack());
      if (stack.isEmpty()) {
         this.fakeThrowMishap(pos, bs, imageIn, env, new MishapBoolDirectrixEmptyStack(pos));
         return new ICircleComponent.ControlFlow.Stop();
      } else {
         Iota last = stack.remove(stack.size() - 1);
         if (last instanceof BooleanIota biota) {
            world.setBlockAndUpdate(pos, (BlockState)bs.setValue(STATE, biota.getBool() ? BlockBooleanDirectrix.State.TRUE : BlockBooleanDirectrix.State.FALSE));
            Direction outputDir = biota.getBool() ? ((Direction)bs.getValue(FACING)).getOpposite() : (Direction)bs.getValue(FACING);
            CastingImage imageOut = imageIn.copy(
               stack, imageIn.getParenCount(), imageIn.getParenthesized(), imageIn.getEscapeNext(), imageIn.getOpsConsumed(), imageIn.getUserData()
            );
            return new ICircleComponent.ControlFlow.Continue(imageOut, List.of(this.exitPositionFromDirection(pos, outputDir)));
         } else {
            this.fakeThrowMishap(pos, bs, imageIn, env, new MishapBoolDirectrixNotBool(last, pos));
            return new ICircleComponent.ControlFlow.Stop();
         }
      }
   }

   @Override
   public boolean canEnterFromDirection(Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world) {
      return enterDir != ((Direction)bs.getValue(FACING)).getOpposite() && enterDir != bs.getValue(FACING);
   }

   @Override
   public EnumSet<Direction> possibleExitDirections(BlockPos pos, BlockState bs, Level world) {
      return EnumSet.of((Direction)bs.getValue(FACING), ((Direction)bs.getValue(FACING)).getOpposite());
   }

   @Override
   public Direction normalDir(BlockPos pos, BlockState bs, Level world, int recursionLeft) {
      return normalDirOfOther(pos.relative((Direction)bs.getValue(FACING)), world, recursionLeft);
   }

   @Override
   public float particleHeight(BlockPos pos, BlockState bs, Level world) {
      return 0.5F;
   }

   @Override
   public BlockState endEnergized(BlockPos pos, BlockState bs, Level world) {
      BlockState newState = (BlockState)((BlockState)bs.setValue(ENERGIZED, false)).setValue(STATE, BlockBooleanDirectrix.State.NEITHER);
      world.setBlockAndUpdate(pos, newState);
      return newState;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{STATE, FACING});
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

   public static enum State implements StringRepresentable {
      NEITHER,
      TRUE,
      FALSE;

      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
