/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxHitFlagEnum;

public class PxHitFlags
extends NativeObject {
    public static final int SIZEOF = PxHitFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxHitFlags() {
    }

    private static native int __sizeOf();

    public static PxHitFlags wrapPointer(long address) {
        return address != 0L ? new PxHitFlags(address) : null;
    }

    public static PxHitFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxHitFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxHitFlags(long address) {
        super(address);
    }

    public static PxHitFlags createAt(long address, short flags) {
        PxHitFlags.__placement_new_PxHitFlags(address, flags);
        PxHitFlags createdObj = PxHitFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxHitFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxHitFlags.__placement_new_PxHitFlags(address, flags);
        PxHitFlags createdObj = PxHitFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxHitFlags(long var0, short var2);

    public PxHitFlags(short flags) {
        this.address = PxHitFlags._PxHitFlags(flags);
    }

    private static native long _PxHitFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxHitFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxHitFlagEnum flag) {
        this.checkNotNull();
        return PxHitFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxHitFlagEnum flag) {
        this.checkNotNull();
        PxHitFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxHitFlagEnum flag) {
        this.checkNotNull();
        PxHitFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

