/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxRigidDynamicLockFlagEnum;

public class PxRigidDynamicLockFlags
extends NativeObject {
    public static final int SIZEOF = PxRigidDynamicLockFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxRigidDynamicLockFlags() {
    }

    private static native int __sizeOf();

    public static PxRigidDynamicLockFlags wrapPointer(long address) {
        return address != 0L ? new PxRigidDynamicLockFlags(address) : null;
    }

    public static PxRigidDynamicLockFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRigidDynamicLockFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRigidDynamicLockFlags(long address) {
        super(address);
    }

    public static PxRigidDynamicLockFlags createAt(long address, byte flags) {
        PxRigidDynamicLockFlags.__placement_new_PxRigidDynamicLockFlags(address, flags);
        PxRigidDynamicLockFlags createdObj = PxRigidDynamicLockFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxRigidDynamicLockFlags createAt(T allocator, NativeObject.Allocator<T> allocate, byte flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxRigidDynamicLockFlags.__placement_new_PxRigidDynamicLockFlags(address, flags);
        PxRigidDynamicLockFlags createdObj = PxRigidDynamicLockFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxRigidDynamicLockFlags(long var0, byte var2);

    public PxRigidDynamicLockFlags(byte flags) {
        this.address = PxRigidDynamicLockFlags._PxRigidDynamicLockFlags(flags);
    }

    private static native long _PxRigidDynamicLockFlags(byte var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxRigidDynamicLockFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxRigidDynamicLockFlagEnum flag) {
        this.checkNotNull();
        return PxRigidDynamicLockFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxRigidDynamicLockFlagEnum flag) {
        this.checkNotNull();
        PxRigidDynamicLockFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxRigidDynamicLockFlagEnum flag) {
        this.checkNotNull();
        PxRigidDynamicLockFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

