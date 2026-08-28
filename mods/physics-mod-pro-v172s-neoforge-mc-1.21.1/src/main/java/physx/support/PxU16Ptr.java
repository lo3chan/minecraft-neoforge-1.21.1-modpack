/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.support.PxU16ConstPtr;

public class PxU16Ptr
extends PxU16ConstPtr {
    public static final int SIZEOF = PxU16Ptr.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxU16Ptr() {
    }

    private static native int __sizeOf();

    public static PxU16Ptr wrapPointer(long address) {
        return address != 0L ? new PxU16Ptr(address) : null;
    }

    public static PxU16Ptr arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxU16Ptr.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxU16Ptr(long address) {
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
        PxU16Ptr._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

