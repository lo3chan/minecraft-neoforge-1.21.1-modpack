package io.github.razordevs.deep_aether.world.feature.tree.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class TwinTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<TwinTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(parts -> trunkPlacerParts(parts).apply(parts, TwinTrunkPlacer::new));

   public TwinTrunkPlacer(int i, int i1, int i2) {
      super(i, i1, i2);
   }

   protected TrunkPlacerType<?> type() {
      return (TrunkPlacerType<?>)DaTrunkPlacerTypes.TWIN_TRUNK_PLACER.get();
   }

   public List<FoliageAttachment> placeTrunk(
      LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> posState, RandomSource random, int p_226082_, BlockPos pos, TreeConfiguration config
   ) {
      Direction direction = Plane.HORIZONTAL.getRandomDirection(random);
      MutableBlockPos blockposState$mutableblockposState = pos.mutable();
      BlockPos pos1 = blockposState$mutableblockposState.below();
      List<FoliageAttachment> list = Lists.newArrayList();
      float m = random.nextFloat() + 1.5F;
      int oldX = 0;
      int oldY = 0;
      int x = 0;
      this.PlaceBaseTrunk(random, direction, level, posState, pos1.above(1), config);
      this.placeLog(level, posState, random, pos1.above(1), config);
      this.placeLog(level, posState, random, pos1.above(1).mutable().move(direction.getOpposite(), 1), config);

      int i;
      for (i = 2; i < p_226082_; i++) {
         x = Math.round((float)Math.round(Math.log(i + 1) / Math.log(m)));
         int y = Math.round((float)Math.round(Math.log(i - 1) / Math.log(m)));
         if (x > oldX + 1) {
            x = oldX + 1;
         }

         if (y > oldY + 1) {
            y = oldY + 1;
         }

         this.placeLog(level, posState, random, pos1.above(i).mutable().move(direction, x), config);
         this.placeLog(level, posState, random, pos1.above(i).mutable().move(direction.getOpposite(), y + 1), config);
         oldX = x;
         oldY = y;
      }

      list.add(new FoliageAttachment(pos1.above(i + random.nextInt(3)).mutable().move(direction, x), 0, false));
      list.add(new FoliageAttachment(pos1.above(i).mutable().move(direction.getOpposite(), x + 1), 0, false));
      return list;
   }

   private void PlaceBaseTrunk(
      RandomSource random,
      Direction direction,
      LevelSimulatedReader level,
      BiConsumer<BlockPos, BlockState> posState,
      BlockPos pos,
      TreeConfiguration configuration
   ) {
      for (int y = 0; y < 3; y++) {
         for (int x = 0; x < 3; x++) {
            int a = x == 1 ? 3 : 1;

            for (int z = 0; z < a; z++) {
               this.placeLog(level, posState, random, pos.above(y - 1).relative(direction, z - a / 2).relative(direction.getClockWise(), x - 1), configuration);
            }
         }
      }
   }
}
