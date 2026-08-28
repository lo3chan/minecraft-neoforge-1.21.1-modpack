/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.snow.storage;

import java.util.Arrays;
import net.diebuddies.physics.snow.Index;
import net.diebuddies.physics.snow.storage.StorageContainer;
import net.diebuddies.physics.snow.storage.StorageType;

public class FullStorageTypeVanilla
implements StorageType {
    public byte[] storage;
    public int size;

    public FullStorageTypeVanilla(byte[] storage, int size) {
        this.storage = storage;
        this.size = size;
    }

    @Override
    public byte getData(int x, int y, int z) {
        return this.storage[Index.vanillaChunkStorage(x, y, z)];
    }

    @Override
    public void setData(StorageContainer storage, int x, int y, int z, byte value) {
        this.storage[Index.vanillaChunkStorage((int)x, (int)y, (int)z)] = value;
    }

    @Override
    public boolean setAndCompareData(StorageContainer storage, int x, int y, int z, byte value) {
        int index = Index.vanillaChunkStorage(x, y, z);
        if (this.storage[index] == value) {
            return false;
        }
        this.storage[index] = value;
        return true;
    }

    public StorageType copy() {
        byte[] copyStorage = Arrays.copyOf(this.storage, this.storage.length);
        return new FullStorageTypeVanilla(copyStorage, this.size);
    }

    @Override
    public byte[] getArray() {
        return this.storage;
    }
}

