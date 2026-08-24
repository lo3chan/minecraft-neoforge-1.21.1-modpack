package DistantHorizons.libraries.electronwill.nightconfig.core.utils;

import java.util.Map.Entry;

public final class ObservedEntry<K, V> extends AbstractObserved implements Entry<K, V> {
   final Entry<K, V> entry;

   protected ObservedEntry(Entry<K, V> entry, Runnable callback) {
      super(callback);
      this.entry = entry;
   }

   @Override
   public K getKey() {
      return this.entry.getKey();
   }

   @Override
   public V getValue() {
      return this.entry.getValue();
   }

   @Override
   public V setValue(V value) {
      V result = this.entry.setValue(value);
      this.callback.run();
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      return this.entry.equals(obj);
   }

   @Override
   public int hashCode() {
      return this.entry.hashCode();
   }
}
