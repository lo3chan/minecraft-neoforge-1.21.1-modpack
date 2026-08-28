/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import physx.NativeObject;
import physx.common.PxU16StridedData;
import physx.geometry.PxSimpleTriangleMesh;

public class PxTriangleMeshDesc
extends PxSimpleTriangleMesh {
    public static final int SIZEOF = PxTriangleMeshDesc.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxTriangleMeshDesc wrapPointer(long address) {
        return address != 0L ? new PxTriangleMeshDesc(address) : null;
    }

    public static PxTriangleMeshDesc arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxTriangleMeshDesc.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxTriangleMeshDesc(long address) {
        super(address);
    }

    public static PxTriangleMeshDesc createAt(long address) {
        PxTriangleMeshDesc.__placement_new_PxTriangleMeshDesc(address);
        PxTriangleMeshDesc createdObj = PxTriangleMeshDesc.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxTriangleMeshDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxTriangleMeshDesc.__placement_new_PxTriangleMeshDesc(address);
        PxTriangleMeshDesc createdObj = PxTriangleMeshDesc.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxTriangleMeshDesc(long var0);

    public PxTriangleMeshDesc() {
        this.address = PxTriangleMeshDesc._PxTriangleMeshDesc();
    }

    private static native long _PxTriangleMeshDesc();

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxTriangleMeshDesc._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxU16StridedData getMaterialIndices() {
        this.checkNotNull();
        return PxU16StridedData.wrapPointer(PxTriangleMeshDesc._getMaterialIndices(this.address));
    }

    private static native long _getMaterialIndices(long var0);

    public void setMaterialIndices(PxU16StridedData value) {
        this.checkNotNull();
        PxTriangleMeshDesc._setMaterialIndices(this.address, value.getAddress());
    }

    private static native void _setMaterialIndices(long var0, long var2);
}

