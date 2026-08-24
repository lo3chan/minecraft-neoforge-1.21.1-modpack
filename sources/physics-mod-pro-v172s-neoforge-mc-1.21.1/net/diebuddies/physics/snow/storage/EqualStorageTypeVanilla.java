package net.diebuddies.physics.snow.storage;

import net.diebuddies.physics.snow.Index;

public class EqualStorageTypeVanilla implements StorageType {
   public volatile byte value;
   public volatile int size;

   public EqualStorageTypeVanilla(byte value, int size) {
      this.value = value;
      this.size = size;
   }

   @Override
   public byte getData(int x, int y, int z) {
      return this.value;
   }

   @Override
   public void setData(StorageContainer storage, int x, int y, int z, byte value) {
      if (this.value != value) {
         byte[] data = new byte[this.size];

         for (int i = 0; i < data.length; i++) {
            data[i] = this.value;
         }

         data[Index.vanillaChunkStorage(x, y, z)] = value;
         storage.setData(new FullStorageTypeVanilla(data, this.size));
      }
   }

   @Override
   public boolean setAndCompareData(StorageContainer storage, int x, int y, int z, byte value) {
      if (this.value == value) {
         return false;
      } else {
         byte[] data = new byte[this.size];

         for (int i = 0; i < data.length; i++) {
            data[i] = this.value;
         }

         data[Index.vanillaChunkStorage(x, y, z)] = value;
         storage.setData(new FullStorageTypeVanilla(data, this.size));
         return true;
      }
   }

   @Override
   public byte[] getArray() {
      return new byte[]{this.value};
   }
}
