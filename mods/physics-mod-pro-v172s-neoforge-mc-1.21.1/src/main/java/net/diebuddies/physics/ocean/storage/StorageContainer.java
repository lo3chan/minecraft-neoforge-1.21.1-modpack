/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.ocean.storage;

import net.diebuddies.physics.ocean.storage.StorageType;

public interface StorageContainer {
    public void setData(StorageType var1);

    public byte getData(int var1, int var2, int var3);

    public byte[] getArray();

    public void setData(int var1, int var2, int var3, byte var4);

    public boolean setAndCompareData(int var1, int var2, int var3, byte var4);

    public StorageType getStorageType();

    public int getSize();
}

