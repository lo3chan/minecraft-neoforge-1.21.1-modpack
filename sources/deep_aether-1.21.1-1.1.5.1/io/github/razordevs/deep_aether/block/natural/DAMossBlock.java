package io.github.razordevs.deep_aether.block.natural;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.natural.AetherDoubleDropBlock;
import io.github.razordevs.deep_aether.datagen.registry.DAConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class DAMossBlock extends AetherDoubleDropBlock implements BonemealableBlock {
   public DAMossBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState p_50899_) {
      return level.getBlockState(pos.above()).isAir();
   }

   public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pPos, BlockState pState) {
      level.registryAccess()
         .registry(Registries.CONFIGURED_FEATURE)
         .flatMap(features -> features.getHolder(DAConfiguredFeatures.AETHER_MOSS_PATCH_BONEMEAL))
         .ifPresent(feature -> ((ConfiguredFeature)feature.value()).place(level, level.getChunkSource().getGenerator(), random, pPos.above()));
   }
}
