package dev.worldgen.lithostitched.api.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public class WeightedList<E> {
   private final int totalWeight;
   private final List<Weighted<E>> items;
   private final WeightedList.Selector<E> selector;

   WeightedList(List<? extends Weighted<E>> entries) {
      this.items = List.copyOf(entries);
      this.totalWeight = getTotalWeight(entries, Weighted::weight);
      if (this.totalWeight == 0) {
         this.selector = null;
      } else if (entries.size() == 1) {
         this.selector = new WeightedList.Single<>((E)((Weighted)entries.getFirst()).value());
      } else if (this.totalWeight < 64) {
         this.selector = new WeightedList.Flat<>(this.items, this.totalWeight);
      } else {
         this.selector = new WeightedList.Compact<>(this.items);
      }
   }

   public static <T> int getTotalWeight(List<T> list, ToIntFunction<T> toIntFunction) {
      long totalWeight = 0L;

      for (T entry : list) {
         totalWeight += toIntFunction.applyAsInt(entry);
      }

      if (totalWeight > 2147483647L) {
         throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
      } else {
         return (int)totalWeight;
      }
   }

   public static <E> WeightedList<E> of() {
      return new WeightedList<>(List.of());
   }

   public static <E> WeightedList<E> of(E entry) {
      return new WeightedList<>(List.of(new Weighted<>(entry, 1)));
   }

   @SafeVarargs
   public static <E> WeightedList<E> of(Weighted<E>... entries) {
      return new WeightedList<>(List.of(entries));
   }

   public static <E> WeightedList<E> of(List<Weighted<E>> entries) {
      return new WeightedList<>(entries);
   }

   public static <E> WeightedList.Builder<E> builder() {
      return new WeightedList.Builder<>();
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public <T> WeightedList<T> map(Function<E, T> mapper) {
      return new WeightedList(Lists.transform(this.items, weighted -> weighted.map(mapper)));
   }

   public Optional<E> getRandom(RandomSource random) {
      if (this.selector == null) {
         return Optional.empty();
      } else {
         int value = random.nextInt(this.totalWeight);
         return Optional.of(this.selector.get(value));
      }
   }

   public E getRandomOrThrow(RandomSource random) {
      if (this.selector == null) {
         throw new IllegalStateException("Weighted list has no elements");
      } else {
         int $$1 = random.nextInt(this.totalWeight);
         return this.selector.get($$1);
      }
   }

   public List<Weighted<E>> unwrap() {
      return this.items;
   }

   public static <E> Codec<WeightedList<E>> codec(Codec<E> codec) {
      return LithostitchedCodecs.compactList(Weighted.codec(codec)).xmap(WeightedList::of, WeightedList::unwrap);
   }

   public static <E> Codec<WeightedList<E>> nonEmptyCodec(Codec<E> codec) {
      return ExtraCodecs.nonEmptyList(LithostitchedCodecs.compactList(Weighted.codec(codec))).xmap(WeightedList::of, WeightedList::unwrap);
   }

   public boolean contains(E entry) {
      for (Weighted<E> weighted : this.items) {
         if (weighted.value().equals(entry)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object that) {
      if (this == that) {
         return true;
      } else {
         return !(that instanceof WeightedList<?> thatList) ? false : this.totalWeight == thatList.totalWeight && Objects.equals(this.items, thatList.items);
      }
   }

   @Override
   public int hashCode() {
      int value = this.totalWeight;
      return 31 * value + this.items.hashCode();
   }

   public static class Builder<E> {
      private final com.google.common.collect.ImmutableList.Builder<Weighted<E>> result = ImmutableList.builder();

      public WeightedList.Builder<E> add(E entry) {
         return this.add(entry, 1);
      }

      public WeightedList.Builder<E> add(E entry, int weight) {
         this.result.add(new Weighted<>(entry, weight));
         return this;
      }

      public WeightedList<E> build() {
         return new WeightedList<>(this.result.build());
      }
   }

   static class Compact<E> implements WeightedList.Selector<E> {
      private final Weighted[] entries;

      Compact(List<Weighted<E>> entries) {
         this.entries = entries.toArray(Weighted[]::new);
      }

      @Override
      public E get(int value) {
         for (Weighted<?> entry : this.entries) {
            value -= entry.weight();
            if (value < 0) {
               return (E)entry.value();
            }
         }

         throw new IllegalStateException(value + " exceeded total weight");
      }
   }

   static class Flat<E> implements WeightedList.Selector<E> {
      private final Object[] entries;

      Flat(List<Weighted<E>> entries, int size) {
         this.entries = new Object[size];
         int totalWeight = 0;

         for (Weighted<E> entry : entries) {
            int weight = entry.weight();
            Arrays.fill(this.entries, totalWeight, totalWeight + weight, entry.value());
            totalWeight += weight;
         }
      }

      @Override
      public E get(int value) {
         return (E)this.entries[value];
      }
   }

   interface Selector<E> {
      E get(int var1);
   }

   static class Single<E> implements WeightedList.Selector<E> {
      private final E entry;

      Single(E entry) {
         this.entry = entry;
      }

      @Override
      public E get(int value) {
         return this.entry;
      }
   }
}
