package net.mehvahdjukaar.moonlight.api.misc;

import java.util.AbstractList;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class RollingBuffer<T> extends AbstractList<T> implements RandomAccess {
   private final Object[] buffer;
   private int start = 0;
   private int size = 0;

   public RollingBuffer(int capacity) {
      if (capacity <= 0) {
         throw new IllegalArgumentException("Capacity must be > 0");
      } else {
         this.buffer = new Object[capacity];
      }
   }

   private int cap() {
      return this.buffer.length;
   }

   private int toPhysical(int logicalIndex) {
      return (this.start + logicalIndex) % this.cap();
   }

   private T elementAtPhysical(int physical) {
      return (T)this.buffer[physical];
   }

   private void setPhysical(int physical, T e) {
      this.buffer[physical] = e;
   }

   public void push(T element) {
      this.addLast(element);
   }

   public int capacity() {
      return this.cap();
   }

   public boolean isFull() {
      return this.size == this.cap();
   }

   public void fillAll(T element) {
      for (int i = 0; i < this.cap(); i++) {
         this.addLast(element);
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public T get(int index) {
      if (index >= 0 && index < this.size) {
         return this.elementAtPhysical(this.toPhysical(index));
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", size: " + this.size);
      }
   }

   @Override
   public T set(int index, T element) {
      if (index >= 0 && index < this.size) {
         int p = this.toPhysical(index);
         T old = (T)this.buffer[p];
         this.buffer[p] = element;
         return old;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", size: " + this.size);
      }
   }

   @Override
   public boolean add(T element) {
      this.addLast(element);
      return true;
   }

   @Override
   public void add(int index, T element) {
      if (index == 0) {
         this.addFirst(element);
      } else {
         if (index != this.size) {
            throw new UnsupportedOperationException("Middle insert not supported in RollingBuffer");
         }

         this.addLast(element);
      }
   }

   @Override
   public T remove(int index) {
      if (index >= 0 && index < this.size) {
         this.modCount++;
         int removePhys = this.toPhysical(index);
         T removed = (T)this.buffer[removePhys];
         if (index < this.size / 2) {
            for (int i = index; i > 0; i--) {
               int from = this.toPhysical(i - 1);
               int to = this.toPhysical(i);
               this.buffer[to] = this.buffer[from];
            }

            this.buffer[this.start] = null;
            this.start = (this.start + 1) % this.cap();
         } else {
            for (int i = index; i < this.size - 1; i++) {
               int from = this.toPhysical(i + 1);
               int to = this.toPhysical(i);
               this.buffer[to] = this.buffer[from];
            }

            int tailPhys = this.toPhysical(this.size - 1);
            this.buffer[tailPhys] = null;
         }

         this.size--;
         return removed;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", size: " + this.size);
      }
   }

   @Override
   public void clear() {
      this.modCount++;

      for (int i = 0; i < this.size; i++) {
         this.buffer[this.toPhysical(i)] = null;
      }

      this.start = 0;
      this.size = 0;
   }

   public void addFirst(T e) {
      this.modCount++;
      if (this.size < this.cap()) {
         this.start = (this.start - 1 + this.cap()) % this.cap();
         this.setPhysical(this.start, e);
         this.size++;
      } else {
         this.start = (this.start - 1 + this.cap()) % this.cap();
         this.setPhysical(this.start, e);
      }
   }

   public void addLast(T e) {
      this.modCount++;
      if (this.size < this.cap()) {
         int tailPhys = this.toPhysical(this.size);
         this.setPhysical(tailPhys, e);
         this.size++;
      } else {
         this.setPhysical(this.start, e);
         this.start = (this.start + 1) % this.cap();
      }
   }

   public T getFirst() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.elementAtPhysical(this.start);
      }
   }

   public T getLast() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         int tailPhys = this.toPhysical(this.size - 1);
         return this.elementAtPhysical(tailPhys);
      }
   }

   public T removeFirst() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         this.modCount++;
         T v = (T)this.buffer[this.start];
         this.buffer[this.start] = null;
         this.start = (this.start + 1) % this.cap();
         this.size--;
         return v;
      }
   }

   public T removeLast() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         this.modCount++;
         int tailPhys = this.toPhysical(this.size - 1);
         T v = (T)this.buffer[tailPhys];
         this.buffer[tailPhys] = null;
         this.size--;
         return v;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("RollingBuffer{");

      for (int i = 0; i < this.size; i++) {
         sb.append(this.get(i));
         if (i < this.size - 1) {
            sb.append(", ");
         }
      }

      sb.append('}');
      return sb.toString();
   }
}
