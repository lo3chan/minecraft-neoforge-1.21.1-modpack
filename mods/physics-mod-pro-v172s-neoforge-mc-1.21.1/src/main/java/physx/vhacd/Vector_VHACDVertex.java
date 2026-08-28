/*
 * Decompiled with CFR 0.152.
 */
package physx.vhacd;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.vhacd.VHACDVertex;

public class Vector_VHACDVertex
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected Vector_VHACDVertex() {
    }

    private static native int __sizeOf();

    public static Vector_VHACDVertex wrapPointer(long address) {
        return address != 0L ? new Vector_VHACDVertex(address) : null;
    }

    public static Vector_VHACDVertex arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return Vector_VHACDVertex.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected Vector_VHACDVertex(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        Vector_VHACDVertex._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public VHACDVertex at(int index) {
        this.checkNotNull();
        return VHACDVertex.wrapPointer(Vector_VHACDVertex._at(this.address, index));
    }

    private static native long _at(long var0, int var2);

    public VHACDVertex data() {
        this.checkNotNull();
        return VHACDVertex.wrapPointer(Vector_VHACDVertex._data(this.address));
    }

    private static native long _data(long var0);

    public int size() {
        this.checkNotNull();
        return Vector_VHACDVertex._size(this.address);
    }

    private static native int _size(long var0);

    public void push_back(VHACDVertex value) {
        this.checkNotNull();
        Vector_VHACDVertex._push_back(this.address, value.getAddress());
    }

    private static native void _push_back(long var0, long var2);

    public void clear() {
        this.checkNotNull();
        Vector_VHACDVertex._clear(this.address);
    }

    private static native void _clear(long var0);

    static {
        PlatformChecks.requirePlatform(15, "physx.vhacd.Vector_VHACDVertex");
        SIZEOF = Vector_VHACDVertex.__sizeOf();
    }
}

