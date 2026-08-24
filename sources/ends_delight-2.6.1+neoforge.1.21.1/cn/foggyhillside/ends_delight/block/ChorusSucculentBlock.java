package cn.foggyhillside.ends_delight.block;

import cn.foggyhillside.ends_delight.registry.ModBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChorusSucculentBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<ChorusSucculentBlock> CODEC = simpleCodec(ChorusSucculentBlock::new);
   public static final IntegerProperty SUCCULENT = ModBlockStateProperties.SUCCULENT_1_3;
   protected static final VoxelShape ONE_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
   protected static final VoxelShape TWO_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
   protected static final VoxelShape THREE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);

   public ChorusSucculentBlock(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(SUCCULENT, 1));
   }

   protected MapCodec<? extends BushBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos());
      return blockstate.is(this)
         ? (BlockState)blockstate.setValue(SUCCULENT, Math.min(3, (Integer)blockstate.getValue(SUCCULENT) + 1))
         : super.getStateForPlacement(pContext);
   }

   protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return !pState.getCollisionShape(pLevel, pPos).getFaceShape(Direction.UP).isEmpty() || pState.isFaceSturdy(pLevel, pPos, Direction.UP);
   }

   protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      BlockPos blockpos = pPos.below();
      return this.mayPlaceOn(pLevel.getBlockState(blockpos), pLevel, blockpos);
   }

   protected boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
      return !pUseContext.isSecondaryUseActive() && pUseContext.getItemInHand().is(this.asItem()) && (Integer)pState.getValue(SUCCULENT) < 3
         || super.canBeReplaced(pState, pUseContext);
   }

   protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      switch (pState.getValue(SUCCULENT)) {
         case 1:
         default:
            return ONE_SHAPE;
         case 2:
            return TWO_SHAPE;
         case 3:
            return THREE_SHAPE;
      }
   }

   protected BlockState updateShape(
      BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos
   ) {
      return !pState.canSurvive(pLevel, pCurrentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
   }

   public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
      return true;
   }

   public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
      return true;
   }

   public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
      if (pLevel.getBlockState(pPos.below()).is(net.neoforged.neoforge.common.Tags.Blocks.END_STONES)) {
         pLevel.setBlock(pPos, (BlockState)pState.setValue(SUCCULENT, 3), 3);
      }
   }

   protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
      return false;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{SUCCULENT});
   }
}
