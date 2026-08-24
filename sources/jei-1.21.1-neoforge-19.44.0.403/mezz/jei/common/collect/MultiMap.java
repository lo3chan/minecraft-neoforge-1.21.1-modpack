package mezz.jei.common.collect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultiMap<K, V, T extends Collection<V>> {
   protected final Map<K, T> map;
   private final Function<K, T> collectionMappingFunction;

   public MultiMap(Supplier<T> collectionSupplier) {
      this(new HashMap<>(), collectionSupplier);
   }

   public MultiMap(Map<K, T> map, Supplier<T> collectionSupplier) {
      this.map = map;
      this.collectionMappingFunction = k -> collectionSupplier.get();
   }

   public Collection<V> get(K key) {
      T collection = this.map.get(key);
      return (Collection<V>)(collection != null ? Collections.unmodifiableCollection(collection) : Collections.emptyList());
   }

   public boolean put(K key, V value) {
      T collection = this.map.computeIfAbsent(key, this.collectionMappingFunction);
      return collection.add(value);
   }

   public boolean putAll(K key, Collection<V> values) {
      T collection = this.map.computeIfAbsent(key, this.collectionMappingFunction);
      return collection.addAll(values);
   }

   public boolean remove(K key, V value) {
      T collection = this.map.get(key);
      return collection != null && collection.remove(value);
   }

   public boolean containsKey(K key) {
      return this.map.containsKey(key);
   }

   public boolean contains(K key, V value) {
      T collection = this.map.get(key);
      return collection != null && collection.contains(value);
   }

   public Set<Entry<K, T>> entrySet() {
      return this.map.entrySet();
   }

   public Set<K> keySet() {
      return this.map.keySet();
   }

   public Collection<V> allValues() {
      List<V> list = new ArrayList<>();

      for (T t : this.map.values()) {
         list.addAll(t);
      }

      return list;
   }

   public void clear() {
      this.map.clear();
   }

   public ImmutableMultimap<K, V> toImmutable() {
      Builder<K, V> builder = ImmutableMultimap.builder();
      this.map.forEach(builder::putAll);
      return builder.build();
   }
}
