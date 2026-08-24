package net.diebuddies.dualcontouring;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class SimplePoolLongObjectHashMap<T> {
   private Long2ObjectOpenHashMap<T>[] objects;
   private int index;

   public SimplePoolLongObjectHashMap(int size) {
      this.objects = new Long2ObjectOpenHashMap[size];

      for (int i = 0; i < size; i++) {
         this.objects[i] = new Long2ObjectOpenHashMap();
      }
   }

   public Long2ObjectOpenHashMap<T> get() {
      if (this.index < this.objects.length) {
         Long2ObjectOpenHashMap<T> obj = this.objects[this.index++];
         obj.clear();
         return obj;
      } else {
         this.resize();
         Long2ObjectOpenHashMap<T> obj = this.objects[this.index++];
         obj.clear();
         return obj;
      }
   }

   private void resize() {
      Long2ObjectOpenHashMap<T>[] newArray = new Long2ObjectOpenHashMap[this.objects.length * 2];
      System.arraycopy(this.objects, 0, newArray, 0, this.objects.length);

      for (int i = this.objects.length; i < newArray.length; i++) {
         newArray[i] = new Long2ObjectOpenHashMap();
      }

      this.objects = newArray;
   }

   public void reset() {
      this.index = 0;
   }
}
