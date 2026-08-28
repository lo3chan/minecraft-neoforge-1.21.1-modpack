/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleGearboxState
extends NativeObject {
    public static final int SIZEOF = PxVehicleGearboxState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleGearboxState wrapPointer(long address) {
        return address != 0L ? new PxVehicleGearboxState(address) : null;
    }

    public static PxVehicleGearboxState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleGearboxState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleGearboxState(long address) {
        super(address);
    }

    public PxVehicleGearboxState() {
        this.address = PxVehicleGearboxState._PxVehicleGearboxState();
    }

    private static native long _PxVehicleGearboxState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleGearboxState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getCurrentGear() {
        this.checkNotNull();
        return PxVehicleGearboxState._getCurrentGear(this.address);
    }

    private static native int _getCurrentGear(long var0);

    public void setCurrentGear(int value) {
        this.checkNotNull();
        PxVehicleGearboxState._setCurrentGear(this.address, value);
    }

    private static native void _setCurrentGear(long var0, int var2);

    public int getTargetGear() {
        this.checkNotNull();
        return PxVehicleGearboxState._getTargetGear(this.address);
    }

    private static native int _getTargetGear(long var0);

    public void setTargetGear(int value) {
        this.checkNotNull();
        PxVehicleGearboxState._setTargetGear(this.address, value);
    }

    private static native void _setTargetGear(long var0, int var2);

    public float getGearSwitchTime() {
        this.checkNotNull();
        return PxVehicleGearboxState._getGearSwitchTime(this.address);
    }

    private static native float _getGearSwitchTime(long var0);

    public void setGearSwitchTime(float value) {
        this.checkNotNull();
        PxVehicleGearboxState._setGearSwitchTime(this.address, value);
    }

    private static native void _setGearSwitchTime(long var0, float var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleGearboxState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

