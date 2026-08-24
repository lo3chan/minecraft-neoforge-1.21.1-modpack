package net.diebuddies.physics.snow.storage;

import java.util.Arrays;
import net.diebuddies.physics.snow.Index;

public class FullStorageType implements StorageType {
   public byte[] storage;
   public int size;

   public FullStorageType(byte[] storage, int size) {
      this.storage = storage;
      this.size = size;
   }

   @Override
   public byte getData(int x, int y, int z) {
      return this.storage[Index.chunkStorage(x, y, z)];
   }

   @Override
   public void setData(StorageContainer storage, int x, int y, int z, byte value) {
      this.storage[Index.chunkStorage(x, y, z)] = value;
   }

   @Override
   public boolean setAndCompareData(StorageContainer storage, int x, int y, int z, byte value) {
      int index = Index.chunkStorage(x, y, z);
      if (this.storage[index] == value) {
         return false;
      } else {
         this.storage[index] = value;
         return true;
      }
   }

   public StorageType copy() {
      byte[] copyStorage = Arrays.copyOf(this.storage, this.storage.length);
      return new FullStorageType(copyStorage, this.size);
   }

   @Override
   public byte[] getArray() {
      return this.storage;
   }
}
