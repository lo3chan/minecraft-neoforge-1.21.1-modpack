package io.github.razordevs.deep_aether.block.natural;

import com.mojang.serialization.MapCodec;
import io.github.razordevs.deep_aether.block.behavior.GoldenVines;
import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class GoldenVinesPlantBlock extends GrowingPlantBodyBlock implements BonemealableBlock, GoldenVines {
   public static final MapCodec<GoldenVinesPlantBlock> CODEC = simpleCodec(GoldenVinesPlantBlock::new);

   public GoldenVinesPlantBlock(Properties p_153000_) {
      super(p_153000_, Direction.UP, SHAPE, false);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BERRIES, Boolean.FALSE));
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)DABlocks.GOLDEN_VINES.get();
   }

   protected MapCodec<? extends GrowingPlantBodyBlock> codec() {
      return CODEC;
   }

   protected BlockState updateHeadAfterConvertedFromBody(BlockState value, BlockState blockState) {
      return (BlockState)blockState.setValue(BERRIES, (Boolean)value.getValue(BERRIES));
   }

   public ItemStack getCloneItemStack(LevelReader blockGetter, BlockPos blockPos, BlockState blockState) {
      return new ItemStack((ItemLike)DAItems.GOLDEN_BERRIES.get());
   }

   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult result) {
      return GoldenVines.use(player, blockState, level, pos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> stateBuilder) {
      stateBuilder.add(new Property[]{BERRIES});
   }

   public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState value) {
      return !(Boolean)value.getValue(BERRIES);
   }

   public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
      serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(BERRIES, Boolean.TRUE), 2);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      boolean HasValidHightAndBlock = false;

      for (int i = 1; i < 5; i++) {
         Block block = level.getBlockState(pos.below(i)).getBlock();
         if (block.defaultBlockState().is(DATags.Blocks.CAN_GOLDEN_VINES_SURVIVE_ON)) {
            HasValidHightAndBlock = true;
         }
      }

      BlockPos blockpos = pos.relative(this.growthDirection.getOpposite());
      BlockState blockstate = level.getBlockState(blockpos);
      return this.canAttachTo(blockstate) && HasValidHightAndBlock
         ? blockstate.is(this.getHeadBlock()) || blockstate.is(this.getBodyBlock()) || blockstate.isFaceSturdy(level, blockpos, this.growthDirection)
         : false;
   }
}
