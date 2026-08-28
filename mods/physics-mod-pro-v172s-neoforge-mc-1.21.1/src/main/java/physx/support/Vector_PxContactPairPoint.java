/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.physics.PxContactPairPoint;

@Deprecated
public class Vector_PxContactPairPoint
extends NativeObject {
    public static final int SIZEOF = Vector_PxContactPairPoint.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static Vector_PxContactPairPoint wrapPointer(long address) {
        return address != 0L ? new Vector_PxContactPairPoint(address) : null;
    }

    public static Vector_PxContactPairPoint arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return Vector_PxContactPairPoint.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected Vector_PxContactPairPoint(long address) {
        super(address);
    }

    public Vector_PxContactPairPoint() {
        this.address = Vector_PxContactPairPoint._Vector_PxContactPairPoint();
    }

    private static native long _Vector_PxContactPairPoint();

    public Vector_PxContactPairPoint(int size) {
        this.address = Vector_PxContactPairPoint._Vector_PxContactPairPoint(size);
    }

    private static native long _Vector_PxContactPairPoint(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        Vector_PxContactPairPoint._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxContactPairPoint at(int index) {
        this.checkNotNull();
        return PxContactPairPoint.wrapPointer(Vector_PxContactPairPoint._at(this.address, index));
    }

    private static native long _at(long var0, int var2);

    public PxContactPairPoint data() {
        this.checkNotNull();
        return PxContactPairPoint.wrapPointer(Vector_PxContactPairPoint._data(this.address));
    }

    private static native long _data(long var0);

    public int size() {
        this.checkNotNull();
        return Vector_PxContactPairPoint._size(this.address);
    }

    private static native int _size(long var0);

    public void push_back(PxContactPairPoint value) {
        this.checkNotNull();
        Vector_PxContactPairPoint._push_back(this.address, value.getAddress());
    }

    private static native void _push_back(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        Vector_PxContactPairPoint._clear(this.address);
    }

    private static native void _clear(long var0);
}

