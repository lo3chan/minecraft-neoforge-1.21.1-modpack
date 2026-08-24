package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.function.Function;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public interface NumberComponent<S, T extends Number> extends RecipeComponent<T> {
   NumberComponent.IntRange INT = new NumberComponent.IntRange(null, -2147483648, 2147483647, Codec.INT);
   NumberComponent.LongRange LONG = new NumberComponent.LongRange(null, -9223372036854775808L, 9223372036854775807L, Codec.LONG);
   NumberComponent.FloatRange FLOAT = new NumberComponent.FloatRange(null, -1.0F / 0.0F, 1.0F / 0.0F, Codec.FLOAT);
   NumberComponent.DoubleRange DOUBLE = new NumberComponent.DoubleRange(null, -1.0 / 0.0, 1.0 / 0.0, Codec.DOUBLE);
   RecipeComponentType<?> INT_TYPE = RecipeComponentType.dynamic(
      KubeJS.id("int"),
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.INT.optionalFieldOf("min", -2147483648).forGetter(NumberComponent.IntRange::min),
               Codec.INT.optionalFieldOf("max", 2147483647).forGetter(NumberComponent.IntRange::max)
            )
            .apply(instance, NumberComponent::intRange)
      )
   );
   RecipeComponentType<?> LONG_TYPE = RecipeComponentType.dynamic(
      KubeJS.id("long"),
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.LONG.optionalFieldOf("min", -9223372036854775808L).forGetter(NumberComponent.LongRange::min),
               Codec.LONG.optionalFieldOf("max", 9223372036854775807L).forGetter(NumberComponent.LongRange::max)
            )
            .apply(instance, NumberComponent::longRange)
      )
   );
   RecipeComponentType<?> FLOAT_TYPE = RecipeComponentType.dynamic(
      KubeJS.id("float"),
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.FLOAT.optionalFieldOf("min", -1.0F / 0.0F).forGetter(NumberComponent.FloatRange::min),
               Codec.FLOAT.optionalFieldOf("max", 1.0F / 0.0F).forGetter(NumberComponent.FloatRange::max)
            )
            .apply(instance, NumberComponent::floatRange)
      )
   );
   RecipeComponentType<?> DOUBLE_TYPE = RecipeComponentType.dynamic(
      KubeJS.id("double"),
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.DOUBLE.optionalFieldOf("min", -1.0 / 0.0).forGetter(NumberComponent.DoubleRange::min),
               Codec.DOUBLE.optionalFieldOf("max", 1.0 / 0.0).forGetter(NumberComponent.DoubleRange::max)
            )
            .apply(instance, NumberComponent::doubleRange)
      )
   );
   RecipeComponentType<Integer> NON_NEGATIVE_INT = RecipeComponentType.unit(
      KubeJS.id("non_negative_int"), type -> new NumberComponent.IntRange(type, 0, 2147483647, KubeJSCodecs.NON_NEGATIVE_INT)
   );
   RecipeComponentType<Integer> POSITIVE_INT = RecipeComponentType.unit(
      KubeJS.id("positive_int"), type -> new NumberComponent.IntRange(type, 1, 2147483647, KubeJSCodecs.POSITIVE_INT)
   );
   RecipeComponentType<Long> NON_NEGATIVE_LONG = RecipeComponentType.unit(
      KubeJS.id("non_negative_long"), type -> new NumberComponent.LongRange(type, 0L, 9223372036854775807L, KubeJSCodecs.NON_NEGATIVE_LONG)
   );
   RecipeComponentType<Long> POSITIVE_LONG = RecipeComponentType.unit(
      KubeJS.id("positive_long"), type -> new NumberComponent.LongRange(type, 1L, 9223372036854775807L, KubeJSCodecs.POSITIVE_LONG)
   );
   RecipeComponentType<Float> NON_NEGATIVE_FLOAT = RecipeComponentType.unit(
      KubeJS.id("non_negative_float"), type -> new NumberComponent.FloatRange(type, 0.0F, 1.0F / 0.0F, KubeJSCodecs.NON_NEGATIVE_FLOAT)
   );
   RecipeComponentType<Float> POSITIVE_FLOAT = RecipeComponentType.unit(
      KubeJS.id("positive_float"), type -> new NumberComponent.FloatRange(type, 1.0E-45F, 1.0F / 0.0F, KubeJSCodecs.POSITIVE_FLOAT)
   );
   RecipeComponentType<Double> NON_NEGATIVE_DOUBLE = RecipeComponentType.unit(
      KubeJS.id("non_negative_double"), type -> new NumberComponent.DoubleRange(type, 0.0, 1.0 / 0.0, KubeJSCodecs.NON_NEGATIVE_DOUBLE)
   );
   RecipeComponentType<Double> POSITIVE_DOUBLE = RecipeComponentType.unit(
      KubeJS.id("positive_double"), type -> new NumberComponent.DoubleRange(type, 5.0E-324, 1.0 / 0.0, KubeJSCodecs.POSITIVE_DOUBLE)
   );

   static NumberComponent.IntRange intRange(int min, int max) {
      return min == -2147483648 && max == 2147483647 ? INT : NumberComponent.IntRange.of(null, min, max);
   }

   static NumberComponent.LongRange longRange(long min, long max) {
      return min == -9223372036854775808L && max == 9223372036854775807L ? LONG : NumberComponent.LongRange.of(null, min, max);
   }

   static NumberComponent.FloatRange floatRange(float min, float max) {
      return min == -1.0F / 0.0F && max == 1.0F / 0.0F ? FLOAT : NumberComponent.FloatRange.of(null, min, max);
   }

   static NumberComponent.DoubleRange doubleRange(double min, double max) {
      return min == -1.0 / 0.0 && max == 1.0 / 0.0 ? DOUBLE : NumberComponent.DoubleRange.of(null, min, max);
   }

   private static Number numberOf(Object from) {
      if (from instanceof Number n) {
         return n;
      } else if (from instanceof JsonPrimitive json) {
         return json.getAsNumber();
      } else if (from instanceof CharSequence) {
         return Double.parseDouble(from.toString());
      } else {
         throw new IllegalStateException("Expected a number!");
      }
   }

   @Override
   default TypeInfo typeInfo() {
      return TypeInfo.NUMBER;
   }

   @Override
   default boolean hasPriority(RecipeMatchContext cx, Object from) {
      return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
   }

   T min();

   T max();

   NumberComponent<S, T> range(T min, T max);

   default NumberComponent<S, T> min(T min) {
      return this.range(min, this.max());
   }

   default NumberComponent<S, T> max(T max) {
      return this.range(this.min(), max);
   }

   default String toString(@Nullable RecipeComponentType<?> typeOverride, String name, T min, T max) {
      if (typeOverride != null) {
         return typeOverride.toString();
      } else {
         T mn = this.min();
         T mx = this.max();
         if (min.equals(mn) && max.equals(mx)) {
            return name;
         } else if (min.equals(mn)) {
            return name + "<min," + mx + ">";
         } else {
            return max.equals(mx) ? name + "<" + mn + ",max>" : name + "<" + mn + "," + mx + ">";
         }
      }
   }

   public record DoubleRange(@Nullable RecipeComponentType<?> typeOverride, Double min, Double max, Codec<Double> codec)
      implements NumberComponent<NumberComponent.DoubleRange, Double> {
      public static NumberComponent.DoubleRange of(@Nullable RecipeComponentType<?> typeOverride, Double min, Double max) {
         return new NumberComponent.DoubleRange(typeOverride, min, max, Codec.doubleRange(min, max));
      }

      @Override
      public RecipeComponentType<?> type() {
         return this.typeOverride == null ? DOUBLE_TYPE : this.typeOverride;
      }

      @Override
      public TypeInfo typeInfo() {
         return TypeInfo.DOUBLE;
      }

      public Double wrap(RecipeScriptContext cx, Object from) {
         return Mth.clamp(NumberComponent.numberOf(from).doubleValue(), this.min, this.max);
      }

      public NumberComponent.DoubleRange range(Double min, Double max) {
         return of(null, min, max);
      }

      @Override
      public String toString() {
         return this.toString(this.typeOverride, "double", -1.0 / 0.0, 1.0 / 0.0);
      }
   }

   public record FloatRange(@Nullable RecipeComponentType<?> typeOverride, Float min, Float max, Codec<Float> codec)
      implements NumberComponent<NumberComponent.FloatRange, Float> {
      public static NumberComponent.FloatRange of(@Nullable RecipeComponentType<?> typeOverride, Float min, Float max) {
         return new NumberComponent.FloatRange(typeOverride, min, max, Codec.floatRange(min, max));
      }

      @Override
      public RecipeComponentType<?> type() {
         return this.typeOverride == null ? FLOAT_TYPE : this.typeOverride;
      }

      @Override
      public TypeInfo typeInfo() {
         return TypeInfo.FLOAT;
      }

      public Float wrap(RecipeScriptContext cx, Object from) {
         return Mth.clamp(NumberComponent.numberOf(from).floatValue(), this.min, this.max);
      }

      public NumberComponent.FloatRange range(Float min, Float max) {
         return of(null, min, max);
      }

      @Override
      public String toString() {
         return this.toString(this.typeOverride, "float", -1.0F / 0.0F, 1.0F / 0.0F);
      }
   }

   public record IntRange(@Nullable RecipeComponentType<?> typeOverride, Integer min, Integer max, Codec<Integer> codec)
      implements NumberComponent<NumberComponent.IntRange, Integer> {
      public static NumberComponent.IntRange of(@Nullable RecipeComponentType<?> typeOverride, Integer min, Integer max) {
         return new NumberComponent.IntRange(typeOverride, min, max, Codec.intRange(min, max));
      }

      @Override
      public RecipeComponentType<?> type() {
         return this.typeOverride == null ? INT_TYPE : this.typeOverride;
      }

      @Override
      public TypeInfo typeInfo() {
         return TypeInfo.INT;
      }

      public Integer wrap(RecipeScriptContext cx, Object from) {
         return Mth.clamp(NumberComponent.numberOf(from).intValue(), this.min, this.max);
      }

      public NumberComponent.IntRange range(Integer min, Integer max) {
         return of(null, min, max);
      }

      @Override
      public String toString() {
         return this.toString(this.typeOverride, "int", -2147483648, 2147483647);
      }
   }

   public record LongRange(@Nullable RecipeComponentType<?> typeOverride, Long min, Long max, Codec<Long> codec)
      implements NumberComponent<NumberComponent.LongRange, Long> {
      public static NumberComponent.LongRange of(@Nullable RecipeComponentType<?> typeOverride, Long min, Long max) {
         Function<Long, DataResult<Long>> checker = Codec.checkRange(min, max);
         return new NumberComponent.LongRange(typeOverride, min, max, Codec.LONG.flatXmap(checker, checker));
      }

      @Override
      public RecipeComponentType<?> type() {
         return this.typeOverride == null ? LONG_TYPE : this.typeOverride;
      }

      @Override
      public TypeInfo typeInfo() {
         return TypeInfo.LONG;
      }

      public Long wrap(RecipeScriptContext cx, Object from) {
         long val = NumberComponent.numberOf(from).longValue();
         return val < this.min ? this.min : Math.min(val, this.max);
      }

      public NumberComponent.LongRange range(Long min, Long max) {
         return of(null, min, max);
      }

      @Override
      public String toString() {
         return this.toString(this.typeOverride, "long", -9223372036854775808L, 9223372036854775807L);
      }
   }
}
