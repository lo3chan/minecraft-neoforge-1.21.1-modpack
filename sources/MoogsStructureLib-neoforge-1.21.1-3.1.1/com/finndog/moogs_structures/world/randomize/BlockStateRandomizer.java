package com.finndog.moogs_structures.world.randomize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateRandomizer {
   public static final Codec<BlockStateRandomizer> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            BlockStateRandomizer.Entry.CODEC.listOf().optionalFieldOf("entries", List.of()).forGetter(r -> r.entries),
            BlockState.CODEC.fieldOf("default").forGetter(r -> r.defaultBlockState)
         )
         .apply(instance, BlockStateRandomizer::new)
   );
   private final List<BlockStateRandomizer.Entry> entries;
   private final BlockState defaultBlockState;

   public BlockStateRandomizer(List<BlockStateRandomizer.Entry> entries, BlockState defaultBlockState) {
      this.entries = entries;
      this.defaultBlockState = defaultBlockState;
   }

   public BlockState getDefaultBlockState() {
      return this.defaultBlockState;
   }

   public BlockState get(RandomSource random, int y) {
      float target = random.nextFloat();
      float currBottom = 0.0F;

      for (BlockStateRandomizer.Entry entry : this.entries) {
         if (currBottom <= target && target < currBottom + entry.probability && entry.passesAltitude(y)) {
            return entry.blockState;
         }

         currBottom += entry.probability;
      }

      return this.defaultBlockState;
   }

   public static BlockStateRandomizer single(BlockState state) {
      return new BlockStateRandomizer(List.of(), state == null ? Blocks.AIR.defaultBlockState() : state);
   }

   public static class Entry {
      public static final Codec<BlockStateRandomizer.Entry> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               BlockState.CODEC.fieldOf("block").forGetter(e -> e.blockState),
               Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(e -> e.probability),
               Codec.INT.optionalFieldOf("min_y").forGetter(e -> e.minY),
               Codec.INT.optionalFieldOf("max_y").forGetter(e -> e.maxY)
            )
            .apply(instance, BlockStateRandomizer.Entry::new)
      );
      public final BlockState blockState;
      public final float probability;
      public final Optional<Integer> minY;
      public final Optional<Integer> maxY;

      public Entry(BlockState blockState, float probability, Optional<Integer> minY, Optional<Integer> maxY) {
         this.blockState = blockState;
         this.probability = probability;
         this.minY = minY;
         this.maxY = maxY;
      }

      public boolean passesAltitude(int y) {
         return this.minY.isPresent() && y < this.minY.get() ? false : this.maxY.isEmpty() || y <= this.maxY.get();
      }
   }
}
