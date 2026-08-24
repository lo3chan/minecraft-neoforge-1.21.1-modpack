package net.blay09.mods.balm.world.item;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

public interface DiscriminatedItems<T> extends Map<T, DeferredItem> {
   Stream<Entry<T, DeferredItem>> sortedEntries(Comparator<T> var1);

   default Stream<Entry<T, DeferredItem>> sortedEntries() {
      return this.sortedEntries(Comparator.nullsFirst(Comparator.naturalOrder()));
   }

   default Stream<DeferredItem> sortedValues(Comparator<T> comparator) {
      return this.sortedEntries(comparator).map(Entry::getValue);
   }

   default Stream<DeferredItem> sortedValues() {
      return this.sortedEntries().map(Entry::getValue);
   }

   static <T> Function<T, String> prefixWith(String name) {
      return it -> name + "_" + it;
   }

   static <T> Function<T, String> suffixWith(String name) {
      return it -> it + "_" + name;
   }

   static <T> Function<T, String> surroundWith(String prefix, String suffix) {
      return it -> prefix + "_" + it + "_" + suffix;
   }
}
