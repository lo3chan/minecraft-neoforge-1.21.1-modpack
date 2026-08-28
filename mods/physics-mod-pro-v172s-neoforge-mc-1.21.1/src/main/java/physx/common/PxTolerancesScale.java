/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;

public class PxTolerancesScale
extends NativeObject {
    public static final int SIZEOF = PxTolerancesScale.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxTolerancesScale wrapPointer(long address) {
        return address != 0L ? new PxTolerancesScale(address) : null;
    }

    public static PxTolerancesScale arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxTolerancesScale.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxTolerancesScale(long address) {
        super(address);
    }

    public PxTolerancesScale() {
        this.address = PxTolerancesScale._PxTolerancesScale();
    }

    private static native long _PxTolerancesScale();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxTolerancesScale._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

