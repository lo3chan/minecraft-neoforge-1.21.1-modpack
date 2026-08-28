/*
 * Decompiled with CFR 0.152.
 */
package physx.cooking;

import physx.NativeObject;
import physx.cooking.PxConvexFlagEnum;

public class PxConvexFlags
extends NativeObject {
    public static final int SIZEOF = PxConvexFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxConvexFlags() {
    }

    private static native int __sizeOf();

    public static PxConvexFlags wrapPointer(long address) {
        return address != 0L ? new PxConvexFlags(address) : null;
    }

    public static PxConvexFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxConvexFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxConvexFlags(long address) {
        super(address);
    }

    public static PxConvexFlags createAt(long address, short flags) {
        PxConvexFlags.__placement_new_PxConvexFlags(address, flags);
        PxConvexFlags createdObj = PxConvexFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxConvexFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxConvexFlags.__placement_new_PxConvexFlags(address, flags);
        PxConvexFlags createdObj = PxConvexFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxConvexFlags(long var0, short var2);

    public PxConvexFlags(short flags) {
        this.address = PxConvexFlags._PxConvexFlags(flags);
    }

    private static native long _PxConvexFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxConvexFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxConvexFlagEnum flag) {
        this.checkNotNull();
        return PxConvexFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxConvexFlagEnum flag) {
        this.checkNotNull();
        PxConvexFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxConvexFlagEnum flag) {
        this.checkNotNull();
        PxConvexFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

