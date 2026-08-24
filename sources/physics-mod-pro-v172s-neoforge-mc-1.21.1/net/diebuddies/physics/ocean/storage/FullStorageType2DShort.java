package net.diebuddies.physics.ocean.storage;

import net.diebuddies.physics.ocean.Index;

public class FullStorageType2DShort {
   private short[] storage;

   public FullStorageType2DShort(int size) {
      this.storage = new short[size];
   }

   public short getData(int x, int z) {
      return this.storage[Index.chunkStorage(x, z)];
   }

   public void setData(int x, int z, short value) {
      this.storage[Index.chunkStorage(x, z)] = value;
   }

   public boolean setAndCompareData(int x, int z, short value) {
      int index = Index.chunkStorage(x, z);
      if (this.storage[index] == value) {
         return false;
      } else {
         this.storage[index] = value;
         return true;
      }
   }

   public short[] getArray() {
      return this.storage;
   }
}
