/*
 * Decompiled with CFR 0.152.
 */
package physx.geometry;

import physx.NativeObject;
import physx.geometry.PxHeightFieldFlagEnum;

public class PxHeightFieldFlags
extends NativeObject {
    public static final int SIZEOF = PxHeightFieldFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxHeightFieldFlags() {
    }

    private static native int __sizeOf();

    public static PxHeightFieldFlags wrapPointer(long address) {
        return address != 0L ? new PxHeightFieldFlags(address) : null;
    }

    public static PxHeightFieldFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxHeightFieldFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxHeightFieldFlags(long address) {
        super(address);
    }

    public static PxHeightFieldFlags createAt(long address, short flags) {
        PxHeightFieldFlags.__placement_new_PxHeightFieldFlags(address, flags);
        PxHeightFieldFlags createdObj = PxHeightFieldFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxHeightFieldFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxHeightFieldFlags.__placement_new_PxHeightFieldFlags(address, flags);
        PxHeightFieldFlags createdObj = PxHeightFieldFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxHeightFieldFlags(long var0, short var2);

    public PxHeightFieldFlags(short flags) {
        this.address = PxHeightFieldFlags._PxHeightFieldFlags(flags);
    }

    private static native long _PxHeightFieldFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxHeightFieldFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxHeightFieldFlagEnum flag) {
        this.checkNotNull();
        return PxHeightFieldFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxHeightFieldFlagEnum flag) {
        this.checkNotNull();
        PxHeightFieldFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxHeightFieldFlagEnum flag) {
        this.checkNotNull();
        PxHeightFieldFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

