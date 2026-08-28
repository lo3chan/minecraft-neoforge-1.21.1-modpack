/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.geometry.PxTetrahedronMeshAnalysisResultEnum;

public class PxTetrahedronMeshAnalysisResults
extends NativeObject {
    public static final int SIZEOF = PxTetrahedronMeshAnalysisResults.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxTetrahedronMeshAnalysisResults() {
    }

    private static native int __sizeOf();

    public static PxTetrahedronMeshAnalysisResults wrapPointer(long address) {
        return address != 0L ? new PxTetrahedronMeshAnalysisResults(address) : null;
    }

    public static PxTetrahedronMeshAnalysisResults arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxTetrahedronMeshAnalysisResults.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxTetrahedronMeshAnalysisResults(long address) {
        super(address);
    }

    public static PxTetrahedronMeshAnalysisResults createAt(long address, int flags) {
        PxTetrahedronMeshAnalysisResults.__placement_new_PxTetrahedronMeshAnalysisResults(address, flags);
        PxTetrahedronMeshAnalysisResults createdObj = PxTetrahedronMeshAnalysisResults.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxTetrahedronMeshAnalysisResults createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxTetrahedronMeshAnalysisResults.__placement_new_PxTetrahedronMeshAnalysisResults(address, flags);
        PxTetrahedronMeshAnalysisResults createdObj = PxTetrahedronMeshAnalysisResults.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxTetrahedronMeshAnalysisResults(long var0, int var2);

    public PxTetrahedronMeshAnalysisResults(int flags) {
        this.address = PxTetrahedronMeshAnalysisResults._PxTetrahedronMeshAnalysisResults(flags);
    }

    private static native long _PxTetrahedronMeshAnalysisResults(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxTetrahedronMeshAnalysisResults._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxTetrahedronMeshAnalysisResultEnum flag) {
        this.checkNotNull();
        return PxTetrahedronMeshAnalysisResults._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxTetrahedronMeshAnalysisResultEnum flag) {
        this.checkNotNull();
        PxTetrahedronMeshAnalysisResults._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxTetrahedronMeshAnalysisResultEnum flag) {
        this.checkNotNull();
        PxTetrahedronMeshAnalysisResults._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

