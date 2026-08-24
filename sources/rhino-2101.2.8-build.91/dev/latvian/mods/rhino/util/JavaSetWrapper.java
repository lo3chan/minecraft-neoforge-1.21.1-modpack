package dev.latvian.mods.rhino.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class JavaSetWrapper<T> extends AbstractList<T> {
   public final Set<T> set;

   public JavaSetWrapper(Set<T> set) {
      this.set = set;
   }

   @Override
   public T get(int index) {
      if (index >= 0 && index < this.size()) {
         if (index == 0) {
            return this.set.iterator().next();
         } else {
            for (T element : this.set) {
               if (index == 0) {
                  return element;
               }

               index--;
            }

            throw new IndexOutOfBoundsException(index);
         }
      } else {
         throw new IndexOutOfBoundsException(index);
      }
   }

   @Override
   public int size() {
      return this.set.size();
   }

   @Override
   public boolean add(T t) {
      return this.set.add(t);
   }

   @Override
   public void add(int index, T element) {
      this.set.add(element);
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      return this.set.addAll(c);
   }

   @Override
   public boolean addAll(int index, Collection<? extends T> c) {
      return this.set.addAll(c);
   }

   @Override
   public T set(int index, T element) {
      if (this.set.remove(element)) {
         this.set.add(element);
         return null;
      } else {
         this.set.add(element);
         return element;
      }
   }

   @Override
   public T remove(int index) {
      if (index >= 0 && index < this.size()) {
         for (Iterator<T> iterator = this.set.iterator(); iterator.hasNext(); index--) {
            T element = iterator.next();
            if (index == 0) {
               iterator.remove();
               return element;
            }
         }

         throw new IndexOutOfBoundsException(index);
      } else {
         throw new IndexOutOfBoundsException(index);
      }
   }

   @Override
   public boolean remove(Object o) {
      return this.set.remove(o);
   }

   @Override
   public void clear() {
      this.set.clear();
   }
}
