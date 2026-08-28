/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxMaterialFlagEnum;

public class PxMaterialFlags
extends NativeObject {
    public static final int SIZEOF = PxMaterialFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxMaterialFlags() {
    }

    private static native int __sizeOf();

    public static PxMaterialFlags wrapPointer(long address) {
        return address != 0L ? new PxMaterialFlags(address) : null;
    }

    public static PxMaterialFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxMaterialFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxMaterialFlags(long address) {
        super(address);
    }

    public static PxMaterialFlags createAt(long address, short flags) {
        PxMaterialFlags.__placement_new_PxMaterialFlags(address, flags);
        PxMaterialFlags createdObj = PxMaterialFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxMaterialFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxMaterialFlags.__placement_new_PxMaterialFlags(address, flags);
        PxMaterialFlags createdObj = PxMaterialFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxMaterialFlags(long var0, short var2);

    public PxMaterialFlags(short flags) {
        this.address = PxMaterialFlags._PxMaterialFlags(flags);
    }

    private static native long _PxMaterialFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxMaterialFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxMaterialFlagEnum flag) {
        this.checkNotNull();
        return PxMaterialFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxMaterialFlagEnum flag) {
        this.checkNotNull();
        PxMaterialFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxMaterialFlagEnum flag) {
        this.checkNotNull();
        PxMaterialFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

