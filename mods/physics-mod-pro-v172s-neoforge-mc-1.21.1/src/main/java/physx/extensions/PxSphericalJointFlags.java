/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.extensions.PxSphericalJointFlagEnum;

public class PxSphericalJointFlags
extends NativeObject {
    public static final int SIZEOF = PxSphericalJointFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxSphericalJointFlags() {
    }

    private static native int __sizeOf();

    public static PxSphericalJointFlags wrapPointer(long address) {
        return address != 0L ? new PxSphericalJointFlags(address) : null;
    }

    public static PxSphericalJointFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxSphericalJointFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxSphericalJointFlags(long address) {
        super(address);
    }

    public static PxSphericalJointFlags createAt(long address, short flags) {
        PxSphericalJointFlags.__placement_new_PxSphericalJointFlags(address, flags);
        PxSphericalJointFlags createdObj = PxSphericalJointFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxSphericalJointFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxSphericalJointFlags.__placement_new_PxSphericalJointFlags(address, flags);
        PxSphericalJointFlags createdObj = PxSphericalJointFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxSphericalJointFlags(long var0, short var2);

    public PxSphericalJointFlags(short flags) {
        this.address = PxSphericalJointFlags._PxSphericalJointFlags(flags);
    }

    private static native long _PxSphericalJointFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxSphericalJointFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxSphericalJointFlagEnum flag) {
        this.checkNotNull();
        return PxSphericalJointFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxSphericalJointFlagEnum flag) {
        this.checkNotNull();
        PxSphericalJointFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxSphericalJointFlagEnum flag) {
        this.checkNotNull();
        PxSphericalJointFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

