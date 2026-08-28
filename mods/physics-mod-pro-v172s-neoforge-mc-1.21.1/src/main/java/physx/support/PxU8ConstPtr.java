/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;

public class PxU8ConstPtr
extends NativeObject {
    public static final int SIZEOF = PxU8ConstPtr.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxU8ConstPtr() {
    }

    private static native int __sizeOf();

    public static PxU8ConstPtr wrapPointer(long address) {
        return address != 0L ? new PxU8ConstPtr(address) : null;
    }

    public static PxU8ConstPtr arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxU8ConstPtr.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxU8ConstPtr(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxU8ConstPtr._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

