package io.github.razordevs.deep_aether.block.utility;

import com.mojang.serialization.MapCodec;
import io.github.razordevs.deep_aether.entity.block.CombinerBlockEntity;
import io.github.razordevs.deep_aether.init.DABlockEntityTypes;
import io.github.razordevs.deep_aether.init.DABlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.Nullable;

public class CombinerBlock extends AbstractFurnaceBlock {
   public static final MapCodec<CombinerBlock> CODEC = simpleCodec(CombinerBlock::new);
   public static final BooleanProperty CHARGING = DABlockStateProperties.COMBINER_CHARGING;
   public static final BooleanProperty COMBINING = DABlockStateProperties.COMBINER_COMBINING;

   public CombinerBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(CHARGING, false))
            .setValue(COMBINING, false)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{CHARGING, COMBINING});
   }

   protected MapCodec<? extends CombinerBlock> codec() {
      return CODEC;
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.getBlock() != pNewState.getBlock()) {
         BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
         if (blockEntity instanceof CombinerBlockEntity) {
            ((CombinerBlockEntity)blockEntity).drops();
         }
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   protected void openContainer(Level level, BlockPos blockPos, Player player) {
      if (!level.isClientSide()) {
         BlockEntity entity = level.getBlockEntity(blockPos);
         if (entity instanceof CombinerBlockEntity) {
            player.openMenu((CombinerBlockEntity)entity);
         }
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return createTicker(level, blockEntityType, (BlockEntityType<? extends CombinerBlockEntity>)DABlockEntityTypes.COMBINER.get());
   }

   @Nullable
   protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(
      Level level, BlockEntityType<T> serverType, BlockEntityType<? extends CombinerBlockEntity> clientType
   ) {
      return level.isClientSide() ? null : createTickerHelper(serverType, clientType, CombinerBlockEntity::serverTick);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new CombinerBlockEntity(blockPos, blockState);
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public boolean isPathfindable(BlockState state, PathComputationType type) {
      return false;
   }
}
