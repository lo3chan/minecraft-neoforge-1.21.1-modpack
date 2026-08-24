package net.diebuddies.physics.ocean.storage;

public interface StorageContainer {
   void setData(StorageType var1);

   byte getData(int var1, int var2, int var3);

   byte[] getArray();

   void setData(int var1, int var2, int var3, byte var4);

   boolean setAndCompareData(int var1, int var2, int var3, byte var4);

   StorageType getStorageType();

   int getSize();
}
