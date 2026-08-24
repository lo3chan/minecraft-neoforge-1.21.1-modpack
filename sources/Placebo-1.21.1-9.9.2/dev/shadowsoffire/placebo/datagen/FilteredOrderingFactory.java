package dev.shadowsoffire.placebo.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.data.DataProvider;

public record FilteredOrderingFactory(Predicate<Path> pathFilter, Predicate<JsonElement> jsonFilter, Comparator<String> comparator)
   implements FieldOrderingFactory {
   @Override
   public Comparator<String> getKeyComparator(JsonElement json, Path path) {
      return this.pathFilter.test(path) && this.jsonFilter.test(json) ? this.comparator : null;
   }

   public static FilteredOrderingFactory.Builder builder() {
      return new FilteredOrderingFactory.Builder();
   }

   public static class Builder {
      private Predicate<Path> pathFilter = p -> true;
      private Predicate<JsonElement> jsonFilter = j -> true;
      private Comparator<String> comparator;

      public FilteredOrderingFactory.Builder forObjectPath(String objPath) {
         String pathSegment = File.separator + objPath + File.separator;
         return this.pathFilter(p -> p.toString().contains(pathSegment));
      }

      public FilteredOrderingFactory.Builder forObjectSubtype(String typeKey, String subtype) {
         return this.jsonFilter(typeKey, new JsonPrimitive(subtype));
      }

      public FilteredOrderingFactory.Builder forObjectSubtype(String subtype) {
         return this.forObjectSubtype("type", subtype);
      }

      public FilteredOrderingFactory.Builder order(String... fieldsInOrder) {
         Object2IntOpenHashMap<String> map = defaultMap();

         for (int i = 0; i < fieldsInOrder.length; i++) {
            map.put(fieldsInOrder[i], 2 + i);
         }

         map.defaultReturnValue(2 + fieldsInOrder.length);
         Comparator<String> comparator = Comparator.<String>comparingInt(map).thenComparing(Function.identity());
         return this.comparator(comparator);
      }

      public FilteredOrderingFactory.Builder orderMap(Consumer<Object2IntOpenHashMap<String>> orderBuilder) {
         Object2IntOpenHashMap<String> map = defaultMap();
         orderBuilder.accept(map);
         Comparator<String> comparator = Comparator.<String>comparingInt(map).thenComparing(Function.identity());
         return this.comparator(comparator);
      }

      public FilteredOrderingFactory.Builder pathFilter(Predicate<Path> filter) {
         this.pathFilter = filter;
         return this;
      }

      public <T> FilteredOrderingFactory.Builder jsonFilter(String key, JsonElement value) {
         return this.jsonFilter(j -> j.isJsonObject() && value.equals(j.getAsJsonObject().get(key)));
      }

      public FilteredOrderingFactory.Builder jsonFilter(Predicate<JsonElement> filter) {
         this.jsonFilter = filter;
         return this;
      }

      public FilteredOrderingFactory.Builder comparator(Comparator<String> comparator) {
         this.comparator = comparator;
         return this;
      }

      public FilteredOrderingFactory build() {
         if (this.comparator == null) {
            throw new IllegalStateException("Comparator must be set");
         } else {
            return new FilteredOrderingFactory(this.pathFilter, this.jsonFilter, this.comparator);
         }
      }

      private static Object2IntOpenHashMap<String> defaultMap() {
         Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap((Object2IntOpenHashMap)DataProvider.FIXED_ORDER_FIELDS);
         map.defaultReturnValue(2);
         return map;
      }
   }
}
