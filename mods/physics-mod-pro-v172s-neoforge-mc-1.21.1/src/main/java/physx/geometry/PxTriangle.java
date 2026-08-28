/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxTriangle
extends NativeObject {
    public static final int SIZEOF = PxTriangle.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxTriangle wrapPointer(long address) {
        return address != 0L ? new PxTriangle(address) : null;
    }

    public static PxTriangle arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxTriangle.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxTriangle(long address) {
        super(address);
    }

    public PxTriangle() {
        this.address = PxTriangle._PxTriangle();
    }

    private static native long _PxTriangle();

    public PxTriangle(PxVec3 p0, PxVec3 p1, PxVec3 p2) {
        this.address = PxTriangle._PxTriangle(p0.getAddress(), p1.getAddress(), p2.getAddress());
    }

    private static native long _PxTriangle(long var0, long var2, long var4);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxTriangle._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public void normal(PxVec3 normal) {
        this.checkNotNull();
        PxTriangle._normal(this.address, normal.getAddress());
    }

    private static native void _normal(long var0, long var2);

    public void denormalizedNormal(PxVec3 normal) {
        this.checkNotNull();
        PxTriangle._denormalizedNormal(this.address, normal.getAddress());
    }

    private static native void _denormalizedNormal(long var0, long var2);

    public float area() {
        this.checkNotNull();
        return PxTriangle._area(this.address);
    }

    private static native float _area(long var0);

    public PxVec3 pointFromUV(float u, float v) {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxTriangle._pointFromUV(this.address, u, v));
    }

    private static native long _pointFromUV(long var0, float var2, float var3);
}

