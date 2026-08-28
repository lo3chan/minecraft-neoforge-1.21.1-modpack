/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.common.PxBounds3;
import physx.common.PxRefCounted;
import physx.common.PxVec3;
import physx.geometry.PxHullPolygon;
import physx.support.PxU8ConstPtr;

public class PxConvexMesh
extends PxRefCounted {
    public static final int SIZEOF = PxConvexMesh.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxConvexMesh() {
    }

    private static native int __sizeOf();

    public static PxConvexMesh wrapPointer(long address) {
        return address != 0L ? new PxConvexMesh(address) : null;
    }

    public static PxConvexMesh arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxConvexMesh.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxConvexMesh(long address) {
        super(address);
    }

    public int getNbVertices() {
        this.checkNotNull();
        return PxConvexMesh._getNbVertices(this.address);
    }

    private static native int _getNbVertices(long var0);

    public PxVec3 getVertices() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxConvexMesh._getVertices(this.address));
    }

    private static native long _getVertices(long var0);

    public PxU8ConstPtr getIndexBuffer() {
        this.checkNotNull();
        return PxU8ConstPtr.wrapPointer(PxConvexMesh._getIndexBuffer(this.address));
    }

    private static native long _getIndexBuffer(long var0);

    public int getNbPolygons() {
        this.checkNotNull();
        return PxConvexMesh._getNbPolygons(this.address);
    }

    private static native int _getNbPolygons(long var0);

    public boolean getPolygonData(int index, PxHullPolygon data) {
        this.checkNotNull();
        return PxConvexMesh._getPolygonData(this.address, index, data.getAddress());
    }

    private static native boolean _getPolygonData(long var0, int var2, long var3);

    public PxBounds3 getLocalBounds() {
        this.checkNotNull();
        return PxBounds3.wrapPointer(PxConvexMesh._getLocalBounds(this.address));
    }

    private static native long _getLocalBounds(long var0);

    public boolean isGpuCompatible() {
        this.checkNotNull();
        return PxConvexMesh._isGpuCompatible(this.address);
    }

    private static native boolean _isGpuCompatible(long var0);
}

