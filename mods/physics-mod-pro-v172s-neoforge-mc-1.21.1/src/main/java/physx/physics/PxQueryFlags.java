/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxQueryFlagEnum;

public class PxQueryFlags
extends NativeObject {
    public static final int SIZEOF = PxQueryFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxQueryFlags() {
    }

    private static native int __sizeOf();

    public static PxQueryFlags wrapPointer(long address) {
        return address != 0L ? new PxQueryFlags(address) : null;
    }

    public static PxQueryFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxQueryFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxQueryFlags(long address) {
        super(address);
    }

    public static PxQueryFlags createAt(long address, short flags) {
        PxQueryFlags.__placement_new_PxQueryFlags(address, flags);
        PxQueryFlags createdObj = PxQueryFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxQueryFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxQueryFlags.__placement_new_PxQueryFlags(address, flags);
        PxQueryFlags createdObj = PxQueryFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxQueryFlags(long var0, short var2);

    public PxQueryFlags(short flags) {
        this.address = PxQueryFlags._PxQueryFlags(flags);
    }

    private static native long _PxQueryFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxQueryFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxQueryFlagEnum flag) {
        this.checkNotNull();
        return PxQueryFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxQueryFlagEnum flag) {
        this.checkNotNull();
        PxQueryFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxQueryFlagEnum flag) {
        this.checkNotNull();
        PxQueryFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

