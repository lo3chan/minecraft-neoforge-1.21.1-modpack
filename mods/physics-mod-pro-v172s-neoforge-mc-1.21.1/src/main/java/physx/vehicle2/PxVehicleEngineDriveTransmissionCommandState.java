/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleEngineDriveTransmissionCommandState
extends NativeObject {
    public static final int SIZEOF = PxVehicleEngineDriveTransmissionCommandState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleEngineDriveTransmissionCommandState wrapPointer(long address) {
        return address != 0L ? new PxVehicleEngineDriveTransmissionCommandState(address) : null;
    }

    public static PxVehicleEngineDriveTransmissionCommandState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleEngineDriveTransmissionCommandState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleEngineDriveTransmissionCommandState(long address) {
        super(address);
    }

    public PxVehicleEngineDriveTransmissionCommandState() {
        this.address = PxVehicleEngineDriveTransmissionCommandState._PxVehicleEngineDriveTransmissionCommandState();
    }

    private static native long _PxVehicleEngineDriveTransmissionCommandState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleEngineDriveTransmissionCommandState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getClutch() {
        this.checkNotNull();
        return PxVehicleEngineDriveTransmissionCommandState._getClutch(this.address);
    }

    private static native float _getClutch(long var0);

    public void setClutch(float value) {
        this.checkNotNull();
        PxVehicleEngineDriveTransmissionCommandState._setClutch(this.address, value);
    }

    private static native void _setClutch(long var0, float var2);

    public int getTargetGear() {
        this.checkNotNull();
        return PxVehicleEngineDriveTransmissionCommandState._getTargetGear(this.address);
    }

    private static native int _getTargetGear(long var0);

    public void setTargetGear(int value) {
        this.checkNotNull();
        PxVehicleEngineDriveTransmissionCommandState._setTargetGear(this.address, value);
    }

    private static native void _setTargetGear(long var0, int var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleEngineDriveTransmissionCommandState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

