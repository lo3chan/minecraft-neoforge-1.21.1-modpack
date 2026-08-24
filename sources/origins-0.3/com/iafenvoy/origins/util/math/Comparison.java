package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public record Comparison(Comparison.CompareOperation comparison, double compareTo) {
   public static final MapCodec<Comparison> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Comparison.CompareOperation.CODEC.fieldOf("comparison").forGetter(Comparison::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(Comparison::compareTo)
         )
         .apply(i, Comparison::new)
   );
   public static final MapCodec<Optional<Comparison>> OPTIONAL_CODEC = CODEC.xmap(Optional::of, Optional::orElseThrow);

   public static MapCodec<Comparison> optionalCodec(Comparison.CompareOperation operation, double compareTo) {
      return RecordCodecBuilder.mapCodec(
         i -> i.group(
               Comparison.CompareOperation.CODEC.optionalFieldOf("comparison", operation).forGetter(Comparison::comparison),
               Codec.DOUBLE.optionalFieldOf("compare_to", compareTo).forGetter(Comparison::compareTo)
            )
            .apply(i, Comparison::new)
      );
   }

   public boolean compare(double current) {
      return this.comparison.compare(current, this.compareTo);
   }

   public boolean compare(int current) {
      return this.comparison.compare((double)current, this.compareTo);
   }

   public static enum CompareOperation implements StringRepresentable {
      LESS_THAN("<", (a, b) -> a < b),
      LESS_THAN_OR_EQUAL("<=", (a, b) -> a <= b),
      GREATER_THAN(">", (a, b) -> a > b),
      GREATER_THAN_OR_EQUAL(">=", (a, b) -> a >= b),
      EQUAL("==", Objects::equals),
      NOT_EQUAL("!=", (a, b) -> !Objects.equals(a, b));

      public static final Codec<Comparison.CompareOperation> CODEC = StringRepresentable.fromValues(Comparison.CompareOperation::values);
      private final String key;
      private final Comparison.CompareOperation.Comparator comparator;

      private CompareOperation(String key, Comparison.CompareOperation.Comparator comparator) {
         this.key = key;
         this.comparator = comparator;
      }

      public boolean compare(double current, double given) {
         return this.comparator.compare(current, given);
      }

      public boolean compare(double current, int given) {
         return this.comparator.compare(current, given);
      }

      @NotNull
      public String getSerializedName() {
         return this.key;
      }

      @FunctionalInterface
      private interface Comparator {
         boolean compare(double var1, double var3);
      }
   }
}
