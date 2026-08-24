package net.diebuddies.physics.ocean.storage;

import net.diebuddies.physics.ocean.Index;

public class FullStorageType2DBit {
   private byte[] storage;

   public FullStorageType2DBit(int size) {
      this.storage = new byte[size >> 3];
   }

   public int getData(int x, int z) {
      int position = Index.chunkStorage(x, z);
      int index = position >> 3;
      int bit = position & 7;
      return this.storage[index] & 1 << bit;
   }

   public void setData(int x, int z) {
      int position = Index.chunkStorage(x, z);
      int index = position >> 3;
      int bit = position & 7;
      this.storage[index] = (byte)(this.storage[index] | 1 << bit);
   }

   public void unsetData(int x, int z) {
      int position = Index.chunkStorage(x, z);
      int index = position >> 3;
      int bit = position & 7;
      this.storage[index] = (byte)(this.storage[index] & ~(1 << bit));
   }

   public boolean setAndCompareData(int x, int z) {
      int position = Index.chunkStorage(x, z);
      int index = position >> 3;
      int bit = position & 7;
      int stored = this.storage[index] & 1 << bit;
      if (stored > 0) {
         return false;
      } else {
         this.storage[index] = (byte)(this.storage[index] | 1 << bit);
         return true;
      }
   }

   public boolean unsetAndCompareData(int x, int z) {
      int position = Index.chunkStorage(x, z);
      int index = position >> 3;
      int bit = position & 7;
      int stored = this.storage[index] & 1 << bit;
      if (stored == 0) {
         return false;
      } else {
         this.storage[index] = (byte)(this.storage[index] & ~(1 << bit));
         return true;
      }
   }

   public byte[] getArray() {
      return this.storage;
   }
}
