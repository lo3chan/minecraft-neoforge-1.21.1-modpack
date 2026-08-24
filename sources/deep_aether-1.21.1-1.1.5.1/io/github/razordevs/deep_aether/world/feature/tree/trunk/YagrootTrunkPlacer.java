package io.github.razordevs.deep_aether.world.feature.tree.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class YagrootTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<YagrootTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
      p_70261_ -> trunkPlacerParts(p_70261_).apply(p_70261_, YagrootTrunkPlacer::new)
   );

   public YagrootTrunkPlacer(int p_161770_, int p_161771_, int p_161772_) {
      super(p_161770_, p_161771_, p_161772_);
   }

   protected TrunkPlacerType<?> type() {
      return (TrunkPlacerType<?>)DaTrunkPlacerTypes.YAGROOT_TRUNK_PLACER.get();
   }

   public List<FoliageAttachment> placeTrunk(
      LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> posState, RandomSource random, int int1, BlockPos pos, TreeConfiguration config
   ) {
      List<FoliageAttachment> list = Lists.newArrayList();

      int i;
      for (i = 0; i < int1; i++) {
         this.placeLog(level, posState, random, pos.above(i), config);
         if (i < int1 / 4 || i > int1 / 4 * 3) {
            this.placeLog(level, posState, random, pos.above(i).east(), config);
            this.placeLog(level, posState, random, pos.above(i).west(), config);
            this.placeLog(level, posState, random, pos.above(i).north(), config);
            this.placeLog(level, posState, random, pos.above(i).south(), config);
         }
      }

      for (int z = 0; z < 4; z++) {
         Direction direction;
         if (z == 0) {
            direction = Direction.NORTH;
         } else if (z == 1) {
            direction = Direction.SOUTH;
         } else if (z == 2) {
            direction = Direction.EAST;
         } else {
            direction = Direction.WEST;
         }

         int a = random.nextInt(3, 5);

         for (int x = 0; x / 2 + 1 <= a; x++) {
            int y = Math.round((float)Math.round(Math.sqrt(Math.pow(a, 2.0) - Math.pow(x - a, 2.0))));
            if (z == 0 || z == 1) {
               this.placeLog(
                  level,
                  posState,
                  random,
                  pos.relative(direction, x).above(i + y - 3),
                  config,
                  state -> (BlockState)state.trySetValue(RotatedPillarBlock.AXIS, Axis.Z)
               );
            }

            if (z == 2 || z == 3) {
               this.placeLog(
                  level,
                  posState,
                  random,
                  pos.relative(direction, x).above(i + y - 3),
                  config,
                  p_161826_ -> (BlockState)p_161826_.trySetValue(RotatedPillarBlock.AXIS, Axis.X)
               );
            }

            list.add(new FoliageAttachment(pos.relative(direction, x).above(i + y - 3), 0, false));
         }
      }

      if (this.isFree(level, pos.below())) {
         this.placeLog(level, posState, random, pos.below(), config);
      }

      if (this.isFree(level, pos.below().south())) {
         this.placeLog(level, posState, random, pos.below().south(), config);
      }

      if (this.isFree(level, pos.below().north())) {
         this.placeLog(level, posState, random, pos.below().north(), config);
      }

      if (this.isFree(level, pos.below().west())) {
         this.placeLog(level, posState, random, pos.below().west(), config);
      }

      if (this.isFree(level, pos.below().east())) {
         this.placeLog(level, posState, random, pos.below().east(), config);
      }

      return list;
   }
}
