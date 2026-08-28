/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.support.PxU8ConstPtr;

public class PxU8Ptr
extends PxU8ConstPtr {
    public static final int SIZEOF = PxU8Ptr.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxU8Ptr() {
    }

    private static native int __sizeOf();

    public static PxU8Ptr wrapPointer(long address) {
        return address != 0L ? new PxU8Ptr(address) : null;
    }

    public static PxU8Ptr arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxU8Ptr.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxU8Ptr(long address) {
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
        PxU8Ptr._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

