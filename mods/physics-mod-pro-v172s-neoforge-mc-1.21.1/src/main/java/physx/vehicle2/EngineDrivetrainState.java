/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleAutoboxState;
import physx.vehicle2.PxVehicleClutchCommandResponseState;
import physx.vehicle2.PxVehicleClutchSlipState;
import physx.vehicle2.PxVehicleDifferentialState;
import physx.vehicle2.PxVehicleEngineDriveThrottleCommandResponseState;
import physx.vehicle2.PxVehicleEngineState;
import physx.vehicle2.PxVehicleGearboxState;
import physx.vehicle2.PxVehicleWheelConstraintGroupState;

public class EngineDrivetrainState
extends NativeObject {
    public static final int SIZEOF = EngineDrivetrainState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static EngineDrivetrainState wrapPointer(long address) {
        return address != 0L ? new EngineDrivetrainState(address) : null;
    }

    public static EngineDrivetrainState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return EngineDrivetrainState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected EngineDrivetrainState(long address) {
        super(address);
    }

    public EngineDrivetrainState() {
        this.address = EngineDrivetrainState._EngineDrivetrainState();
    }

    private static native long _EngineDrivetrainState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        EngineDrivetrainState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehicleEngineDriveThrottleCommandResponseState getThrottleCommandResponseState() {
        this.checkNotNull();
        return PxVehicleEngineDriveThrottleCommandResponseState.wrapPointer(EngineDrivetrainState._getThrottleCommandResponseState(this.address));
    }

    private static native long _getThrottleCommandResponseState(long var0);

    public void setThrottleCommandResponseState(PxVehicleEngineDriveThrottleCommandResponseState value) {
        this.checkNotNull();
        EngineDrivetrainState._setThrottleCommandResponseState(this.address, value.getAddress());
    }

    private static native void _setThrottleCommandResponseState(long var0, long var2);

    public PxVehicleAutoboxState getAutoboxState() {
        this.checkNotNull();
        return PxVehicleAutoboxState.wrapPointer(EngineDrivetrainState._getAutoboxState(this.address));
    }

    private static native long _getAutoboxState(long var0);

    public void setAutoboxState(PxVehicleAutoboxState value) {
        this.checkNotNull();
        EngineDrivetrainState._setAutoboxState(this.address, value.getAddress());
    }

    private static native void _setAutoboxState(long var0, long var2);

    public PxVehicleClutchCommandResponseState getClutchCommandResponseState() {
        this.checkNotNull();
        return PxVehicleClutchCommandResponseState.wrapPointer(EngineDrivetrainState._getClutchCommandResponseState(this.address));
    }

    private static native long _getClutchCommandResponseState(long var0);

    public void setClutchCommandResponseState(PxVehicleClutchCommandResponseState value) {
        this.checkNotNull();
        EngineDrivetrainState._setClutchCommandResponseState(this.address, value.getAddress());
    }

    private static native void _setClutchCommandResponseState(long var0, long var2);

    public PxVehicleDifferentialState getDifferentialState() {
        this.checkNotNull();
        return PxVehicleDifferentialState.wrapPointer(EngineDrivetrainState._getDifferentialState(this.address));
    }

    private static native long _getDifferentialState(long var0);

    public void setDifferentialState(PxVehicleDifferentialState value) {
        this.checkNotNull();
        EngineDrivetrainState._setDifferentialState(this.address, value.getAddress());
    }

    private static native void _setDifferentialState(long var0, long var2);

    public PxVehicleWheelConstraintGroupState getWheelConstraintGroupState() {
        this.checkNotNull();
        return PxVehicleWheelConstraintGroupState.wrapPointer(EngineDrivetrainState._getWheelConstraintGroupState(this.address));
    }

    private static native long _getWheelConstraintGroupState(long var0);

    public void setWheelConstraintGroupState(PxVehicleWheelConstraintGroupState value) {
        this.checkNotNull();
        EngineDrivetrainState._setWheelConstraintGroupState(this.address, value.getAddress());
    }

    private static native void _setWheelConstraintGroupState(long var0, long var2);

    public PxVehicleEngineState getEngineState() {
        this.checkNotNull();
        return PxVehicleEngineState.wrapPointer(EngineDrivetrainState._getEngineState(this.address));
    }

    private static native long _getEngineState(long var0);

    public void setEngineState(PxVehicleEngineState value) {
        this.checkNotNull();
        EngineDrivetrainState._setEngineState(this.address, value.getAddress());
    }

    private static native void _setEngineState(long var0, long var2);

    public PxVehicleGearboxState getGearboxState() {
        this.checkNotNull();
        return PxVehicleGearboxState.wrapPointer(EngineDrivetrainState._getGearboxState(this.address));
    }

    private static native long _getGearboxState(long var0);

    public void setGearboxState(PxVehicleGearboxState value) {
        this.checkNotNull();
        EngineDrivetrainState._setGearboxState(this.address, value.getAddress());
    }

    private static native void _setGearboxState(long var0, long var2);

    public PxVehicleClutchSlipState getClutchState() {
        this.checkNotNull();
        return PxVehicleClutchSlipState.wrapPointer(EngineDrivetrainState._getClutchState(this.address));
    }

    private static native long _getClutchState(long var0);

    public void setClutchState(PxVehicleClutchSlipState value) {
        this.checkNotNull();
        EngineDrivetrainState._setClutchState(this.address, value.getAddress());
    }

    private static native void _setClutchState(long var0, long var2);

    public void setToDefault() {
        this.checkNotNull();
        EngineDrivetrainState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

