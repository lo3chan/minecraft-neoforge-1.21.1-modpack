/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleWheelActuationState
extends NativeObject {
    public static final int SIZEOF = PxVehicleWheelActuationState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleWheelActuationState wrapPointer(long address) {
        return address != 0L ? new PxVehicleWheelActuationState(address) : null;
    }

    public static PxVehicleWheelActuationState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleWheelActuationState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleWheelActuationState(long address) {
        super(address);
    }

    public PxVehicleWheelActuationState() {
        this.address = PxVehicleWheelActuationState._PxVehicleWheelActuationState();
    }

    private static native long _PxVehicleWheelActuationState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleWheelActuationState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public boolean getIsBrakeApplied() {
        this.checkNotNull();
        return PxVehicleWheelActuationState._getIsBrakeApplied(this.address);
    }

    private static native boolean _getIsBrakeApplied(long var0);

    public void setIsBrakeApplied(boolean value) {
        this.checkNotNull();
        PxVehicleWheelActuationState._setIsBrakeApplied(this.address, value);
    }

    private static native void _setIsBrakeApplied(long var0, boolean var2);

    public boolean getIsDriveApplied() {
        this.checkNotNull();
        return PxVehicleWheelActuationState._getIsDriveApplied(this.address);
    }

    private static native boolean _getIsDriveApplied(long var0);

    public void setIsDriveApplied(boolean value) {
        this.checkNotNull();
        PxVehicleWheelActuationState._setIsDriveApplied(this.address, value);
    }

    private static native void _setIsDriveApplied(long var0, boolean var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleWheelActuationState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

