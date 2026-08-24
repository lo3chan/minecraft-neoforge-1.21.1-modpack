package com.iafenvoy.jupiter.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

public class CopyOnWriteHashMap<K, V> implements Map<K, V> {
   private volatile Map<K, V> internalMap;
   private final ReentrantLock lock = new ReentrantLock();

   public CopyOnWriteHashMap() {
      this.internalMap = new HashMap<>();
   }

   public CopyOnWriteHashMap(Map<? extends K, ? extends V> m) {
      this.internalMap = new HashMap<>(m);
   }

   @Override
   public V get(Object key) {
      return this.internalMap.get(key);
   }

   @Override
   public V put(K key, V value) {
      this.lock.lock();

      Object var5;
      try {
         Map<K, V> newMap = new HashMap<>(this.internalMap);
         V oldValue = newMap.put(key, value);
         this.internalMap = newMap;
         var5 = oldValue;
      } finally {
         this.lock.unlock();
      }

      return (V)var5;
   }

   @Override
   public void putAll(@NotNull Map<? extends K, ? extends V> m) {
      this.lock.lock();

      try {
         Map<K, V> newMap = new HashMap<>(this.internalMap);
         newMap.putAll(m);
         this.internalMap = newMap;
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public V remove(Object key) {
      this.lock.lock();

      Object var4;
      try {
         Map<K, V> newMap = new HashMap<>(this.internalMap);
         V oldValue = newMap.remove(key);
         this.internalMap = newMap;
         var4 = oldValue;
      } finally {
         this.lock.unlock();
      }

      return (V)var4;
   }

   @Override
   public void clear() {
      this.lock.lock();

      try {
         this.internalMap = new HashMap<>();
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public boolean containsKey(Object key) {
      return this.internalMap.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.internalMap.containsValue(value);
   }

   @Override
   public int size() {
      return this.internalMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this.internalMap.isEmpty();
   }

   @NotNull
   @Override
   public Set<K> keySet() {
      return Collections.unmodifiableSet(this.internalMap.keySet());
   }

   @NotNull
   @Override
   public Collection<V> values() {
      return Collections.unmodifiableCollection(this.internalMap.values());
   }

   @NotNull
   @Override
   public Set<Entry<K, V>> entrySet() {
      return Collections.unmodifiableSet(this.internalMap.entrySet());
   }

   @Override
   public V putIfAbsent(K key, V value) {
      this.lock.lock();

      Object var3;
      try {
         if (this.internalMap.containsKey(key)) {
            return this.internalMap.get(key);
         }

         var3 = this.put(key, value);
      } finally {
         this.lock.unlock();
      }

      return (V)var3;
   }

   @Override
   public boolean remove(Object key, Object value) {
      this.lock.lock();

      boolean var3;
      try {
         if (!this.internalMap.containsKey(key) || !Objects.equals(this.internalMap.get(key), value)) {
            return false;
         }

         this.remove(key);
         var3 = true;
      } finally {
         this.lock.unlock();
      }

      return var3;
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue) {
      this.lock.lock();

      boolean var4;
      try {
         if (!this.internalMap.containsKey(key) || !Objects.equals(this.internalMap.get(key), oldValue)) {
            return false;
         }

         this.put(key, newValue);
         var4 = true;
      } finally {
         this.lock.unlock();
      }

      return var4;
   }

   @Override
   public V replace(K key, V value) {
      this.lock.lock();

      Object var3;
      try {
         if (!this.internalMap.containsKey(key)) {
            return null;
         }

         var3 = this.put(key, value);
      } finally {
         this.lock.unlock();
      }

      return (V)var3;
   }

   @Override
   public String toString() {
      return this.internalMap.toString();
   }
}
