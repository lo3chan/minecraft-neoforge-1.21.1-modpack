/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.PxVehicleDirectDriveTransmissionCommandStateEnum;

public class PxVehicleDirectDriveTransmissionCommandState
extends NativeObject {
    public static final int SIZEOF = PxVehicleDirectDriveTransmissionCommandState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleDirectDriveTransmissionCommandState wrapPointer(long address) {
        return address != 0L ? new PxVehicleDirectDriveTransmissionCommandState(address) : null;
    }

    public static PxVehicleDirectDriveTransmissionCommandState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleDirectDriveTransmissionCommandState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleDirectDriveTransmissionCommandState(long address) {
        super(address);
    }

    public PxVehicleDirectDriveTransmissionCommandState() {
        this.address = PxVehicleDirectDriveTransmissionCommandState._PxVehicleDirectDriveTransmissionCommandState();
    }

    private static native long _PxVehicleDirectDriveTransmissionCommandState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleDirectDriveTransmissionCommandState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxVehicleDirectDriveTransmissionCommandStateEnum getGear() {
        this.checkNotNull();
        return PxVehicleDirectDriveTransmissionCommandStateEnum.forValue(PxVehicleDirectDriveTransmissionCommandState._getGear(this.address));
    }

    private static native int _getGear(long var0);

    public void setGear(PxVehicleDirectDriveTransmissionCommandStateEnum value) {
        this.checkNotNull();
        PxVehicleDirectDriveTransmissionCommandState._setGear(this.address, value.value);
    }

    private static native void _setGear(long var0, int var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleDirectDriveTransmissionCommandState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

