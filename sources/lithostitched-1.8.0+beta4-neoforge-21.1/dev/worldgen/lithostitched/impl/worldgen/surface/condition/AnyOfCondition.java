package dev.worldgen.lithostitched.impl.worldgen.surface.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;

public record AnyOfCondition(List<ConditionSource> conditions) implements ConditionSource {
   public static final MapCodec<AnyOfCondition> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ConditionSource.CODEC.listOf().fieldOf("conditions").forGetter(AnyOfCondition::conditions))
         .apply(instance, AnyOfCondition::new)
   );
   public static final KeyDispatchDataCodec<AnyOfCondition> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

   public KeyDispatchDataCodec<? extends ConditionSource> codec() {
      return DATA_CODEC;
   }

   public net.minecraft.world.level.levelgen.SurfaceRules.Condition apply(Context context) {
      return new AnyOfCondition.Condition(
         this.conditions.stream().map(source -> (net.minecraft.world.level.levelgen.SurfaceRules.Condition)source.apply(context)).toList()
      );
   }

   private record Condition(List<net.minecraft.world.level.levelgen.SurfaceRules.Condition> conditions)
      implements net.minecraft.world.level.levelgen.SurfaceRules.Condition {
      public boolean test() {
         for (net.minecraft.world.level.levelgen.SurfaceRules.Condition condition : this.conditions) {
            if (condition.test()) {
               return true;
            }
         }

         return false;
      }
   }
}
