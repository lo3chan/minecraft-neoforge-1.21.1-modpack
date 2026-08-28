/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxArticulationCacheFlagEnum;

public class PxArticulationCacheFlags
extends NativeObject {
    public static final int SIZEOF = PxArticulationCacheFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxArticulationCacheFlags() {
    }

    private static native int __sizeOf();

    public static PxArticulationCacheFlags wrapPointer(long address) {
        return address != 0L ? new PxArticulationCacheFlags(address) : null;
    }

    public static PxArticulationCacheFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArticulationCacheFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArticulationCacheFlags(long address) {
        super(address);
    }

    public static PxArticulationCacheFlags createAt(long address, int flags) {
        PxArticulationCacheFlags.__placement_new_PxArticulationCacheFlags(address, flags);
        PxArticulationCacheFlags createdObj = PxArticulationCacheFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxArticulationCacheFlags createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxArticulationCacheFlags.__placement_new_PxArticulationCacheFlags(address, flags);
        PxArticulationCacheFlags createdObj = PxArticulationCacheFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxArticulationCacheFlags(long var0, int var2);

    public PxArticulationCacheFlags(int flags) {
        this.address = PxArticulationCacheFlags._PxArticulationCacheFlags(flags);
    }

    private static native long _PxArticulationCacheFlags(int var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxArticulationCacheFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxArticulationCacheFlagEnum flag) {
        this.checkNotNull();
        return PxArticulationCacheFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxArticulationCacheFlagEnum flag) {
        this.checkNotNull();
        PxArticulationCacheFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxArticulationCacheFlagEnum flag) {
        this.checkNotNull();
        PxArticulationCacheFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

