package net.diebuddies.physics.snow.storage;

public class StorageVanilla implements StorageContainer {
   private static final int VANILLA_SIZE = 4096;
   public volatile StorageType data;
   public final int divider;
   public final int offset;

   public StorageVanilla(byte data, int divider) {
      this.divider = divider;
      this.offset = 32 - Integer.numberOfLeadingZeros(divider - 1);
      this.data = new EqualStorageTypeVanilla(data, 4096);
   }

   public StorageVanilla(byte[] data, int divider) {
      this.divider = divider;
      this.offset = 32 - Integer.numberOfLeadingZeros(divider - 1);
      this.data = new FullStorageTypeVanilla(data, 4096);
   }

   @Override
   public byte getData(int x, int y, int z) {
      return this.data.getData(x >> this.offset, y >> this.offset, z >> this.offset);
   }

   @Override
   public byte[] getArray() {
      return this.data.getArray();
   }

   @Override
   public void setData(int x, int y, int z, byte value) {
      this.data.setData(this, x >> this.offset, y >> this.offset, z >> this.offset, value);
   }

   @Override
   public boolean setAndCompareData(int x, int y, int z, byte value) {
      return this.data.setAndCompareData(this, x >> this.offset, y >> this.offset, z >> this.offset, value);
   }

   @Override
   public StorageType getStorageType() {
      return this.data;
   }

   @Override
   public int getSize() {
      return 4096;
   }

   @Override
   public void setData(StorageType data) {
      this.data = data;
   }
}
