/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.geometry.PxGeometry;
import physx.geometry.PxTriangleMeshGeometry;
import physx.support.PxU32ConstPtr;

public class PxMeshOverlapUtil
extends NativeObject {
    public static final int SIZEOF = PxMeshOverlapUtil.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxMeshOverlapUtil wrapPointer(long address) {
        return address != 0L ? new PxMeshOverlapUtil(address) : null;
    }

    public static PxMeshOverlapUtil arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxMeshOverlapUtil.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxMeshOverlapUtil(long address) {
        super(address);
    }

    public PxMeshOverlapUtil() {
        this.address = PxMeshOverlapUtil._PxMeshOverlapUtil();
    }

    private static native long _PxMeshOverlapUtil();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxMeshOverlapUtil._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int findOverlap(PxGeometry geom, PxTransform geomPose, PxTriangleMeshGeometry meshGeom, PxTransform meshPose) {
        this.checkNotNull();
        return PxMeshOverlapUtil._findOverlap(this.address, geom.getAddress(), geomPose.getAddress(), meshGeom.getAddress(), meshPose.getAddress());
    }

    private static native int _findOverlap(long var0, long var2, long var4, long var6, long var8);

    public PxU32ConstPtr getResults() {
        this.checkNotNull();
        return PxU32ConstPtr.wrapPointer(PxMeshOverlapUtil._getResults(this.address));
    }

    private static native long _getResults(long var0);

    public int getNbResults() {
        this.checkNotNull();
        return PxMeshOverlapUtil._getNbResults(this.address);
    }

    private static native int _getNbResults(long var0);
}

