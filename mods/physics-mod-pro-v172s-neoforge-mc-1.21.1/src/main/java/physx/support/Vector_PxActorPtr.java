/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.physics.PxActor;
import physx.support.PxActorPtr;

@Deprecated
public class Vector_PxActorPtr
extends NativeObject {
    public static final int SIZEOF = Vector_PxActorPtr.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static Vector_PxActorPtr wrapPointer(long address) {
        return address != 0L ? new Vector_PxActorPtr(address) : null;
    }

    public static Vector_PxActorPtr arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return Vector_PxActorPtr.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected Vector_PxActorPtr(long address) {
        super(address);
    }

    public Vector_PxActorPtr() {
        this.address = Vector_PxActorPtr._Vector_PxActorPtr();
    }

    private static native long _Vector_PxActorPtr();

    public Vector_PxActorPtr(int size) {
        this.address = Vector_PxActorPtr._Vector_PxActorPtr(size);
    }

    private static native long _Vector_PxActorPtr(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        Vector_PxActorPtr._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxActor at(int index) {
        this.checkNotNull();
        return PxActor.wrapPointer(Vector_PxActorPtr._at(this.address, index));
    }

    private static native long _at(long var0, int var2);

    public PxActorPtr data() {
        this.checkNotNull();
        return PxActorPtr.wrapPointer(Vector_PxActorPtr._data(this.address));
    }

    private static native long _data(long var0);

    public int size() {
        this.checkNotNull();
        return Vector_PxActorPtr._size(this.address);
    }

    private static native int _size(long var0);

    public void push_back(PxActor value) {
        this.checkNotNull();
        Vector_PxActorPtr._push_back(this.address, value.getAddress());
    }

    private static native void _push_back(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        Vector_PxActorPtr._clear(this.address);
    }

    private static native void _clear(long var0);
}

