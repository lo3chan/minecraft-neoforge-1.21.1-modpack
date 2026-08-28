/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.common.PxVec3;
import physx.geometry.PxGeometry;

public class PxBoxGeometry
extends PxGeometry {
    public static final int SIZEOF = PxBoxGeometry.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxBoxGeometry() {
    }

    private static native int __sizeOf();

    public static PxBoxGeometry wrapPointer(long address) {
        return address != 0L ? new PxBoxGeometry(address) : null;
    }

    public static PxBoxGeometry arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxBoxGeometry.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxBoxGeometry(long address) {
        super(address);
    }

    public static PxBoxGeometry createAt(long address, float hx, float hy, float hz) {
        PxBoxGeometry.__placement_new_PxBoxGeometry(address, hx, hy, hz);
        PxBoxGeometry createdObj = PxBoxGeometry.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxBoxGeometry createAt(T allocator, NativeObject.Allocator<T> allocate, float hx, float hy, float hz) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxBoxGeometry.__placement_new_PxBoxGeometry(address, hx, hy, hz);
        PxBoxGeometry createdObj = PxBoxGeometry.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxBoxGeometry(long var0, float var2, float var3, float var4);

    public PxBoxGeometry(float hx, float hy, float hz) {
        this.address = PxBoxGeometry._PxBoxGeometry(hx, hy, hz);
    }

    private static native long _PxBoxGeometry(float var0, float var1, float var2);

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxBoxGeometry._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVec3 getHalfExtents() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxBoxGeometry._getHalfExtents(this.address));
    }

    private static native long _getHalfExtents(long var0);

    public void setHalfExtents(PxVec3 value) {
        this.checkNotNull();
        PxBoxGeometry._setHalfExtents(this.address, value.getAddress());
    }

    private static native void _setHalfExtents(long var0, long var2);
}

