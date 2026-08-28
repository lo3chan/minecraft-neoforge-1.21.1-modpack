/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleClutchSlipState
extends NativeObject {
    public static final int SIZEOF = PxVehicleClutchSlipState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleClutchSlipState wrapPointer(long address) {
        return address != 0L ? new PxVehicleClutchSlipState(address) : null;
    }

    public static PxVehicleClutchSlipState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleClutchSlipState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleClutchSlipState(long address) {
        super(address);
    }

    public PxVehicleClutchSlipState() {
        this.address = PxVehicleClutchSlipState._PxVehicleClutchSlipState();
    }

    private static native long _PxVehicleClutchSlipState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleClutchSlipState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getClutchSlip() {
        this.checkNotNull();
        return PxVehicleClutchSlipState._getClutchSlip(this.address);
    }

    private static native float _getClutchSlip(long var0);

    public void setClutchSlip(float value) {
        this.checkNotNull();
        PxVehicleClutchSlipState._setClutchSlip(this.address, value);
    }

    private static native void _setClutchSlip(long var0, float var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleClutchSlipState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

