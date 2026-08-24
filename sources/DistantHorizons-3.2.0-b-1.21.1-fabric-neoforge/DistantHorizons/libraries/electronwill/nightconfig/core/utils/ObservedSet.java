package DistantHorizons.libraries.electronwill.nightconfig.core.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public final class ObservedSet<K> extends AbstractObserved implements Set<K> {
   private final Set<K> set;

   public ObservedSet(Set<K> set, Runnable callback) {
      super(callback);
      this.set = set;
   }

   @Override
   public int size() {
      return this.set.size();
   }

   @Override
   public boolean isEmpty() {
      return this.set.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.set.contains(o);
   }

   @Override
   public Iterator<K> iterator() {
      return this.set.iterator();
   }

   @Override
   public Object[] toArray() {
      return this.set.toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return (T[])this.set.toArray(a);
   }

   @Override
   public boolean add(K k) {
      boolean result = this.set.add(k);
      this.callback.run();
      return result;
   }

   @Override
   public boolean remove(Object o) {
      boolean result = this.set.remove(o);
      this.callback.run();
      return result;
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return this.set.containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends K> c) {
      boolean result = this.set.addAll(c);
      this.callback.run();
      return result;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      boolean result = this.set.retainAll(c);
      this.callback.run();
      return result;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      boolean result = this.set.removeAll(c);
      this.callback.run();
      return result;
   }

   @Override
   public void clear() {
      this.set.clear();
      this.callback.run();
   }

   @Override
   public boolean removeIf(Predicate<? super K> filter) {
      boolean removed = this.set.removeIf(filter);
      if (removed) {
         this.callback.run();
      }

      return removed;
   }

   @Override
   public boolean equals(Object obj) {
      return this.set.equals(obj);
   }

   @Override
   public int hashCode() {
      return this.set.hashCode();
   }
}
