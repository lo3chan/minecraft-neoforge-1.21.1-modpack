package org.jcodec.common;

import java.util.Arrays;

public class IntIntMap {
   private static final int GROW_BY = 128;
   private static final int MIN_VALUE = -2147483648;
   private int[] storage = createArray(128);
   private int _size;

   public IntIntMap() {
      Arrays.fill(this.storage, -2147483648);
   }

   public void put(int key, int val) {
      if (val == -2147483648) {
         throw new IllegalArgumentException("This implementation can not store -2147483648");
      } else {
         if (this.storage.length <= key) {
            int[] ns = createArray(key + 128);
            System.arraycopy(this.storage, 0, ns, 0, this.storage.length);
            Arrays.fill(ns, this.storage.length, ns.length, -2147483648);
            this.storage = ns;
         }

         if (this.storage[key] == -2147483648) {
            this._size++;
         }

         this.storage[key] = val;
      }
   }

   public int get(int key) {
      return key >= this.storage.length ? -2147483648 : this.storage[key];
   }

   public boolean contains(int key) {
      return key >= 0 && key < this.storage.length;
   }

   public int[] keys() {
      int[] result = new int[this._size];
      int i = 0;

      for (int r = 0; i < this.storage.length; i++) {
         if (this.storage[i] != -2147483648) {
            result[r++] = i;
         }
      }

      return result;
   }

   public void clear() {
      for (int i = 0; i < this.storage.length; i++) {
         this.storage[i] = -2147483648;
      }

      this._size = 0;
   }

   public int size() {
      return this._size;
   }

   public void remove(int key) {
      if (this.storage[key] != -2147483648) {
         this._size--;
      }

      this.storage[key] = -2147483648;
   }

   public int[] values() {
      int[] result = createArray(this._size);
      int i = 0;

      for (int r = 0; i < this.storage.length; i++) {
         if (this.storage[i] != -2147483648) {
            result[r++] = this.storage[i];
         }
      }

      return result;
   }

   private static int[] createArray(int size) {
      return new int[size];
   }
}
