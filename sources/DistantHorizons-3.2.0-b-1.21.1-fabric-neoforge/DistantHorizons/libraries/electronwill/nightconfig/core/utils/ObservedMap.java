package DistantHorizons.libraries.electronwill.nightconfig.core.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ObservedMap<K, V> extends AbstractObserved implements Map<K, V> {
   private final Map<K, V> map;

   public ObservedMap(Map<K, V> map, Runnable callback) {
      super(callback);
      this.map = map;
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return this.map.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.map.containsValue(value);
   }

   @Override
   public V get(Object key) {
      return this.map.get(key);
   }

   @Override
   public V put(K key, V value) {
      V result = this.map.put(key, value);
      this.callback.run();
      return result;
   }

   @Override
   public V remove(Object key) {
      V result = this.map.remove(key);
      this.callback.run();
      return result;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      this.map.putAll(m);
      this.callback.run();
   }

   @Override
   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      this.map.replaceAll(function);
      this.callback.run();
   }

   @Override
   public V putIfAbsent(K key, V value) {
      V result = this.map.putIfAbsent(key, value);
      if (result != value) {
         this.callback.run();
      }

      return result;
   }

   @Override
   public boolean remove(Object key, Object value) {
      boolean removed = this.map.remove(key, value);
      if (removed) {
         this.callback.run();
      }

      return removed;
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue) {
      boolean replaced = this.map.replace(key, oldValue, newValue);
      if (replaced) {
         this.callback.run();
      }

      return replaced;
   }

   @Override
   public V replace(K key, V value) {
      V result = this.map.replace(key, value);
      if (result != value) {
         this.callback.run();
      }

      return result;
   }

   @Override
   public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
      V result = this.map.computeIfAbsent(key, mappingFunction);
      if (result != null) {
         this.callback.run();
      }

      return result;
   }

   @Override
   public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      V result = this.map.computeIfPresent(key, remappingFunction);
      this.callback.run();
      return result;
   }

   @Override
   public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      V result = this.map.compute(key, remappingFunction);
      this.callback.run();
      return result;
   }

   @Override
   public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      V result = this.map.merge(key, value, remappingFunction);
      this.callback.run();
      return result;
   }

   @Override
   public void clear() {
      this.map.clear();
      this.callback.run();
   }

   @Override
   public Set<K> keySet() {
      return new ObservedSet<>(this.map.keySet(), this.callback);
   }

   @Override
   public Collection<V> values() {
      return this.map.values();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      Function<Entry<K, V>, ObservedEntry<K, V>> readT = e -> new ObservedEntry<>(e, this.callback);
      Function<ObservedEntry<K, V>, Entry<K, V>> writeT = oe -> oe.entry;
      Function<Object, Object> searchT = o -> {
         if (o instanceof ObservedEntry) {
            ObservedEntry<?, ?> observedEntry = (ObservedEntry<?, ?>)o;
            return observedEntry.entry;
         } else {
            return o;
         }
      };
      TransformingSet<Entry<K, V>, ObservedEntry<K, V>> tSet = new TransformingSet<>(this.map.entrySet(), readT, writeT, searchT);
      return new ObservedSet2<>(this.callback, tSet, mapEntry -> new ObservedEntry<>(mapEntry, this.callback));
   }

   @Override
   public boolean equals(Object obj) {
      return this.map.equals(obj);
   }

   @Override
   public int hashCode() {
      return this.map.hashCode();
   }
}
