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

public class HolidayFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<HolidayFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
      instance -> foliagePlacerParts(instance)
         .and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter(placer -> placer.trunkHeight))
         .apply(instance, HolidayFoliagePlacer::new)
   );
   private final IntProvider trunkHeight;

   public HolidayFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
      super(radius, offset);
      this.trunkHeight = height;
   }

   protected FoliagePlacerType<?> type() {
      return (FoliagePlacerType<?>)AetherFoliagePlacerTypes.HOLIDAY_FOLIAGE_PLACER.get();
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

      for (int l = offset; l >= offset - 7; l--) {
         switch (i) {
            case 1:
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos, 1, l, attachment.doubleTrunk());
               break;
            case 2:
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos, 1, l, attachment.doubleTrunk());
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos.east().north(), 0, l, attachment.doubleTrunk());
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos.west().north(), 0, l, attachment.doubleTrunk());
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos.east().south(), 0, l, attachment.doubleTrunk());
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos.west().south(), 0, l, attachment.doubleTrunk());
               break;
            case 3:
               this.disk360(level, foliageSetter, random, config, attachment.doubleTrunk(), blockPos, l, 1, 1);
               break;
            case 4:
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos, 2, l, attachment.doubleTrunk());
               this.disk360(level, foliageSetter, random, config, attachment.doubleTrunk(), blockPos, l, 3, 0);
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos.east(2).north(2), 0, l, attachment.doubleTrunk());
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos.west(2).north(2), 0, l, attachment.doubleTrunk());
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos.east(2).south(2), 0, l, attachment.doubleTrunk());
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos.west(2).south(2), 0, l, attachment.doubleTrunk());
               break;
            case 5:
               this.disk360(level, foliageSetter, random, config, attachment.doubleTrunk(), blockPos, l, 1, 2);
               break;
            case 6:
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos, 3, l, attachment.doubleTrunk());
               this.disk360(level, foliageSetter, random, config, attachment.doubleTrunk(), blockPos, l, 4, 0);
               break;
            case 7:
               this.disk360(level, foliageSetter, random, config, attachment.doubleTrunk(), blockPos, l, 2, 2);
               break;
            default:
               this.placeLeavesRow(level, foliageSetter, random, config, blockPos, i, l, attachment.doubleTrunk());
         }

         i++;
      }
   }

   private void disk360(
      LevelSimulatedReader level,
      FoliageSetter foliageSetter,
      RandomSource random,
      TreeConfiguration config,
      boolean doubleTrunk,
      BlockPos blockPos,
      int height,
      int distance,
      int range
   ) {
      this.placeLeavesRow(level, foliageSetter, random, config, blockPos.east(distance), range, height, doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, config, blockPos.south(distance), range, height, doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, config, blockPos.west(distance), range, height, doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, config, blockPos.north(distance), range, height, doubleTrunk);
   }

   public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
      return Math.max(4, height - this.trunkHeight.sample(random));
   }

   protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
      return localX == range && localZ == range && range > 0;
   }
}
