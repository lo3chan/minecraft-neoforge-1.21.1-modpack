/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.NativeObject;
import physx.character.PxControllerBehaviorFlagEnum;

public class PxControllerBehaviorFlags
extends NativeObject {
    public static final int SIZEOF = PxControllerBehaviorFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxControllerBehaviorFlags() {
    }

    private static native int __sizeOf();

    public static PxControllerBehaviorFlags wrapPointer(long address) {
        return address != 0L ? new PxControllerBehaviorFlags(address) : null;
    }

    public static PxControllerBehaviorFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxControllerBehaviorFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxControllerBehaviorFlags(long address) {
        super(address);
    }

    public static PxControllerBehaviorFlags createAt(long address, byte flags) {
        PxControllerBehaviorFlags.__placement_new_PxControllerBehaviorFlags(address, flags);
        PxControllerBehaviorFlags createdObj = PxControllerBehaviorFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxControllerBehaviorFlags createAt(T allocator, NativeObject.Allocator<T> allocate, byte flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxControllerBehaviorFlags.__placement_new_PxControllerBehaviorFlags(address, flags);
        PxControllerBehaviorFlags createdObj = PxControllerBehaviorFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxControllerBehaviorFlags(long var0, byte var2);

    public PxControllerBehaviorFlags(byte flags) {
        this.address = PxControllerBehaviorFlags._PxControllerBehaviorFlags(flags);
    }

    private static native long _PxControllerBehaviorFlags(byte var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxControllerBehaviorFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxControllerBehaviorFlagEnum flag) {
        this.checkNotNull();
        return PxControllerBehaviorFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxControllerBehaviorFlagEnum flag) {
        this.checkNotNull();
        PxControllerBehaviorFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxControllerBehaviorFlagEnum flag) {
        this.checkNotNull();
        PxControllerBehaviorFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

