/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleTireSlipState
extends NativeObject {
    public static final int SIZEOF = PxVehicleTireSlipState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleTireSlipState wrapPointer(long address) {
        return address != 0L ? new PxVehicleTireSlipState(address) : null;
    }

    public static PxVehicleTireSlipState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleTireSlipState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleTireSlipState(long address) {
        super(address);
    }

    public PxVehicleTireSlipState() {
        this.address = PxVehicleTireSlipState._PxVehicleTireSlipState();
    }

    private static native long _PxVehicleTireSlipState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleTireSlipState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getSlips(int index) {
        this.checkNotNull();
        return PxVehicleTireSlipState._getSlips(this.address, index);
    }

    private static native float _getSlips(long var0, int var2);

    public void setSlips(int index, float value) {
        this.checkNotNull();
        PxVehicleTireSlipState._setSlips(this.address, index, value);
    }

    private static native void _setSlips(long var0, int var2, float var3);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleTireSlipState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

