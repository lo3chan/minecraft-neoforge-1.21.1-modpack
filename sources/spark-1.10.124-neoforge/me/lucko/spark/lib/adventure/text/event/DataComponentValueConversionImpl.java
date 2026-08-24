package me.lucko.spark.lib.adventure.text.event;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.internal.Internals;
import me.lucko.spark.lib.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

final class DataComponentValueConversionImpl<I, O> implements DataComponentValueConverterRegistry.Conversion<I, O> {
   private final Class<I> source;
   private final Class<O> destination;
   private final BiFunction<Key, I, O> conversion;

   DataComponentValueConversionImpl(@NotNull final Class<I> source, @NotNull final Class<O> destination, @NotNull final BiFunction<Key, I, O> conversion) {
      this.source = source;
      this.destination = destination;
      this.conversion = conversion;
   }

   @NotNull
   @Override
   public Class<I> source() {
      return this.source;
   }

   @NotNull
   @Override
   public Class<O> destination() {
      return this.destination;
   }

   @NotNull
   @Override
   public O convert(@NotNull final Key key, @NotNull final I input) {
      return this.conversion.apply(Objects.requireNonNull(key, "key"), Objects.requireNonNull(input, "input"));
   }

   @NotNull
   @Override
   public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(
         ExaminableProperty.of("source", this.source),
         ExaminableProperty.of("destination", this.destination),
         ExaminableProperty.of("conversion", this.conversion)
      );
   }

   @Override
   public String toString() {
      return Internals.toString(this);
   }

   @Override
   public boolean equals(final Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         DataComponentValueConversionImpl<?, ?> that = (DataComponentValueConversionImpl<?, ?>)other;
         return Objects.equals(this.source, that.source)
            && Objects.equals(this.destination, that.destination)
            && Objects.equals(this.conversion, that.conversion);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.source, this.destination, this.conversion);
   }
}
