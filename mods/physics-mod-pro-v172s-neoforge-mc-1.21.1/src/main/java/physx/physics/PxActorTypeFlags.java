/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxActorTypeFlagEnum;

public class PxActorTypeFlags
extends NativeObject {
    public static final int SIZEOF = PxActorTypeFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxActorTypeFlags() {
    }

    private static native int __sizeOf();

    public static PxActorTypeFlags wrapPointer(long address) {
        return address != 0L ? new PxActorTypeFlags(address) : null;
    }

    public static PxActorTypeFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxActorTypeFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxActorTypeFlags(long address) {
        super(address);
    }

    public static PxActorTypeFlags createAt(long address, short flags) {
        PxActorTypeFlags.__placement_new_PxActorTypeFlags(address, flags);
        PxActorTypeFlags createdObj = PxActorTypeFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxActorTypeFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxActorTypeFlags.__placement_new_PxActorTypeFlags(address, flags);
        PxActorTypeFlags createdObj = PxActorTypeFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxActorTypeFlags(long var0, short var2);

    public PxActorTypeFlags(short flags) {
        this.address = PxActorTypeFlags._PxActorTypeFlags(flags);
    }

    private static native long _PxActorTypeFlags(short var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxActorTypeFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxActorTypeFlagEnum flag) {
        this.checkNotNull();
        return PxActorTypeFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxActorTypeFlagEnum flag) {
        this.checkNotNull();
        PxActorTypeFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxActorTypeFlagEnum flag) {
        this.checkNotNull();
        PxActorTypeFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

