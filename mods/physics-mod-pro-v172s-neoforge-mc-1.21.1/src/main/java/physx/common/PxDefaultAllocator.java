/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;

public class PxDefaultAllocator
extends NativeObject {
    public static final int SIZEOF = PxDefaultAllocator.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxDefaultAllocator wrapPointer(long address) {
        return address != 0L ? new PxDefaultAllocator(address) : null;
    }

    public static PxDefaultAllocator arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxDefaultAllocator.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxDefaultAllocator(long address) {
        super(address);
    }

    public PxDefaultAllocator() {
        this.address = PxDefaultAllocator._PxDefaultAllocator();
    }

    private static native long _PxDefaultAllocator();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxDefaultAllocator._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

