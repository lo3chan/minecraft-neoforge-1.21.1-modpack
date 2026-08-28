/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.geometry.PxGeometry;
import physx.geometry.PxHeightField;
import physx.geometry.PxMeshGeometryFlags;

public class PxHeightFieldGeometry
extends PxGeometry {
    public static final int SIZEOF = PxHeightFieldGeometry.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxHeightFieldGeometry wrapPointer(long address) {
        return address != 0L ? new PxHeightFieldGeometry(address) : null;
    }

    public static PxHeightFieldGeometry arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxHeightFieldGeometry.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxHeightFieldGeometry(long address) {
        super(address);
    }

    public static PxHeightFieldGeometry createAt(long address) {
        PxHeightFieldGeometry.__placement_new_PxHeightFieldGeometry(address);
        PxHeightFieldGeometry createdObj = PxHeightFieldGeometry.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxHeightFieldGeometry createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxHeightFieldGeometry.__placement_new_PxHeightFieldGeometry(address);
        PxHeightFieldGeometry createdObj = PxHeightFieldGeometry.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxHeightFieldGeometry(long var0);

    public static PxHeightFieldGeometry createAt(long address, PxHeightField hf, PxMeshGeometryFlags flags, float heightScale, float rowScale, float columnScale) {
        PxHeightFieldGeometry.__placement_new_PxHeightFieldGeometry(address, hf.getAddress(), flags.getAddress(), heightScale, rowScale, columnScale);
        PxHeightFieldGeometry createdObj = PxHeightFieldGeometry.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxHeightFieldGeometry createAt(T allocator, NativeObject.Allocator<T> allocate, PxHeightField hf, PxMeshGeometryFlags flags, float heightScale, float rowScale, float columnScale) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxHeightFieldGeometry.__placement_new_PxHeightFieldGeometry(address, hf.getAddress(), flags.getAddress(), heightScale, rowScale, columnScale);
        PxHeightFieldGeometry createdObj = PxHeightFieldGeometry.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxHeightFieldGeometry(long var0, long var2, long var4, float var6, float var7, float var8);

    public PxHeightFieldGeometry() {
        this.address = PxHeightFieldGeometry._PxHeightFieldGeometry();
    }

    private static native long _PxHeightFieldGeometry();

    public PxHeightFieldGeometry(PxHeightField hf, PxMeshGeometryFlags flags, float heightScale, float rowScale, float columnScale) {
        this.address = PxHeightFieldGeometry._PxHeightFieldGeometry(hf.getAddress(), flags.getAddress(), heightScale, rowScale, columnScale);
    }

    private static native long _PxHeightFieldGeometry(long var0, long var2, float var4, float var5, float var6);

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxHeightFieldGeometry._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxHeightField getHeightField() {
        this.checkNotNull();
        return PxHeightField.wrapPointer(PxHeightFieldGeometry._getHeightField(this.address));
    }

    private static native long _getHeightField(long var0);

    public void setHeightField(PxHeightField value) {
        this.checkNotNull();
        PxHeightFieldGeometry._setHeightField(this.address, value.getAddress());
    }

    private static native void _setHeightField(long var0, long var2);

    public float getHeightScale() {
        this.checkNotNull();
        return PxHeightFieldGeometry._getHeightScale(this.address);
    }

    private static native float _getHeightScale(long var0);

    public void setHeightScale(float value) {
        this.checkNotNull();
        PxHeightFieldGeometry._setHeightScale(this.address, value);
    }

    private static native void _setHeightScale(long var0, float var2);

    public float getRowScale() {
        this.checkNotNull();
        return PxHeightFieldGeometry._getRowScale(this.address);
    }

    private static native float _getRowScale(long var0);

    public void setRowScale(float value) {
        this.checkNotNull();
        PxHeightFieldGeometry._setRowScale(this.address, value);
    }

    private static native void _setRowScale(long var0, float var2);

    public float getColumnScale() {
        this.checkNotNull();
        return PxHeightFieldGeometry._getColumnScale(this.address);
    }

    private static native float _getColumnScale(long var0);

    public void setColumnScale(float value) {
        this.checkNotNull();
        PxHeightFieldGeometry._setColumnScale(this.address, value);
    }

    private static native void _setColumnScale(long var0, float var2);

    public PxMeshGeometryFlags getHeightFieldFlags() {
        this.checkNotNull();
        return PxMeshGeometryFlags.wrapPointer(PxHeightFieldGeometry._getHeightFieldFlags(this.address));
    }

    private static native long _getHeightFieldFlags(long var0);

    public void setHeightFieldFlags(PxMeshGeometryFlags value) {
        this.checkNotNull();
        PxHeightFieldGeometry._setHeightFieldFlags(this.address, value.getAddress());
    }

    private static native void _setHeightFieldFlags(long var0, long var2);

    public boolean isValid() {
        this.checkNotNull();
        return PxHeightFieldGeometry._isValid(this.address);
    }

    private static native boolean _isValid(long var0);
}

