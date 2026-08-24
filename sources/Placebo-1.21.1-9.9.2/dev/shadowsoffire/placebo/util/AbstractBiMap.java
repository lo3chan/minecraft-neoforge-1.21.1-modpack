package dev.shadowsoffire.placebo.util;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingSet;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public abstract class AbstractBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable {
   private transient Map<K, V> delegate;
   transient AbstractBiMap<V, K> inverse;
   @LazyInit
   private transient Set<K> keySet;
   private transient Set<V> valueSet;
   @LazyInit
   private transient Set<Entry<K, V>> entrySet;

   public AbstractBiMap(Map<K, V> forward, Map<V, K> backward) {
      this.setDelegates(forward, backward);
   }

   private AbstractBiMap(Map<K, V> backward, AbstractBiMap<V, K> forward) {
      this.delegate = backward;
      this.inverse = forward;
   }

   protected Map<K, V> delegate() {
      return this.delegate;
   }

   K checkKey(K key) {
      return key;
   }

   V checkValue(V value) {
      return value;
   }

   void setDelegates(Map<K, V> forward, Map<V, K> backward) {
      Preconditions.checkState(this.delegate == null);
      Preconditions.checkState(this.inverse == null);
      Preconditions.checkArgument(forward.isEmpty());
      Preconditions.checkArgument(backward.isEmpty());
      Preconditions.checkArgument(forward != backward);
      this.delegate = forward;
      this.inverse = this.makeInverse(backward);
   }

   AbstractBiMap<V, K> makeInverse(Map<V, K> backward) {
      return new AbstractBiMap.Inverse<>(backward, this);
   }

   void setInverse(AbstractBiMap<V, K> inverse) {
      this.inverse = inverse;
   }

   public boolean containsValue(Object value) {
      return this.inverse.containsKey(value);
   }

   public V put(K key, V value) {
      return this.putInBothMaps(key, value, false);
   }

   public V forcePut(K key, V value) {
      return this.putInBothMaps(key, value, true);
   }

   private V putInBothMaps(K key, V value, boolean force) {
      this.checkKey(key);
      this.checkValue(value);
      boolean containedKey = this.containsKey(key);
      if (containedKey && Objects.equal(value, this.get(key))) {
         return value;
      } else {
         if (force) {
            this.inverse().remove(value);
         } else {
            Preconditions.checkArgument(!this.containsValue(value), "value already present: %s", value);
         }

         V oldValue = this.delegate.put(key, value);
         this.updateInverseMap(key, containedKey, oldValue, value);
         return oldValue;
      }
   }

   private void updateInverseMap(K key, boolean containedKey, V oldValue, V newValue) {
      if (containedKey) {
         this.removeFromInverseMap(oldValue);
      }

      this.inverse.delegate.put(newValue, key);
   }

   public V remove(Object key) {
      return this.containsKey(key) ? this.removeFromBothMaps(key) : null;
   }

   private V removeFromBothMaps(Object key) {
      V oldValue = this.delegate.remove(key);
      this.removeFromInverseMap(oldValue);
      return oldValue;
   }

   private void removeFromInverseMap(V oldValue) {
      this.inverse.delegate.remove(oldValue);
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
         this.put((K)entry.getKey(), (V)entry.getValue());
      }
   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      this.delegate.replaceAll(function);
      this.inverse.delegate.clear();
      Entry<K, V> broken = null;
      Iterator<Entry<K, V>> itr = this.delegate.entrySet().iterator();

      while (itr.hasNext()) {
         Entry<K, V> entry = itr.next();
         K k = entry.getKey();
         V v = entry.getValue();
         K conflict = this.inverse.delegate.putIfAbsent(v, k);
         if (conflict != null) {
            broken = entry;
            itr.remove();
         }
      }

      if (broken != null) {
         throw new IllegalArgumentException("value already present: " + broken.getValue());
      }
   }

   public void clear() {
      this.delegate.clear();
      this.inverse.delegate.clear();
   }

   public BiMap<V, K> inverse() {
      return this.inverse;
   }

   public Set<K> keySet() {
      Set<K> result = this.keySet;
      return result == null ? (this.keySet = new AbstractBiMap.KeySet()) : result;
   }

   public Set<V> values() {
      Set<V> result = this.valueSet;
      return result == null ? (this.valueSet = new AbstractBiMap.ValueSet()) : result;
   }

   public Set<Entry<K, V>> entrySet() {
      Set<Entry<K, V>> result = this.entrySet;
      return result == null ? (this.entrySet = new AbstractBiMap.EntrySet()) : result;
   }

   Iterator<Entry<K, V>> entrySetIterator() {
      final Iterator<Entry<K, V>> iterator = this.delegate.entrySet().iterator();
      return new Iterator<Entry<K, V>>() {
         Entry<K, V> entry;

         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         public Entry<K, V> next() {
            this.entry = iterator.next();
            return AbstractBiMap.this.new BiMapEntry(this.entry);
         }

         @Override
         public void remove() {
            if (this.entry == null) {
               throw new IllegalStateException("no calls to next() since the last call to remove()");
            } else {
               V value = this.entry.getValue();
               iterator.remove();
               AbstractBiMap.this.removeFromInverseMap(value);
               this.entry = null;
            }
         }
      };
   }

   class BiMapEntry extends ForwardingMapEntry<K, V> {
      private final Entry<K, V> delegate;

      BiMapEntry(Entry<K, V> delegate) {
         this.delegate = delegate;
      }

      protected Entry<K, V> delegate() {
         return this.delegate;
      }

      public V setValue(V value) {
         AbstractBiMap.this.checkValue(value);
         Preconditions.checkState(AbstractBiMap.this.entrySet().contains(this), "entry no longer in map");
         if (Objects.equal(value, this.getValue())) {
            return value;
         } else {
            Preconditions.checkArgument(!AbstractBiMap.this.containsValue(value), "value already present: %s", value);
            V oldValue = this.delegate.setValue(value);
            Preconditions.checkState(Objects.equal(value, AbstractBiMap.this.get(this.getKey())), "entry no longer in map");
            AbstractBiMap.this.updateInverseMap((K)this.getKey(), true, oldValue, value);
            return oldValue;
         }
      }
   }

   private class EntrySet extends ForwardingSet<Entry<K, V>> {
      final Set<Entry<K, V>> esDelegate = AbstractBiMap.this.delegate.entrySet();

      protected Set<Entry<K, V>> delegate() {
         return this.esDelegate;
      }

      public void clear() {
         AbstractBiMap.this.clear();
      }

      public boolean remove(Object object) {
         if (this.esDelegate.contains(object) && object instanceof Entry<?, ?> entry) {
            AbstractBiMap.this.inverse.delegate.remove(entry.getValue());
            this.esDelegate.remove(entry);
            return true;
         } else {
            return false;
         }
      }

      public Iterator<Entry<K, V>> iterator() {
         return AbstractBiMap.this.entrySetIterator();
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public <T> T[] toArray(T[] array) {
         return (T[])this.standardToArray(array);
      }

      public boolean contains(Object o) {
         return !(o instanceof Entry) ? false : this.delegate().contains((Entry)o);
      }

      public boolean containsAll(Collection<?> c) {
         return this.standardContainsAll(c);
      }

      public boolean removeAll(Collection<?> c) {
         return this.standardRemoveAll(c);
      }

      public boolean retainAll(Collection<?> c) {
         return this.standardRetainAll(c);
      }
   }

   static class Inverse<K, V> extends AbstractBiMap<K, V> {
      Inverse(Map<K, V> backward, AbstractBiMap<V, K> forward) {
         super(backward, forward);
      }

      @Override
      K checkKey(K key) {
         return this.inverse.checkValue(key);
      }

      @Override
      V checkValue(V value) {
         return this.inverse.checkKey(value);
      }

      private void writeObject(ObjectOutputStream stream) throws IOException {
         stream.defaultWriteObject();
         stream.writeObject(this.inverse());
      }

      private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
         stream.defaultReadObject();
         this.setInverse((AbstractBiMap<V, K>)stream.readObject());
      }

      Object readResolve() {
         return this.inverse().inverse();
      }
   }

   private class KeySet extends ForwardingSet<K> {
      protected Set<K> delegate() {
         return AbstractBiMap.this.delegate.keySet();
      }

      public void clear() {
         AbstractBiMap.this.clear();
      }

      public boolean remove(Object key) {
         if (!this.contains(key)) {
            return false;
         } else {
            AbstractBiMap.this.removeFromBothMaps(key);
            return true;
         }
      }

      public boolean removeAll(Collection<?> keysToRemove) {
         return this.standardRemoveAll(keysToRemove);
      }

      public boolean retainAll(Collection<?> keysToRetain) {
         return this.standardRetainAll(keysToRetain);
      }

      public Iterator<K> iterator() {
         return new AbstractBiMap<K, V>.TransformedIterator<Entry<K, V>, K>(AbstractBiMap.this.entrySet().iterator()) {
            K transform(Entry<K, V> entry) {
               return entry.getKey();
            }
         };
      }
   }

   abstract class TransformedIterator<F, T> implements Iterator<T> {
      final Iterator<? extends F> backingIterator;

      TransformedIterator(Iterator<? extends F> backingIterator) {
         this.backingIterator = (Iterator<? extends F>)Preconditions.checkNotNull(backingIterator);
      }

      abstract T transform(F var1);

      @Override
      public final boolean hasNext() {
         return this.backingIterator.hasNext();
      }

      @Override
      public final T next() {
         return this.transform((F)this.backingIterator.next());
      }

      @Override
      public final void remove() {
         this.backingIterator.remove();
      }
   }

   private class ValueSet extends ForwardingSet<V> {
      final Set<V> valuesDelegate = AbstractBiMap.this.inverse.keySet();

      protected Set<V> delegate() {
         return this.valuesDelegate;
      }

      public Iterator<V> iterator() {
         return new AbstractBiMap<K, V>.TransformedIterator<Entry<K, V>, V>(AbstractBiMap.this.entrySet().iterator()) {
            V transform(Entry<K, V> entry) {
               return entry.getValue();
            }
         };
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public <T> T[] toArray(T[] array) {
         return (T[])this.standardToArray(array);
      }

      public String toString() {
         return this.standardToString();
      }
   }
}
