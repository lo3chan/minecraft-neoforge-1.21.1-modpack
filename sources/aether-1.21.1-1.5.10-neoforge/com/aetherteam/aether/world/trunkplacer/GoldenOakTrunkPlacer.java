package com.aetherteam.aether.world.trunkplacer;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class GoldenOakTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<GoldenOakTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
      instance -> trunkPlacerParts(instance).apply(instance, GoldenOakTrunkPlacer::new)
   );

   public GoldenOakTrunkPlacer(int height, int heightRandA, int heightRandB) {
      super(height, heightRandA, heightRandB);
   }

   protected TrunkPlacerType<?> type() {
      return (TrunkPlacerType<?>)AetherTrunkPlacerTypes.GOLDEN_OAK_TRUNK_PLACER.get();
   }

   public List<FoliageAttachment> placeTrunk(
      LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int height, BlockPos pos, TreeConfiguration config
   ) {
      TrunkPlacer.setDirtAt(level, blockSetter, random, pos.below(), config);

      for (int i = 0; i < height; i++) {
         if (i > 4 && random.nextInt(3) > 0 && i < 9) {
            this.branch(level, random, blockSetter, pos.getX(), pos.getY() + i, pos.getZ(), i / 4 - 1, config);
         }

         this.placeLog(level, blockSetter, random, pos.above(i), config);
      }

      return ImmutableList.of(new FoliageAttachment(pos.above(height), 0, false));
   }

   public void branch(
      LevelSimulatedReader level, RandomSource random, BiConsumer<BlockPos, BlockState> blockSetter, int i, int j, int k, int slant, TreeConfiguration config
   ) {
      int directionX = random.nextInt(3) - 1;
      int directionZ = random.nextInt(3) - 1;

      for (int n = 0; n < random.nextInt(2) + 1; n++) {
         i += directionX;
         j += slant;
         k += directionZ;
         this.placeLog(level, blockSetter, random, new BlockPos(i, j, k), config);
      }
   }
}
