/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.extensions.PxRevoluteJointFlagEnum;

public class PxRevoluteJointFlags
extends NativeObject {
    public static final int SIZEOF = PxRevoluteJointFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxRevoluteJointFlags() {
    }

    private static native int __sizeOf();

    public static PxRevoluteJointFlags wrapPointer(long address) {
        return address != 0L ? new PxRevoluteJointFlags(address) : null;
    }

    public static PxRevoluteJointFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRevoluteJointFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRevoluteJointFlags(long address) {
        super(address);
    }

    public static PxRevoluteJointFlags createAt(long address, short flags) {
        PxRevoluteJointFlags.__placement_new_PxRevoluteJointFlags(address, flags);
        PxRevoluteJointFlags createdObj = PxRevoluteJointFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxRevoluteJointFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxRevoluteJointFlags.__placement_new_PxRevoluteJointFlags(address, flags);
        PxRevoluteJointFlags createdObj = PxRevoluteJointFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxRevoluteJointFlags(long var0, short var2);

    public PxRevoluteJointFlags(short flags) {
        this.address = PxRevoluteJointFlags._PxRevoluteJointFlags(flags);
    }

    private static native long _PxRevoluteJointFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxRevoluteJointFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxRevoluteJointFlagEnum flag) {
        this.checkNotNull();
        return PxRevoluteJointFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxRevoluteJointFlagEnum flag) {
        this.checkNotNull();
        PxRevoluteJointFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxRevoluteJointFlagEnum flag) {
        this.checkNotNull();
        PxRevoluteJointFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

