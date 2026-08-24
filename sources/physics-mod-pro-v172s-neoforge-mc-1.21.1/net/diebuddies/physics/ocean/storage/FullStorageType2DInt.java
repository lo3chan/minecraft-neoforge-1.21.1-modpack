package net.diebuddies.physics.ocean.storage;

import net.diebuddies.physics.ocean.Index;

public class FullStorageType2DInt {
   private int[] storage;

   public FullStorageType2DInt(int[] data) {
      this.storage = data;
   }

   public FullStorageType2DInt(int size) {
      this.storage = new int[size];
   }

   public int getData(int x, int z) {
      return this.storage[Index.chunkStorage(x, z)];
   }

   public void setData(int x, int z, int value) {
      this.storage[Index.chunkStorage(x, z)] = value;
   }

   public boolean setAndCompareData(int x, int z, int value) {
      int index = Index.chunkStorage(x, z);
      if (this.storage[index] == value) {
         return false;
      } else {
         this.storage[index] = value;
         return true;
      }
   }

   public int[] getArray() {
      return this.storage;
   }
}
