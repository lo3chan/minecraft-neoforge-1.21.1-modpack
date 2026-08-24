package dev.worldgen.lithostitched.impl.worldgen.surface.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.duck.ContextAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import java.util.function.BiFunction;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.LazyXZCondition;

public record SlopeCondition(InclusiveRange<Integer> threshold) implements ConditionSource {
   private static final InclusiveRange<Integer> BASE_DIFFERENCE = new InclusiveRange(4, 2147483647);
   public static final MapCodec<SlopeCondition> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(LithostitchedCodecs.INT_RANGE.fieldOf("height_difference").orElse(BASE_DIFFERENCE).forGetter(SlopeCondition::threshold))
         .apply(instance, SlopeCondition::new)
   );
   public static final KeyDispatchDataCodec<SlopeCondition> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

   public KeyDispatchDataCodec<? extends ConditionSource> codec() {
      return DATA_CODEC;
   }

   public net.minecraft.world.level.levelgen.SurfaceRules.Condition apply(Context context) {
      return new SlopeCondition.Condition(context, this.threshold);
   }

   private static int operate(int a, int b, int c, int d, BiFunction<Integer, Integer, Integer> operation) {
      return operation.apply(operation.apply(a, b), operation.apply(c, d));
   }

   private static class Condition extends LazyXZCondition {
      private final ContextAccessor context;
      private final InclusiveRange<Integer> threshold;

      private Condition(Context context, InclusiveRange<Integer> threshold) {
         super(context);
         this.context = (ContextAccessor)context;
         this.threshold = threshold;
      }

      public boolean compute() {
         ChunkAccess chunkAccess = this.context.getChunk();
         int x = this.context.getX() & 15;
         int z = this.context.getZ() & 15;
         int north = Math.max(z - 1, 0);
         int south = Math.min(z + 1, 15);
         int west = Math.max(x - 1, 0);
         int east = Math.min(x + 1, 15);
         int northHeight = chunkAccess.getHeight(Types.WORLD_SURFACE_WG, x, north);
         int southHeight = chunkAccess.getHeight(Types.WORLD_SURFACE_WG, x, south);
         int westHeight = chunkAccess.getHeight(Types.WORLD_SURFACE_WG, west, z);
         int eastHeight = chunkAccess.getHeight(Types.WORLD_SURFACE_WG, east, z);
         return this.threshold
            .isValueInRange(
               SlopeCondition.operate(northHeight, southHeight, eastHeight, westHeight, Math::max)
                  - SlopeCondition.operate(northHeight, southHeight, eastHeight, westHeight, Math::min)
            );
      }
   }
}
