/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxArticulationSensorFlagEnum;

public class PxArticulationSensorFlags
extends NativeObject {
    public static final int SIZEOF = PxArticulationSensorFlags.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxArticulationSensorFlags() {
    }

    private static native int __sizeOf();

    public static PxArticulationSensorFlags wrapPointer(long address) {
        return address != 0L ? new PxArticulationSensorFlags(address) : null;
    }

    public static PxArticulationSensorFlags arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArticulationSensorFlags.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArticulationSensorFlags(long address) {
        super(address);
    }

    public static PxArticulationSensorFlags createAt(long address, byte flags) {
        PxArticulationSensorFlags.__placement_new_PxArticulationSensorFlags(address, flags);
        PxArticulationSensorFlags createdObj = PxArticulationSensorFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    public static <T> PxArticulationSensorFlags createAt(T allocator, NativeObject.Allocator<T> allocate, byte flags) {
        long address = allocate.on(allocator, 8, SIZEOF);
        PxArticulationSensorFlags.__placement_new_PxArticulationSensorFlags(address, flags);
        PxArticulationSensorFlags createdObj = PxArticulationSensorFlags.wrapPointer(address);
        createdObj.isExternallyAllocated = true;
        return createdObj;
    }

    private static native void __placement_new_PxArticulationSensorFlags(long var0, byte var2);

    public PxArticulationSensorFlags(byte flags) {
        this.address = PxArticulationSensorFlags._PxArticulationSensorFlags(flags);
    }

    private static native long _PxArticulationSensorFlags(byte var0);

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxArticulationSensorFlags._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean isSet(PxArticulationSensorFlagEnum flag) {
        this.checkNotNull();
        return PxArticulationSensorFlags._isSet(this.address, flag.value);
    }

    private static native boolean _isSet(long var0, int var2);

    public void raise(PxArticulationSensorFlagEnum flag) {
        this.checkNotNull();
        PxArticulationSensorFlags._raise(this.address, flag.value);
    }

    private static native void _raise(long var0, int var2);

    public void clear(PxArticulationSensorFlagEnum flag) {
        this.checkNotNull();
        PxArticulationSensorFlags._clear(this.address, flag.value);
    }

    private static native void _clear(long var0, int var2);
}

