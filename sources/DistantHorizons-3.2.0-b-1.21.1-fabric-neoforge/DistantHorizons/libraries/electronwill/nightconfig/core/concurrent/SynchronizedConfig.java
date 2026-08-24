package DistantHorizons.libraries.electronwill.nightconfig.core.concurrent;

import DistantHorizons.libraries.electronwill.nightconfig.core.AbstractCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.AbstractConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.InMemoryCommentedFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.TransformingMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SynchronizedConfig implements ConcurrentCommentedConfig {
   private SynchronizedConfig.DataHolder dataHolder;
   final Object rootMonitor;

   public static SynchronizedConfig convert(Config c) {
      return convert(c, null);
   }

   private static SynchronizedConfig convert(Config c, SynchronizedConfig parent) {
      if (c instanceof SynchronizedConfig) {
         return (SynchronizedConfig)c;
      } else {
         SynchronizedConfig result = new SynchronizedConfig(c.configFormat(), Config.getDefaultMapCreator(false), parent);
         CommentedConfig cc = CommentedConfig.fake(c);
         convertSubConfigs(cc, result);
         result.putAll(cc);
         result.putAllComments(cc);
         return result;
      }
   }

   private static void convertSubConfigs(Config c, SynchronizedConfig parent) {
      if (c instanceof AbstractConfig) {
         AbstractConfig conf = (AbstractConfig)c;
         conf.valueMap().replaceAll((k, v) -> convertValue(v, parent));
      } else {
         for (Config.Entry entry : c.entrySet()) {
            Object value = entry.getRawValue();
            Object converted = convertValue(value, parent);
            if (value != converted) {
               entry.setValue(converted);
            }
         }
      }
   }

   private static Object convertValue(Object v, SynchronizedConfig parent) {
      if (v instanceof Config) {
         SynchronizedConfig subConfig = convert((Config)v, parent);
         convertSubConfigs(subConfig, subConfig);
         return subConfig;
      } else if (v instanceof List) {
         List<?> l = (List<?>)v;
         List<Object> newList = new ArrayList<>((Collection<? extends Object>)l);
         newList.replaceAll(elem -> convertValue(elem, parent));
         return newList;
      } else {
         return v;
      }
   }

   public SynchronizedConfig() {
      this(InMemoryCommentedFormat.defaultInstance(), Config.getDefaultMapCreator(false));
   }

   public SynchronizedConfig(ConfigFormat<?> configFormat, Supplier<Map<String, Object>> mapSupplier) {
      this.rootMonitor = new Object();
      this.dataHolder = new SynchronizedConfig.DataHolder(this, configFormat, mapSupplier);
   }

   public SynchronizedConfig(ConfigFormat<?> configFormat, Supplier<Map<String, Object>> mapSupplier, SynchronizedConfig parent) {
      this.rootMonitor = parent == null ? new Object() : parent.rootMonitor;
      this.dataHolder = new SynchronizedConfig.DataHolder(parent == null ? this : parent, configFormat, mapSupplier);
   }

   public void replaceContentBy(SynchronizedConfig newContent) {
      synchronized (this.rootMonitor) {
         synchronized (newContent.rootMonitor) {
            this.dataHolder = newContent.dataHolder;
            newContent.dataHolder = null;
         }
      }
   }

   public void replaceContentBy(Config newContent) {
      if (newContent instanceof SynchronizedConfig) {
         this.replaceContentBy((SynchronizedConfig)newContent);
      } else {
         if (newContent instanceof StampedConfig) {
            throw new UnsupportedOperationException("SynchronizedConfig.replaceContentBy(StampedConfig) is illegal (and useless anyway).");
         }

         CommentedConfig cc = CommentedConfig.fake(newContent);
         Supplier<Map<String, Object>> mapSupplier = null;
         if (newContent instanceof StampedConfig.Accumulator) {
            mapSupplier = ((StampedConfig.Accumulator)newContent).mapSupplier();
         } else if (newContent instanceof AbstractConfig) {
            try {
               Map<String, Object> map = ((AbstractConfig)newContent).valueMap();
               if (map instanceof HashMap) {
                  mapSupplier = HashMap::new;
               } else if (map instanceof LinkedHashMap) {
                  mapSupplier = LinkedHashMap::new;
               }
            } catch (UnsupportedOperationException var8) {
               mapSupplier = null;
            }
         }

         if (mapSupplier == null) {
            mapSupplier = Config.getDefaultMapCreator(false);
         }

         synchronized (this.rootMonitor) {
            SynchronizedConfig.DataHolder dataHolder = new SynchronizedConfig.DataHolder(this, newContent.configFormat(), mapSupplier);
            dataHolder.putAll(cc);
            dataHolder.putAllComments(cc);
            convertSubConfigs(dataHolder, this);
            this.dataHolder = dataHolder;
         }
      }
   }

   @Override
   public <R> R bulkCommentedRead(Function<? super UnmodifiableCommentedConfig, R> action) {
      synchronized (this.rootMonitor) {
         return action.apply(this.dataHolder);
      }
   }

   @Override
   public <R> R bulkCommentedUpdate(Function<? super CommentedConfig, R> action) {
      synchronized (this.rootMonitor) {
         return action.apply(this.dataHolder);
      }
   }

   @Override
   public boolean add(List<String> path, Object value) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.add(path, value);
      }
   }

   @Override
   public void clearComments() {
      synchronized (this.rootMonitor) {
         this.dataHolder.clearComments();
      }
   }

   @Override
   public Map<String, String> commentMap() {
      synchronized (this.rootMonitor) {
         return new SynchronizedConfig.SynchronizedMap<>(this.dataHolder.commentMap(), this.rootMonitor);
      }
   }

   @Override
   public String removeComment(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.removeComment(path);
      }
   }

   @Override
   public String setComment(List<String> path, String comment) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.setComment(path, comment);
      }
   }

   @Override
   public boolean containsComment(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.containsComment(path);
      }
   }

   @Override
   public String getComment(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.getComment(path);
      }
   }

   @Override
   public ConfigFormat<?> configFormat() {
      synchronized (this.rootMonitor) {
         return this.dataHolder.configFormat();
      }
   }

   @Override
   public void clear() {
      synchronized (this.rootMonitor) {
         this.dataHolder.clear();
      }
   }

   public SynchronizedConfig createSubConfig() {
      return this.dataHolder.createSubConfig();
   }

   @Override
   public Set<? extends CommentedConfig.Entry> entrySet() {
      synchronized (this.rootMonitor) {
         return new SynchronizedConfig.SynchronizedSet<>(this.dataHolder.entrySet(), this.rootMonitor);
      }
   }

   @Override
   public <T> T remove(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.remove(path);
      }
   }

   @Override
   public <T> T set(List<String> path, Object value) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.set(path, value);
      }
   }

   @Override
   public String toString() {
      synchronized (this.rootMonitor) {
         return "SynchronizedConfig{" + this.dataHolder.toString() + "}";
      }
   }

   @Override
   public boolean contains(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.contains(path);
      }
   }

   @Override
   public boolean equals(Object obj) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.equals(obj);
      }
   }

   @Override
   public <T> T getRaw(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.getRaw(path);
      }
   }

   @Override
   public int hashCode() {
      synchronized (this.rootMonitor) {
         return this.dataHolder.hashCode();
      }
   }

   @Override
   public boolean isEmpty() {
      synchronized (this.rootMonitor) {
         return this.dataHolder.isEmpty();
      }
   }

   @Override
   public int size() {
      synchronized (this.rootMonitor) {
         return this.dataHolder.size();
      }
   }

   @Deprecated
   @Override
   public Map<String, Object> valueMap() {
      synchronized (this.rootMonitor) {
         Map<String, Object> transformingMap = new TransformingMap<>(this.dataHolder.valueMap(), o -> (Object)o, toWrite -> convertValue(toWrite, this), o -> o);
         return new SynchronizedConfig.SynchronizedMap<>(transformingMap, this.rootMonitor);
      }
   }

   @Override
   public boolean add(String path, Object value) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.add(path, value);
      }
   }

   @Override
   public void addAll(UnmodifiableConfig other) {
      synchronized (this.rootMonitor) {
         this.dataHolder.addAll(other);
      }
   }

   @Override
   public void putAll(UnmodifiableConfig other) {
      synchronized (this.rootMonitor) {
         this.dataHolder.putAll(other);
      }
   }

   @Override
   public <T> T remove(String path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.remove(path);
      }
   }

   @Override
   public void removeAll(UnmodifiableConfig toRemove) {
      synchronized (this.rootMonitor) {
         this.dataHolder.removeAll(toRemove);
      }
   }

   @Override
   public <T> T set(String path, Object value) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.set(path, value);
      }
   }

   @Override
   public UnmodifiableCommentedConfig unmodifiable() {
      return this.dataHolder.unmodifiable();
   }

   @Override
   public void update(List<String> path, Object value) {
      synchronized (this.rootMonitor) {
         this.dataHolder.update(path, value);
      }
   }

   @Override
   public <T> T apply(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.apply(path);
      }
   }

   @Override
   public <T> T get(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.get(path);
      }
   }

   @Override
   public boolean isNull(List<String> path) {
      synchronized (this.rootMonitor) {
         return this.dataHolder.isNull(path);
      }
   }

   @Override
   public Map<String, UnmodifiableCommentedConfig.CommentNode> getComments() {
      synchronized (this.rootMonitor) {
         return this.dataHolder.getComments();
      }
   }

   @Override
   public void getComments(Map<String, UnmodifiableCommentedConfig.CommentNode> destination) {
      synchronized (this.rootMonitor) {
         this.dataHolder.getComments(destination);
      }
   }

   @Override
   public void putAllComments(Map<String, UnmodifiableCommentedConfig.CommentNode> comments) {
      synchronized (this.rootMonitor) {
         this.dataHolder.putAllComments(comments);
      }
   }

   @Override
   public void putAllComments(UnmodifiableCommentedConfig commentedConfig) {
      synchronized (this.rootMonitor) {
         this.dataHolder.putAllComments(commentedConfig);
      }
   }

   private static final class DataHolder extends AbstractCommentedConfig {
      private SynchronizedConfig syncConfig;
      private final ConfigFormat<?> format;

      DataHolder(SynchronizedConfig parent) {
         super(parent.dataHolder.mapCreator);
         this.format = parent.configFormat();
         this.syncConfig = parent;
      }

      DataHolder(SynchronizedConfig syncConfig, ConfigFormat<?> configFormat, Supplier<Map<String, Object>> mapCreator) {
         super(mapCreator);
         this.format = configFormat;
         this.syncConfig = syncConfig;
      }

      @Override
      public AbstractCommentedConfig clone() {
         throw new UnsupportedOperationException();
      }

      public SynchronizedConfig createSubConfig() {
         synchronized (this.syncConfig.rootMonitor) {
            return new SynchronizedConfig(this.format, this.mapCreator, this.syncConfig);
         }
      }

      @Override
      public ConfigFormat<?> configFormat() {
         return this.format;
      }
   }

   private static class SynchronizedCollection<E> implements Collection<E> {
      private final Collection<E> coll;
      private final Object rootMonitor;

      SynchronizedCollection(Collection<E> coll, Object rootMonitor) {
         this.coll = coll;
         this.rootMonitor = rootMonitor;
      }

      @Override
      public boolean add(E e) {
         synchronized (this.rootMonitor) {
            return this.coll.add(e);
         }
      }

      @Override
      public boolean addAll(Collection<? extends E> c) {
         synchronized (this.rootMonitor) {
            return this.coll.addAll(c);
         }
      }

      @Override
      public void clear() {
         synchronized (this.rootMonitor) {
            this.coll.clear();
         }
      }

      @Override
      public boolean contains(Object o) {
         synchronized (this.rootMonitor) {
            return this.coll.contains(o);
         }
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         synchronized (this.rootMonitor) {
            return this.coll.containsAll(c);
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.rootMonitor) {
            return this.coll.isEmpty();
         }
      }

      @Override
      public Iterator<E> iterator() {
         synchronized (this.rootMonitor) {
            return new SynchronizedConfig.SynchronizedIterator<>(this.coll.iterator(), this.rootMonitor);
         }
      }

      @Override
      public boolean remove(Object o) {
         synchronized (this.rootMonitor) {
            return this.coll.remove(o);
         }
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         synchronized (this.rootMonitor) {
            return this.coll.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         synchronized (this.rootMonitor) {
            return this.coll.retainAll(c);
         }
      }

      @Override
      public int size() {
         synchronized (this.rootMonitor) {
            return this.coll.size();
         }
      }

      @Override
      public Object[] toArray() {
         synchronized (this.rootMonitor) {
            return this.coll.toArray();
         }
      }

      @Override
      public <T> T[] toArray(T[] a) {
         synchronized (this.rootMonitor) {
            return (T[])this.coll.toArray(a);
         }
      }

      @Override
      public boolean removeIf(Predicate<? super E> filter) {
         synchronized (this.rootMonitor) {
            return this.coll.removeIf(filter);
         }
      }

      @Override
      public void forEach(Consumer<? super E> action) {
         synchronized (this.rootMonitor) {
            this.coll.forEach(action);
         }
      }
   }

   private static final class SynchronizedIterator<E> implements Iterator<E> {
      private final Iterator<E> iter;
      private final Object rootMonitor;

      SynchronizedIterator(Iterator<E> iter, Object rootMonitor) {
         this.iter = iter;
         this.rootMonitor = rootMonitor;
      }

      @Override
      public void forEachRemaining(Consumer<? super E> action) {
         synchronized (this.rootMonitor) {
            this.iter.forEachRemaining(action);
         }
      }

      @Override
      public boolean hasNext() {
         synchronized (this.rootMonitor) {
            return this.iter.hasNext();
         }
      }

      @Override
      public E next() {
         synchronized (this.rootMonitor) {
            return this.iter.next();
         }
      }

      @Override
      public void remove() {
         synchronized (this.rootMonitor) {
            this.iter.remove();
         }
      }
   }

   private static final class SynchronizedMap<K, V> implements Map<K, V> {
      private final Map<K, V> map;
      private final Object rootMonitor;

      SynchronizedMap(Map<K, V> map, Object monitor) {
         this.map = map;
         this.rootMonitor = monitor;
      }

      @Override
      public boolean equals(Object obj) {
         synchronized (this.rootMonitor) {
            return this.map.equals(obj);
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.rootMonitor) {
            return this.map.hashCode();
         }
      }

      @Override
      public String toString() {
         synchronized (this.rootMonitor) {
            return this.map.toString();
         }
      }

      @Override
      public void clear() {
         synchronized (this.rootMonitor) {
            this.map.clear();
         }
      }

      @Override
      public boolean containsKey(Object key) {
         synchronized (this.rootMonitor) {
            return this.map.containsKey(key);
         }
      }

      @Override
      public boolean containsValue(Object value) {
         synchronized (this.rootMonitor) {
            return this.map.containsValue(value);
         }
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         synchronized (this.rootMonitor) {
            return new SynchronizedConfig.SynchronizedSet<>(this.map.entrySet(), this.rootMonitor);
         }
      }

      @Override
      public V get(Object key) {
         synchronized (this.rootMonitor) {
            return this.map.get(key);
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.rootMonitor) {
            return this.map.isEmpty();
         }
      }

      @Override
      public Set<K> keySet() {
         synchronized (this.rootMonitor) {
            return this.map.keySet();
         }
      }

      @Override
      public V put(K key, V value) {
         synchronized (this.rootMonitor) {
            return this.map.put(key, value);
         }
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> m) {
         synchronized (this.rootMonitor) {
            this.map.putAll(m);
         }
      }

      @Override
      public V remove(Object key) {
         synchronized (this.rootMonitor) {
            return this.map.remove(key);
         }
      }

      @Override
      public int size() {
         synchronized (this.rootMonitor) {
            return this.map.size();
         }
      }

      @Override
      public Collection<V> values() {
         synchronized (this.rootMonitor) {
            return this.map.values();
         }
      }
   }

   private static final class SynchronizedSet<E> extends SynchronizedConfig.SynchronizedCollection<E> implements Set<E> {
      SynchronizedSet(Set<E> coll, Object rootMonitor) {
         super(coll, rootMonitor);
      }
   }
}
