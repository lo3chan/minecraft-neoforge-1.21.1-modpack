package at.petrak.hexcasting.common.misc;

import at.petrak.hexcasting.common.lib.HexConfiguredFeatures;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class AkashicTreeGrower {
   public static final AkashicTreeGrower INSTANCE = new AkashicTreeGrower();
   public static final List<ResourceKey<ConfiguredFeature<?, ?>>> GROWERS = Lists.newArrayList();

   public static void init() {
      GROWERS.add(HexConfiguredFeatures.AMETHYST_EDIFIED_TREE);
      GROWERS.add(HexConfiguredFeatures.AVENTURINE_EDIFIED_TREE);
      GROWERS.add(HexConfiguredFeatures.CITRINE_EDIFIED_TREE);
   }

   private ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom) {
      return GROWERS.get(pRandom.nextInt(GROWERS.size()));
   }

   public boolean growTree(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random) {
      Reference<ConfiguredFeature<?, ?>> holder = (Reference<ConfiguredFeature<?, ?>>)level.registryAccess()
         .registryOrThrow(Registries.CONFIGURED_FEATURE)
         .getHolder(this.getConfiguredFeature(random))
         .orElse(null);
      if (holder == null) {
         return false;
      } else {
         level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
         if (((ConfiguredFeature)holder.value()).place(level, generator, random, pos)) {
            return true;
         } else {
            level.setBlock(pos, state, 4);
            return false;
         }
      }
   }
}
