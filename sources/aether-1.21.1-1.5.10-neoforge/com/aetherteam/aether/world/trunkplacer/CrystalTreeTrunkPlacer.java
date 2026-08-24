package com.aetherteam.aether.world.trunkplacer;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class CrystalTreeTrunkPlacer extends StraightTrunkPlacer {
   public static final MapCodec<CrystalTreeTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
      instance -> trunkPlacerParts(instance).apply(instance, CrystalTreeTrunkPlacer::new)
   );

   public CrystalTreeTrunkPlacer(int height, int heightRandA, int heightRandB) {
      super(height, heightRandA, heightRandB);
   }

   protected TrunkPlacerType<?> type() {
      return (TrunkPlacerType<?>)AetherTrunkPlacerTypes.CRYSTAL_TREE_TRUNK_PLACER.get();
   }

   public List<FoliageAttachment> placeTrunk(
      LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int height, BlockPos pos, TreeConfiguration config
   ) {
      TrunkPlacer.setDirtAt(level, blockSetter, random, pos.below(), config);
      super.placeTrunk(level, blockSetter, random, height, pos, config);
      float f = 0.0F;

      for (int i = 2; i < 7; i += 3) {
         for (int l = 0; l < 4; l++) {
            int j = (int)Mth.cos(f);
            int k = (int)Mth.sin(f);
            BlockPos blockPos = pos.offset(j, i, k);
            this.placeLog(level, blockSetter, random, blockPos, config);
            f++;
         }
      }

      return ImmutableList.of(new FoliageAttachment(pos.above(height), 0, false));
   }
}
