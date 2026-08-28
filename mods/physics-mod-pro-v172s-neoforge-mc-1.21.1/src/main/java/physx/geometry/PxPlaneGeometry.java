/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.geometry.PxGeometry;

public class PxPlaneGeometry
extends PxGeometry {
    public static final int SIZEOF = PxPlaneGeometry.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxPlaneGeometry wrapPointer(long address) {
        return address != 0L ? new PxPlaneGeometry(address) : null;
    }

    public static PxPlaneGeometry arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxPlaneGeometry.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxPlaneGeometry(long address) {
        super(address);
    }

    public static PxPlaneGeometry createAt(long address) {
        PxPlaneGeometry.__placement_new_PxPlaneGeometry(address);
        PxPlaneGeometry createdObj = PxPlaneGeometry.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxPlaneGeometry createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxPlaneGeometry.__placement_new_PxPlaneGeometry(address);
        PxPlaneGeometry createdObj = PxPlaneGeometry.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxPlaneGeometry(long var0);

    public PxPlaneGeometry() {
        this.address = PxPlaneGeometry._PxPlaneGeometry();
    }

    private static native long _PxPlaneGeometry();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxPlaneGeometry._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);
}

