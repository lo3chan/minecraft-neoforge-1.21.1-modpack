package com.aetherteam.aether.world.foliageplacer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageSetter;

public class GoldenOakFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<GoldenOakFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
      instance -> foliagePlacerParts(instance)
         .and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter(placer -> placer.trunkHeight))
         .apply(instance, GoldenOakFoliagePlacer::new)
   );
   private final IntProvider trunkHeight;

   public GoldenOakFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
      super(radius, offset);
      this.trunkHeight = height;
   }

   protected FoliagePlacerType<?> type() {
      return (FoliagePlacerType<?>)AetherFoliagePlacerTypes.GOLDEN_OAK_FOLIAGE_PLACER.get();
   }

   protected void createFoliage(
      LevelSimulatedReader level,
      FoliageSetter foliageSetter,
      RandomSource random,
      TreeConfiguration config,
      int maxFreeTreeHeight,
      FoliageAttachment attachment,
      int foliageHeight,
      int foliageRadius,
      int offset
   ) {
      for (int i = offset; i >= offset - foliageHeight; i--) {
         this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos(), 4, i, attachment.doubleTrunk());
      }
   }

   public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
      return 7;
   }

   protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
      return Mth.square(localX) + Mth.square(localY + 2) + Mth.square(localZ) > 12 + random.nextInt(5);
   }
}
