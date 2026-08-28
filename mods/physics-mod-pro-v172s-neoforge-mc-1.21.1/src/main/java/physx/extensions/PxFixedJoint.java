/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.extensions.PxJoint;

public class PxFixedJoint
extends PxJoint {
    public static final int SIZEOF = PxFixedJoint.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxFixedJoint() {
    }

    private static native int __sizeOf();

    public static PxFixedJoint wrapPointer(long address) {
        return address != 0L ? new PxFixedJoint(address) : null;
    }

    public static PxFixedJoint arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxFixedJoint.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxFixedJoint(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxFixedJoint._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

