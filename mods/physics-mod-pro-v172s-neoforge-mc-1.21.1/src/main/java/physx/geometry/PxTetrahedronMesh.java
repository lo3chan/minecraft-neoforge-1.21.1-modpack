/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.common.PxBounds3;
import physx.common.PxRefCounted;
import physx.common.PxVec3;
import physx.geometry.PxTetrahedronMeshFlags;
import physx.support.PxU32ConstPtr;

public class PxTetrahedronMesh
extends PxRefCounted {
    public static final int SIZEOF = PxTetrahedronMesh.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxTetrahedronMesh() {
    }

    private static native int __sizeOf();

    public static PxTetrahedronMesh wrapPointer(long address) {
        return address != 0L ? new PxTetrahedronMesh(address) : null;
    }

    public static PxTetrahedronMesh arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxTetrahedronMesh.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxTetrahedronMesh(long address) {
        super(address);
    }

    public int getNbVertices() {
        this.checkNotNull();
        return PxTetrahedronMesh._getNbVertices(this.address);
    }

    private static native int _getNbVertices(long var0);

    public PxVec3 getVertices() {
        this.checkNotNull();
        return PxVec3.wrapPointer(PxTetrahedronMesh._getVertices(this.address));
    }

    private static native long _getVertices(long var0);

    public int getNbTetrahedrons() {
        this.checkNotNull();
        return PxTetrahedronMesh._getNbTetrahedrons(this.address);
    }

    private static native int _getNbTetrahedrons(long var0);

    public NativeObject getTetrahedrons() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxTetrahedronMesh._getTetrahedrons(this.address));
    }

    private static native long _getTetrahedrons(long var0);

    public PxTetrahedronMeshFlags getTetrahedronMeshFlags() {
        this.checkNotNull();
        return PxTetrahedronMeshFlags.wrapPointer(PxTetrahedronMesh._getTetrahedronMeshFlags(this.address));
    }

    private static native long _getTetrahedronMeshFlags(long var0);

    public PxU32ConstPtr getTetrahedraRemap() {
        this.checkNotNull();
        return PxU32ConstPtr.wrapPointer(PxTetrahedronMesh._getTetrahedraRemap(this.address));
    }

    private static native long _getTetrahedraRemap(long var0);

    public PxBounds3 getLocalBounds() {
        this.checkNotNull();
        return PxBounds3.wrapPointer(PxTetrahedronMesh._getLocalBounds(this.address));
    }

    private static native long _getLocalBounds(long var0);
}

