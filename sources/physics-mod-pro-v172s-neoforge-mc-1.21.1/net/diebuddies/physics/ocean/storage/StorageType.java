package net.diebuddies.physics.ocean.storage;

public interface StorageType {
   byte getData(int var1, int var2, int var3);

   void setData(StorageContainer var1, int var2, int var3, int var4, byte var5);

   boolean setAndCompareData(StorageContainer var1, int var2, int var3, int var4, byte var5);

   byte[] getArray();
}
