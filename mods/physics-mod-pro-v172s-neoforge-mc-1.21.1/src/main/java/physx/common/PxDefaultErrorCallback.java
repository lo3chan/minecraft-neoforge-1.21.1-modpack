/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.common.PxErrorCallback;

public class PxDefaultErrorCallback
extends PxErrorCallback {
    public static final int SIZEOF = PxDefaultErrorCallback.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxDefaultErrorCallback wrapPointer(long address) {
        return address != 0L ? new PxDefaultErrorCallback(address) : null;
    }

    public static PxDefaultErrorCallback arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxDefaultErrorCallback.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxDefaultErrorCallback(long address) {
        super(address);
    }

    public PxDefaultErrorCallback() {
        this.address = PxDefaultErrorCallback._PxDefaultErrorCallback();
    }

    private static native long _PxDefaultErrorCallback();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxDefaultErrorCallback._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

