package net.diebuddies.physics.ocean.storage;

public class StorageSimple implements StorageContainer {
   public volatile StorageType data;
   public final int size;

   public StorageSimple(byte data, int size) {
      this.size = size;
      this.data = new EqualStorageType(data, size);
   }

   public StorageSimple(byte[] data, int size) {
      this.size = size;
      this.data = this.createStorage(data, size);
   }

   @Override
   public byte getData(int x, int y, int z) {
      return this.data.getData(x, y, z);
   }

   @Override
   public byte[] getArray() {
      return this.data.getArray();
   }

   @Override
   public void setData(int x, int y, int z, byte value) {
      this.data.setData(this, x, y, z, value);
   }

   @Override
   public boolean setAndCompareData(int x, int y, int z, byte value) {
      return this.data.setAndCompareData(this, x, y, z, value);
   }

   private StorageType createStorage(byte[] data, int size) {
      return new FullStorageType(data, size);
   }

   @Override
   public StorageType getStorageType() {
      return this.data;
   }

   @Override
   public int getSize() {
      return this.size;
   }

   @Override
   public void setData(StorageType data) {
      this.data = data;
   }
}
