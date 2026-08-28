/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.common.PxVec3;

@Deprecated
public class Vector_PxVec3
extends NativeObject {
    public static final int SIZEOF = Vector_PxVec3.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static Vector_PxVec3 wrapPointer(long address) {
        return address != 0L ? new Vector_PxVec3(address) : null;
    }

    public static Vector_PxVec3 arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return Vector_PxVec3.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected Vector_PxVec3(long address) {
        super(address);
    }

    public Vector_PxVec3() {
        this.address = Vector_PxVec3._Vector_PxVec3();
    }

    private static native long _Vector_PxVec3();

    public Vector_PxVec3(int size) {
        this.address = Vector_PxVec3._Vector_PxVec3(size);
    }

    private static native long _Vector_PxVec3(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        Vector_PxVec3._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVec3 at(int index) {
        this.checkNotNull();
        return PxVec3.wrapPointer(Vector_PxVec3._at(this.address, index));
    }

    private static native long _at(long var0, int var2);

    public PxVec3 data() {
        this.checkNotNull();
        return PxVec3.wrapPointer(Vector_PxVec3._data(this.address));
    }

    private static native long _data(long var0);

    public int size() {
        this.checkNotNull();
        return Vector_PxVec3._size(this.address);
    }

    private static native int _size(long var0);

    public void push_back(PxVec3 value) {
        this.checkNotNull();
        Vector_PxVec3._push_back(this.address, value.getAddress());
    }

    private static native void _push_back(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        Vector_PxVec3._clear(this.address);
    }

    private static native void _clear(long var0);
}

