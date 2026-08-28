/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleSuspensionState
extends NativeObject {
    public static final int SIZEOF = PxVehicleSuspensionState.__sizeOf();
    public static final int ALIGNOF = 8;

    private static native int __sizeOf();

    public static PxVehicleSuspensionState wrapPointer(long address) {
        return address != 0L ? new PxVehicleSuspensionState(address) : null;
    }

    public static PxVehicleSuspensionState arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxVehicleSuspensionState.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxVehicleSuspensionState(long address) {
        super(address);
    }

    public PxVehicleSuspensionState() {
        this.address = PxVehicleSuspensionState._PxVehicleSuspensionState();
    }

    private static native long _PxVehicleSuspensionState();

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxVehicleSuspensionState._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public float getJounce() {
        this.checkNotNull();
        return PxVehicleSuspensionState._getJounce(this.address);
    }

    private static native float _getJounce(long var0);

    public void setJounce(float value) {
        this.checkNotNull();
        PxVehicleSuspensionState._setJounce(this.address, value);
    }

    private static native void _setJounce(long var0, float var2);

    public float getJounceSpeed() {
        this.checkNotNull();
        return PxVehicleSuspensionState._getJounceSpeed(this.address);
    }

    private static native float _getJounceSpeed(long var0);

    public void setJounceSpeed(float value) {
        this.checkNotNull();
        PxVehicleSuspensionState._setJounceSpeed(this.address, value);
    }

    private static native void _setJounceSpeed(long var0, float var2);

    public float getSeparation() {
        this.checkNotNull();
        return PxVehicleSuspensionState._getSeparation(this.address);
    }

    private static native float _getSeparation(long var0);

    public void setSeparation(float value) {
        this.checkNotNull();
        PxVehicleSuspensionState._setSeparation(this.address, value);
    }

    private static native void _setSeparation(long var0, float var2);

    public void setToDefault(float _jounce, float _separation) {
        this.checkNotNull();
        PxVehicleSuspensionState._setToDefault(this.address, _jounce, _separation);
    }

    private static native void _setToDefault(long var0, float var2, float var3);
}

