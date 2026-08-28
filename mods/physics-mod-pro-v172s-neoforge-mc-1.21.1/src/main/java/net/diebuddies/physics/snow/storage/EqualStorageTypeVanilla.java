/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.snow.storage;

import net.diebuddies.physics.snow.Index;
import net.diebuddies.physics.snow.storage.FullStorageTypeVanilla;
import net.diebuddies.physics.snow.storage.StorageContainer;
import net.diebuddies.physics.snow.storage.StorageType;

public class EqualStorageTypeVanilla
implements StorageType {
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
            for (int i = 0; i < data.length; ++i) {
                data[i] = this.value;
            }
            data[Index.vanillaChunkStorage((int)x, (int)y, (int)z)] = value;
            storage.setData(new FullStorageTypeVanilla(data, this.size));
        }
    }

    @Override
    public boolean setAndCompareData(StorageContainer storage, int x, int y, int z, byte value) {
        if (this.value != value) {
            byte[] data = new byte[this.size];
            for (int i = 0; i < data.length; ++i) {
                data[i] = this.value;
            }
            data[Index.vanillaChunkStorage((int)x, (int)y, (int)z)] = value;
            storage.setData(new FullStorageTypeVanilla(data, this.size));
            return true;
        }
        return false;
    }

    @Override
    public byte[] getArray() {
        return new byte[]{this.value};
    }
}

