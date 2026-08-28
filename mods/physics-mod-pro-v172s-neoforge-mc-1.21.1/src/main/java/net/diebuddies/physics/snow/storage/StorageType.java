/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.snow.storage;

import net.diebuddies.physics.snow.storage.StorageContainer;

public interface StorageType {
    public byte getData(int var1, int var2, int var3);

    public void setData(StorageContainer var1, int var2, int var3, int var4, byte var5);

    public boolean setAndCompareData(StorageContainer var1, int var2, int var3, int var4, byte var5);

    public byte[] getArray();
}

