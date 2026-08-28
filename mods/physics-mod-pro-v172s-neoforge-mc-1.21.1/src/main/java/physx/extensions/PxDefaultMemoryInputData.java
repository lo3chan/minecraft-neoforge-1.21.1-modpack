/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.common.PxInputData;
import physx.support.PxU8Ptr;

public class PxDefaultMemoryInputData
extends PxInputData {
    public static final int SIZEOF = PxDefaultMemoryInputData.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxDefaultMemoryInputData() {
    }

    private static native int __sizeOf();

    public static PxDefaultMemoryInputData wrapPointer(long address) {
        return address != 0L ? new PxDefaultMemoryInputData(address) : null;
    }

    public static PxDefaultMemoryInputData arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxDefaultMemoryInputData.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxDefaultMemoryInputData(long address) {
        super(address);
    }

    public PxDefaultMemoryInputData(PxU8Ptr data, int length) {
        this.address = PxDefaultMemoryInputData._PxDefaultMemoryInputData(data.getAddress(), length);
    }

    private static native long _PxDefaultMemoryInputData(long var0, int var2);

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxDefaultMemoryInputData._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int read(NativeObject dest, int count) {
        this.checkNotNull();
        return PxDefaultMemoryInputData._read(this.address, dest.getAddress(), count);
    }

    private static native int _read(long var0, long var2, int var4);

    public int getLength() {
        this.checkNotNull();
        return PxDefaultMemoryInputData._getLength(this.address);
    }

    private static native int _getLength(long var0);

    public void seek(int pos) {
        this.checkNotNull();
        PxDefaultMemoryInputData._seek(this.address, pos);
    }

    private static native void _seek(long var0, int var2);

    public int tell() {
        this.checkNotNull();
        return PxDefaultMemoryInputData._tell(this.address);
    }

    private static native int _tell(long var0);
}

