package com.aetherteam.aether.world.foliageplacer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageSetter;

public class CrystalFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<CrystalFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
      instance -> foliagePlacerParts(instance)
         .and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter(placer -> placer.trunkHeight))
         .apply(instance, CrystalFoliagePlacer::new)
   );
   private final IntProvider trunkHeight;

   public CrystalFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
      super(radius, offset);
      this.trunkHeight = height;
   }

   protected FoliagePlacerType<?> type() {
      return (FoliagePlacerType<?>)AetherFoliagePlacerTypes.CRYSTAL_FOLIAGE_PLACER.get();
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
      BlockPos blockPos = attachment.pos();
      int i = 0;

      for (int l = offset; l >= offset - 5; l--) {
         this.placeLeavesRow(level, foliageSetter, random, config, blockPos, switch (i) {
            case 1, 3 -> 1;
            case 2, 4 -> {
               this.placeLeavesDiamond(level, foliageSetter, random, config, attachment, 1, l);
               yield 1;
            }
            case 5 -> 2;
            default -> 0;
         }, l, attachment.doubleTrunk());
         i++;
      }
   }

   private void placeLeavesDiamond(
      LevelSimulatedReader level,
      FoliageSetter foliageSetter,
      RandomSource random,
      TreeConfiguration config,
      FoliageAttachment attachment,
      int radius,
      int offset
   ) {
      this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos().north(), radius, offset, attachment.doubleTrunk());
      this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos().south(), radius, offset, attachment.doubleTrunk());
      this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos().west(), radius, offset, attachment.doubleTrunk());
      this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos().east(), radius, offset, attachment.doubleTrunk());
   }

   public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
      return Math.max(4, height - this.trunkHeight.sample(random));
   }

   protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
      return localX == range && localZ == range && range > 0;
   }
}
