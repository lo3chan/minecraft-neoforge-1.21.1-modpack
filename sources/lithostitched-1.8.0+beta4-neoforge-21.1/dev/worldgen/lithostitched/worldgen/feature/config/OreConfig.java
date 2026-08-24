package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record OreConfig(int size, List<OreConfig.Target> targets) implements FeatureConfiguration {
   public static final Codec<OreConfig> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.intRange(0, 128).fieldOf("size").forGetter(OreConfig::size), OreConfig.Target.CODEC.listOf().fieldOf("targets").forGetter(OreConfig::targets)
         )
         .apply(instance, OreConfig::new)
   );

   public static OreConfig create(int size, List<Pair<BlockPredicate, BlockStateProvider>> targets) {
      return new OreConfig(
         size, targets.stream().map(pair -> new OreConfig.Target((BlockPredicate)pair.getFirst(), (BlockStateProvider)pair.getSecond())).toList()
      );
   }

   public record Target(BlockPredicate predicate, BlockStateProvider stateProvider) {
      public static final Codec<OreConfig.Target> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               BlockPredicate.CODEC.fieldOf("predicate").forGetter(OreConfig.Target::predicate),
               BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(OreConfig.Target::stateProvider)
            )
            .apply(instance, OreConfig.Target::new)
      );
   }
}
