/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleAutoboxState
extends NativeObject {
    public static final int SIZEOF = PxVehicleAutoboxState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleAutoboxState wrapPointer(long address) {
        return address != 0L ? new PxVehicleAutoboxState(address) : null;
    }

    public static PxVehicleAutoboxState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleAutoboxState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleAutoboxState(long address) {
        super(address);
    }

    public PxVehicleAutoboxState() {
        this.address = PxVehicleAutoboxState._PxVehicleAutoboxState();
    }

    private static native long _PxVehicleAutoboxState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleAutoboxState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getTimeSinceLastShift() {
        this.checkNotNull();
        return PxVehicleAutoboxState._getTimeSinceLastShift(this.address);
    }

    private static native float _getTimeSinceLastShift(long var0);

    public void setTimeSinceLastShift(float value) {
        this.checkNotNull();
        PxVehicleAutoboxState._setTimeSinceLastShift(this.address, value);
    }

    private static native void _setTimeSinceLastShift(long var0, float var2);

    public boolean getActiveAutoboxGearShift() {
        this.checkNotNull();
        return PxVehicleAutoboxState._getActiveAutoboxGearShift(this.address);
    }

    private static native boolean _getActiveAutoboxGearShift(long var0);

    public void setActiveAutoboxGearShift(boolean value) {
        this.checkNotNull();
        PxVehicleAutoboxState._setActiveAutoboxGearShift(this.address, value);
    }

    private static native void _setActiveAutoboxGearShift(long var0, boolean var2);

    public void setToDefault() {
        this.checkNotNull();
        PxVehicleAutoboxState._setToDefault(this.address);
    }

    private static native void _setToDefault(long var0);
}

