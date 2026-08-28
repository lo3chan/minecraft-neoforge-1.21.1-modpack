/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.common.PxBoundedData;
import physx.geometry.PxMeshFlags;

public class PxSimpleTriangleMesh
extends NativeObject {
    public static final int SIZEOF = PxSimpleTriangleMesh.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxSimpleTriangleMesh wrapPointer(long address) {
        return address != 0L ? new PxSimpleTriangleMesh(address) : null;
    }

    public static PxSimpleTriangleMesh arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxSimpleTriangleMesh.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxSimpleTriangleMesh(long address) {
        super(address);
    }

    public PxSimpleTriangleMesh() {
        this.address = PxSimpleTriangleMesh._PxSimpleTriangleMesh();
    }

    private static native long _PxSimpleTriangleMesh();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxSimpleTriangleMesh._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxBoundedData getPoints() {
        this.checkNotNull();
        return PxBoundedData.wrapPointer(PxSimpleTriangleMesh._getPoints(this.address));
    }

    private static native long _getPoints(long var0);

    public void setPoints(PxBoundedData value) {
        this.checkNotNull();
        PxSimpleTriangleMesh._setPoints(this.address, value.getAddress());
    }

    private static native void _setPoints(long var0, long var2);

    public PxBoundedData getTriangles() {
        this.checkNotNull();
        return PxBoundedData.wrapPointer(PxSimpleTriangleMesh._getTriangles(this.address));
    }

    private static native long _getTriangles(long var0);

    public void setTriangles(PxBoundedData value) {
        this.checkNotNull();
        PxSimpleTriangleMesh._setTriangles(this.address, value.getAddress());
    }

    private static native void _setTriangles(long var0, long var2);

    public PxMeshFlags getFlags() {
        this.checkNotNull();
        return PxMeshFlags.wrapPointer(PxSimpleTriangleMesh._getFlags(this.address));
    }

    private static native long _getFlags(long var0);

    public void setFlags(PxMeshFlags value) {
        this.checkNotNull();
        PxSimpleTriangleMesh._setFlags(this.address, value.getAddress());
    }

    private static native void _setFlags(long var0, long var2);

    public void setToDefault() {
        this.checkNotNull();
        PxSimpleTriangleMesh._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);

    public boolean isValid() {
        this.checkNotNull();
        return PxSimpleTriangleMesh._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

