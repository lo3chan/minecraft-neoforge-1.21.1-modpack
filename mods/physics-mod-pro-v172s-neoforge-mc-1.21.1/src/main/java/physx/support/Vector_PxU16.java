/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;

@Deprecated
public class Vector_PxU16
extends NativeObject {
    public static final int SIZEOF = Vector_PxU16.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static Vector_PxU16 wrapPointer(long address) {
        return address != 0L ? new Vector_PxU16(address) : null;
    }

    public static Vector_PxU16 arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return Vector_PxU16.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected Vector_PxU16(long address) {
        super(address);
    }

    public Vector_PxU16() {
        this.address = Vector_PxU16._Vector_PxU16();
    }

    private static native long _Vector_PxU16();

    public Vector_PxU16(int size) {
        this.address = Vector_PxU16._Vector_PxU16(size);
    }

    private static native long _Vector_PxU16(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        Vector_PxU16._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public short at(int index) {
        this.checkNotNull();
        return Vector_PxU16._at(this.address, index);
    }

    private static native short _at(long var0, int var2);

    public NativeObject data() {
        this.checkNotNull();
        return NativeObject.wrapPointer(Vector_PxU16._data(this.address));
    }

    private static native long _data(long var0);

    public int size() {
        this.checkNotNull();
        return Vector_PxU16._size(this.address);
    }

    private static native int _size(long var0);

    public void push_back(short value) {
        this.checkNotNull();
        Vector_PxU16._push_back(this.address, value);
    }

    private static native void _push_back(long var0, short var2);

    public void clear() {
        this.checkNotNull();
        Vector_PxU16._clear(this.address);
    }

    private static native void _clear(long var0);
}

