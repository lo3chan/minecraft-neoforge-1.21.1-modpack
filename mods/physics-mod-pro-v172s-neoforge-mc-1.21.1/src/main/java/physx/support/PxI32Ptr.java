/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.support.PxI32ConstPtr;

public class PxI32Ptr
extends PxI32ConstPtr {
    public static final int SIZEOF = PxI32Ptr.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxI32Ptr() {
    }

    private static native int __sizeOf();

    public static PxI32Ptr wrapPointer(long address) {
        return address != 0L ? new PxI32Ptr(address) : null;
    }

    public static PxI32Ptr arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxI32Ptr.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxI32Ptr(long address) {
        super(address);
    }

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxI32Ptr._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

