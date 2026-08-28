/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.extensions.PxPrismaticJointFlagEnum;

public class PxPrismaticJointFlags
extends NativeObject {
    public static final int SIZEOF = PxPrismaticJointFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxPrismaticJointFlags() {
    }

    private static native int __sizeOf();

    public static PxPrismaticJointFlags wrapPointer(long address) {
        return address != 0L ? new PxPrismaticJointFlags(address) : null;
    }

    public static PxPrismaticJointFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxPrismaticJointFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxPrismaticJointFlags(long address) {
        super(address);
    }

    public static PxPrismaticJointFlags createAt(long address, short flags) {
        PxPrismaticJointFlags.__placement_new_PxPrismaticJointFlags(address, flags);
        PxPrismaticJointFlags createdObj = PxPrismaticJointFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxPrismaticJointFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxPrismaticJointFlags.__placement_new_PxPrismaticJointFlags(address, flags);
        PxPrismaticJointFlags createdObj = PxPrismaticJointFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxPrismaticJointFlags(long var0, short var2);

    public PxPrismaticJointFlags(short flags) {
        this.address = PxPrismaticJointFlags._PxPrismaticJointFlags(flags);
    }

    private static native long _PxPrismaticJointFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxPrismaticJointFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxPrismaticJointFlagEnum flag) {
        this.checkNotNull();
        return PxPrismaticJointFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxPrismaticJointFlagEnum flag) {
        this.checkNotNull();
        PxPrismaticJointFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxPrismaticJointFlagEnum flag) {
        this.checkNotNull();
        PxPrismaticJointFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

