/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;

public class PxRaycastCallback
extends NativeObject {
    public static final int SIZEOF = PxRaycastCallback.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxRaycastCallback() {
    }

    private static native int __sizeOf();

    public static PxRaycastCallback wrapPointer(long address) {
        return address != 0L ? new PxRaycastCallback(address) : null;
    }

    public static PxRaycastCallback arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRaycastCallback.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRaycastCallback(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxRaycastCallback._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean hasAnyHits() {
        this.checkNotNull();
        return PxRaycastCallback._hasAnyHits(this.address);
    }

    private static native boolean _hasAnyHits(long var0);
}

