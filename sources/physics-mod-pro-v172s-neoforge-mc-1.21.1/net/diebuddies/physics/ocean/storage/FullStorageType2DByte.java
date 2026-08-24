package net.diebuddies.physics.ocean.storage;

import net.diebuddies.physics.ocean.Index;

public class FullStorageType2DByte {
   private byte[] storage;

   public FullStorageType2DByte(int size) {
      this.storage = new byte[size];
   }

   public FullStorageType2DByte(byte[] data) {
      this.storage = data;
   }

   public byte getData(int x, int z) {
      return this.storage[Index.chunkStorage(x, z)];
   }

   public void setData(int x, int z, byte value) {
      this.storage[Index.chunkStorage(x, z)] = value;
   }

   public boolean setAndCompareData(int x, int z, byte value) {
      int index = Index.chunkStorage(x, z);
      if (this.storage[index] == value) {
         return false;
      } else {
         this.storage[index] = value;
         return true;
      }
   }

   public byte[] getArray() {
      return this.storage;
   }
}
