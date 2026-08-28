/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.physics.PxMaterial;
import physx.support.PxMaterialConstPtr;

public class PxArray_PxMaterialConst
extends NativeObject {
    public static final int SIZEOF = PxArray_PxMaterialConst.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxArray_PxMaterialConst wrapPointer(long address) {
        return address != 0L ? new PxArray_PxMaterialConst(address) : null;
    }

    public static PxArray_PxMaterialConst arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArray_PxMaterialConst.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArray_PxMaterialConst(long address) {
        super(address);
    }

    public static PxArray_PxMaterialConst createAt(long address) {
        PxArray_PxMaterialConst.__placement_new_PxArray_PxMaterialConst(address);
        PxArray_PxMaterialConst createdObj = PxArray_PxMaterialConst.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxArray_PxMaterialConst createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxArray_PxMaterialConst.__placement_new_PxArray_PxMaterialConst(address);
        PxArray_PxMaterialConst createdObj = PxArray_PxMaterialConst.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxArray_PxMaterialConst(long var0);

    public static PxArray_PxMaterialConst createAt(long address, int size) {
        PxArray_PxMaterialConst.__placement_new_PxArray_PxMaterialConst(address, size);
        PxArray_PxMaterialConst createdObj = PxArray_PxMaterialConst.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxArray_PxMaterialConst createAt(T allocator, NativeObject.Allocator<T> allocate, int size) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxArray_PxMaterialConst.__placement_new_PxArray_PxMaterialConst(address, size);
        PxArray_PxMaterialConst createdObj = PxArray_PxMaterialConst.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxArray_PxMaterialConst(long var0, int var2);

    public PxArray_PxMaterialConst() {
        this.address = PxArray_PxMaterialConst._PxArray_PxMaterialConst();
    }

    private static native long _PxArray_PxMaterialConst();

    public PxArray_PxMaterialConst(int size) {
        this.address = PxArray_PxMaterialConst._PxArray_PxMaterialConst(size);
    }

    private static native long _PxArray_PxMaterialConst(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxArray_PxMaterialConst._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxMaterial get(int index) {
        this.checkNotNull();
        return PxMaterial.wrapPointer(PxArray_PxMaterialConst._get(this.address, index));
    }

    private static native long _get(long var0, int var2);

    public void set(int index, PxMaterialConstPtr value) {
        this.checkNotNull();
        PxArray_PxMaterialConst._set(this.address, index, value.getAddress());
    }

    private static native void _set(long var0, int var2, long var3);

    public PxMaterialConstPtr begin() {
        this.checkNotNull();
        return PxMaterialConstPtr.wrapPointer(PxArray_PxMaterialConst._begin(this.address));
    }

    private static native long _begin(long var0);

    public int size() {
        this.checkNotNull();
        return PxArray_PxMaterialConst._size(this.address);
    }

    private static native int _size(long var0);

    public void pushBack(PxMaterial value) {
        this.checkNotNull();
        PxArray_PxMaterialConst._pushBack(this.address, value.getAddress());
    }

    private static native void _pushBack(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        PxArray_PxMaterialConst._clear(this.address);
    }

    private static native void _clear(long var0);
}

