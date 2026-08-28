/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;

public class PxArray_PxReal
extends NativeObject {
    public static final int SIZEOF = PxArray_PxReal.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxArray_PxReal wrapPointer(long address) {
        return address != 0L ? new PxArray_PxReal(address) : null;
    }

    public static PxArray_PxReal arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArray_PxReal.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArray_PxReal(long address) {
        super(address);
    }

    public static PxArray_PxReal createAt(long address) {
        PxArray_PxReal.__placement_new_PxArray_PxReal(address);
        PxArray_PxReal createdObj = PxArray_PxReal.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxArray_PxReal createAt(T allocator, NativeObject.Allocator<T> allocate) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxArray_PxReal.__placement_new_PxArray_PxReal(address);
        PxArray_PxReal createdObj = PxArray_PxReal.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxArray_PxReal(long var0);

    public static PxArray_PxReal createAt(long address, int size) {
        PxArray_PxReal.__placement_new_PxArray_PxReal(address, size);
        PxArray_PxReal createdObj = PxArray_PxReal.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxArray_PxReal createAt(T allocator, NativeObject.Allocator<T> allocate, int size) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxArray_PxReal.__placement_new_PxArray_PxReal(address, size);
        PxArray_PxReal createdObj = PxArray_PxReal.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxArray_PxReal(long var0, int var2);

    public PxArray_PxReal() {
        this.address = PxArray_PxReal._PxArray_PxReal();
    }

    private static native long _PxArray_PxReal();

    public PxArray_PxReal(int size) {
        this.address = PxArray_PxReal._PxArray_PxReal(size);
    }

    private static native long _PxArray_PxReal(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxArray_PxReal._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float get(int index) {
        this.checkNotNull();
        return PxArray_PxReal._get(this.address, index);
    }

    private static native float _get(long var0, int var2);

    public void set(int index, float value) {
        this.checkNotNull();
        PxArray_PxReal._set(this.address, index, value);
    }

    private static native void _set(long var0, int var2, float var3);

    public NativeObject begin() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxArray_PxReal._begin(this.address));
    }

    private static native long _begin(long var0);

    public int size() {
        this.checkNotNull();
        return PxArray_PxReal._size(this.address);
    }

    private static native int _size(long var0);

    public void pushBack(float value) {
        this.checkNotNull();
        PxArray_PxReal._pushBack(this.address, value);
    }

    private static native void _pushBack(long var0, float var2);

    public void clear() {
        this.checkNotNull();
        PxArray_PxReal._clear(this.address);
    }

    private static native void _clear(long var0);
}

