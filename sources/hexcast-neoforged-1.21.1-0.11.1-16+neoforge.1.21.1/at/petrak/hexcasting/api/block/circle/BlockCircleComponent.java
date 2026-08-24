package at.petrak.hexcasting.api.block.circle;

import at.petrak.hexcasting.api.casting.circles.ICircleComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class BlockCircleComponent extends Block implements ICircleComponent {
   public static final BooleanProperty ENERGIZED = BooleanProperty.create("energized");

   public BlockCircleComponent(Properties p_49795_) {
      super(p_49795_);
   }

   @Override
   public BlockState startEnergized(BlockPos pos, BlockState bs, Level world) {
      BlockState newState = (BlockState)bs.setValue(ENERGIZED, true);
      world.setBlockAndUpdate(pos, newState);
      return newState;
   }

   @Override
   public boolean isEnergized(BlockPos pos, BlockState bs, Level world) {
      return (Boolean)bs.getValue(ENERGIZED);
   }

   @Override
   public BlockState endEnergized(BlockPos pos, BlockState bs, Level world) {
      BlockState newState = (BlockState)bs.setValue(ENERGIZED, false);
      world.setBlockAndUpdate(pos, newState);
      return newState;
   }

   public Direction normalDir(BlockPos pos, BlockState bs, Level world) {
      return this.normalDir(pos, bs, world, 16);
   }

   public abstract Direction normalDir(BlockPos var1, BlockState var2, Level var3, int var4);

   public static Direction normalDirOfOther(BlockPos other, Level world, int recursionLeft) {
      if (recursionLeft <= 0) {
         return Direction.UP;
      } else {
         BlockState stateThere = world.getBlockState(other);
         return stateThere.getBlock() instanceof BlockCircleComponent bcc ? bcc.normalDir(other, stateThere, world, recursionLeft - 1) : Direction.UP;
      }
   }

   public abstract float particleHeight(BlockPos var1, BlockState var2, Level var3);

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{ENERGIZED});
   }

   public boolean hasAnalogOutputSignal(BlockState pState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
      return pState.getValue(ENERGIZED) ? 15 : 0;
   }

   public static BlockState placeStateDirAndSneak(BlockState stock, BlockPlaceContext ctx) {
      Direction dir = ctx.getNearestLookingDirection();
      if (ctx.getPlayer() != null && ctx.getPlayer().isDiscrete()) {
         dir = dir.getOpposite();
      }

      return (BlockState)stock.setValue(BlockStateProperties.FACING, dir);
   }
}
