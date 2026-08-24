package net.mehvahdjukaar.moonlight.api.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class FrequencyOrderedCollection<T> implements Collection<T> {
   private final Map<T, Integer> frequencies = new HashMap<>();
   private List<Entry<T, Integer>> sortedEntries = new ArrayList<>();

   @Override
   public boolean add(T obj) {
      return this.add(obj, 1);
   }

   public boolean add(T obj, int count) {
      if (count <= 0) {
         return false;
      } else {
         boolean wasAdded = this.frequencies.containsKey(obj);
         this.frequencies.merge(obj, count, Integer::sum);
         if (wasAdded) {
            this.updateSortedEntries();
         } else {
            this.sortedEntries = new ArrayList<>(this.frequencies.entrySet());
            this.sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
         }

         return true;
      }
   }

   public boolean remove(T obj, int count) {
      if (count > 0 && this.frequencies.containsKey(obj)) {
         this.frequencies.merge(obj, -count, (oldCount, delta) -> {
            int newCount = oldCount + delta;
            return newCount > 0 ? newCount : null;
         });
         this.updateSortedEntries();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean remove(Object obj) {
      if (this.frequencies.remove(obj) != null) {
         this.updateSortedEntries();
         return true;
      } else {
         return false;
      }
   }

   public boolean removeAllOccurrences(T obj) {
      return this.remove(obj);
   }

   private void updateSortedEntries() {
      this.sortedEntries = new ArrayList<>(this.frequencies.entrySet());
      this.sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
   }

   public T getFirst() {
      return (T)(!this.sortedEntries.isEmpty() ? ((Entry)this.sortedEntries.getFirst()).getKey() : null);
   }

   public T getLast() {
      return (T)(!this.sortedEntries.isEmpty() ? ((Entry)this.sortedEntries.getLast()).getKey() : null);
   }

   @Override
   public Iterator<T> iterator() {
      return this.sortedEntries.stream().map(Entry::getKey).iterator();
   }

   @Override
   public int size() {
      return this.frequencies.size();
   }

   @Override
   public boolean isEmpty() {
      return this.frequencies.isEmpty();
   }

   @Override
   public boolean contains(Object obj) {
      return this.frequencies.containsKey(obj);
   }

   @Override
   public Object[] toArray() {
      return this.sortedEntries.stream().map(Entry::getKey).toArray();
   }

   @Override
   public <U> U[] toArray(U[] a) {
      return (U[])this.sortedEntries.stream().map(Entry::getKey).toArray(size -> a);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return this.frequencies.keySet().containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      boolean changed = false;

      for (T item : c) {
         changed |= this.add(item);
      }

      return changed;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      boolean changed = false;

      for (Object item : c) {
         changed |= this.remove(item);
      }

      return changed;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      boolean changed = false;
      Iterator<T> iterator = this.iterator();
      Set<T> toRemove = new HashSet<>();

      while (iterator.hasNext()) {
         T item = iterator.next();
         if (!c.contains(item)) {
            toRemove.add(item);
         }
      }

      for (T item : toRemove) {
         this.frequencies.remove(item);
         changed = true;
      }

      if (changed) {
         this.updateSortedEntries();
      }

      return changed;
   }

   @Override
   public void clear() {
      this.frequencies.clear();
      this.sortedEntries.clear();
   }
}
