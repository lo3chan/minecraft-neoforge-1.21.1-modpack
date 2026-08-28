/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.physics.PxSweepHit;

@Deprecated
public class Vector_PxSweepHit
extends NativeObject {
    public static final int SIZEOF = Vector_PxSweepHit.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static Vector_PxSweepHit wrapPointer(long address) {
        return address != 0L ? new Vector_PxSweepHit(address) : null;
    }

    public static Vector_PxSweepHit arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return Vector_PxSweepHit.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected Vector_PxSweepHit(long address) {
        super(address);
    }

    public Vector_PxSweepHit() {
        this.address = Vector_PxSweepHit._Vector_PxSweepHit();
    }

    private static native long _Vector_PxSweepHit();

    public Vector_PxSweepHit(int size) {
        this.address = Vector_PxSweepHit._Vector_PxSweepHit(size);
    }

    private static native long _Vector_PxSweepHit(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        Vector_PxSweepHit._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxSweepHit at(int index) {
        this.checkNotNull();
        return PxSweepHit.wrapPointer(Vector_PxSweepHit._at(this.address, index));
    }

    private static native long _at(long var0, int var2);

    public PxSweepHit data() {
        this.checkNotNull();
        return PxSweepHit.wrapPointer(Vector_PxSweepHit._data(this.address));
    }

    private static native long _data(long var0);

    public int size() {
        this.checkNotNull();
        return Vector_PxSweepHit._size(this.address);
    }

    private static native int _size(long var0);

    public void push_back(PxSweepHit value) {
        this.checkNotNull();
        Vector_PxSweepHit._push_back(this.address, value.getAddress());
    }

    private static native void _push_back(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        Vector_PxSweepHit._clear(this.address);
    }

    private static native void _clear(long var0);
}

