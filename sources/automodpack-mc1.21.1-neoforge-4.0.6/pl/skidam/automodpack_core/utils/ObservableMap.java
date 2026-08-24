package pl.skidam.automodpack_core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ObservableMap<K, V> {
   private final Map<K, V> synchronizedMap;
   private List<BiConsumer<K, V>> onPutCallbacks = new ArrayList<>();
   private List<BiConsumer<K, V>> onRemoveCallbacks = new ArrayList<>();

   public ObservableMap() {
      this.synchronizedMap = Collections.synchronizedMap(new HashMap<>());
   }

   public ObservableMap(int initialCapacity) {
      this.synchronizedMap = Collections.synchronizedMap(new HashMap<>(initialCapacity));
   }

   public ObservableMap(Map<? extends K, ? extends V> m) {
      this.synchronizedMap = Collections.synchronizedMap(new HashMap<>(m));
   }

   public synchronized V put(K key, V value) {
      V result = this.synchronizedMap.put(key, value);

      for (BiConsumer<K, V> callback : this.onPutCallbacks) {
         callback.accept(key, value);
      }

      return result;
   }

   public synchronized V remove(Object key) {
      V result = this.synchronizedMap.remove(key);

      for (BiConsumer<K, V> callback : this.onRemoveCallbacks) {
         callback.accept((K)key, result);
      }

      return result;
   }

   public void clear() {
      this.synchronizedMap.clear();
      this.onPutCallbacks = new ArrayList<>();
      this.onRemoveCallbacks = new ArrayList<>();
   }

   public void addOnPutCallback(BiConsumer<K, V> callback) {
      this.onPutCallbacks.add(callback);
   }

   public void addOnRemoveCallback(BiConsumer<K, V> callback) {
      this.onRemoveCallbacks.add(callback);
   }

   public Map<K, V> getMap() {
      return Collections.unmodifiableMap(this.synchronizedMap);
   }
}
