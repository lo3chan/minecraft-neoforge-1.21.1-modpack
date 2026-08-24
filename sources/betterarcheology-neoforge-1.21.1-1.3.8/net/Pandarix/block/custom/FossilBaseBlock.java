package net.Pandarix.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FossilBaseBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<FossilBaseBlock> CODEC = simpleCodec(FossilBaseBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

   @NotNull
   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   protected FossilBaseBlock(Properties settings) {
      super(settings);
   }

   public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      if (!levelAccessor.isClientSide()) {
         levelAccessor.playSound(null, blockPos, SoundEvents.SKELETON_HURT, SoundSource.BLOCKS, 0.1F, 0.35F);
      }

      super.destroy(levelAccessor, blockPos, blockState);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{FACING});
   }
}
