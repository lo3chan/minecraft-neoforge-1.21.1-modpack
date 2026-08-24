package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalUnmodifiableMap<K, V> implements Map<K, V>, CustomJavaToJsWrapper {
   private static final String MESSAGE = "'global' cannot be assigned to in client or server scripts";
   private final Map<K, V> unmodifiableMap;

   public GlobalUnmodifiableMap(Map<K, V> map) {
      this.unmodifiableMap = Collections.unmodifiableMap(map);
   }

   @Override
   public int size() {
      return this.unmodifiableMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this.unmodifiableMap.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return this.unmodifiableMap.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.unmodifiableMap.containsValue(value);
   }

   @Override
   public V get(Object key) {
      return this.unmodifiableMap.get(key);
   }

   @Nullable
   @Override
   public V put(K key, V value) {
      throw new KubeRuntimeException("'global' cannot be assigned to in client or server scripts");
   }

   @Nullable
   public V put(Context cx, K key, V value) {
      throw new KubeRuntimeException("'global' cannot be assigned to in client or server scripts").source(SourceLine.of(cx));
   }

   @Override
   public V remove(Object key) {
      throw new KubeRuntimeException("'global' cannot be assigned to in client or server scripts");
   }

   public V remove(Context cx, Object key) {
      throw new KubeRuntimeException("'global' cannot be assigned to in client or server scripts").source(SourceLine.of(cx));
   }

   @Override
   public void putAll(@NotNull Map<? extends K, ? extends V> m) {
      throw new KubeRuntimeException("'global' cannot be assigned to in client or server scripts");
   }

   public void putAll(Context cx, @NotNull Map<? extends K, ? extends V> m) {
      throw new KubeRuntimeException("'global' cannot be assigned to in client or server scripts").source(SourceLine.of(cx));
   }

   @Override
   public void clear() {
      throw new KubeRuntimeException("'global' cannot be assigned to in client or server scripts");
   }

   public void clear(Context cx) {
      throw new KubeRuntimeException("'global' cannot be assigned to in client or server scripts").source(SourceLine.of(cx));
   }

   @NotNull
   @Override
   public Set<K> keySet() {
      return this.unmodifiableMap.keySet();
   }

   @NotNull
   @Override
   public Collection<V> values() {
      return this.unmodifiableMap.values();
   }

   @NotNull
   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.unmodifiableMap.entrySet();
   }

   @Override
   public boolean equals(Object obj) {
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return this.unmodifiableMap.hashCode();
   }

   @Override
   public String toString() {
      return this.unmodifiableMap.toString();
   }

   public Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo staticType) {
      return new NativeJavaReadonlyMap(cx, scope, this, this, staticType, "'global' cannot be assigned to in client or server scripts");
   }
}
