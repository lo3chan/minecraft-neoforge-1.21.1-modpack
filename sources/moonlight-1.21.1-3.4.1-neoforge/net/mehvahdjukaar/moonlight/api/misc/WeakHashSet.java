package net.mehvahdjukaar.moonlight.api.misc;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashSet<T> extends AbstractSet<T> {
   private final Map<T, Object> map = new WeakHashMap<>();

   @Override
   public boolean contains(Object obj) {
      return this.map.containsKey(obj);
   }

   @Override
   public boolean add(T obj) {
      return this.map.put(obj, Boolean.TRUE) == null;
   }

   @Override
   public boolean remove(Object obj) {
      return this.map.remove(obj) != null;
   }

   @Override
   public Iterator<T> iterator() {
      return this.map.keySet().iterator();
   }

   @Override
   public int size() {
      return this.map.size();
   }
}
