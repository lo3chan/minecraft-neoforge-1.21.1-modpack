/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.geometry.PxBoxGeometry;
import physx.geometry.PxCapsuleGeometry;
import physx.geometry.PxConvexMeshGeometry;
import physx.geometry.PxGeometry;
import physx.geometry.PxGeometryTypeEnum;
import physx.geometry.PxHeightFieldGeometry;
import physx.geometry.PxPlaneGeometry;
import physx.geometry.PxSphereGeometry;
import physx.geometry.PxTriangleMeshGeometry;

public class PxGeometryHolder
extends NativeObject {
    public static final int SIZEOF = PxGeometryHolder.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxGeometryHolder wrapPointer(long address) {
        return address != 0L ? new PxGeometryHolder(address) : null;
    }

    public static PxGeometryHolder arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxGeometryHolder.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxGeometryHolder(long address) {
        super(address);
    }

    public PxGeometryHolder() {
        this.address = PxGeometryHolder._PxGeometryHolder();
    }

    private static native long _PxGeometryHolder();

    public PxGeometryHolder(PxGeometry geometry) {
        this.address = PxGeometryHolder._PxGeometryHolder(geometry.getAddress());
    }

    private static native long _PxGeometryHolder(long var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxGeometryHolder._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxGeometryTypeEnum getType() {
        this.checkNotNull();
        return PxGeometryTypeEnum.forValue(PxGeometryHolder._getType(this.address));
    }

    private static native int _getType(long var0);

    public PxSphereGeometry sphere() {
        this.checkNotNull();
        return PxSphereGeometry.wrapPointer(PxGeometryHolder._sphere(this.address));
    }

    private static native long _sphere(long var0);

    public PxPlaneGeometry plane() {
        this.checkNotNull();
        return PxPlaneGeometry.wrapPointer(PxGeometryHolder._plane(this.address));
    }

    private static native long _plane(long var0);

    public PxCapsuleGeometry capsule() {
        this.checkNotNull();
        return PxCapsuleGeometry.wrapPointer(PxGeometryHolder._capsule(this.address));
    }

    private static native long _capsule(long var0);

    public PxBoxGeometry box() {
        this.checkNotNull();
        return PxBoxGeometry.wrapPointer(PxGeometryHolder._box(this.address));
    }

    private static native long _box(long var0);

    public PxConvexMeshGeometry convexMesh() {
        this.checkNotNull();
        return PxConvexMeshGeometry.wrapPointer(PxGeometryHolder._convexMesh(this.address));
    }

    private static native long _convexMesh(long var0);

    public PxTriangleMeshGeometry triangleMesh() {
        this.checkNotNull();
        return PxTriangleMeshGeometry.wrapPointer(PxGeometryHolder._triangleMesh(this.address));
    }

    private static native long _triangleMesh(long var0);

    public PxHeightFieldGeometry heightField() {
        this.checkNotNull();
        return PxHeightFieldGeometry.wrapPointer(PxGeometryHolder._heightField(this.address));
    }

    private static native long _heightField(long var0);

    public void storeAny(PxGeometry geometry) {
        this.checkNotNull();
        PxGeometryHolder._storeAny(this.address, geometry.getAddress());
    }

    private static native void _storeAny(long var0, long var2);
}

